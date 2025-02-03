package com.manticoreprojects.tools.xmldoclet;

import com.manticoreprojects.tools.xmldoclet.xjc.Root;

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

/**
 * Execute the javadoc command line tool to process source code files.
 *
 * @author Manoel Campos
 */
public class JavaDocCLI {
    private final static Logger LOGGER = Logger.getLogger(JavaDocCLI.class.getName());

    private final String extendedClassPath;
    private final String[] sourcePaths;
    private final String[] packages;
    private final String[] sourceFiles;
    private final String[] subPackages;
    private final String[] additionalArguments;

    /**
     * List of arguments to be defined in {@link #execute()} and passed to the
     * javadoc CLI.
     */
    private final List<String> argumentList = new ArrayList<>();

    /**
     * Instance of the {@link XmlDoclet} being executed.
     */
    private XmlDoclet xmlDoclet;

    /**
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
     */
    private JavaDocCLI(
            final String extendedClassPath, final String[] sourcePaths, final String[] packages,
            final String[] sourceFiles, final String[] subPackages, final String[] additionalArguments) {
        this.extendedClassPath = extendedClassPath;
        this.sourcePaths = sourcePaths;
        this.packages = packages;
        this.sourceFiles = sourceFiles;
        this.subPackages = subPackages;
        this.additionalArguments = additionalArguments;
    }

    public JavaDocCLI(final String[] sourceFiles, final String[] additionalArguments) {
        this(null, null, null, sourceFiles, null, additionalArguments);
    }

    public JavaDocCLI(
            final String extendedClassPath, final String[] sourcePaths,
            final String[] subPackages, final String[] additionalArguments) {
        this(extendedClassPath, sourcePaths, null, null, subPackages, additionalArguments);
    }

    /**
     * Processes the source code using javadoc.
     *
     * @return XStream compatible data structure
     */
    public Root execute() {
        try {
            addsArgumentsToList();

            LOGGER.info("Executing doclet with arguments: " + AbstractTest.join(" ", argumentList));
            final DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();
            if (javadoc == null) {
                throw new IllegalStateException("No javadoc command available on the system");
            }

            final var task = createTask(javadoc);
            runJavaDocTask(task);

            LOGGER.info("done with doclet processing");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "doclet exception", e);
        } catch (Error e) {
            LOGGER.log(Level.SEVERE, "doclet error", e);
        }

        return XmlDoclet.getRoot();
    }

    /** Create a task to run the doclet. */
    private DocumentationTool.DocumentationTask createTask(
            final DocumentationTool javadoc) {
        return javadoc.getTask(
                new PrintWriter(System.out, true, Charset.defaultCharset()), // output writer
                null, // file manager (use default)
                null, // diagnostic listener (use default)
                XmlDoclet.class,
                argumentList, // arguments
                null // compilation units (use default)
        );
    }

    /**
     * Actually starts the javadoc CLI.
     *
     * @param task javadoc task to run
     */
    private static void runJavaDocTask(final DocumentationTool.DocumentationTask task) {
        if (task.call())
            System.out.println("Doclet ran successfully");
        else
            System.err.println("Doclet execution failed");
    }

    /** Aggregate arguments (including package names). */
    private void addsArgumentsToList() {
        // by setting this to 'private', nothing is omitted in the parsing
        argumentList.add("-private");

        final String classPath = getClassPath(extendedClassPath);
        argumentList.add("-classpath");
        argumentList.add(classPath);

        if (sourcePaths != null) {
            final String concatenatedSourcePaths = AbstractTest.join(File.pathSeparator, sourcePaths);
            if (!concatenatedSourcePaths.isEmpty()) {
                argumentList.add("-sourcepath");
                argumentList.add(concatenatedSourcePaths);
            }
        }

        if (subPackages != null) {
            final String concatenatedSubPackages = AbstractTest.join(";", subPackages);
            if (!concatenatedSubPackages.isEmpty()) {
                argumentList.add("-subpackages");
                argumentList.add(concatenatedSubPackages);
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
    }

    private String getClassPath(final String extendedClassPath) {
        final String classPath = System.getProperty("java.class.path", ".");
        return extendedClassPath == null ? classPath : classPath + File.pathSeparator + extendedClassPath;

    }
}
