package com.manticore.tools.xmldoclet;

import jdk.javadoc.doclet.Doclet;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author Manoel Campos
 */
public class CustomOption implements Doclet.Option {
    private final String description;

    /**
     * The name of the option, such as {@code d} or {@code debug},
     * without a preceding hyphen (included automatically).
     */
    private final String name;

    /**
     * A user-friendly string description of the option's parameters (arguments), or the empty string if this option has no parameters.
     * This is used to generate the help message for the option.
     * If the option expectes a file name as parameter, the description may include the word "file"
     * to indicate that.
     *
     * <p>
     * If the option has multiple parameters, this attribute should provide
     * a string representation of all of them, such as: file1 file2
     * </p>
     */
    private final String parameters;

    /**
     * A function ({@link java.util.function.Predicate}) to validate the option arguments.
     * The function can check the number of required arguments, try to convert them to the expected type
     * and perform any validation operation required.
     * If the arguments are valid, the Predicate must return true.
     * 
     * @see #process(String, List)
     */
    private final BiPredicate<String, List<String>> argumentsProcessor;

    /**
     * The number of arguments this option will consume,
     * which is the number of expected {@link #parameters}.
     */
    private final int argumentCount;

    /**
     * Creates an Option with a single argument value and a given specification.
     * 
     * @param argName the name of the single argument to be passed to the option, used in the help message
     */
    public static CustomOption newOneArg(
            final String name, final String description,
            final String argName,
            final BiPredicate<String, List<String>> argumentsProcessor) {
        return new CustomOption(name, description, argName, 1, argumentsProcessor);
    }

    /**
     * Creates an Option with no arguments and a given specification
     */
    public static CustomOption newNoArgs(final String name, final String description,
            final BiPredicate<String, List<String>> argumentsProcessor) {
        return new CustomOption(name, description, "", 0, argumentsProcessor);
    }

    /**
     * Creates an Option with a given specification and a default arguments processor.
     * 
     * @param argName the name of the single argument to be passed to the option, used in the help message
     */
    private CustomOption(final String name, final String description, final String argName, final int argumentCount) {
        this(name, description, argName, argumentCount, (option, args) -> args.size() == argumentCount);
    }

    /**
     * Creates an Option with a given specification.
     */
    private CustomOption(
            final String name, final String description,
            final String parameters, final int argumentCount,
            final BiPredicate<String, List<String>> argumentsProcessor) {
        this.name = addHyphenPrefix(name);
        this.description = description;
        this.parameters = Objects.requireNonNullElse(parameters, "");
        this.argumentCount = argumentCount;
        this.argumentsProcessor = argumentsProcessor;
    }

    static String addHyphenPrefix(final String name) {
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

    /**
     * {@inheritDoc}
     * In the case of this class, the list has only one element, the single option name.
     * 
     * @return a list with a single element containing the name of the option
     * @see #getName()
     */
    @Override
    public List<String> getNames() {
        return List.of(name);
    }

    /**
     * This class provides a single name for the option.
     * Therefore, no alternative names are supported.
     *
     * @return the name of the option
     */
    public String getName() {
        return name;
    }

    /**
     * @return a String with the name of the single parameter expected by the option (if any)
     *         The number of parameter for this option class can be 0 or 1, according to {@link #argumentCount}.
     *         That is why this attribute is a single parameter name as a String.
     */
    @Override
    public String getParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     * It must check if the given option arguments are valid.
     * 
     * @param option {@inheritDoc}
     * @param arguments {@inheritDoc} received during the doclet execution
     * @return if the option has the expected number of arguments and they are valid
     */
    @Override
    public final boolean process(final String option, final List<String> arguments) {
        return argumentsProcessor.test(option, arguments);
    }
}
