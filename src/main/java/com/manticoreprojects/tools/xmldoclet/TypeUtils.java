package com.manticoreprojects.tools.xmldoclet;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Manoel Campos
 */
public class TypeUtils {
    private final Types types;
    private final Elements elements;
    /*
     * The TypeMirror.toString() method returns the fully qualified name of the type.
     * If the type is a method signature, it places the parameters list (parenteses)
     * before the return type (that is void if none), which is an odd convention for Java Code.
     * Instead of returning "void (int)" for a method that receives an int and returns void,
     * it returns "(int)void" (the return type before the parameters list).
     *
     * This way, we invert that order for a conventional representation of a method signature.
     */
    private static final Pattern METHOD_SIGNATURE_WITH_RETURN_TYPE_AT_RIGHT_SIDE = Pattern.compile("^(\\(.*\\))(.*)");

    public TypeUtils(final Types types, final Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public static String getMethodSignature(final ExecutableElement methodDoc) {
        return getQualifiedName(methodDoc);
    }

    /**
     * Checks if an element has a given modifier
     *
     * @param element the element to check
     * @param modifier the modifier we are looking for in the element
     * @return true if the modifier is present in the element declaration, false otherwise
     */
    public static boolean hasModifier(final Element element, final Modifier modifier) {
        return element.getModifiers().contains(modifier);
    }

    /**
     * {@return the list of fields from a given class element}
     *
     * @param classElement the class to get its fields
     */
    public static List<VariableElement> getFields(final TypeElement classElement) {
        return ElementFilter.fieldsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of constructors from a given class element}
     *
     * @param classElement the class to get its constructors
     */
    public static List<ExecutableElement> getConstructors(final TypeElement classElement) {
        return ElementFilter.constructorsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of methods from a given class element}
     *
     * @param classElement the class to get its methods
     */
    public static List<ExecutableElement> getMethods(final TypeElement classElement) {
        return ElementFilter.methodsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return a type as WildcardType if it is such a type, or null otherwise}
     *
     * @param typeMirror the type to get it as a wildcard type
     */
    public static WildcardType getWildcardType(final TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.WILDCARD) {
            return (WildcardType) typeMirror;
        }

        return null;
    }

    /**
     * Gets a type as DeclaredType if the typeMirror has type arguments (such a {@code List<String>}).
     *
     * @param typeMirror the type to get it as a wildcard type
     * @return the type as DeclaredType if it has type arguments, or null otherwise
     */
    public static DeclaredType getParameterizedType(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType declaredType) {
            if (!declaredType.getTypeArguments().isEmpty()) {
                return declaredType;
            }
        }

        return null;
    }

    public static boolean isArray(final TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY;
    }

    /**
     * {@return the dimension of type that represents an array, or an empty string if the type is not an array}
     *
     * @param typeMirror the array type to get its dimension
     */
    public static String getArrayDimension(final TypeMirror typeMirror) {
        int dimension = -1;
        var type = typeMirror;
        while (type.getKind() == TypeKind.ARRAY) {
            dimension++;
            type = ((ArrayType) type).getComponentType();
        }
        return dimension == -1 ? "" : String.valueOf(dimension + 1);
    }

    static String getQualifiedName(final Element element) {
        return getQualifiedName(element.asType());
    }

    static String getQualifiedName(final TypeMirror typeMirror) {
        final String qualified = typeMirror.toString();
        final var matcher = METHOD_SIGNATURE_WITH_RETURN_TYPE_AT_RIGHT_SIDE.matcher(qualified);

        return matcher.matches() ? matcher.group(2) + " " + matcher.group(1) : qualified;
    }

    /**
     * Gets the enum constants from a TypeElement that represents an enum type.
     *
     * @param enumTypeElement the TypeElement representing the enum type
     * @return a list of VariableElement representing the enum constants
     */
    public static List<VariableElement> getEnumConstants(final TypeElement enumTypeElement) {
        return ElementFilter.fieldsIn(enumTypeElement.getEnclosedElements())
                .stream()
                .filter(field -> field.getKind() == ElementKind.ENUM_CONSTANT)
                .toList();
    }

    public boolean isException(final TypeElement typeElement) {
        final TypeMirror exceptionType = elements.getTypeElement("java.lang.Exception").asType();
        return types.isSubtype(typeElement.asType(), exceptionType);
    }

    public boolean isError(final TypeElement typeElement) {
        final TypeMirror errorType = elements.getTypeElement("java.lang.Error").asType();
        return types.isSubtype(typeElement.asType(), errorType);
    }

    public boolean isSerializable(final TypeElement typeElement) {
        final TypeMirror serializableType =
                elements.getTypeElement("java.io.Serializable").asType();
        return types.isSubtype(typeElement.asType(), serializableType);
    }

    public boolean isExternalizable(final TypeElement typeElement) {
        final TypeMirror serializableType =
                elements.getTypeElement("java.io.Externalizable").asType();
        return types.isSubtype(typeElement.asType(), serializableType);
    }

}
