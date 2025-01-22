package com.github.markusbernhardt.xmldoclet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test group for Tags
 */
public class TagTest extends AbstractTest {

    /**
     * testing a simple tags
     */
    @Test
    public void testTag1() {
        final var javaDocElements = newJavaDocElements("Tag1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 0);
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getTag().size(), 7);
        assertEquals(classNode.getMethod().getFirst().getTag().size(), 3);
    }
}
