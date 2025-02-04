package com.manticore.tools.xmldoclet;

import com.manticore.tools.xmldoclet.xjc.Method;
import com.manticore.tools.xmldoclet.xjc.MethodParameter;
import com.manticore.tools.xmldoclet.xjc.TypeInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for implementing tests for methods.
 */
abstract class AbstractMethodsTest extends AbstractTest {
    protected final List<Method> testMethods;

    public AbstractMethodsTest(final String sourceFileName) {
        final var javadocElements = newJavaDocElements(sourceFileName);
        this.testMethods = javadocElements.classNode().getMethod();
    }

    /**
     * Short way of finding methodNodes. It's meant to only be used for methodNodes that do not
     * share the same name in the same class. In fact, this class will junit assert that there is
     * only 1 methodNode matching this name in the supplied <code>list</code>
     * methodParameterNodeeter.
     *
     * @param methodNodeName the shortname of the methodNode
     * @return The matching methodNode
     */
    protected Method findByMethodName(final String methodNodeName) {
        for (final Method methodNode : testMethods) {
            if (methodNode.getName().equals(methodNodeName)) {
                return methodNode;
            }
        }

        fail();
        return null;
    }

    protected static void checkParamType(final TypeInfo methodParamType, final String fullQualifiedParamType) {
        assertEquals(fullQualifiedParamType, methodParamType.getQualified());
    }

    protected Method assertMethodSignature(
            final String methodName, final int paramCount, final String paramTypeList) {
        final Method methodNode = findByMethodName(methodName);
        assertEquals(methodNode.getParameter().size(), paramCount);
        assertEquals(methodNode.getSignature(), paramTypeList);
        return methodNode;
    }

    /**
     * Asserts that the first parameter of the given method has the provided full qualified name
     * @param method method to check
     * @param fullQualifiedParamType full qualified name of the first parameter
     * @return the method's first parameter
     */
    protected MethodParameter assertParamTypes(final Method method, final String fullQualifiedParamType) {
        assertParamTypes(method, new String[] {fullQualifiedParamType});
        return method.getParameter().getFirst();
    }

    /**
     * Asserts that the parameters of the given method have the provided full qualified names
     * @param method method to check
     * @param fullQualifiedParamTypes full qualified names of the method's parameters
     */
    protected void assertParamTypes(final Method method, final String... fullQualifiedParamTypes) {
        for (int i = 0; i < fullQualifiedParamTypes.length; i++) {
            final String paramType = fullQualifiedParamTypes[i];
            final var methodParameter = method.getParameter().get(i);
            assertEquals(methodParameter.getType().getQualified(), paramType);
        }
    }

    protected static void paramHasNoGenericsNoDimensionNoWildcard(final TypeInfo paramTypeInfo) {
        paramHasNoDimensionNoWildcard(paramTypeInfo, 0);
    }

    /**
     * Asserts that a method parameter has no dimension, no wildcard and a given number of
     * generic types (types between &lt; and &gt;).
     * @param paramTypeInfo type information for a method parameter
     * @param paramGenericTypes number of generic types expected for the parameter type
     */
    protected static void paramHasNoDimensionNoWildcard(final TypeInfo paramTypeInfo, final int paramGenericTypes) {
        assertNull(paramTypeInfo.getDimension());
        assertNull(paramTypeInfo.getWildcard());
        assertEquals(paramTypeInfo.getGeneric().size(), paramGenericTypes);
    }
}
