package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.MethodsB;
import com.github.markusbernhardt.xmldoclet.xjc.MethodParameter;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test group for Methods in {@link MethodsB}.
 */
public class MethodsBTest extends AbstractMethodsTest {
    public MethodsBTest() {
        super("MethodsB.java");
    }

    @Test
    public void testMethodsArguments1() {
        // methodNode - methodNode with no arguments
        assertMethodSignature("method1", 0, "()");
    }

    @Test
    public void testMethodsArguments2() {
        // methodNode2 - methodNode with one Object-derived argument
        final var methodNode2 = assertMethodSignature("method2", 1, "(" + Integer.class.getName() + ")");

        // one should be able to reliably access getParameter() in this fashion
        // since XML order is important, and order of getParameter() to
        // methodNodes is likewise important. ORDER MATTERS AND SHOULD BE TRUSTY!
        assertParamTypes(methodNode2, "java.lang.Integer");
    }

    @Test
    public void testMethodsArguments3() {
        // methodNode3 - check primitive argument
        final var method = assertMethodSignature("method3", 1, "(int)");

        final var parameter = assertParamTypes(method, "int");
        paramHasNoGenericsNoDimensionNoWildcard(parameter.getType());
    }

    @Test
    public void testMethodsArguments4() {
        // methodNode4 - check that two args are OK
        final var method = findByMethodName("method4");
        assertEquals(method.getParameter().size(), 2);
        assertEquals(method.getSignature(), "(" + Integer.class.getName() + ", " + Integer.class.getName() + ")");

        final var parameter1 = method.getParameter().get(0);
        checkParamType(parameter1.getType(), "java.lang.Integer");

        final var parameter2 = method.getParameter().get(1);
        checkParamType(parameter2.getType(), "java.lang.Integer");
    }

    @Test
    public void testMethodsArguments5() {
        // methodNode5 - check that a generic argument is valid
        final var method = findByMethodName("method5");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.util.ArrayList<java.lang.String>)");

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "arg1", "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(parameter.getType(), 1);

        final TypeInfo genericParamType = parameter.getType().getGeneric().getFirst();
        checkParamType(genericParamType, "java.lang.String");
        paramHasNoGenericsNoDimensionNoWildcard(genericParamType);
    }

    @Test
    public void testMethodsArguments6() {
        // methodNode6 - check that a wildcard argument is valid
        final var method = findByMethodName("method6");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.util.ArrayList<?>)");

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "arg1", "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(parameter.getType(), 1);

        final TypeInfo genericParamType = parameter.getType().getGeneric().getFirst();
        checkParamType(genericParamType, "?");
        paramHasNoGenericsNoDimensionSomeWildcard(genericParamType);

        final var method6Wildcard = genericParamType.getWildcard();
        assertTrue(method6Wildcard.getExtendsBound().isEmpty());
        assertTrue(method6Wildcard.getSuperBound().isEmpty());
    }

    @Test
    public void testMethodsArguments7() {
        // methodNode7 - check that a wildcard argument is valid with extends clause
        final var method = findByMethodName("method7");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.util.ArrayList<? extends java.lang.String>)");

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "arg1", "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(parameter.getType(), 1);

        final var genericParamType = parameter.getType().getGeneric().getFirst();
        checkParamType(genericParamType, "?");
        paramHasNoGenericsNoDimensionSomeWildcard(genericParamType);

        final var genericParamWildcard = genericParamType.getWildcard();
        assertEquals(genericParamWildcard.getExtendsBound().size(), 1);
        assertTrue(genericParamWildcard.getSuperBound().isEmpty());

        final TypeInfo extendsBound = genericParamWildcard.getExtendsBound().getFirst();
        checkParamType(extendsBound, "java.lang.String");
        paramHasNoGenericsNoDimensionNoWildcard(extendsBound);
    }

    @Test
    public void testMethodsArguments8() {
        // methodNode8 - check that a wildcard argument is valid with super clause
        final var method = findByMethodName("method8");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.util.ArrayList<? super java.lang.String>)");

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "arg1", "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(parameter.getType(), 1);

        final var genericParamType = parameter.getType().getGeneric().getFirst();
        checkParamType(genericParamType, "?");
        paramHasNoGenericsNoDimensionSomeWildcard(genericParamType);

        final var genericParamWildcard = genericParamType.getWildcard();
        assertEquals(genericParamWildcard.getSuperBound().size(), 1);
        assertTrue(genericParamWildcard.getExtendsBound().isEmpty());

        final TypeInfo superBounds = genericParamWildcard.getSuperBound().getFirst();
        checkParamType(superBounds, "java.lang.String");
        paramHasNoGenericsNoDimensionNoWildcard(superBounds);
    }

    @Test
    public void testMethodsArguments9() {
        // methodNode9 - check that a two-level deep nested generic
        final var method = findByMethodName("method9");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.util.ArrayList<java.util.ArrayList<java.lang.String>>)");

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "arg1", "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(parameter.getType(), 1);

        final var genericParamTypeLevel1 = parameter.getType().getGeneric().getFirst();
        checkParamType(genericParamTypeLevel1, "java.util.ArrayList");
        paramHasNoDimensionNoWildcard(genericParamTypeLevel1, 1);

        final var genericParamTypeLevel2 = genericParamTypeLevel1.getGeneric().getFirst();
        checkParamType(genericParamTypeLevel2, "java.lang.String");
        paramHasNoGenericsNoDimensionNoWildcard(genericParamTypeLevel2);

        // methodNode9--check var args negative test
        assertFalse(method.isVarArgs());
    }

    @Test
    public void testMethodsArguments10() {
        // methodNode10 - check var args
        final var method = findByMethodName("method10");
        assertEquals(method.getParameter().size(), 1);
        assertEquals(method.getSignature(), "(java.lang.Object...)");
        assertTrue(method.isVarArgs());

        final var parameter = method.getParameter().getFirst();
        checkParamNameAndType(parameter, "object", "java.lang.Object");
        assertEquals(parameter.getType().getDimension(), "[]");
    }

    private static void checkParamNameAndType(final MethodParameter methodParam, final String paramName, final String fullQualifiedParamType) {
        assertEquals(methodParam.getName(), paramName);
        checkParamType(methodParam.getType(), fullQualifiedParamType);
    }

    private static void paramHasNoGenericsNoDimensionSomeWildcard(final TypeInfo paramTypeInfo) {
        assertNull(paramTypeInfo.getDimension());
        assertNotNull(paramTypeInfo.getWildcard());
        assertTrue(paramTypeInfo.getGeneric().isEmpty());
    }
}
