package com.manticore.tools.xmldoclet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit test group for Tags
 */
class TagTest extends AbstractTest {

    /**
     * testing a simple tags
     */
    @Test
    void testTag1() {
        final var javaDocElements = newJavaDocElements("Tag1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertEquals(0, packageNode.getEnum().size());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(1, packageNode.getClazz().size());

        assertEquals(7, classNode.getTag().size());
        assertEquals(3, classNode.getMethod().getFirst().getTag().size());
    }
}
