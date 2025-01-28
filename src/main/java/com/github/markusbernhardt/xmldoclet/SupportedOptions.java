package com.github.markusbernhardt.xmldoclet;

import jdk.javadoc.doclet.Doclet;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Defines the command line supported options for the XMLDoclet.
 * These options are used by the Apache Commons CLI library to parse the
 * command line arguments and define the options that the user can pass to the XMLDoclet.
 * This way, a List of {@link org.apache.commons.cli.Option} is converted to a List of {@link jdk.javadoc.doclet.Doclet.Option}.
 *
 * @author Manoel Campos
 */
final class SupportedOptions {
    private final Options cliOptions = new Options();

    public SupportedOptions() {
        newArgOption("d", "directory", "Destination directory for output file.\nDefault: .");
        newArgOption("docencoding", "encoding", "Encoding of the output file.\nDefault: UTF8");
        newNoArgOption("dryrun", "Parse javadoc, but don't write output file.\nDefault: false");
        newNoArgOption("rst", "Transform the XML into a Restructured Text file (*.rst).\nDefault: false");
        newNoArgOption("md", "Transform the XML into a Markdown file (*.md).\nDefault: false");
        newNoArgOption("docbook", "Transform the XML into a DocBook file (*.db.xml).\nDefault: false");
        newNoArgOption("adoc", "Transform the XML into an Ascii Doctor file (*.adoc).\nDefault: false");
        newOneArgOption("filename", "Name of the output file.\nDefault: javadoc.xml");
        newOneArgOption("basePackage", "Name of the base package.\n");
        newOneArgOption("doctitle", "Document Title\n");
        newOneArgOption("windowtitle", "Window Title\n");
        newNoArgOption("noTimestamp", "No Timestamp.\n");
        newNoArgOption("withFloatingToc", "Renders a Floating TOC on the right side.\n");
    }

    /**
     * {@return the Apache Commons CLI options supported by the XMLDoclet}
     */
    Options get() {
        return cliOptions;
    }

    /**
     * Converts the Apache Commons CLI {@link Option}s to a Set of {@link Doclet.Option} actually used by the Doclet.
     *
     * @return the Set of {@link Doclet.Option} defining the supported options for the XMLDoclet,
     *         based on the Apache Commons CLI options.
     */
    Set<CustomOption> toDocletOptions() {
        return cliOptions.getOptions().stream().map(CustomOption::of).collect(toSet());
    }

    private void newOneArgOption(final String optionName, final String description) {
        final var option = Option.builder(optionName)
                .argName(optionName)
                .required(false)
                .numberOfArgs(1)
                .desc(description)
                .build();

        cliOptions.addOption(option);
    }

    private void newArgOption(final String optionName, final String argName, final String description) {
        final var option = Option.builder(optionName)
                .argName(argName)
                .required(false)
                .hasArg()
                .desc(description)
                .build();

        cliOptions.addOption(option);
    }

    private void newNoArgOption(final String optionName, final String description) {
        final var option = Option.builder(optionName)
                .argName(optionName)
                .required(false)
                .hasArg(false)
                .desc(description)
                .build();

        cliOptions.addOption(option);
    }
}
