package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.simpledata.*;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import com.github.markusbernhardt.xmldoclet.xjc.TypeParameter;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test group for Classes
 */
@SuppressWarnings("deprecation")
class ClassTest extends AbstractTest {

    /**
     * Testing nested Annotations
     *
     * @see ClassAnnotationCascade
     */
    @Test
    void testClassAnnotationCascade() {
        final var javaDocElements = newJavaDocElements("ClassAnnotationCascade.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(1, rootNode.getPackage().size());
        assertEquals(null, packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertEquals(1, packageNode.getClazz().size());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());

        assertEquals("ClassAnnotationCascade", classNode.getComment());
        assertEquals("ClassAnnotationCascade", classNode.getName());

        assertEquals(ClassAnnotationCascade.class.getName(), classNode.getQualified());

        assertEquals(1, classNode.getAnnotation().size());
        final AnnotationInstance annotationNode = classNode.getAnnotation().getFirst();

        assertEquals("AnnotationCascade", annotationNode.getName());
        assertEquals(1, annotationNode.getArgument().size());

        final AnnotationArgument annotationArgNode = annotationNode.getArgument().getFirst();

        // Two nested annotations in child attribute
        assertEquals("children", annotationArgNode.getName());
        assertTrue(annotationArgNode.getValue().isEmpty());
        assertEquals(2, annotationArgNode.getAnnotation().size());

        final AnnotationInstance annonNodePrimitive = annotationArgNode.getAnnotation().get(0);
        final AnnotationInstance annonNodeNested = annotationArgNode.getAnnotation().get(1);

        // Equal attribs
        assertEquals(AnnotationCascadeChild.class.getSimpleName(), annonNodePrimitive.getName());
        assertEquals(AnnotationCascadeChild.class.getSimpleName(), annonNodeNested.getName());
        assertEquals(AnnotationCascadeChild.class.getName(), annonNodePrimitive.getQualified());
        assertEquals(AnnotationCascadeChild.class.getName(), annonNodeNested.getQualified());
        assertEquals(2, annonNodePrimitive.getArgument().size());
        assertEquals(2, annonNodeNested.getArgument().size());
        assertEquals("name", annonNodePrimitive.getArgument().getFirst().getName());
        assertEquals("name", annonNodeNested.getArgument().getFirst().getName());

        // Primitive
        final AnnotationArgument annArgNodePrimitive = annonNodePrimitive.getArgument().get(1);
        assertEquals("dummyData", annArgNodePrimitive.getName());
        assertEquals("java.lang.String", annArgNodePrimitive.getType().getQualified());
        assertEquals(0, annArgNodePrimitive.getAnnotation().size());
        assertEquals(3, annArgNodePrimitive.getValue().size());
        assertEquals("A", annArgNodePrimitive.getValue().get(0));
        assertEquals("B", annArgNodePrimitive.getValue().get(1));
        assertEquals("C", annArgNodePrimitive.getValue().get(2));

        // Nested
        final AnnotationArgument annArgNodeNested = annonNodeNested.getArgument().get(1);
        assertEquals("subAnnotations", annArgNodeNested.getName());
        assertEquals(Annotation3.class.getName(), annArgNodeNested.getType().getQualified());
        assertEquals(3, annArgNodeNested.getAnnotation().size());
        assertEquals(0, annArgNodeNested.getValue().size());
        assertEquals(Annotation3.class.getSimpleName(), annArgNodeNested.getAnnotation().get(0).getName());
        assertEquals(Annotation3.class.getName(), annArgNodeNested.getAnnotation().get(1).getQualified());
        assertEquals(1, annArgNodeNested.getAnnotation().get(2).getArgument().size());

        assertEquals("666", annArgNodeNested.getAnnotation().get(2).getArgument().getFirst().getValue().getFirst());
    }

    /**
     * testing a class with nothing defined EMPIRICAL OBSERVATION: The default constructor created
     * by the java compiler is not marked synthetic. um what?
     */
    @Test
    void testClass1() {
        final var javaDocElements = newJavaDocElements("Class1.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(1, packageNode.getClazz().size());

        assertEquals("Class1", classNode.getComment());
        assertEquals(Class1.class.getSimpleName(), classNode.getName());
        assertEquals(Class1.class.getName(), classNode.getQualified());
        assertEquals("public", classNode.getScope());
        assertEquals(1, classNode.getConstructor().size());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(Object.class.getName(), classNode.getClazz().getQualified());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing a class with 1 constructor
     */
    @Test
    void testClass2() {
        final var javaDocElements = newJavaDocElements("Class2.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final var constructor = classNode.getConstructor().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(1, packageNode.getClazz().size());

        assertEquals(classNode.getComment(), "Class2");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class2.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class2.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());

        assertEquals(constructor.getComment(), "Constructor1");
        assertEquals(constructor.getName(), "Class2");
        assertTrue(constructor.getParameter().isEmpty());
        assertTrue(constructor.getAnnotation().isEmpty());
    }

    /**
     * testing a class with 1 method
     */
    @Test
    void testClass3() {
        final var javaDocElements = newJavaDocElements("Class3.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final var method = classNode.getMethod().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class3");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class3.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class3.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertEquals(classNode.getMethod().size(), 1);
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());

        assertEquals(method.getComment(), "method1");
        assertEquals(method.getName(), "method1");
        assertEquals(method.getSignature(), "()");
        assertFalse(method.isFinal());
        assertFalse(method.isNative());
        assertFalse(method.isStatic());
        assertFalse(method.isSynchronized());
        assertFalse(method.isVarArgs());
        assertEquals(method.getQualified(), getElementPathFromSimpleDataPackage("Class3.method1"));
        assertEquals(method.getScope(), "public");
        assertTrue(method.getAnnotation().isEmpty());
        assertTrue(method.getParameter().isEmpty());
        assertTrue(method.getException().isEmpty());

        final TypeInfo returnNode = method.getReturn();
        assertEquals(returnNode.getQualified(), "int");
        assertNull(returnNode.getDimension());
        assertTrue(returnNode.getGeneric().isEmpty());
        assertNull(returnNode.getWildcard());
    }

    /**
     * testing a class with 1 field
     */
    @Test
    void testClass4() {
        final var javaDocElements = newJavaDocElements("Class4.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final var field = classNode.getField().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class4");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class4.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class4.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertEquals(classNode.getField().size(), 1);
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());

        // test field
        assertEquals(field.getComment(), "field1");
        assertEquals(field.getName(), "field1");
        assertEquals(field.getScope(), "public");
        assertEquals(field.getType().getQualified(), "int");
        assertNull(field.getType().getDimension());
        assertTrue(field.getType().getGeneric().isEmpty());
        assertNull(field.getType().getWildcard());
        assertFalse(field.isStatic());
        assertFalse(field.isTransient());
        assertFalse(field.isVolatile());
        assertFalse(field.isFinal());
        assertNull(field.getConstant());
        assertTrue(field.getAnnotation().isEmpty());
    }

    /**
     * testing a class that extends another class with 1 method
     */
    @Test
    void testClass5() {
        final var javaDocElements = newJavaDocElements("Class5.java", "Class3.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();


        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 2);

        assertEquals(classNode.getComment(), "Class5");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class5.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class5.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), getElementPathFromSimpleDataPackage("Class3"));
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing a class that implements one interface
     */
    @Test
    void testClass6() {
        final var javaDocElements = newJavaDocElements("Class6.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final TypeInfo interfaceNode = classNode.getInterface().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class6");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class6.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class6.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertEquals(classNode.getAnnotation().size(), 1);
        assertEquals(classNode.getInterface().size(), 1);
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());

        // the particular interface chosen for this test also will change this flag to true!
        assertTrue(classNode.isSerializable());

        // verify interface
        assertEquals(interfaceNode.getQualified(), Serializable.class.getName());
    }

    /**
     * testing one annotation instance on the class
     */
    @Test
    void testClass7() {
        final var javaDocElements = newJavaDocElements("Class7.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        final AnnotationInstance annotationNode = classNode.getAnnotation().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class7");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class7.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class7.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertEquals(classNode.getAnnotation().size(), 1);
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());

        // test annotation 'deprecated' on class
        assertEquals(annotationNode.getQualified(), "java.lang.Deprecated");
        assertEquals(annotationNode.getName(), "Deprecated");
        assertTrue(annotationNode.getArgument().isEmpty());
    }

    /**
     * testing abstract keyword on class
     */
    @Test
    void testClass8() {
        final var javaDocElements = newJavaDocElements("Class8.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();


        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class8");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class8.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class8.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertTrue(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing java.io.Externalizable interface on class
     */
    @Test
    void testClass9() {
        final var javaDocElements = newJavaDocElements("Class9.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class9");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), Class9.class.getSimpleName());
        assertEquals(classNode.getQualified(), Class9.class.getName());
        assertEquals(classNode.getScope(), "public");
        assertEquals(classNode.getMethod().size(), 2);
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertEquals(classNode.getInterface().size(), 1);
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertTrue(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertTrue(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing difference of scope modifier on class
     */
    @Test
    void testClass10() {
        final var javaDocElements = newJavaDocElements("Class10.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class10");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), "Class10");
        assertEquals(classNode.getQualified(), getElementPathFromSimpleDataPackage("Class10"));
        assertEquals(classNode.getScope(), "");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing if isException is populated correctly
     */
    @Test
    void testClass11() {
        final var javaDocElements = newJavaDocElements("Class11.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class11");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), "Class11");
        assertEquals(classNode.getQualified(), getElementPathFromSimpleDataPackage("Class11"));
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertEquals(classNode.getAnnotation().size(), 1);
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Exception.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertTrue(classNode.isSerializable());
        assertTrue(classNode.isException());
        assertFalse(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing if isError is populated correctly
     */
    @Test
    void testClass12() {
        final var javaDocElements = newJavaDocElements("Class12.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();


        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class12");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), "Class12");
        assertEquals(classNode.getQualified(), getElementPathFromSimpleDataPackage("Class12"));
        assertEquals(classNode.getScope(), "public");
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertEquals(classNode.getAnnotation().size(), 1);
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Error.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertTrue(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertTrue(classNode.isError());
        assertTrue(classNode.getGeneric().isEmpty());
    }

    /**
     * testing if type variables can be determined
     */
    @Test
    void testClass13() {
        final var javaDocElements = newJavaDocElements("Class13.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();

        final TypeParameter typeParameter = classNode.getGeneric().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class13");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), "Class13");
        assertEquals(classNode.getQualified(), getElementPathFromSimpleDataPackage("Class13"));
        assertEquals(classNode.getScope(), "public");
        assertEquals(classNode.getGeneric().size(), 1);
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());

        // check the 'fun' type var
        assertEquals("Fun", typeParameter.getName());
        assertTrue(typeParameter.getBound().isEmpty());
    }

    /**
     * testing if a single bounds can be determined
     */
    @Test
    void testClass14() {
        final var javaDocElements = newJavaDocElements("Class14.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final TypeParameter typeParameter = classNode.getGeneric().getFirst();

        assertEquals(1, rootNode.getPackage().size());
        assertNull(packageNode.getComment());
        assertEquals(SIMPLE_DATA_PACKAGE, packageNode.getName());
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(1, packageNode.getClazz().size());

        assertEquals("Class14", classNode.getComment());
        assertEquals(1, classNode.getConstructor().size());
        assertEquals("Class14", classNode.getName());
        assertEquals(getElementPathFromSimpleDataPackage("Class14"), classNode.getQualified());
        assertEquals("public", classNode.getScope());
        assertEquals(1, classNode.getGeneric().size());
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());

        // check the 'fun' type var
        assertEquals("Fun", typeParameter.getName());
        assertEquals(1, typeParameter.getBound().size());
        assertEquals(Number.class.getName(), typeParameter.getBound().getFirst());
    }

    /**
     * testing if a multiple bounds can be determined
     */
    @Test
    void testClass15() {
        final var javaDocElements = newJavaDocElements("Class15.java");
        final var rootNode = javaDocElements.rootNode();
        final var packageNode = javaDocElements.packageNode();
        final var classNode = javaDocElements.classNode();
        final TypeParameter typeParameter = classNode.getGeneric().getFirst();

        assertEquals(rootNode.getPackage().size(), 1);
        assertNull(packageNode.getComment());
        assertEquals(packageNode.getName(), SIMPLE_DATA_PACKAGE);
        assertTrue(packageNode.getAnnotation().isEmpty());
        assertTrue(packageNode.getEnum().isEmpty());
        assertTrue(packageNode.getInterface().isEmpty());
        assertEquals(packageNode.getClazz().size(), 1);

        assertEquals(classNode.getComment(), "Class15");
        assertEquals(classNode.getConstructor().size(), 1);
        assertEquals(classNode.getName(), "Class15");
        assertEquals(classNode.getQualified(), getElementPathFromSimpleDataPackage("Class15"));
        assertEquals(classNode.getScope(), "public");
        assertEquals(classNode.getGeneric().size(), 1);
        assertTrue(classNode.getMethod().isEmpty());
        assertTrue(classNode.getField().isEmpty());
        assertTrue(classNode.getAnnotation().isEmpty());
        assertTrue(classNode.getInterface().isEmpty());
        assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
        assertFalse(classNode.isAbstract());
        assertFalse(classNode.isExternalizable());
        assertTrue(classNode.isIncluded());
        assertFalse(classNode.isSerializable());
        assertFalse(classNode.isException());
        assertFalse(classNode.isError());

        // check the 'fun' type var
        assertEquals(typeParameter.getName(), "Fun");
        assertEquals(typeParameter.getBound().size(), 2);
        assertEquals(typeParameter.getBound().get(0), Number.class.getName());
        assertEquals(typeParameter.getBound().get(1), Runnable.class.getName());
    }

    /**
     * testing integer annotation argument
     */
    @Test
    void testClass16() {
        final var javaDocElements = newJavaDocElements("Class16.java", "Annotation3.java");
        final var classNode = javaDocElements.classNode();

        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();
        assertEquals(argument.getName(), "id");
        assertEquals(argument.getType().getQualified(), "int");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "3");
        assertTrue(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing integer array annotation argument
     */
    @Test
    void testClass17() {
        final var javaDocElements = newJavaDocElements("Class17.java", "Annotation5.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "int");
        assertEquals(argument.getValue().size(), 2);
        assertEquals(argument.getValue().get(0), "1");
        assertEquals(argument.getValue().get(1), "2");
        assertTrue(argument.isPrimitive());
        assertTrue(argument.isArray());
    }

    /**
     * testing integer array annotation argument
     */
    @Test
    void testClass18() {
        final var javaDocElements = newJavaDocElements("Class18.java", "Annotation6.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "java.lang.String");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "hey");
        assertFalse(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing enum annotation argument
     */
    @Test
    void testClass19() {
        final var javaDocElements = newJavaDocElements("Class19.java", "Annotation7.java", "Enum1.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), getElementPathFromSimpleDataPackage("Enum1"));
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "a");
        assertFalse(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing class annotation argument
     */
    @Test
    void testClass20() {
        final var javaDocElements = newJavaDocElements("Class20.java", "Annotation8.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();
        assertEquals(argument.getType().getQualified(), "java.lang.Class");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "java.lang.String");
        assertFalse(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing character annotation argument
     */
    @Test
    void testClass21() {
        final var javaDocElements = newJavaDocElements("Class21.java", "Annotation10.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "char");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), Integer.toString((int) 'a'));
        assertTrue(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing 0 character annotation argument
     */
    @Test
    void testClass22() {
        final var javaDocElements = newJavaDocElements("Class22.java", "Annotation10.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "char");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "0");
        assertTrue(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing boolean annotation argument
     */
    @Test
    void testClass23() {
        final var javaDocElements = newJavaDocElements("Class23.java", "Annotation11.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "boolean");
        assertEquals(argument.getValue().size(), 1);
        assertEquals(argument.getValue().getFirst(), "true");
        assertTrue(argument.isPrimitive());
        assertFalse(argument.isArray());
    }

    /**
     * testing empty int array annotation argument
     */
    @Test
    void testClass24() {
        final var javaDocElements = newJavaDocElements("Class24.java", "Annotation5.java");
        final var classNode = javaDocElements.classNode();
        final AnnotationInstance instance = classNode.getAnnotation().getFirst();
        final AnnotationArgument argument = instance.getArgument().getFirst();

        assertEquals(argument.getType().getQualified(), "int");
        assertTrue(argument.getValue().isEmpty());
        assertTrue(argument.isPrimitive());
        assertTrue(argument.isArray());
    }
}
