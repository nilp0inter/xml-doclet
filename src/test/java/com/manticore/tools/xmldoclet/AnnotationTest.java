package com.manticore.tools.xmldoclet;

import com.manticore.tools.xmldoclet.simpledata.Annotation1;
import com.manticore.tools.xmldoclet.simpledata.Annotation2;
import com.manticore.tools.xmldoclet.simpledata.Annotation3;
import com.manticore.tools.xmldoclet.xjc.Annotation;
import com.manticore.tools.xmldoclet.xjc.AnnotationElement;
import com.manticore.tools.xmldoclet.xjc.AnnotationInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test group for Annotations
 */
@SuppressWarnings("deprecation")
class AnnotationTest extends AbstractTest {

    /**
     * testing an annotation with nothing defined
     */
    @Test
    void testAnnotation1() {
        final var javaDocElements = newJavaDocElements("Annotation1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Annotation annotationNode = packageNode.getAnnotation().get(0);

        assertEquals(rootNode.getPackage().size(), 1);
        assertEquals(packageNode.getComment(), null);
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 1);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 0);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(annotationNode.getComment(), "Annotation1");
        assertEquals(annotationNode.getName(), Annotation1.class.getSimpleName());
        assertEquals(annotationNode.getQualified(), Annotation1.class.getName());
        assertEquals(annotationNode.getScope(), "public");
        assertEquals(annotationNode.getAnnotation().size(), 0);
        assertEquals(annotationNode.getElement().size(), 0);
        assertTrue(annotationNode.isIncluded());
    }

    /**
     * testing an annotation with an annotation decorating it
     */
    @Test
    void testAnnotation2() {
        final var javaDocElements = newJavaDocElements("Annotation2.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Annotation annotationNode = packageNode.getAnnotation().get(0);
        final AnnotationInstance annotationInstance = annotationNode.getAnnotation().get(0);

        assertEquals(rootNode.getPackage().size(), 1);
        assertEquals(packageNode.getComment(), null);
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 1);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 0);
        assertEquals(packageNode.getClazz().size(), 0);

        assertEquals(annotationNode.getComment(), "Annotation2");
        assertEquals(annotationNode.getName(), Annotation2.class.getSimpleName());
        assertEquals(annotationNode.getQualified(), Annotation2.class.getName());
        assertEquals(annotationNode.getScope(), "public");
        assertEquals(annotationNode.getAnnotation().size(), 1);
        assertTrue(annotationNode.getElement().isEmpty());
        assertTrue(annotationNode.isIncluded());

        // test annotation 'deprecated' on class
        assertEquals("java.lang.Deprecated", annotationInstance.getQualified());
        assertEquals("Deprecated", annotationInstance.getName());
        assertTrue(annotationInstance.getArgument().isEmpty());
    }

    /**
     * testing an annotation with one element field
     */
    @Test
    void testAnnotation3() {
        final var javaDocElements = newJavaDocElements("Annotation3.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Annotation annotationNode = packageNode.getAnnotation().get(0);
        final AnnotationElement element = annotationNode.getElement().get(0);

        assertEquals(rootNode.getPackage().size(), 1);
        assertEquals(packageNode.getComment(), null);
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 1);
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals(annotationNode.getComment(), "Annotation3");
        assertEquals(annotationNode.getName(), Annotation3.class.getSimpleName());
        assertEquals(annotationNode.getQualified(), Annotation3.class.getName());
        assertEquals(annotationNode.getScope(), "public");
        assertTrue(annotationNode.getAnnotation().isEmpty());
        assertEquals(annotationNode.getElement().size(), 1);
        assertTrue(annotationNode.isIncluded());

        // test annotation element
        assertEquals("id", element.getName());
        assertEquals("int ()", element.getQualified());
        assertEquals("int", element.getType().getQualified());
        assertEquals("3", element.getDefault());
    }

    /**
     * testing an annotation with non-public definition
     */
    @Test
    void testAnnotation4() {
        final var javaDocElements = newJavaDocElements("Annotation4.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final Annotation annotationNode = packageNode.getAnnotation().get(0);

        assertEquals(rootNode.getPackage().size(), 1);
        assertEquals(packageNode.getComment(), null);
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 1);
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertTrue(packageNode.getClazz().isEmpty());

        assertEquals(annotationNode.getComment(), "Annotation4");
        assertEquals(annotationNode.getName(), "Annotation4");
        assertEquals(annotationNode.getQualified(), getElementPathFromSimpleDataPackage("Annotation4"));
        assertEquals(annotationNode.getScope(), "");
        assertTrue(annotationNode.getAnnotation().isEmpty());
        assertTrue(annotationNode.getElement().isEmpty());
        assertTrue(annotationNode.isIncluded());
    }
}
