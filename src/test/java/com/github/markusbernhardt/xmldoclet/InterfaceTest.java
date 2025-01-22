package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.Interface1;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface2;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface3;
import com.github.markusbernhardt.xmldoclet.simpledata.Interface4;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Interface;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.TypeParameter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test group for Interfaces
 */
@SuppressWarnings("deprecation")
public class InterfaceTest extends AbstractTest {
    /**
     * testing a interface with nothing defined
     */
    @Test
    public void testInterface1() {
        final var javaDocElements = newJavaDocElements("Interface1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface1");
        assertEquals(interfaceNode.getName(), Interface1.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface1.class.getName());
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());
    }

    /**
     * testing a interface with 1 method
     */
    @Test
    public void testInterface2() {
        final var javaDocElements = newJavaDocElements("Interface2.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final Method method = interfaceNode.getMethod().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface2");
        assertEquals(interfaceNode.getName(), Interface2.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface2.class.getName());
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 1);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        // verify method
        assertEquals(method.getComment(), "method1");
        assertEquals(method.getName(), "method1");
        assertEquals(method.getSignature(), "()");
        assertFalse(method.isFinal());
        assertFalse(method.isNative());
        assertFalse(method.isStatic());
        assertFalse(method.isSynchronized());
        assertFalse(method.isVarArgs());
        assertEquals(method.getQualified(), getElementPathFromSimpleDataPackage("Interface2.method1"));
        assertEquals(method.getScope(), "public");
        assertEquals(method.getAnnotation().size(), 0);
        assertEquals(method.getParameter().size(), 0);
        assertEquals(method.getException().size(), 0);

    }

    /**
     * testing a interface that extends another interface
     */
    @Test
    public void testInterface3() {
        final var javaDocElements = newJavaDocElements("Interface3.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface3");
        assertEquals(interfaceNode.getName(), Interface3.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface3.class.getName());
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 1);
        assertTrue(interfaceNode.isIncluded());

        // verify interface
        assertEquals(interfaceNode.getInterface().getFirst().getQualified(), java.io.Serializable.class.getName());
    }

    /**
     * testing a interface that implements one annotation
     */
    @Test
    public void testInterface4() {
        final var javaDocElements = newJavaDocElements("Interface4.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final AnnotationInstance annotationInstanceNode = interfaceNode.getAnnotation().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface4");
        assertEquals(interfaceNode.getName(), Interface4.class.getSimpleName());
        assertEquals(interfaceNode.getQualified(), Interface4.class.getName());
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 1);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        // verify deprecated annotation
        // test annotation 'deprecated' on class
        assertEquals(annotationInstanceNode.getQualified(), "java.lang.Deprecated");
        assertEquals(annotationInstanceNode.getName(), "Deprecated");
        assertEquals(annotationInstanceNode.getArgument().size(), 0);
    }

    /**
     * testing a interface that is abstract
     */
    @Test
    public void testInterface5() {
        final var javaDocElements = newJavaDocElements("Interface5.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        Method method = interfaceNode.getMethod().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface5");
        assertEquals(interfaceNode.getName(), "Interface5");
        assertEquals(interfaceNode.getQualified(), getElementPathFromSimpleDataPackage("Interface5"));
        assertEquals(interfaceNode.getScope(), "");
        assertEquals(interfaceNode.getMethod().size(), 1);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        // verify method
        assertEquals(method.getComment(), "method1");
        assertEquals(method.getName(), "method1");
        assertEquals(method.getSignature(), "()");
        assertFalse(method.isFinal());
        assertFalse(method.isNative());
        assertFalse(method.isStatic());
        assertFalse(method.isSynchronized());
        assertFalse(method.isVarArgs());
        assertEquals(method.getQualified(), getElementPathFromSimpleDataPackage("Interface5.method1"));

        // all interface methods are public
        assertEquals(method.getScope(), "public");
        assertEquals(method.getAnnotation().size(), 0);
        assertEquals(method.getParameter().size(), 0);
        assertEquals(method.getException().size(), 0);
    }

    /**
     * testing a interface that has a type variable
     */
    @Test
    public void testInterface6() {
        final var javaDocElements = newJavaDocElements("Interface6.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface6");
        assertEquals(interfaceNode.getName(), "Interface6");
        assertEquals(interfaceNode.getQualified(), getElementPathFromSimpleDataPackage("Interface6"));
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        assertEquals(typeParameterNode.getName(), "Fun");
        assertEquals(typeParameterNode.getBound().size(), 0);
    }

    /**
     * testing a interface that has a type variable with extends
     */
    @Test
    public void testInterface7() {
        final var javaDocElements = newJavaDocElements("Interface7.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        final TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface7");
        assertEquals(interfaceNode.getName(), "Interface7");
        assertEquals(interfaceNode.getQualified(), getElementPathFromSimpleDataPackage("Interface7"));
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        assertEquals(typeParameterNode.getBound().size(), 1);
        assertEquals(typeParameterNode.getBound().getFirst(), "java.lang.Number");
    }

    /**
     * testing a interface that has a type variable with extends of a class and interface
     */
    @Test
    public void testInterface8() {
        final var javaDocElements = newJavaDocElements("Interface8.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Interface interfaceNode = packageNode.getInterface().getFirst();
        TypeParameter typeParameterNode = interfaceNode.getGeneric().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 1);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(interfaceNode.getComment(), "Interface8");
        assertEquals(interfaceNode.getName(), "Interface8");
        assertEquals(interfaceNode.getQualified(), getElementPathFromSimpleDataPackage("Interface8"));
        assertEquals(interfaceNode.getScope(), "public");
        assertEquals(interfaceNode.getMethod().size(), 0);
        assertEquals(interfaceNode.getAnnotation().size(), 0);
        assertEquals(interfaceNode.getInterface().size(), 0);
        assertTrue(interfaceNode.isIncluded());

        assertEquals(typeParameterNode.getBound().size(), 2);
        assertEquals(typeParameterNode.getBound().get(0), "java.lang.Number");
        assertEquals(typeParameterNode.getBound().get(1), "java.lang.Runnable");
    }
}
