package com.github.markusbernhardt.xmldoclet;

import jdk.javadoc.doclet.Doclet;
import org.apache.commons.cli.Option;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author Manoel Campos
 */
public class CustomOption implements Doclet.Option {
    private final String description;

    /**
     * The names of the option, such as {@code d} or {@code debug},
     * without a preceding hyphen (included automatically).
     */
    private final List<String> names;

    /**
     * A user-friendly string description of the option's parameters, or the empty string if this option has no parameters.
     * This is used to generate the help message for the option.
     * If the option expectes a file name as parameter, the description may include the word "file"
     * to indicate that.
     *
     * <p>If the option has multiple parameters, this attribute should provide
     * a string representation of all of them, such as: file1 file2</p>
     */
    private final String parameters;

    /**
     * A function ({@link java.util.function.Predicate}) to validate the option arguments.
     * The function can check the number of required arguments, try to convert them to the expected type
     * and perform any validation operation required.
     * If the arguments are valid, the Predicate must return true.
     * @see #process(String, List)
     */
    private final BiPredicate<String, List<String>> argumentsProcessor;

    /**
     * The number of arguments this option will consume,
     * which is the number of expected {@link #parameters}.
     */
    private final int argumentCount;

    /**
     * Creates a Custom {@link Doclet.Option} based on a {@link org.apache.commons.cli.Option} instance.
     *
     * @param cliOption Apache Commons CLI Option instance
     * @return the created {@link CustomOption}
     */
    public static CustomOption of(final Option cliOption) {
        return new CustomOption(
                cliOption.getDescription(), List.of(cliOption.getOpt()),
                cliOption.getArgName(), cliOption.getArgs()
        );
    }

    public CustomOption(
            final String description, final List<String> names, final String parameters, final int argumentCount) {
        this(description, names, parameters, argumentCount, (option, args) -> args.size() == argumentCount);
    }

    public CustomOption(
            final String description, final List<String> names,
            final String parameters, final int argumentCount,
            final BiPredicate<String, List<String>> argumentsProcessor) {
        this.description = description;
        this.names = names.stream().map(this::addHyphenPrefix).toList();
        this.parameters = Objects.requireNonNullElse(parameters, "");
        this.argumentCount = argumentCount;
        this.argumentsProcessor = argumentsProcessor;
    }

    private String addHyphenPrefix(final String name) {
        return name.startsWith("-") ? name : "-" + name;
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

    /**
     * {@inheritDoc}
     * It must check if the given option arguments are valid.
     * @param option {@inheritDoc}
     * @param arguments {@inheritDoc} received during the doclet execution
     * @return if the option has the expected number of arguments and they are valid
     */
    @Override
    public final boolean process(final String option, final List<String> arguments) {
        return argumentsProcessor.test(option, arguments);
    }
}
