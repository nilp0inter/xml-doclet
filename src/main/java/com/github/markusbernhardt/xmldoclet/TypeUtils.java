package com.github.markusbernhardt.xmldoclet;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Manoel Campos
 */
public class TypeUtils {
    private final Types types;
    private final Elements elements;

    public TypeUtils(final Types types, final Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public static String getMethodSignature(final ExecutableElement methodDoc) {
        return methodDoc.asType().toString();
    }

    /**
     * Checks if an element has a given modifier
     * @param element the element to check
     * @param modifier the modifier we are looking for in the element
     * @return true if the modifier is present in the element declaration, false otherwise
     */
    public static boolean hasModifier(final Element element, final Modifier modifier) {
        return element.getModifiers().contains(modifier);
    }

    /**
     * {@return the list of fields from a given class element}
     * @param classElement the class to get its fields
     */
    public static List<VariableElement> getFields(final TypeElement classElement) {
        return ElementFilter.fieldsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of constructors from a given class element}
     * @param classElement the class to get its constructors
     */
    public static List<ExecutableElement> getConstructors(final TypeElement classElement) {
        return ElementFilter.constructorsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return the list of methods from a given class element}
     * @param classElement the class to get its methods
     */
    public static List<ExecutableElement> getMethods(final TypeElement classElement) {
        return ElementFilter.methodsIn(classElement.getEnclosedElements());
    }

    /**
     * {@return a type as WildcardType if it is such a type, or null otherwise}
     * @param typeMirror the type to get it as a wildcard type
     */
    public static WildcardType getWildcardType(final TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.WILDCARD) {
            return (WildcardType) typeMirror;
        }

        return null;
    }

    /**
     * {@return a type as ParameterizedType if it is such a type, or null otherwise}
     * @param typeMirror the type to get it as a wildcard type
     */
    public static ParameterizedType getParameterizedType(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType declaredType) {
            if (!declaredType.getTypeArguments().isEmpty()) {
                return (ParameterizedType) declaredType;
            }
        }

        return null;
    }

    public static boolean isArray(final TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY;
    }

    /**
     * {@return the dimension of type that represents an array, or an empty string if the type is not an array}
     * @param typeMirror the array type to get its dimension
     */
    public static String getArrayDimension(TypeMirror typeMirror) {
        int dimension = -1;
        while (typeMirror.getKind() == TypeKind.ARRAY) {
            dimension++;
            typeMirror = ((ArrayType) typeMirror).getComponentType();
        }
        return dimension == -1 ? "" : String.valueOf(dimension+1);
    }

    /**
     * {@return a TypeMirror for a given Type instance}
     * @param type the {@link Type} instance to get a {@link TypeMirror}
     */
    public TypeMirror getTypeMirror(final Type type) {
        if (type instanceof java.lang.Class<?>) {
            return elements.getTypeElement(((java.lang.Class<?>) type).getCanonicalName()).asType();
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
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
        final TypeMirror serializableType = elements.getTypeElement("java.io.Serializable").asType();
        return types.isSubtype(typeElement.asType(), serializableType);
    }

    public boolean isExternalizable(final TypeElement typeElement) {
        final TypeMirror serializableType = elements.getTypeElement("java.io.Externalizable").asType();
        return types.isSubtype(typeElement.asType(), serializableType);
    }

}
