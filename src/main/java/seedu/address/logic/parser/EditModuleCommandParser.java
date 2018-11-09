package seedu.address.logic.parser;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static seedu.address.logic.parser.ParserUtil.argsAreNameValuePair;
import static seedu.address.logic.parser.ParserUtil.argsWithBounds;
import static seedu.address.logic.parser.ParserUtil.parseException;
import static seedu.address.logic.parser.ParserUtil.targetCodeNotNull;
import static seedu.address.logic.parser.ParserUtil.targetYearNullIffTargetSemesterNull;
import static seedu.address.logic.parser.ParserUtil.tokenize;
import static seedu.address.logic.parser.ParserUtil.validateName;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;

import seedu.address.logic.commands.EditModuleCommand;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * {@code EditModuleCommandParser} parses input arguments for
 * {@code EditModuleCommand}.
 */
public class EditModuleCommandParser implements Parser<EditModuleCommand> {
    /**
     * Message that informs that the command is in a wrong format and
     * prints the usage for edit command.
     */
    public static final String MESSAGE_INVALID_FORMAT =
            ParserUtil.MESSAGE_INVALID_FORMAT
                    + "\n"
                    + EditModuleCommand.MESSAGE_USAGE;

    /**
     * Message that informs that the command does not lead to any changes.
     */
    public static final String MESSAGE_NO_NEW_VALUE = "No new value provided.\n"
            + EditModuleCommand.MESSAGE_USAGE;

    /**
     * Message that informs that the target code is required and prints the
     * usage for edit command.
     */
    public static final String MESSAGE_TARGET_CODE_REQUIRED =
            ParserUtil.MESSAGE_TARGET_CODE_REQUIRED
                    + "\n"
                    + EditModuleCommand.MESSAGE_USAGE;

    /**
     * Message that informs that target year has to be specified if and only if
     * semester is specified, and prints the usage for edit command.
     */
    public static final String MESSAGE_YEAR_AND_SEMESTER_XOR_NULL =
            ParserUtil.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL
                    + "\n"
                    + EditModuleCommand.MESSAGE_USAGE;

    /**
     * Immutable map that maps string argument to edit argument enum.
     */
    private static final Map<String, EditArgument> NAME_TO_ARGUMENT_MAP;

    /**
     * Immutable set containing the allowable size of arguments.
     */
    private static final Set<Integer> ALLOWED_ARG_SIZE;

    /**
     * Populate {@code NAME_TO_ARGUMENT_MAP} with short name and long name as
     * key and the respective {@code EditArgument} instance as value.
     */
    static {
        Map<String, EditArgument> map = new HashMap<>();
        for (EditArgument instance : EditArgument.values()) {
            map.put(instance.getShortName(), instance);
            map.put(instance.getLongName(), instance);
        }
        NAME_TO_ARGUMENT_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Populate {@code ALLOWED_ARG_SIZE} with a set of numbers representing the
     * allowed argument sizes.
     */
    static {
        ALLOWED_ARG_SIZE = IntStream.range(4, 17)
                .filter(index -> index % 2 == 0)
                .boxed()
                .collect(collectingAndThen(toSet(), ImmutableSet::copyOf));
    }

    /**
     * Parses {@code argsInString} in the context of {@code EditModuleCommand}
     * and returns {@code EditModuleCommand} for execution.
     * <p>
     * Throws {@code ParseException} when:
     * <ul>
     *     <li>Number of argument is not between 4 and 16.</li>
     *     <li>Number of argument is not even.</li>
     *     <li>Argument is not in name-value pair format</li>
     *     <li>Argument contains illegal name</li>
     *     <li>Same name appeared more than once</li>
     *     <li>Target code is not provided.</li>
     *     <li>Target year is provided but target semester is not provided.</li>
     *     <li>Target semester is provided but target year is not provided.</li>
     *     <li>No new value provided.</li>
     *     <li>Unable to parse any field.</li>
     * </ul>
     *
     * @param argsInString String that contains all the argument
     * @return {@code EditModuleCommand} object for execution
     * @throws ParseException thrown when user input does not conform to the
     * expected format
     */
    public EditModuleCommand parse(String argsInString) throws ParseException {
        // Converts argument string to tokenize argument array.
        String[] args = tokenize(argsInString);

        // Size of argument should be between 4 to 16.
        // Size of argument should be even.
        // Arguments should be in name-value pair.
        // Name should be legal.
        // No duplicate name.
        argsWithBounds(args, ALLOWED_ARG_SIZE);
        argsAreNameValuePair(args, MESSAGE_INVALID_FORMAT);
        validateName(args, NAME_TO_ARGUMENT_MAP, MESSAGE_INVALID_FORMAT);

        // Parse values.
        parseValues(args);

        // Map the object of the parsed value to {@code EditArgument} instance.
        // Target code should not be null.
        // Target year is null if and only if target semester is null.
        // At least one new value should be specified.
        EnumMap<EditArgument, Object> argMap = parseValues(args);
        targetCodeNotNull(
                argMap.get(EditArgument.TARGET_CODE),
                MESSAGE_TARGET_CODE_REQUIRED);
        targetYearNullIffTargetSemesterNull(
                argMap.get(EditArgument.TARGET_YEAR),
                argMap.get(EditArgument.TARGET_SEMESTER),
                MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);
        atLeastOneNewValueSpecified(argMap);

        // Return edit module command for execution.
        return new EditModuleCommand(argMap);
    }

    /**
     * Parses the value into its relevant object.
     *
     * @param args array of name-value pair arguments
     * @throws ParseException thrown when the value cannot be parsed
     */
    private EnumMap<EditArgument, Object> parseValues(String[] args)
            throws ParseException {
        // Initialise argument map
        EnumMap<EditArgument, Object> argMap =
                new EnumMap<>(EditArgument.class);

        for (int index = 0; index < args.length; index = index + 2) {
            EditArgument name = NAME_TO_ARGUMENT_MAP.get(args[index]);
            Object value = name.getValue(args[index + 1]);
            argMap.put(name, value);
        }

        return argMap;
    }

    /**
     * Checks that one of code, year, semester, credit, or grade should have a
     * new value.
     *
     * @throws ParseException Thrown when code, year, semester, credit, and
     * grade are all null
     */
    private void atLeastOneNewValueSpecified(EnumMap<EditArgument,
            Object> argMap) throws ParseException {
        boolean allNewValueIsNull = argMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().name().startsWith("NEW"))
                .map(entry -> entry.getValue())
                .allMatch(Objects::isNull);

        if (allNewValueIsNull) {
            throw parseException(MESSAGE_NO_NEW_VALUE);
        }
    }
}
