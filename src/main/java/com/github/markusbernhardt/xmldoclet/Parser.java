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

/**
 * The main parser class. It scans the given Doclet document root and creates the XML tree.
 *
 * @author markus
 */
public class Parser {
    private final static Logger LOGGER = Logger.getLogger(Parser.class.getName());

    protected Map<String, Package> packages = new TreeMap<>();

    protected ObjectFactory objectFactory = new ObjectFactory();
    private DocTrees docTrees;

    public Set<TypeElement> getClasses(final DocletEnvironment env) {
        final Set<? extends Element> elements = env.getIncludedElements();
        return ElementFilter.typesIn(elements);
    }

    private String getJavaDoc(final Element element){
        final var docCommentTree = docTrees.getDocCommentTree(element);
        return docCommentTree == null ? "" : docCommentTree.getFullBody().toString();
    }

    /**
     * {@return the package element of the class}
     * @param classElement class to get its package
     */
    public PackageElement getPackageElement(final TypeElement classElement) {
        return (PackageElement) classElement.getEnclosingElement();
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
     * @param env the operating environment of a single invocation of the doclet
     * @return The root node, containing everything parsed from javadoc doclet
     */
    public Root parseRootDoc(final DocletEnvironment env) {
        this.docTrees = env.getDocTrees();
        final Root rootNode = objectFactory.createRoot();

        for (final TypeElement classDoc : getClasses(env)) {
            final PackageElement packageDoc = getPackageElement(classDoc);

            final Package packageNode = packages.get(packageDoc.getQualifiedName().toString());
            if (packageNode == null) {
                packageNode = parsePackage(packageDoc);
                packages.put(packageDoc.getQualifiedName().toString(), packageNode);
                rootNode.getPackage().add(packageNode);
            }

            if (classDoc instanceof AnnotationTypeDoc) {
                packageNode.getAnnotation()
                        .add(parseAnnotationTypeDoc((AnnotationTypeDoc) classDoc));
            } else if (classDoc.isEnum()) {
                packageNode.getEnum().add(parseEnum(classDoc));
            } else if (classDoc.isInterface()) {
                packageNode.getInterface().add(parseInterface(classDoc));
            } else {
                packageNode.getClazz().add(parseClass(classDoc));
            }
        }

        return rootNode;
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
     * Parse an annotation.
     *
     * @param annotationTypeDoc A AnnotationTypeDoc instance
     * @return the annotation node
     */
    protected Annotation parseAnnotationTypeDoc(final TypeElement annotationTypeDoc) {
        final Annotation annotationNode = objectFactory.createAnnotation();
        annotationNode.setName(annotationTypeDoc.name());
        annotationNode.setQualified(annotationTypeDoc.qualifiedName());
        final String comment = annotationTypeDoc.commentText();
        if (!comment.isEmpty()) {
            annotationNode.setComment(comment);
        }
        annotationNode.setIncluded(annotationTypeDoc.isIncluded());
        annotationNode.setScope(parseScope(annotationTypeDoc));

        for (ExecutableElement annotationTypeElementDoc : annotationTypeDoc.elements()) {
            annotationNode.getElement()
                    .add(parseAnnotationTypeElementDoc(annotationTypeElementDoc));
        }

        for (AnnotationMirror annotationDesc : annotationTypeDoc.annotations()) {
            annotationNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, annotationTypeDoc.qualifiedName()));
        }

        for (Tag tag : annotationTypeDoc.tags()) {
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
        annotationElementNode.setName(annotationTypeElementDoc.name());
        annotationElementNode.setQualified(annotationTypeElementDoc.qualifiedName());
        annotationElementNode.setType(parseTypeInfo(annotationTypeElementDoc.returnType()));

        final AnnotationValue value = annotationTypeElementDoc.defaultValue();
        if (value != null) {
            annotationElementNode.setDefault(value.toString());
        }

        return annotationElementNode;
    }

    /**
     * Parses annotation instances of an annotable program element
     *
     * @param annotationDesc annotationDesc
     * @param programElement programElement
     * @return representation of annotations
     */
    protected AnnotationInstance parseAnnotationDesc(final AnnotationMirror annotationDesc, String programElement) {
        final AnnotationInstance annotationInstanceNode = objectFactory.createAnnotationInstance();

        try {
            final AnnotationTypeDoc annotTypeInfo = annotationDesc.annotationType();
            annotationInstanceNode.setName(annotTypeInfo.name());
            annotationInstanceNode.setQualified(annotTypeInfo.qualifiedTypeName());
        } catch (ClassCastException castException) {
            LOGGER.severe(
                    "Unable to obtain type data about an annotation found on: " + programElement);
            LOGGER.severe("Add to the classpath the class/jar that defines this annotation.");
        }

        for (final AnnotationValue elementValuesPair : annotationDesc.elementValues()) {
            final AnnotationArgument annotationArgumentNode = objectFactory.createAnnotationArgument();
            annotationArgumentNode.setName(elementValuesPair.element().name());

            final TypeMirror annotationArgumentType = elementValuesPair.element().returnType();
            annotationArgumentNode.setType(parseTypeInfo(annotationArgumentType));
            annotationArgumentNode.setPrimitive(annotationArgumentType.isPrimitive());
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
                case VariableElement fieldDoc -> annotationArgumentNode.getValue().add(fieldDoc.name());
                case TypeElement classDoc -> annotationArgumentNode.getValue().add(classDoc.qualifiedTypeName());
                case null, default -> annotationArgumentNode.getValue().add(objValue.toString());
            }

            annotationInstanceNode.getArgument().add(annotationArgumentNode);
        }

        return annotationInstanceNode;
    }

    protected Enum parseEnum(final TypeElement classDoc) {
        final Enum enumNode = objectFactory.createEnum();
        enumNode.setName(classDoc.name());
        enumNode.setQualified(classDoc.qualifiedName());
        final String comment = classDoc.commentText();
        if (!comment.isEmpty()) {
            enumNode.setComment(comment);
        }
        enumNode.setIncluded(classDoc.isIncluded());
        enumNode.setScope(parseScope(classDoc));

        final TypeMirror superClassType = classDoc.superclassType();
        if (superClassType != null) {
            enumNode.setClazz(parseTypeInfo(superClassType));
        }

        for (final TypeMirror interfaceType : classDoc.interfaceTypes()) {
            enumNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final VariableElement field : classDoc.enumConstants()) {
            enumNode.getConstant().add(parseEnumConstant(field));
        }

        for (AnnotationMirror annotationDesc : classDoc.annotations()) {
            enumNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
        }

        for (Tag tag : classDoc.tags()) {
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
        enumConstant.setName(fieldDoc.name());
        String comment = fieldDoc.commentText();
        if (!comment.isEmpty()) {
            enumConstant.setComment(comment);
        }

        for (final AnnotationMirror annotationDesc : fieldDoc.annotations()) {
            enumConstant.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
        }

        for (Tag tag : fieldDoc.tags()) {
            enumConstant.getTag().add(parseTag(tag));
        }

        return enumConstant;
    }

    protected Interface parseInterface(final TypeElement classDoc) {
        final Interface interfaceNode = objectFactory.createInterface();
        interfaceNode.setName(classDoc.name());
        interfaceNode.setQualified(classDoc.qualifiedName());
        String comment = classDoc.commentText();
        if (!comment.isEmpty()) {
            interfaceNode.setComment(comment);
        }
        interfaceNode.setIncluded(classDoc.isIncluded());
        interfaceNode.setScope(parseScope(classDoc));

        for (final TypeVariable typeVariable : classDoc.typeParameters()) {
            interfaceNode.getGeneric().add(parseTypeParameter(typeVariable));
        }

        for (final TypeMirror interfaceType : classDoc.interfaceTypes()) {
            interfaceNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final ExecutableElement method : classDoc.methods()) {
            interfaceNode.getMethod().add(parseMethod(method));
        }

        for (final AnnotationMirror annotationDesc : classDoc.annotations()) {
            interfaceNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
        }

        for (Tag tag : classDoc.tags()) {
            interfaceNode.getTag().add(parseTag(tag));
        }

        for (final VariableElement field : classDoc.fields()) {
            interfaceNode.getField().add(parseField(field));
        }

        return interfaceNode;
    }

    protected Class parseClass(final TypeElement classDoc) {
        final Class classNode = objectFactory.createClass();
        classNode.setName(classDoc.name());
        classNode.setQualified(classDoc.qualifiedName());
        String comment = classDoc.commentText();
        if (!comment.isEmpty()) {
            classNode.setComment(comment);
        }
        classNode.setAbstract(classDoc.isAbstract());
        classNode.setError(classDoc.isError());
        classNode.setException(classDoc.isException());
        classNode.setExternalizable(classDoc.isExternalizable());
        classNode.setIncluded(classDoc.isIncluded());
        classNode.setSerializable(classDoc.isSerializable());
        classNode.setScope(parseScope(classDoc));

        for (TypeVariable typeVariable : classDoc.typeParameters()) {
            classNode.getGeneric().add(parseTypeParameter(typeVariable));
        }

        final TypeMirror superClassType = classDoc.superclassType();
        if (superClassType != null) {
            classNode.setClazz(parseTypeInfo(superClassType));
        }

        for (final TypeMirror interfaceType : classDoc.interfaceTypes()) {
            classNode.getInterface().add(parseTypeInfo(interfaceType));
        }

        for (final ExecutableElement method : classDoc.methods()) {
            classNode.getMethod().add(parseMethod(method));
        }

        for (final AnnotationMirror annotationDesc : classDoc.annotations()) {
            classNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
        }

        for (final ExecutableElement constructor : classDoc.constructors()) {
            classNode.getConstructor().add(parseConstructor(constructor));
        }

        for (final VariableElement field : classDoc.fields()) {
            classNode.getField().add(parseField(field));
        }

        for (Tag tag : classDoc.tags()) {
            classNode.getTag().add(parseTag(tag));
        }

        return classNode;
    }

    protected Constructor parseConstructor(final ExecutableElement constructorDoc) {
        final Constructor constructorNode = objectFactory.createConstructor();

        constructorNode.setName(constructorDoc.name());
        constructorNode.setQualified(constructorDoc.qualifiedName());
        String comment = constructorDoc.commentText();
        if (!comment.isEmpty()) {
            constructorNode.setComment(comment);
        }
        constructorNode.setScope(parseScope(constructorDoc));
        constructorNode.setIncluded(constructorDoc.isIncluded());
        constructorNode.setFinal(constructorDoc.isFinal());
        constructorNode.setNative(constructorDoc.isNative());
        constructorNode.setStatic(constructorDoc.isStatic());
        constructorNode.setSynchronized(constructorDoc.isSynchronized());
        constructorNode.setVarArgs(constructorDoc.isVarArgs());
        constructorNode.setSignature(constructorDoc.signature());

        for (final VariableElement parameter : constructorDoc.parameters()) {
            constructorNode.getParameter().add(parseMethodParameter(parameter));
        }

        for (final TypeMirror exceptionType : constructorDoc.thrownExceptionTypes()) {
            constructorNode.getException().add(parseTypeInfo(exceptionType));
        }

        for (final AnnotationMirror annotationDesc : constructorDoc.annotations()) {
            constructorNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, constructorDoc.qualifiedName()));
        }

        for (Tag tag : constructorDoc.tags()) {
            constructorNode.getTag().add(parseTag(tag));
        }

        return constructorNode;
    }

    protected Method parseMethod(final ExecutableElement methodDoc) {
        final Method methodNode = objectFactory.createMethod();

        methodNode.setName(methodDoc.name());
        methodNode.setQualified(methodDoc.qualifiedName());
        String comment = methodDoc.commentText();
        if (!comment.isEmpty()) {
            methodNode.setComment(comment);
        }
        methodNode.setScope(parseScope(methodDoc));
        methodNode.setAbstract(methodDoc.isAbstract());
        methodNode.setIncluded(methodDoc.isIncluded());
        methodNode.setFinal(methodDoc.isFinal());
        methodNode.setNative(methodDoc.isNative());
        methodNode.setStatic(methodDoc.isStatic());
        methodNode.setSynchronized(methodDoc.isSynchronized());
        methodNode.setVarArgs(methodDoc.isVarArgs());
        methodNode.setSignature(methodDoc.signature());
        methodNode.setReturn(parseTypeInfo(methodDoc.returnType()));

        for (final VariableElement parameter : methodDoc.parameters()) {
            methodNode.getParameter().add(parseMethodParameter(parameter));
        }

        for (final TypeMirror exceptionType : methodDoc.thrownExceptionTypes()) {
            methodNode.getException().add(parseTypeInfo(exceptionType));
        }

        for (final AnnotationMirror annotationDesc : methodDoc.annotations()) {
            methodNode.getAnnotation()
                    .add(parseAnnotationDesc(annotationDesc, methodDoc.qualifiedName()));
        }

        for (Tag tag : methodDoc.tags()) {
            methodNode.getTag().add(parseTag(tag));
        }

        return methodNode;
    }

    protected MethodParameter parseMethodParameter(final VariableElement parameter) {
        final MethodParameter parameterMethodNode = objectFactory.createMethodParameter();
        parameterMethodNode.setName(parameter.name());
        parameterMethodNode.setType(parseTypeInfo(parameter.type()));

        for (final AnnotationMirror annotationDesc : parameter.annotations()) {
            parameterMethodNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, parameter.typeName()));
        }

        return parameterMethodNode;
    }

    protected Field parseField(final VariableElement fieldDoc) {
        final Field fieldNode = objectFactory.createField();
        fieldNode.setType(parseTypeInfo(fieldDoc.type()));
        fieldNode.setName(fieldDoc.name());
        fieldNode.setQualified(fieldDoc.qualifiedName());
        String comment = fieldDoc.commentText();
        if (!comment.isEmpty()) {
            fieldNode.setComment(comment);
        }
        fieldNode.setScope(parseScope(fieldDoc));
        fieldNode.setFinal(fieldDoc.isFinal());
        fieldNode.setStatic(fieldDoc.isStatic());
        fieldNode.setVolatile(fieldDoc.isVolatile());
        fieldNode.setTransient(fieldDoc.isTransient());
        fieldNode.setConstant(fieldDoc.constantValueExpression());

        for (final AnnotationMirror annotationDesc : fieldDoc.annotations()) {
            fieldNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
        }

        for (Tag tag : fieldDoc.tags()) {
            fieldNode.getTag().add(parseTag(tag));
        }

        return fieldNode;
    }

    protected TypeInfo parseTypeInfo(final TypeMirror type) {
        final TypeInfo typeInfoNode = objectFactory.createTypeInfo();
        typeInfoNode.setQualified(type.qualifiedTypeName());
        String dimension = type.dimension();
        if (!dimension.isEmpty()) {
            typeInfoNode.setDimension(dimension);
        }

        final WildcardType wildcard = type.asWildcardType();
        if (wildcard != null) {
            typeInfoNode.setWildcard(parseWildcard(wildcard));
        }

        final ParameterizedType parameterized = type.asParameterizedType();
        if (parameterized != null) {
            for (Type typeArgument : parameterized.typeArguments()) {
                typeInfoNode.getGeneric().add(parseTypeInfo(typeArgument));
            }
        }

        return typeInfoNode;
    }

    protected Wildcard parseWildcard(final WildcardType wildcard) {
        final Wildcard wildcardNode = objectFactory.createWildcard();

        for (final TypeMirror extendType : wildcard.extendsBounds()) {
            wildcardNode.getExtendsBound().add(parseTypeInfo(extendType));
        }

        for (final TypeMirror superType : wildcard.superBounds()) {
            wildcardNode.getSuperBound().add(parseTypeInfo(superType));
        }

        return wildcardNode;
    }

    /**
     * Parse type variables for generics
     *
     * @param typeVariable
     * @return
     */
    protected TypeParameter parseTypeParameter(final TypeVariable typeVariable) {
        final TypeParameter typeParameter = objectFactory.createTypeParameter();
        typeParameter.setName(typeVariable.typeName());

        for (final TypeMirror bound : typeVariable.bounds()) {
            typeParameter.getBound().add(bound.qualifiedTypeName());
        }

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
