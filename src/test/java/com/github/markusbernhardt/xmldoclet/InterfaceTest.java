package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.Interface1;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface2;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface3;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface4;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Interface;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.TypeParameter;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test group for Interfaces
 */
@SuppressWarnings("deprecation")
class InterfaceTest extends AbstractTest {
    /**
     * testing a interface with nothing defined
     */
    @Test
    void testInterface1() {
        final var javaDocElements = newJavaDocElements("Interface1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface1", interfaceNode.getComment());
        assertEquals(interfaceNode.getName(), Interface1.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface1.class.getName());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());
    }

    /**
     * testing a interface with 1 method
     */
    @Test
    void testInterface2() {
        final var javaDocElements = newJavaDocElements("Interface2.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final Method method = interfaceNode.getMethod().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface2", interfaceNode.getComment());
        assertEquals(interfaceNode.getName(), Interface2.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface2.class.getName());
        assertEquals("public", interfaceNode.getScope());
        assertEquals(1, interfaceNode.getMethod().size());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        // verify method
        assertEquals("method1", method.getComment());
        assertEquals("method1", method.getName());
        assertEquals("int ()", method.getSignature());
        assertFalse(method.isFinal());
        assertFalse(method.isNative());
        assertFalse(method.isStatic());
        assertFalse(method.isSynchronized());
        assertFalse(method.isVarArgs());
        assertEquals("method1", method.getQualified());
        assertEquals("public", method.getScope());
        assertTrue(method.getAnnotation().isEmpty());
        assertTrue(method.getParameter().isEmpty());
        assertTrue(method.getException().isEmpty());
    }

    /**
     * testing a interface that extends another interface
     */
    @Test
    void testInterface3() {
        final var javaDocElements = newJavaDocElements("Interface3.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface3", interfaceNode.getComment());
        assertEquals(interfaceNode.getName(), Interface3.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface3.class.getName());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertEquals(1, interfaceNode.getInterface().size());
        assertTrue(interfaceNode.isIncluded());

        // verify interface
        assertEquals(interfaceNode.getInterface().getFirst().getQualified(), Serializable.class.getName());
    }

    /**
     * testing a interface that implements one annotation
     */
    @Test
    void testInterface4() {
        final var javaDocElements = newJavaDocElements("Interface4.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final AnnotationInstance annotationInstanceNode = interfaceNode.getAnnotation().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface4", interfaceNode.getComment());
        assertEquals(Interface4.class.getSimpleName(), interfaceNode.getName());
        assertEquals(Interface4.class.getName(), interfaceNode.getQualified());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertEquals(1, interfaceNode.getAnnotation().size());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        // verify deprecated annotation
        // test annotation 'deprecated' on class
        assertEquals("java.lang.Deprecated", annotationInstanceNode.getQualified());
        assertEquals("Deprecated", annotationInstanceNode.getName());
        assertTrue(annotationInstanceNode.getArgument().isEmpty());
    }

    /**
     * testing a interface that is abstract
     */
    @Test
    void testInterface5() {
        final var javaDocElements = newJavaDocElements("Interface5.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final Method method = interfaceNode.getMethod().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface5", interfaceNode.getComment());
        assertEquals("Interface5", interfaceNode.getName());
        assertEquals(getElementPathFromSimpleDataPackage("Interface5"), interfaceNode.getQualified());
        assertEquals("", interfaceNode.getScope());
        assertEquals(1, interfaceNode.getMethod().size());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        // verify method
        assertEquals("method1", method.getComment());
        assertEquals("method1", method.getName());
        assertEquals("void ()", method.getSignature());
        assertFalse(method.isFinal());
        assertFalse(method.isNative());
        assertFalse(method.isStatic());
        assertFalse(method.isSynchronized());
        assertFalse(method.isVarArgs());
        assertEquals("method1", method.getQualified());

        // all interface methods are public
        assertEquals("public", method.getScope());
        assertTrue(method.getAnnotation().isEmpty());
        assertTrue(method.getParameter().isEmpty());
        assertTrue(method.getException().isEmpty());
    }

    /**
     * testing a interface that has a type variable
     */
    @Test
    void testInterface6() {
        final var javaDocElements = newJavaDocElements("Interface6.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface6", interfaceNode.getComment());
        assertEquals("Interface6", interfaceNode.getName());
        assertEquals(getElementPathFromSimpleDataPackage("Interface6<Fun>"), interfaceNode.getQualified());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        assertEquals("Fun", typeParameterNode.getName());
        assertTrue(typeParameterNode.getBound().isEmpty());
    }

    /**
     * testing a interface that has a type variable with extends
     */
    @Test
    void testInterface7() {
        final var javaDocElements = newJavaDocElements("Interface7.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface7", interfaceNode.getComment());
        assertEquals("Interface7", interfaceNode.getName());
        assertEquals(getElementPathFromSimpleDataPackage("Interface7<Fun>"), interfaceNode.getQualified());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        assertEquals(1, typeParameterNode.getBound().size());
        assertEquals("java.lang.Number", typeParameterNode.getBound().getFirst());
    }

    /**
     * testing a interface that has a type variable with extends of a class and interface
     */
    @Test
    void testInterface8() {
        final var javaDocElements = newJavaDocElements("Interface8.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertEquals(1, packageNode.getInterface().size());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals("Interface8", interfaceNode.getComment());
        assertEquals("Interface8", interfaceNode.getName());
        assertEquals(getElementPathFromSimpleDataPackage("Interface8<Fun>"), interfaceNode.getQualified());
        assertEquals("public", interfaceNode.getScope());
        assertTrue(interfaceNode.getMethod().isEmpty());
        assertTrue(interfaceNode.getAnnotation().isEmpty());
        assertTrue(interfaceNode.getInterface().isEmpty());
        assertTrue(interfaceNode.isIncluded());

        assertEquals(2, typeParameterNode.getBound().size());
        assertEquals("java.lang.Number", typeParameterNode.getBound().get(0));
        assertEquals("java.lang.Runnable", typeParameterNode.getBound().get(1));
    }
}
