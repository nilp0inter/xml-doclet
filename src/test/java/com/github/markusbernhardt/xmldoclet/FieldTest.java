package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation12;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test group for Fields
 */
public class FieldTest extends AbstractTest {
    private final Root rootNode;
    private final Package packageNode;
    private final List<Field> fields;

    public FieldTest() {
        final var javaDocElements = newJavaDocElements("Field1.java");
        this.rootNode = javaDocElements.rootNode();
        this.packageNode = javaDocElements.packageNode();
        final Class classNode = packageNode.getClazz().getFirst();
        this.fields = classNode.getField();
    }

    @Test
    public void mainTest() {
        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertEquals(packageNode.getAnnotation().size(), 0);
        assertEquals(packageNode.getEnum().size(), 0);
        assertEquals(packageNode.getInterface().size(), 0);
        assertEquals(packageNode.getClazz().size(), 1);
    }

    /**
     * testing a returns of fields
     */
    @Test
    public void testField0() {
        // field0 -- test name
        final var field = findByFieldName("field0", fields);
        assertEquals(field.getName(), "field0");
    }

    @Test
    public void testField1() {
        // field1 -- test public field
        final var field = findByFieldName("field1", fields);
        assertEquals(field.getScope(), "public");
    }

    @Test
    public void testField2() {
        // field2 -- test private field
        final var field = findByFieldName("field2", fields);
        assertEquals(field.getScope(), "private");
    }

    @Test
    public void testField3() {
        // field3 -- default scope field (non defined)
        final var field = findByFieldName("field3", fields);
        assertEquals(field.getScope(), "");
    }

    @Test
    public void testField4() {
        // field4 -- protected scope field
        final var field = findByFieldName("field4", fields);
        assertEquals(field.getScope(), "protected");

        // negative test of final
        assertFalse(field.isFinal());


        // negative test of static
        assertFalse(field.isStatic());

        // negative test of volatile
        assertFalse(field.isVolatile());

        // negative test of transient
        assertFalse(field.isTransient());
    }

    @Test
    public void testField5() {
        // field5 -- volatile field
        final var field = findByFieldName("field5", fields);
        assertTrue(field.isVolatile());
    }

    @Test
    public void testField6() {
        // field6 -- static field
        final var field = findByFieldName("field6", fields);
        assertTrue(field.isStatic());
    }

    @Test
    public void testField7() {
        // field7 -- transient field
        final var field = findByFieldName("field7", fields);
        assertTrue(field.isTransient());
    }

    @Test
    public void testField8() {
        // field8 -- final field
        final var field = findByFieldName("field8", fields);
        assertTrue(field.isFinal());
    }

    @Test
    public void testField9() {
        // field9 -- string final expression
        final var field = findByFieldName("field9", fields);
        assertEquals(field.getConstant(), "\"testy\"");
    }

    @Test
    public void testField10() {
        // field10 -- int final expression
        final var field = findByFieldName("field10", fields);
        assertEquals(field.getConstant(), "10");
    }

    @Test
    public void testField11() {
        // field11 -- annotation
        final var field = findByFieldName("field11", fields);
        assertEquals(field.getAnnotation().size(), 1);

        AnnotationInstance annotation = field.getAnnotation().getFirst();
        assertEquals(annotation.getQualified(), "java.lang.Deprecated");
        assertEquals(annotation.getName(), "Deprecated");
        assertEquals(annotation.getArgument().size(), 0);
    }

    @Test
    public void testField12() {
        // field12 -- two annotations
        final var field = findByFieldName("field12", fields);
        assertEquals(field.getAnnotation().size(), 2);

        final var annotation1 = field.getAnnotation().get(0);
        assertEquals(annotation1.getQualified(), "java.lang.Deprecated");
        assertEquals(annotation1.getName(), "Deprecated");
        assertEquals(annotation1.getArgument().size(), 0);

        final var annotation2 = field.getAnnotation().get(1);
        assertEquals(annotation2.getQualified(), Annotation12.class.getName());
        assertEquals(annotation2.getName(), Annotation12.class.getSimpleName());
        assertEquals(annotation2.getArgument().size(), 1);

        final AnnotationArgument argument = annotation2.getArgument().getFirst();
        assertEquals(argument.getName(), "value");
        assertEquals(argument.getValue().getFirst(), "mister");
    }

    @Test
    public void testField13() {
        // field13 - type testing
        final var field = findByFieldName("field13", fields);
        assertNotNull(field.getType());
        assertEquals(field.getType().getQualified(), "java.lang.String");
        assertNull(field.getType().getDimension());
        assertNull(field.getType().getWildcard());
        assertEquals(field.getType().getGeneric().size(), 0);
    }

    @Test
    public void testField14() {
        // field14 - wild card
        final var field = findByFieldName("field14", fields);
        assertNotNull(field.getType());
        assertEquals(field.getType().getQualified(), "java.util.ArrayList");
        assertNotNull(field.getType().getGeneric());
        assertEquals(field.getType().getGeneric().size(), 1);
        assertEquals(field.getType().getGeneric().getFirst().getQualified(), "?");
        assertNotNull(field.getType().getGeneric().getFirst().getWildcard());
    }

    @Test
    public void testField15() {
        // field15 - typed generic
        final var field = findByFieldName("field15", fields);
        assertNotNull(field.getType());
        assertEquals(field.getType().getQualified(), "java.util.HashMap");
        assertEquals(field.getType().getGeneric().size(), 2);
        assertEquals(field.getType().getGeneric().get(0).getQualified(), "java.lang.String");
        assertNull(field.getType().getGeneric().get(0).getWildcard());
        assertEquals(field.getType().getGeneric().get(1).getQualified(), "java.lang.Integer");
        assertNull(field.getType().getGeneric().get(1).getWildcard());
    }

    @Test
    public void testField16() {
        // field16 - array
        final var field = findByFieldName("field16", fields);
        assertNotNull(field.getType());
        assertEquals(field.getType().getQualified(), "java.lang.String");
        assertEquals(field.getType().getDimension(), "[]");
    }

    /**
     * Short way of finding fields.
     *
     * @param fieldName the shortname of the method
     * @param fields the list of methods to look through.
     * @return The matching field
     */
    private Field findByFieldName(final String fieldName, final List<Field> fields) {
        for (final var field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }

        fail();
        return null;
    }
}
