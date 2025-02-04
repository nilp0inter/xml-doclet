package com.manticore.tools.xmldoclet;

import com.manticore.tools.xmldoclet.simpledata.MethodsA;
import com.manticore.tools.xmldoclet.xjc.Method;
import org.junit.jupiter.api.Test;

/**
 * Unit test group for Methods in {@link MethodsA}.
 */
class MethodsATest extends AbstractMethodsTest {
    public MethodsATest() {
        super("MethodsA.java");
    }

    @Test
    void testMethodsReturn1() {
        // with methodNode1 we are checking that a simple methodNode can exist
        // with no arguments and no return
        final Method methodNode1 = findByMethodName("method1");
        checkParamType(methodNode1.getReturn(), "void");
        paramHasNoGenericsNoDimensionNoWildcard(methodNode1.getReturn());
    }

    @Test
    void testMethodsReturn2() {
        // methodNode2 - checking Object based returns
        final Method methodNode2 = findByMethodName("method2");
        checkParamType(methodNode2.getReturn(), "java.lang.Integer");
        paramHasNoGenericsNoDimensionNoWildcard(methodNode2.getReturn());
    }

    @Test
    void testMethodsReturn3() {
        // methodNode 3 - checking primitive based returns
        final Method methodNode3 = findByMethodName("method3");
        checkParamType(methodNode3.getReturn(), "int");
        paramHasNoGenericsNoDimensionNoWildcard(methodNode3.getReturn());
    }
}
