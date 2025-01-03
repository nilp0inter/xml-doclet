package com.github.markusbernhardt.xmldoclet;

import jdk.javadoc.doclet.Doclet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manoel Campos
 */
public class CustomOption implements Doclet.Option {
    private final int argumentCount;
    private final String description;
    private final List<String> names;
    private final String parameters;

    /**
     * Creates a Custom {@link Doclet.Option} based on a {@link org.apache.commons.cli.Option}
     * instance.
     *
     * @param cliOption Apache Commons CLI Option instance
     * @return the created {@link CustomOption}
     */
    public static CustomOption of(org.apache.commons.cli.Option cliOption) {
        return new CustomOption(
                cliOption.getArgs(),
                cliOption.getDescription(),
                List.of(cliOption.getOpt()),
                cliOption.getArgName());
    }

    public CustomOption(final int argumentCount, final String description,
            final List<String> names, final String parameters) {
        this.argumentCount = argumentCount;
        this.description = description;
        this.names = new ArrayList<>(names);
        this.parameters = parameters;
    }

    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        // return List.of("-customOption");
        return Collections.unmodifiableList(names);
    }

    @Override
    public String getParameters() {
        return parameters;
    }

    /**
     * Gets the list of parameters as an array of strings.
     * 
     * @see #getParameters()
     */
    public String[] getParameterArray() {
        return parameters.split(" ");
    }

    @Override
    public boolean process(final String option, final List<String> arguments) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
