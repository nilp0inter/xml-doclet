package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.*;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNullElse;

/**
 * The main parser class. It scans the given Doclet document root and creates the XML tree.
 *
 * @author markus
 */
public class Parser {
    private final static Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /**
     * A map where each key is a package name and each value is an object containing a package's JavaDoc.
     */
    protected Map<String, Package> packages = new TreeMap<>();

    protected ObjectFactory objectFactory = new ObjectFactory();

    /**
     * The operating environment of a single invocation of the doclet
     */
    private final DocletEnvironment env;
    private final DocTrees docTrees;
    private final TypeUtils typeUtils;

    /**
     * @param env the operating environment of a single invocation of the doclet
     */
    public Parser(final DocletEnvironment env) {
        this.env = env;
        this.docTrees = env.getDocTrees();
        this.typeUtils = new TypeUtils(env.getTypeUtils(), env.getElementUtils());
    }

    public Set<TypeElement> getClasses(final DocletEnvironment env) {
        final Set<? extends Element> elements = env.getIncludedElements();
        return ElementFilter.typesIn(elements);
    }

    private String getJavaDoc(final Element element){
        final var docCommentTree = docTrees.getDocCommentTree(element);
        return docCommentTree == null ? "" : docCommentTree.getFullBody().toString();
    }

    /**
     * {@return the tags inside a JavaDoc comment}
     * @param element the Java element to get its JavaDoc tags
     */
    public List<? extends DocTree> getTags(final Element element) {
        final var docCommentTree = docTrees.getDocCommentTree(element);
        return docCommentTree == null ? List.of() : docCommentTree.getBlockTags();
    }

    /**
     * The entry point into parsing the javadoc.
     *
     * @return The root node, containing everything parsed from javadoc doclet
     */
    public Root parseRootDoc() {
        final Root rootNode = objectFactory.createRoot();

        for (final TypeElement classDoc : getClasses(env)) {
            final Package packageNode = getPackage(rootNode, classDoc);

            switch (classDoc.getKind()) {
                case ANNOTATION_TYPE -> packageNode.getAnnotation().add(parseAnnotationTypeDoc(classDoc));
                case ENUM -> packageNode.getEnum().add(parseEnum(classDoc));
                case INTERFACE -> packageNode.getInterface().add(parseInterface(classDoc));
                default -> packageNode.getClazz().add(parseClass(classDoc));
            }
        }

        return rootNode;
    }

    /**
     * {@return the package node for the given class element}
     * @param rootNode
     * @param classElement class to get its package
     */
    private Package getPackage(final Root rootNode, final TypeElement classElement) {
        // Gets the package element of the given class
        final var packageDoc = (PackageElement) classElement.getEnclosingElement();

        return packages.computeIfAbsent(packageDoc.getQualifiedName().toString(), pkgName -> {
            final var packageNode = parsePackage(packageDoc);
            packages.put(pkgName, packageNode);
            rootNode.getPackage().add(packageNode);
            return packageNode;
        });
    }

    protected Package parsePackage(final PackageElement packageDoc) {
        final Package packageNode = objectFactory.createPackage();
        packageNode.setName(packageDoc.getQualifiedName().toString());
        final String comment = getJavaDoc(packageDoc);
        if (!comment.isEmpty()) {
            packageNode.setComment(comment);
        }

        for (final DocTree tag : getTags(packageDoc)) {
            packageNode.getTag().add(parseTag(tag));
        }

        return packageNode;
    }

    /**
     * Parse an annotation's JavaDoc.
     *
     * @param annotationTypeDoc A AnnotationTypeDoc instance
     * @return the annotation node
     */
    protected Annotation parseAnnotationTypeDoc(final TypeElement annotationTypeDoc) {
        final Annotation annotationNode = objectFactory.createAnnotation();
        annotationNode.setName(annotationTypeDoc.getSimpleName().toString());
        annotationNode.setQualified(annotationTypeDoc.getQualifiedName().toString());
        final String comment = getJavaDoc(annotationTypeDoc);
        if (!comment.isEmpty()) {
            annotationNode.setComment(comment);
        }

        // TODO: What does isIncluded() mean?
        //annotationNode.setIncluded(annotationTypeDoc.isIncluded());

        annotationNode.setScope(parseScope(annotationTypeDoc));

        for (final ExecutableElement annotationTypeElementDoc : annotationTypeDoc.elements()) {
            final var annotationElement = parseAnnotationTypeElementDoc(annotationTypeElementDoc);
            annotationNode.getElement().add(annotationElement);
        }

        for (final AnnotationMirror annotationDesc : annotationTypeDoc.getAnnotationMirrors()) {
            final var annotationInstance = parseAnnotationDesc(annotationDesc, annotationTypeDoc.getQualifiedName());
            annotationNode.getAnnotation().add(annotationInstance);
        }

        for (final DocTree tag : getTags(annotationTypeDoc)) {
            annotationNode.getTag().add(parseTag(tag));
        }

        return annotationNode;
    }

    /**
     * Parse the elements of an annotation
     *
     * @param annotationTypeElementDoc A AnnotationTypeElementDoc instance
     * @return the annotation element node
     */
    protected AnnotationElement parseAnnotationTypeElementDoc(final ExecutableElement annotationTypeElementDoc) {
        final AnnotationElement annotationElementNode = objectFactory.createAnnotationElement();
        annotationElementNode.setName(annotationTypeElementDoc.getSimpleName().toString());
        annotationElementNode.setQualified(annotationTypeElementDoc.getSimpleName().toString());
        annotationElementNode.setType(parseTypeInfo(annotationTypeElementDoc.getReturnType()));

        final AnnotationValue value = annotationTypeElementDoc.getDefaultValue();
        if (value != null) {
            annotationElementNode.setDefault(value.toString());
        }

        return annotationElementNode;
    }

    /**
     * Parses annotation instances of an annotable program element
     *
     * @param annotationDesc annotationDesc
     * @param programElement the name of a program element to parse
     * @return representation of annotations
     */
    protected AnnotationInstance parseAnnotationDesc(final AnnotationMirror annotationDesc, final Name programElement) {
        final AnnotationInstance annotationInstanceNode = objectFactory.createAnnotationInstance();

        try {
            final var annotTypeInfo = annotationDesc.getAnnotationType();
            annotationInstanceNode.setName(annotTypeInfo.asElement().getSimpleName().toString());
            annotationInstanceNode.setQualified(annotTypeInfo.asElement().getSimpleName().toString());
        } catch (ClassCastException castException) {
            LOGGER.severe("Unable to obtain type data about an annotation found on: " + programElement);
            LOGGER.severe("Add to the classpath the class/jar that defines this annotation.");
        }

        for (final AnnotationValue elementValuesPair : annotationDesc.elementValues()) {
            final AnnotationArgument annotationArgumentNode = objectFactory.createAnnotationArgument();
            annotationArgumentNode.setName(elementValuesPair.element().name());

            final TypeMirror annotationArgumentType = elementValuesPair.element().returnType();
            annotationArgumentNode.setType(parseTypeInfo(annotationArgumentType));
            annotationArgumentNode.setPrimitive(annotationArgumentType.getKind().isPrimitive());
            annotationArgumentNode.setArray(annotationArgumentType.dimension().length() > 0);

            final Object objValue = elementValuesPair.value().value();
            switch (objValue) {
                case AnnotationValue[] annotationValues -> {
                    for (AnnotationValue annotationValue : annotationValues) {
                        if (annotationValue.value() instanceof AnnotationMirror annoDesc)
                            annotationArgumentNode.getAnnotation().add(parseAnnotationDesc(annoDesc, programElement));
                        else annotationArgumentNode.getValue().add(annotationValue.value().toString());
                    }
                }
                case VariableElement fieldDoc -> annotationArgumentNode.getValue().add(fieldDoc.getSimpleName().toString());
                case TypeElement classDoc -> annotationArgumentNode.getValue().add(classDoc.getQualifiedName().toString());
                case null, default -> annotationArgumentNode.getValue().add(objValue.toString());
            }

            annotationInstanceNode.getArgument().add(annotationArgumentNode);
        }

        return annotationInstanceNode;
    }

    protected Enum parseEnum(final TypeElement classDoc) {
        final Enum enumNode = objectFactory.createEnum();
        enumNode.setName(classDoc.getSimpleName().toString());
        enumNode.setQualified(classDoc.getQualifiedName().toString());
        final String comment = getJavaDoc(classDoc);
        if (!comment.isEmpty()) {
            enumNode.setComment(comment);
        }

        // TODO: What does isIncluded() mean?
        //enumNode.setIncluded(classDoc.isIncluded());

        enumNode.setScope(parseScope(classDoc));

        final TypeMirror superClassType = classDoc.getSuperclass();
        if (superClassType != null) {
            enumNode.setClazz(parseTypeInfo(superClassType));
        }

        for (final TypeMirror interfaceType : classDoc.getInterfaces()) {
            enumNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final VariableElement field : classDoc.enumConstants()) {
            enumNode.getConstant().add(parseEnumConstant(field));
        }

        for (final AnnotationMirror annotationDesc : classDoc.getAnnotationMirrors()) {
            enumNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, classDoc.getQualifiedName()));
        }

        for (final DocTree tag : getTags(classDoc)) {
            enumNode.getTag().add(parseTag(tag));
        }

        return enumNode;
    }

    /**
     * Parses an enum type definition
     *
     * @param fieldDoc
     * @return
     */
    protected EnumConstant parseEnumConstant(final VariableElement fieldDoc) {
        final EnumConstant enumConstant = objectFactory.createEnumConstant();
        enumConstant.setName(fieldDoc.getSimpleName().toString());
        final String comment = getJavaDoc(fieldDoc);
        if (!comment.isEmpty()) {
            enumConstant.setComment(comment);
        }

        for (final AnnotationMirror annotationDesc : fieldDoc.getAnnotationMirrors()) {
            enumConstant.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.getSimpleName()));
        }

        for (final DocTree tag : getTags(fieldDoc)) {
            enumConstant.getTag().add(parseTag(tag));
        }

        return enumConstant;
    }

    protected Interface parseInterface(final TypeElement classDoc) {
        final Interface interfaceNode = objectFactory.createInterface();
        interfaceNode.setName(classDoc.getSimpleName().toString());
        interfaceNode.setQualified(classDoc.getQualifiedName().toString());
        final String comment = getJavaDoc(classDoc);
        if (!comment.isEmpty()) {
            interfaceNode.setComment(comment);
        }

        // TODO: What does isIncluded() mean?
        //interfaceNode.setIncluded(classDoc.isIncluded());

        interfaceNode.setScope(parseScope(classDoc));

        for (final TypeParameterElement typeVariable : classDoc.getTypeParameters()) {
            interfaceNode.getGeneric().add(parseTypeParameter(typeVariable));
        }

        for (final TypeMirror interfaceType : classDoc.getInterfaces()) {
            interfaceNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final ExecutableElement method : getMethods(classDoc)) {
            interfaceNode.getMethod().add(parseMethod(method));
        }

        for (final AnnotationMirror annotationDesc : classDoc.getAnnotationMirrors()) {
            interfaceNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, classDoc.getQualifiedName()));
        }

        for (final DocTree tag : getTags(classDoc)) {
            interfaceNode.getTag().add(parseTag(tag));
        }

        for (final VariableElement field : getFields(classDoc)) {
            interfaceNode.getField().add(parseField(field));
        }

        return interfaceNode;
    }

    protected Class parseClass(final TypeElement classDoc) {
        final Class classNode = objectFactory.createClass();
        classNode.setName(classDoc.getSimpleName().toString());
        classNode.setQualified(classDoc.getQualifiedName().toString());
        final String comment = getJavaDoc(classDoc);
        if (!comment.isEmpty()) {
            classNode.setComment(comment);
        }
        classNode.setAbstract(hasModifier(classDoc, Modifier.ABSTRACT));
        classNode.setError(typeUtils.isError(classDoc));
        classNode.setException(typeUtils.isException(classDoc));
        classNode.setExternalizable(typeUtils.isExternalizable(classDoc));

        // TODO: What does isIncluded() mean?
        //classNode.setIncluded(classDoc.isIncluded());

        classNode.setSerializable(typeUtils.isSerializable(classDoc));
        classNode.setScope(parseScope(classDoc));

        for (final var typeVariable : classDoc.getTypeParameters()) {
            classNode.getGeneric().add(parseTypeParameter(typeVariable));
        }

        final TypeMirror superClassType = classDoc.getSuperclass();
        if (superClassType != null) {
            classNode.setClazz(parseTypeInfo(superClassType));
        }

        for (final TypeMirror interfaceType : classDoc.getInterfaces()) {
            classNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final ExecutableElement method : getMethods(classDoc)) {
            classNode.getMethod().add(parseMethod(method));
        }

        for (final AnnotationMirror annotationDesc : classDoc.getAnnotationMirrors()) {
            final var annotationInstance = parseAnnotationDesc(annotationDesc, classDoc.getQualifiedName());
            classNode.getAnnotation().add(annotationInstance);
        }

        for (final ExecutableElement constructor : getConstructors(classDoc)) {
            classNode.getConstructor().add(parseConstructor(constructor));
        }

        for (final VariableElement field : getFields(classDoc)) {
            classNode.getField().add(parseField(field));
        }

        for (final DocTree tag : getTags(classDoc)) {
            classNode.getTag().add(parseTag(tag));
        }

        return classNode;
    }

    protected Constructor parseConstructor(final ExecutableElement constructorDoc) {
        final Constructor constructorNode = objectFactory.createConstructor();

        constructorNode.setName(constructorDoc.getSimpleName().toString());
        constructorNode.setQualified(constructorDoc.getSimpleName().toString());
        final String comment = getJavaDoc(constructorDoc);
        if (!comment.isEmpty()) {
            constructorNode.setComment(comment);
        }
        constructorNode.setScope(parseScope(constructorDoc));

        // TODO: What does isIncluded() mean?
        //constructorNode.setIncluded(constructorDoc.isIncluded());

        constructorNode.setFinal(hasModifier(constructorDoc, Modifier.FINAL));
        constructorNode.setNative(hasModifier(constructorDoc, Modifier.NATIVE));
        constructorNode.setStatic(hasModifier(constructorDoc, Modifier.STATIC));
        constructorNode.setSynchronized(hasModifier(constructorDoc, Modifier.SYNCHRONIZED));
        constructorNode.setVarArgs(constructorDoc.isVarArgs());
        constructorNode.setSignature(TypeUtils.getMethodSignature(constructorDoc));

        for (final VariableElement parameter : constructorDoc.getParameters()) {
            constructorNode.getParameter().add(parseMethodParameter(parameter));
        }

        for (final TypeMirror exceptionType : constructorDoc.getThrownTypes()) {
            constructorNode.getException().add(parseTypeInfo(exceptionType));
        }

        for (final AnnotationMirror annotationDesc : constructorDoc.getAnnotationMirrors()) {
            final var annotationInstance = parseAnnotationDesc(annotationDesc, constructorDoc.getSimpleName());
            constructorNode.getAnnotation().add(annotationInstance);
        }

        for (final DocTree tag : getTags(constructorDoc)) {
            constructorNode.getTag().add(parseTag(tag));
        }

        return constructorNode;
    }

    protected Method parseMethod(final ExecutableElement methodDoc) {
        final Method methodNode = objectFactory.createMethod();

        methodNode.setName(methodDoc.getSimpleName().toString());
        methodNode.setQualified(methodDoc.getSimpleName().toString());
        final String comment = getJavaDoc(methodDoc);
        if (!comment.isEmpty()) {
            methodNode.setComment(comment);
        }
        methodNode.setScope(parseScope(methodDoc));
        methodNode.setAbstract(hasModifier(methodDoc, Modifier.ABSTRACT));

        // TODO: What does isIncluded() mean?
        //methodNode.setIncluded(methodDoc.isIncluded());

        methodNode.setFinal(hasModifier(methodDoc, Modifier.FINAL));
        methodNode.setNative(hasModifier(methodDoc, Modifier.NATIVE));
        methodNode.setStatic(hasModifier(methodDoc, Modifier.STATIC));
        methodNode.setSynchronized(hasModifier(methodDoc, Modifier.SYNCHRONIZED));
        methodNode.setVarArgs(methodDoc.isVarArgs());
        methodNode.setSignature(TypeUtils.getMethodSignature(methodDoc));
        methodNode.setReturn(parseTypeInfo(methodDoc.getReturnType()));

        for (final VariableElement parameter : methodDoc.getParameters()) {
            methodNode.getParameter().add(parseMethodParameter(parameter));
        }

        for (final TypeMirror exceptionType : methodDoc.getThrownTypes()) {
            methodNode.getException().add(parseTypeInfo(exceptionType));
        }

        for (final AnnotationMirror annotationDesc : methodDoc.getAnnotationMirrors()) {
            final var annotationInstance = parseAnnotationDesc(annotationDesc, methodDoc.getSimpleName());
            methodNode.getAnnotation().add(annotationInstance);
        }

        for (final DocTree tag : getTags(methodDoc)) {
            methodNode.getTag().add(parseTag(tag));
        }

        return methodNode;
    }

    protected MethodParameter parseMethodParameter(final VariableElement parameter) {
        final MethodParameter parameterMethodNode = objectFactory.createMethodParameter();
        parameterMethodNode.setName(parameter.getSimpleName().toString());
        parameterMethodNode.setType(parseTypeInfo(parameter.asType()));

        for (final AnnotationMirror annotationDesc : parameter.getAnnotationMirrors()) {
            final var annotationInstance = parseAnnotationDesc(annotationDesc, parameter.getSimpleName());
            parameterMethodNode.getAnnotation().add(annotationInstance);
        }

        return parameterMethodNode;
    }

    /**
     * Checks if an element has a given modifier
     * @param element the element to check
     * @param modifier the modifier we are looking for in the element
     * @return true if the modifier is present in the element declaration, false otherwise
     */
    public boolean hasModifier(final Element element, final Modifier modifier) {
        return element.getModifiers().contains(modifier);
    }

    /**
     * {@return the list of fields from a given class element}
     * @param classElement the class to get its fields
     */
    private List<VariableElement> getFields(final TypeElement classElement) {
        return ElementFilter.fieldsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of constructors from a given class element}
     * @param classElement the class to get its constructors
     */
    private List<ExecutableElement> getConstructors(final TypeElement classElement) {
        return ElementFilter.constructorsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of methods from a given class element}
     * @param classElement the class to get its methods
     */
    private List<ExecutableElement> getMethods(final TypeElement classElement) {
        return ElementFilter.methodsIn(classElement.getEnclosedElements());
    }

    protected Field parseField(final VariableElement fieldDoc) {
        final Field fieldNode = objectFactory.createField();
        fieldNode.setType(parseTypeInfo(fieldDoc.asType()));
        fieldNode.setName(fieldDoc.getSimpleName().toString());
        fieldNode.setQualified(fieldDoc.getSimpleName().toString());
        String comment = getJavaDoc(fieldDoc);
        if (!comment.isEmpty()) {
            fieldNode.setComment(comment);
        }
        fieldNode.setScope(parseScope(fieldDoc));
        fieldNode.setFinal(hasModifier(fieldDoc, Modifier.FINAL));
        fieldNode.setStatic(hasModifier(fieldDoc, Modifier.STATIC));
        fieldNode.setVolatile(hasModifier(fieldDoc, Modifier.VOLATILE));
        fieldNode.setTransient(hasModifier(fieldDoc, Modifier.TRANSIENT));
        fieldNode.setConstant(requireNonNullElse(fieldDoc.getConstantValue(), "").toString());

        for (final AnnotationMirror annotationDesc : fieldDoc.getAnnotationMirrors()) {
            fieldNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.getSimpleName()));
        }

        for (final DocTree tag : getTags(fieldDoc)) {
            fieldNode.getTag().add(parseTag(tag));
        }

        return fieldNode;
    }

    protected TypeInfo parseTypeInfo(final TypeMirror type) {
        final TypeInfo typeInfoNode = objectFactory.createTypeInfo();
        typeInfoNode.setQualified(type.toString());
        final String dimension = type.dimension();
        if (!dimension.isEmpty()) {
            typeInfoNode.setDimension(dimension);
        }

        final WildcardType wildcard = type.asWildcardType();
        if (wildcard != null) {
            typeInfoNode.setWildcard(parseWildcard(wildcard));
        }

        final ParameterizedType parameterized = type.asParameterizedType();

        if (parameterized != null) {
            for (final Type typeArgument : parameterized.getActualTypeArguments()) {
                typeInfoNode.getGeneric().add(parseTypeInfo(typeArgument));
            }
        }

        return typeInfoNode;
    }

    protected Wildcard parseWildcard(final WildcardType wildcard) {
        final Wildcard wildcardNode = objectFactory.createWildcard();

        final TypeMirror extendType = wildcard.getExtendsBound();
        wildcardNode.getExtendsBound().add(parseTypeInfo(extendType));

        final TypeMirror superType = wildcard.getSuperBound();
        wildcardNode.getSuperBound().add(parseTypeInfo(superType));

        return wildcardNode;
    }

    protected TypeParameter parseTypeParameter(final TypeParameterElement typeParameter) {
        return parseTypeParameter((TypeVariable) typeParameter.asType());
    }

    /**
     * Parse type variables for generics
     *
     * @param typeVariable
     * @return
     */
    protected TypeParameter parseTypeParameter(final TypeVariable typeVariable) {
        final TypeParameter typeParameter = objectFactory.createTypeParameter();
        typeParameter.setName(typeVariable.toString());

        final List<String> bounds = typeParameter.getBound();
        bounds.add(typeVariable.getLowerBound().toString());
        bounds.add(typeVariable.getUpperBound().toString());

        return typeParameter;
    }

    protected TagInfo parseTag(final DocTree tagDoc) {
        final TagInfo tagNode = objectFactory.createTagInfo();
        tagNode.setName(tagDoc.getKind().tagName);
        tagNode.setText(tagDoc.toString());
        return tagNode;
    }

    /**
     * {@return string representation of the element scope}
     * @param doc the element to get its scope
     */
    protected String parseScope(final Element doc) {
        if (doc.getModifiers().contains(Modifier.PRIVATE)) {
            return "private";
        } else if (doc.getModifiers().contains(Modifier.PROTECTED)) {
            return "protected";
        } else if (doc.getModifiers().contains(Modifier.PUBLIC)) {
            return "public";
        }

        return "";
    }
}
