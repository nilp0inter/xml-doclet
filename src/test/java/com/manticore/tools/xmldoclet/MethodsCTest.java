package com.manticore.tools.xmldoclet;

import com.manticore.tools.xmldoclet.simpledata.Annotation12;
import com.manticore.tools.xmldoclet.simpledata.MethodsC;
import com.manticore.tools.xmldoclet.xjc.AnnotationArgument;
import com.manticore.tools.xmldoclet.xjc.AnnotationInstance;
import com.manticore.tools.xmldoclet.xjc.Method;
import com.manticore.tools.xmldoclet.xjc.TypeInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test group for Methods in {@link MethodsC}.
 */
class MethodsCTest extends AbstractMethodsTest {
    public MethodsCTest() {
        super("MethodsC.java");
    }

    @Test
    void testMethodsProperties1() {
        final Method method = findByMethodName("method1");
        assertMethodScope(method, "public");
    }

    @Test
    void testMethodsProperties2() {
        // method -- we check package scope
        final Method method = findByMethodName("method2");
        assertMethodScope(method, "");
    }

    @Test
    void testMethodsProperties3() {
        final Method method = findByMethodName("method3");
        assertMethodScope(method, "private");
    }

    @Test
    void testMethodsProperties4() {
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
    void testMethodsProperties5() {
        final Method method = findByMethodName("method5");
        assertTrue(method.isNative());
    }

    @Test
    void testMethodsProperties6() {
        final Method method = findByMethodName("method6");
        assertTrue(method.isStatic());
    }

    @Test
    void testMethodsProperties7() {
        final Method method = findByMethodName("method7");
        assertTrue(method.isFinal());
    }

    @Test
    void testMethodsProperties8() {
        final Method method = findByMethodName("method8");
        assertTrue(method.isSynchronized());
    }

    @Test
    void testMethodsProperties9() {
        // methodNode9 -- we check one thrown exception
        final Method method = findByMethodName("method9");
        assertEquals(1, method.getException().size());

        final TypeInfo exception = method.getException().getFirst();
        checkParamType(exception, "java.lang.Exception");
        paramHasNoGenericsNoDimensionNoWildcard(exception);
    }

    @Test
    void testMethodsProperties10() {
        // methodNode10 -- we check two thrown exceptions
        final Method method = findByMethodName("method10");
        assertEquals(2, method.getException().size());

        final var exception1 = method.getException().get(0);
        checkParamType(exception1, "java.lang.OutOfMemoryError");
        paramHasNoGenericsNoDimension(exception1);

        final var exception2 = method.getException().get(1);
        checkParamType(exception2, "java.lang.IllegalArgumentException");
        paramHasNoGenericsNoDimension(exception2);
    }

    @Test
    void testMethodsProperties11() {
        // methodNode11 -- 1 annotation instance
        final Method method = findByMethodName("method11");
        assertEquals(1, method.getAnnotation().size());

        final AnnotationInstance annotation = method.getAnnotation().getFirst();
        assertEquals("java.lang.Deprecated", annotation.getQualified());
        assertTrue(annotation.getArgument().isEmpty());
    }

    @Test
    void testMethodsProperties12() {
        // methodNode12 -- 2 annotation instances
        final Method method = findByMethodName("method12");
        assertEquals(2, method.getAnnotation().size());

        final var annotation1 = method.getAnnotation().get(0);
        assertEquals("java.lang.Deprecated", annotation1.getQualified());

        final var annotation2 = method.getAnnotation().get(1);
        assertEquals(Annotation12.class.getName(), annotation2.getQualified());
        assertEquals(1, annotation2.getArgument().size());
        final AnnotationArgument annotationArgument = annotation2.getArgument().getFirst();
        assertEquals("value", annotationArgument.getName());
        assertEquals("java.lang.Warning", annotationArgument.getValue().getFirst());
    }

    private static void assertMethodScope(final Method method, final String scope) {
        assertEquals(scope, method.getScope());
    }

    private static void paramHasNoGenericsNoDimension(final TypeInfo paramTypeInfo) {
        assertNull(paramTypeInfo.getDimension());
        assertTrue(paramTypeInfo.getGeneric().isEmpty());
    }

}
