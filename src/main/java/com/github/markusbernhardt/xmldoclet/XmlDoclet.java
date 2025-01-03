package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import net.sf.saxon.s9api.*;
import org.apache.commons.cli.*;

import javax.lang.model.SourceVersion;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Doclet class.
 *
 * @author markus
 * @see <a href=
 *      "https://docs.oracle.com/en/java/javase/21/docs/api/jdk.javadoc/jdk/javadoc/doclet/package-summary.html">Doclet
 *      API</a>
 * @see <a href="https://openjdk.org/groups/compiler/using-new-doclet.html">Using the new JDK 9
 *      Doclet API (refined in JDK 13)</a>
 */
public class XmlDoclet implements Doclet {
    /*
     * TODO: <a href="https://chatgpt.com/c/675c3ead-dbc8-800a-bac5-46df2b61bef3">JDK 13 Doclet API migration</a>
     * TODO: <a href="https://stackoverflow.com/questions/77082583/migrating-to-java-17-how-to-get-gradle-to-generate-java-classes-from-xsd">JAXB XJC Gradle JDK 17</a>
     * TODO: https://stackoverflow.com/questions/70423036/cxf-codegen-plugin-in-gradle
     * TODO: https://www.baeldung.com/gradle-build-to-maven-pom
     * TODO: Promissing (there is a PR for JDK 21): https://github.com/qaware/xsd2java-gradle-plugin
     */
    private static final Logger LOGGER = Logger.getLogger(XmlDoclet.class.getName());

    public static final String RESTRUCTURED_XSL = "/com/manticore/xsl/restructured.xsl";
    public static final String MARKDOWN_XSL = "/com/manticore/xsl/markdown.xsl";

    /**
     * The parsed object model. Used in unit tests.
     */
    private Root root;

    /**
     * The Options instance to parse command line strings, that defines the supported XMLDoclet
     * {@link #options}.
     */
    public final Options cliOptions;

    /**
     * Set of supported Doclet options, generated from the Apache Commons CLI Options.
     *
     * @see #cliOptions
     */
    private final Set<CustomOption> options;

    private Reporter reporter;
    private PrintWriter stdout;

    public XmlDoclet() {
        final var supportedOptions = new SupportedOptions();
        this.cliOptions = supportedOptions.get();
        this.options = supportedOptions.toDocletOptions();
    }

    @Override
    public void init(final Locale locale, final Reporter reporter) {
        this.reporter = reporter;
        this.stdout = reporter.getStandardWriter();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends CustomOption> getSupportedOptions() {
        return Collections.unmodifiableSet(options);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_21;
    }

    /**
     * Processes the JavaDoc documentation. This method is required for all doclets.
     *
     * @see Doclet#run(DocletEnvironment)
     *
     * @param env the operating environment of a single invocation of the doclet
     *
     * @return <code>true</code> if processing was successful.
     */
    @Override
    public boolean run(final DocletEnvironment env) {
        final var commandLine = parseCommandLine(getOptionsMatrix());
        root = new Parser(env).parseRootDoc();
        save(commandLine, root);
        return true;
    }

    /**
     * {@return the two-dimensional array of options} Each line in the matrix represents a single
     * option and its parameters.
     */
    private String[][] getOptionsMatrix() {
        return getSupportedOptions().stream().map(CustomOption::getParameterArray)
                .toArray(String[][]::new);
    }

    public static void transform(
            final InputStream xsltInputStream,
            final File xmlFile, final File outFile,
            final Map<String, String> parameters)
            throws IOException, SaxonApiException {
        try (InputStream xmlInputStream = new FileInputStream(xmlFile);
                OutputStream output = new FileOutputStream(outFile);) {
            // Create a Saxon Processor
            Processor processor = new Processor(false);

            // Create a DocumentBuilder
            DocumentBuilder docBuilder = processor.newDocumentBuilder();

            // Parse the XML input
            XdmNode xmlDoc = docBuilder.build(new StreamSource(xmlInputStream));

            // Create a XsltCompiler
            XsltCompiler compiler = processor.newXsltCompiler();

            // Set the ClassLoader for the compiler to load resources from the classpath
            compiler.setResourceResolver(new ClasspathResourceURIResolver());

            // Create a XsltExecutable from the XSLT input stream
            XsltExecutable xsltExecutable = compiler.compile(new StreamSource(xsltInputStream));
            XsltTransformer transformer = xsltExecutable.load();

            // Set the source document
            transformer.setInitialContextNode(xmlDoc);

            // Set the result destination
            Serializer serializer = processor.newSerializer(output);
            transformer.setDestination(serializer);

            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                transformer.setParameter(new QName(parameter.getKey()),
                        new XdmAtomicValue(parameter.getValue()));
            }

            // Transform the XML
            transformer.transform();
        }
    }

    /**
     * Save XML object model to a file via JAXB.
     *
     * @param commandLine the parsed command line arguments
     * @param root the document root
     */
    public void save(final CommandLine commandLine, final Root root) {
        if (commandLine.hasOption("dryrun")) {
            return;
        }

        final String filename = commandLine.hasOption("filename")
                ? commandLine.getOptionValue("filename")
                : "javadoc.xml";

        final String basename = filename.toLowerCase().endsWith(".xml")
                ? filename.substring(0, filename.length() - ".xml".length())
                : filename;

        final File xmlFile = commandLine.hasOption("d")
                ? new File(commandLine.getOptionValue("d"), filename)
                : new File(filename);

        try (
                var fileOutputStream = new FileOutputStream(xmlFile);
                var bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
            final var contextObj = JAXBContext.newInstance(Root.class);

            final var marshaller = contextObj.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (commandLine.hasOption("docencoding")) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING,
                        commandLine.getOptionValue("docencoding"));
            }


            marshaller.marshal(root, bufferedOutputStream);
            bufferedOutputStream.flush();
            fileOutputStream.flush();

            LOGGER.info("Wrote XML to: " + xmlFile.getAbsolutePath());

            final Map<String, String> parameters = new HashMap<>();
            for (final var option : commandLine.getOptions()) {
                if (option.getValue() == null) {
                    parameters.put(option.getArgName(), "true");
                } else {
                    parameters.put(option.getArgName(), option.getValue());
                }
            }

            if (commandLine.hasOption("rst")) {
                File outFile = new File(xmlFile.getParent(), basename + ".rst");
                try (final var inputStream =
                        XmlDoclet.class.getResourceAsStream(RESTRUCTURED_XSL);) {
                    transform(inputStream, xmlFile, outFile, parameters);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Failed to write Restructured Text", ex);
                }
                LOGGER.info("Wrote Restructured Text to: " + outFile.getAbsolutePath());
            }

            if (commandLine.hasOption("md")) {
                final var outFile = new File(xmlFile.getParent(), basename + ".md");
                try (final var inputStream = XmlDoclet.class.getResourceAsStream(MARKDOWN_XSL);) {
                    transform(inputStream, xmlFile, outFile, parameters);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Failed to write Markdown", ex);
                }
                LOGGER.info("Wrote Markdown to: " + outFile.getAbsolutePath());
            }

            if (commandLine.hasOption("docbook")) {
                LOGGER.info("Docbook transformation is not supported yet.");
            }

            if (commandLine.hasOption("adoc")) {
                LOGGER.info("ASCII Doctor transformation is not supported yet.");
            }
        } catch (RuntimeException | IOException | JAXBException e) {
            LOGGER.log(Level.SEVERE, "Failed to write the XML File", e);
        }
    }

    /**
     * Parse the given options.
     *
     * @param optionsMatrix The two-dimensional array of options.
     * @return the parsed command line arguments.
     */
    public CommandLine parseCommandLine(final String[][] optionsMatrix) {
        try {
            final List<String> argumentList = new ArrayList<>();
            for (final String[] optionsArray : optionsMatrix) {
                argumentList.addAll(Arrays.asList(optionsArray));
            }

            final CommandLineParser commandLineParser = new DefaultParser();
            return commandLineParser.parse(cliOptions, argumentList.toArray(String[]::new), true);
        } catch (final ParseException e) {
            final var printWriter = new PrintWriter(System.out, true, Charset.defaultCharset());
            final var helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(printWriter, 74,
                    "javadoc -doclet %s [options]".formatted(XmlDoclet.class.getName()),
                    null, cliOptions, 1, 3, null, false);
            return CommandLine.builder().build();
        }
    }
}
