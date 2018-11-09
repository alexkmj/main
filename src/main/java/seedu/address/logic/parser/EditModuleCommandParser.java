package seedu.address.logic.parser;

import static seedu.address.logic.parser.ParserUtil.parseException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.address.logic.commands.EditModuleCommand;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * {@code EditModuleCommandParser} parses input arguments for
 * {@code EditModuleCommand}.
 */
public class EditModuleCommandParser implements Parser<EditModuleCommand> {
    // Constants for ParseException.
    public static final String MESSAGE_TARGET_CODE_REQUIRED = "Target code"
            + " required.\n" + EditModuleCommand.MESSAGE_USAGE;
    public static final String MESSAGE_YEAR_AND_SEMESTER_XOR_NULL = "Year and"
            + " semester should be concurrently specified or not specified.\n"
            + EditModuleCommand.MESSAGE_USAGE;;
    public static final String MESSAGE_NO_CHANGES = "Command does not cause"
            + " any changes. Not enough parameters specified.\n"
            + EditModuleCommand.MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid format\n"
            + EditModuleCommand.MESSAGE_USAGE;

    // Immutable map that maps string argument to edit argument enum.
    private static final Map<String, EditArgument> NAME_TO_ARGUMENT_MAP;
    static {
        Map<String, EditArgument> map = new HashMap<>();
        for (EditArgument instance : EditArgument.values()) {
            map.put(instance.getShortName(), instance);
            map.put(instance.getLongName(), instance);
        }
        NAME_TO_ARGUMENT_MAP = Collections.unmodifiableMap(map);
    }

    // Map object to argument.
    private EnumMap<EditArgument, Object> argMap;

    /**
     * Parses {@code args} in the context of {@code EditModuleCommand} and
     * returns {@code EditModuleCommand} for execution.
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
        // Setup argument map.
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, null);
        argMap.put(EditArgument.TARGET_YEAR, null);
        argMap.put(EditArgument.TARGET_SEMESTER, null);
        argMap.put(EditArgument.NEW_CODE, null);
        argMap.put(EditArgument.NEW_YEAR, null);
        argMap.put(EditArgument.NEW_SEMESTER, null);
        argMap.put(EditArgument.NEW_CREDIT, null);
        argMap.put(EditArgument.NEW_GRADE, null);

        // Converts argument string to tokenize argument array.
        String[] args = ParserUtil.tokenize(argsInString);

        ParserUtil.argsWithBounds(args, 4, 16);
        ParserUtil.argsAreNameValuePair(args, MESSAGE_INVALID_FORMAT);
        validateName(args);

        // Parse values.
        parseValues(args);

        targetCodeNotNull();
        targetYearNullIffTargetSemesterNull();
        atLeastOneNewValueSpecified();

        // Return edit module command for execution.
        return new EditModuleCommand(argMap);
    }

    /**
     * Checks if argument array does not contain the same name twice and all
     * names are legal.
     *
     * @param args array of name-value pair arguments
     * @throws ParseException
     */
    public static void validateName(String[] args) throws ParseException {
        List<EditArgument> nameArray = IntStream.range(0, args.length)
                .filter(index -> index % 2 == 0)
                .mapToObj(index -> NAME_TO_ARGUMENT_MAP.get(args[index]))
                .collect(Collectors.toList());

        boolean illegalNameExist = nameArray.stream()
                .anyMatch(Objects::isNull);

        if (illegalNameExist) {
            throw parseException(MESSAGE_INVALID_FORMAT);
        }

        Set<EditArgument> nameSet = new HashSet<>(nameArray);

        if (nameArray.size() != nameSet.size()) {
            throw parseException(MESSAGE_INVALID_FORMAT);
        }
    }

    /**
     * Parses the value into its relevant object.
     *
     * @param args array of name-value pair arguments
     * @throws ParseException thrown when the value cannot be parsed
     */
    private void parseValues(String[] args) throws ParseException {
        for (int index = 0; index < args.length; index = index + 2) {
            EditArgument name = NAME_TO_ARGUMENT_MAP.get(args[index]);
            Object value = name.getValue(args[index + 1]);
            argMap.put(name, value);
        }
    }

    /**
     * Checks that target code is not be null.
     *
     * @throws ParseException thrown when target code is null
     */
    private void targetCodeNotNull() throws ParseException {
        // Throw parse exception if target code is null.
        Object targetCode = argMap.get(EditArgument.TARGET_CODE);
        if (targetCode == null) {
            throw parseException(MESSAGE_TARGET_CODE_REQUIRED);
        }
    }

    /**
     * Checks that target year is null if and only if target semester is also
     * null.
     * <p>
     * Target year is null if and only if target semester is also null.
     *
     * @throws ParseException thrown when target year and target semester is
     * exclusively null
     */
    private void targetYearNullIffTargetSemesterNull() throws ParseException {
        Object targetYear = argMap.get(EditArgument.TARGET_YEAR);
        Object targetSemester = argMap.get(EditArgument.TARGET_SEMESTER);
        if (targetYear == null ^ targetSemester == null) {
            throw parseException(MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);
        }
    }

    /**
     * Checks that one of code, year, semester, credit, or grade should have a
     * new value.
     *
     * @throws ParseException Thrown when code, year, semester, credit, and
     * grade are all null
     */
    private void atLeastOneNewValueSpecified() throws ParseException {
        Object code = argMap.get(EditArgument.NEW_CODE);
        Object year = argMap.get(EditArgument.NEW_YEAR);
        Object semester = argMap.get(EditArgument.NEW_SEMESTER);
        Object credit = argMap.get(EditArgument.NEW_CREDIT);
        Object grade = argMap.get(EditArgument.NEW_GRADE);

        if (code == null && year == null && semester == null && credit == null
                && grade == null) {
            throw parseException(MESSAGE_NO_CHANGES);
        }
    }
}
