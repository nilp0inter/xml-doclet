package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import org.junit.Test;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.stream;

/**
 * Base class for all tests.
 *
 * @author markus
 */
abstract class AbstractTest {
    private final static Logger LOGGER = Logger.getLogger(AbstractTest.class.getName());

    protected static final String[] TEST_DIR = { "./src/test/java/" };
    protected static final String SIMPLE_DATA_PACKAGE = "com.github.markusbernhardt.xmldoclet.simpledata";
    protected static final String SIMPLE_DATA_DIR = TEST_DIR[0] + SIMPLE_DATA_PACKAGE.replaceAll("\\.", "/");

    protected static final String[] ARGS = { "-dryrun" };
    protected static final String[] SUB_PACKAGES = { "com" };

    /**
     * Gets the first {@link Package} and {@link Class} from a JavaDoc {@link Root} element.
     * @param sourceFileName Name of a source files inside the {@link #SIMPLE_DATA_DIR} to test.
     */
    public JavaDocElements newJavaDocElements(final String ...sourceFileName) {
        final var sourceFiles = stream(sourceFileName).map(this::getFilePathFromSimpleDataDir).toArray(String[]::new);
        final var rootNode = executeJavadoc(null, null, null, sourceFiles, null, ARGS);

        final var packageNode = rootNode.getPackage().getFirst();
        final var classNode = packageNode.getClazz().isEmpty() ? null : packageNode.getClazz().getFirst();
        return new JavaDocElements(rootNode, packageNode, classNode);
    }

    /**
     * Rigourous Parser :-)
     */
    @Test
    public void testSampledoc() {
        executeJavadoc(".", TEST_DIR, null, null, SUB_PACKAGES, ARGS);
    }

    /**
     * Processes the source code using javadoc.
     *
     * @param extendedClassPath Any classpath information required to help along javadoc. Javadoc
     *        will actually compile the source code you specify; so if there are any jars or classes
     *        that are referenced by the source code to process, then including those compiled items
     *        in the classpath will give you more complete data in the resulting XML.
     * @param sourcePaths Usually sourcePaths is specified in conjunction with either/both packages &
     *        subpackages. The sourcepaths value should be the path of the source files right before
     *        the standard package-based folder layout of projects begins. For example, if you have
     *        code that exists in package foo.bar, and your code is physically in /MyFolder/foo/bar/,
     *        then the sourcePaths would be /MyFolder
     * @param packages Use if you want to detail specific packages to process (contrast with
     *        subpackages, which is probably the easiest/most brute force way of using xml-doclet).
     *        If you have within your code two packages, foo.bar and bar.foo, but only wanted
     *        foo.bar processed, then specify just 'foo.bar' for this argument.
     * @param sourceFiles You can specify source files individually. This usually is used instead of
     *        sourcePaths/subPackages/packages. If you use this parameter, specify the full path of
     *        any java file you want processed.
     * @param subPackages You can specify 'subPackages', which simply gives one an easy way to
     *        specify the root package, and have javadoc recursively look through everything under
     *        that package. So for instance, if you had foo.bar, foo.bar.bar, and bar.foo,
     *        specifying 'foo' will process foo.bar and foo.bar.bar packages, but not bar.foo
     *        (unless you specify 'bar' as a subpackage, too)
     * @param additionalArguments Additional Arguments.
     * @return XStream compatible data structure
     */
    public Root executeJavadoc(
            final String extendedClassPath, final String[] sourcePaths, final String[] packages,
            final String[] sourceFiles, final String[] subPackages, final String[] additionalArguments) {
        try {

            final var errorWriter = new PrintWriter(System.err, true, Charset.defaultCharset());
            final var infoWriter = new PrintWriter(System.out, true, Charset.defaultCharset());

            // aggregate arguments and packages
            final var argumentList = new ArrayList<String>();

            // by setting this to 'private', nothing is omitted in the parsing
            argumentList.add("-private");

            final String classPath = getClassPath(extendedClassPath);
            argumentList.add("-classpath");
            argumentList.add(classPath);

            if (sourcePaths != null) {
                final String concatedSourcePaths = join(File.pathSeparator, sourcePaths);
                if (!concatedSourcePaths.isEmpty()) {
                    argumentList.add("-sourcepath");
                    argumentList.add(concatedSourcePaths);
                }
            }

            if (subPackages != null) {
                final String concatedSubPackages = join(";", subPackages);
                if (!concatedSubPackages.isEmpty()) {
                    argumentList.add("-subpackages");
                    argumentList.add(concatedSubPackages);
                }
            }

            if (packages != null) {
                argumentList.addAll(Arrays.asList(packages));
            }

            if (sourceFiles != null) {
                argumentList.addAll(Arrays.asList(sourceFiles));
            }

            if (additionalArguments != null) {
                argumentList.addAll(Arrays.asList(additionalArguments));
            }

            LOGGER.info("Executing doclet with arguments: " + join(" ", argumentList));

            final DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();
            if (javadoc == null) {
                throw new IllegalStateException("No javadoc command available on the system");
            }

            // Create a task to run the doclet
            final var task = javadoc.getTask(
                    new PrintWriter(System.out, true, Charset.defaultCharset()), // output writer
                    null, // file manager (use default)
                    null, // diagnostic listener (use default)
                    XmlDoclet.class,
                    argumentList, // arguments
                    null // compilation units (use default)
            );

            // Run the task
            if (task.call()) {
                System.out.println("Doclet ran successfully");
            } else {
                System.err.println("Doclet execution failed");
            }

            LOGGER.info("done with doclet processing");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "doclet exception", e);
        } catch (Error e) {
            LOGGER.log(Level.SEVERE, "doclet error", e);
        }

        return XmlDoclet.getRoot();
    }

    private static String getClassPath(final String extendedClassPath) {
        final String classPath = System.getProperty("java.class.path", ".");
        return extendedClassPath == null ? classPath : classPath + File.pathSeparator + extendedClassPath;

    }

    /**
     * Helper method to concat strings.
     *
     * @param glue the separator.
     * @param strings the strings to concat.
     * @return concatenated string
     */
    public static String join(final String glue, final String[] strings) {
        return join(glue, Arrays.asList(strings));
    }

    /**
     * Helper method to concat strings.
     *
     * @param glue the separator.
     * @param strings the strings to concat.
     * @return concatenated string
     */
    public static String join(final String glue, final List<String> strings) {
        if (strings == null) {
            return null;
        }

        return String.join(glue, strings);
    }


    /**
     * {@return the path (separated by /) to a source file in the {@link #SIMPLE_DATA_DIR}}
     * @param sourceFileName the name of the file to get its full path from the {@link #SIMPLE_DATA_DIR}
     */
    protected String getFilePathFromSimpleDataDir(final String sourceFileName) {
        return join("/", List.of(SIMPLE_DATA_DIR, sourceFileName));
    }

    /**
     * {@return the full qualified name of a class, interface, record, enum or even some other element inside
     * those ones (separated by .) in the {@link #SIMPLE_DATA_PACKAGE}}
     * Those internal elements can be something such as fields, methods, constructors, etc.
     * @param elementName the name of the element (class, interface, record, method, etc.) to get its
     *                    full qualified name from the {@link #SIMPLE_DATA_PACKAGE}
     */
    protected static String getElementPathFromSimpleDataPackage(final String elementName) {
        return join(".", List.of(SIMPLE_DATA_PACKAGE, elementName));
    }

}
