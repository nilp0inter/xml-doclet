package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation12;
import com.github.markusbernhardt.xmldoclet.simpledata.MethodsC;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test group for Methods in {@link MethodsC}.
 */
public class MethodsCTest extends AbstractMethodsTest {
    public MethodsCTest() {
        super("MethodsC.java");
    }


    @Test
    public void testMethodsProperties1() {
        final Method method = findByMethodName("method1");
        assertMethodScope(method, "public");
    }

    @Test
    public void testMethodsProperties2() {
        // method -- we check package scope
        final Method method = findByMethodName("method2");
        assertMethodScope(method, "");
    }

    @Test
    public void testMethodsProperties3() {
        final Method method = findByMethodName("method3");
        assertMethodScope(method, "private");
    }

    @Test
    public void testMethodsProperties4() {
        final Method method = findByMethodName("method4");
        assertMethodScope(method, "protected");
        assertFalse(method.isStatic());
        assertFalse(method.isNative());
        assertFalse(method.isFinal());
        assertFalse(method.isSynchronized());
        assertTrue(method.getException().isEmpty());
        assertTrue(method.getAnnotation().isEmpty());
    }

    @Test
    public void testMethodsProperties5() {
        final Method method = findByMethodName("method5");
        assertTrue(method.isNative());
    }

    @Test
    public void testMethodsProperties6() {
        final Method method = findByMethodName("method6");
        assertTrue(method.isStatic());
    }

    @Test
    public void testMethodsProperties7() {
        final Method method = findByMethodName("method7");
        assertTrue(method.isFinal());
    }

    @Test
    public void testMethodsProperties8() {
        final Method method = findByMethodName("method8");
        assertTrue(method.isSynchronized());
    }

    @Test
    public void testMethodsProperties9() {
        // methodNode9 -- we check one thrown exception
        final Method method = findByMethodName("method9");
        assertEquals(method.getException().size(), 1);

        final TypeInfo exception = method.getException().getFirst();
        checkParamType(exception, "java.lang.Exception");
        paramHasNoGenericsNoDimensionNoWildcard(exception);
    }

    @Test
    public void testMethodsProperties10() {
        // methodNode10 -- we check two thrown exceptions
        final Method method = findByMethodName("method10");
        assertEquals(method.getException().size(), 2);

        final var exception1 = method.getException().get(0);
        checkParamType(exception1, "java.lang.OutOfMemoryError");
        paramHasNoGenericsNoDimension(exception1);

        final var exception2 = method.getException().get(1);
        checkParamType(exception2, "java.lang.IllegalArgumentException");
        paramHasNoGenericsNoDimension(exception2);
    }

    @Test
    public void testMethodsProperties11() {
        // methodNode11 -- 1 annotation instance
        final Method method = findByMethodName("method11");
        assertEquals(method.getAnnotation().size(), 1);

        final AnnotationInstance annotation = method.getAnnotation().getFirst();
        assertEquals(annotation.getQualified(), "java.lang.Deprecated");
        assertTrue(annotation.getArgument().isEmpty());
    }

    @Test
    public void testMethodsProperties12() {
        // methodNode12 -- 2 annotation instances
        final Method method = findByMethodName("method12");
        assertEquals(method.getAnnotation().size(), 2);

        final var annotation1 = method.getAnnotation().get(0);
        assertEquals(annotation1.getQualified(), "java.lang.Deprecated");

        final var annotation2 = method.getAnnotation().get(1);
        assertEquals(annotation2.getQualified(), Annotation12.class.getName());
        assertEquals(annotation2.getArgument().size(), 1);
        final AnnotationArgument annotationArgument = annotation2.getArgument().getFirst();
        assertEquals(annotationArgument.getName(), "value");
        assertEquals(annotationArgument.getValue().getFirst(), "java.lang.Warning");
    }

    private static void assertMethodScope(final Method method, final String scope) {
        assertEquals(method.getScope(), scope);
    }

    private static void paramHasNoGenericsNoDimension(final TypeInfo paramTypeInfo) {
        assertNull(paramTypeInfo.getDimension());
        assertTrue(paramTypeInfo.getGeneric().isEmpty());
    }

}
