package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation12;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.EnumConstant;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test group for Enumerations
 */
public class EnumTest extends AbstractTest {

    /**
     * testing a simple enum
     */
    @Test
    public void testEnum1() {
        final var javaDocElements = newJavaDocElements("Enum1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 1);
        assertEquals(packageNode.getInterface().size(), 0);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(enumNode.getName(), "Enum1");
        assertEquals(enumNode.getComment(), "Enum1");
        assertEquals(enumNode.getQualified(), getElementPathFromSimpleDataPackage("Enum1"));
        assertEquals(enumNode.getConstant().size(), 3);
        assertEquals(enumNode.getConstant().get(0).getName(), "a");
        assertEquals(enumNode.getConstant().get(1).getName(), "b");
        assertEquals(enumNode.getConstant().get(2).getName(), "c");
    }

    /**
     * testing an empty enum
     */
    @Test
    public void testEnum2() {
        final var javaDocElements = newJavaDocElements("Enum2.java");
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();

        assertEquals(enumNode.getName(), "Enum2");
        assertEquals(enumNode.getComment(), "Enum2");
        assertEquals(enumNode.getQualified(), getElementPathFromSimpleDataPackage("Enum2"));
        assertEquals(enumNode.getConstant().size(), 0);
    }

    /**
     * testing enum comment
     */
    @Test
    public void testEnum3() {
        final var javaDocElements = newJavaDocElements("Enum3.java");
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();
        assertEquals(enumNode.getComment(), "Enum3");
    }

    /**
     * testing enum field comment
     */
    @Test
    public void testEnum4() {
        final var javaDocElements = newJavaDocElements("Enum4.java");
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();

        final EnumConstant enumConstantNode = enumNode.getConstant().getFirst();
        assertEquals(enumConstantNode.getComment(), "field1");
    }

    /**
     * testing single annotation
     */
    @Test
    public void testEnum5() {
        final var javaDocElements = newJavaDocElements("Enum5.java");
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();
        assertEquals(enumNode.getAnnotation().size(), 1);
        AnnotationInstance annotationInstanceNode = enumNode.getAnnotation().getFirst();
        assertEquals(annotationInstanceNode.getQualified(), "java.lang.Deprecated");
    }

    /**
     * testing multiple annotation
     */
    @Test
    public void testEnum6() {
        final var javaDocElements = newJavaDocElements("Enum6.java");
        final var packageNode = javaDocElements.packageNode();
        final Enum enumNode = packageNode.getEnum().getFirst();
        assertEquals(enumNode.getAnnotation().size(), 2);

        final AnnotationInstance annotationInstance1 = enumNode.getAnnotation().get(0);
        assertEquals(annotationInstance1.getQualified(), "java.lang.Deprecated");
        assertEquals(annotationInstance1.getName(), "Deprecated");
        assertEquals(annotationInstance1.getArgument().size(), 0);

        final var annotationInstance2 = enumNode.getAnnotation().get(1);
        assertEquals(annotationInstance2.getQualified(), Annotation12.class.getName());
        assertEquals(annotationInstance2.getName(), Annotation12.class.getSimpleName());
        assertEquals(annotationInstance2.getArgument().size(), 1);

        final AnnotationArgument annotationArgumentNode = annotationInstance2.getArgument().getFirst();
        assertEquals(annotationArgumentNode.getName(), "value");
        assertEquals(annotationArgumentNode.getValue().getFirst(), "mister");

    }
}
