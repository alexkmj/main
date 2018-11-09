package seedu.address.logic.parser;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static seedu.address.logic.parser.ParserUtil.argsAreNameValuePair;
import static seedu.address.logic.parser.ParserUtil.argsWithBounds;
import static seedu.address.logic.parser.ParserUtil.targetCodeNotNull;
import static seedu.address.logic.parser.ParserUtil.targetYearNullIffTargetSemesterNull;
import static seedu.address.logic.parser.ParserUtil.validateName;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;

import seedu.address.logic.commands.DeleteModuleCommand;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author alexkmj
/**
 * {@code DeleteModuleCommandParser} parses input arguments for
 * {@code DeleteModuleCommand}.
 */
public class DeleteModuleCommandParser implements Parser<DeleteModuleCommand> {
    /**
     * Message that informs that the target code is required and prints the
     * usage for delete command.
     */
    public static final String MESSAGE_TARGET_CODE_REQUIRED =
            ParserUtil.MESSAGE_TARGET_CODE_REQUIRED
                    + "\n"
                    + DeleteModuleCommand.MESSAGE_USAGE;

    /**
     * Message that informs that target year has to be specified if and only if
     * semester is specified, and prints the usage for delete command.
     */
    public static final String MESSAGE_YEAR_AND_SEMESTER_XOR_NULL =
            ParserUtil.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL
                    + "\n"
                    + DeleteModuleCommand.MESSAGE_USAGE;

    /**
     * Message that informs that the command is in a wrong format and
     * prints the usage for delete command.
     */
    public static final String MESSAGE_INVALID_FORMAT =
            ParserUtil.MESSAGE_INVALID_FORMAT
                    + "\n"
                    + DeleteModuleCommand.MESSAGE_USAGE;

    /**
     * Immutable map that maps string argument to edit argument enum.
     */
    private static final Map<String, DeleteArgument> NAME_TO_ARGUMENT_MAP;

    /**
     * Immutable set containing the allowable size of arguments.
     */
    private static final Set<Integer> ALLOWED_ARG_SIZE;

    static {
        Map<String, DeleteArgument> map = new HashMap<>();
        for (DeleteArgument instance : DeleteArgument.values()) {
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
        ALLOWED_ARG_SIZE = IntStream.range(2, 7)
                .filter(index -> index % 2 == 0)
                .boxed()
                .collect(collectingAndThen(toSet(), ImmutableSet::copyOf));
    }

    /**
     * Parses {@code args} in the context of {@code DeleteModuleCommand} and
     * returns {@code DeleteModuleCommand} for execution.
     * <p>
     * Throws {@code ParseException} when:
     * <ul>
     *     <li>Number of argument is not between 2 and 6.</li>
     *     <li>Number of argument is not even.</li>
     *     <li>Argument is not in name-value pair format</li>
     *     <li>Argument contains illegal name</li>
     *     <li>Same name appeared more than once</li>
     *     <li>Target code is not provided.</li>
     *     <li>Target year is provided but target semester is not provided.</li>
     *     <li>Target semester is provided but target year is not provided.</li>
     * </ul>
     *
     * @param argsInString String that contains all the argument
     * @return {@code EditModuleCommand} object for execution
     * @throws ParseException thrown when user input does not conform to the
     * expected format
     */
    public DeleteModuleCommand parse(String argsInString)
            throws ParseException {
        // Converts argument string to tokenize argument array.
        String[] args = ParserUtil.tokenize(argsInString);

        // Size of argument should be between 2 to 6.
        // Size of argument should be even.
        // Arguments should be in name-value pair.
        // Name should be legal.
        // No duplicate name.
        argsWithBounds(args, ALLOWED_ARG_SIZE);
        argsAreNameValuePair(args, MESSAGE_INVALID_FORMAT);
        validateName(args, NAME_TO_ARGUMENT_MAP, MESSAGE_INVALID_FORMAT);

        // Map the object of the parsed value to {@code DeleteArgument}
        // instance.
        // Target code should not be null.
        // Target year is null if and only if target semester is null.
        EnumMap<DeleteArgument, Object> argMap = parseValues(args);
        targetCodeNotNull(argMap.get(DeleteArgument.TARGET_CODE),
                MESSAGE_TARGET_CODE_REQUIRED);
        targetYearNullIffTargetSemesterNull(
                argMap.get(DeleteArgument.TARGET_YEAR),
                argMap.get(DeleteArgument.TARGET_SEMESTER),
                MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);

        // Return delete module command for execution.
        return new DeleteModuleCommand(argMap);
    }

    /**
     * Parse the value into its relevant object and put it in {@code argMap}.
     *
     * @param args array of name-value pair arguments
     * @throws ParseException thrown when the value cannot be parsed
     */
    private EnumMap<DeleteArgument, Object> parseValues(String[] args)
            throws ParseException {
        // Initialise argument map.
        EnumMap<DeleteArgument, Object> argMap =
                new EnumMap<>(DeleteArgument.class);

        for (int index = 0; index < args.length; index = index + 2) {
            DeleteArgument name = NAME_TO_ARGUMENT_MAP.get(args[index]);
            Object value = name.getValue(args[index + 1]);
            argMap.put(name, value);
        }

        return argMap;
    }
}
