package com.github.markusbernhardt.xmldoclet;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * @author Manoel Campos
 */
public class TypeUtils {
    private final Types typeUtils;
    private final Elements elementUtils;

    public TypeUtils(final Types typeUtils, final Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
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

    public boolean isException(final TypeElement typeElement) {
        final TypeMirror exceptionType = elementUtils.getTypeElement("java.lang.Exception").asType();
        return typeUtils.isSubtype(typeElement.asType(), exceptionType);
    }

    public boolean isError(final TypeElement typeElement) {
        final TypeMirror errorType = elementUtils.getTypeElement("java.lang.Error").asType();
        return typeUtils.isSubtype(typeElement.asType(), errorType);
    }

    public boolean isSerializable(final TypeElement typeElement) {
        final TypeMirror serializableType = elementUtils.getTypeElement("java.io.Serializable").asType();
        return typeUtils.isSubtype(typeElement.asType(), serializableType);
    }

    public boolean isExternalizable(final TypeElement typeElement) {
        final TypeMirror serializableType = elementUtils.getTypeElement("java.io.Externalizable").asType();
        return typeUtils.isSubtype(typeElement.asType(), serializableType);
    }

}
