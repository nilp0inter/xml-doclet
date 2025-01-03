package com.github.markusbernhardt.xmldoclet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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
