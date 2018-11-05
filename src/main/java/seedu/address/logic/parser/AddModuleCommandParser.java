package seedu.address.logic.parser;

import static seedu.address.logic.parser.ParserUtil.argsAreNameValuePair;
import static seedu.address.logic.parser.ParserUtil.argsWithBounds;
import static seedu.address.logic.parser.ParserUtil.parseException;
import static seedu.address.logic.parser.ParserUtil.targetCodeNotNull;
import static seedu.address.logic.parser.ParserUtil.targetYearNullIffTargetSemesterNull;
import static seedu.address.logic.parser.ParserUtil.validateName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import seedu.address.logic.commands.AddModuleCommand;
import seedu.address.logic.commands.DeleteModuleCommand;
import seedu.address.logic.parser.arguments.AddArgument;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credit;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Module;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;
import seedu.address.model.util.ModuleBuilder;

//@@author alexkmj
/**
 * Parses input arguments and creates a new AddModuleCommand object
 */
public class AddModuleCommandParser implements Parser<AddModuleCommand> {
    /**
     * Message that informs that the command is in a wrong format and
     * prints the usage for delete command.
     */
    public static final String MESSAGE_INVALID_FORMAT =
            ParserUtil.MESSAGE_INVALID_FORMAT
                    + "\n"
                    + AddModuleCommand.MESSAGE_USAGE;

    /**
     * Immutable map that maps string argument to add argument enum.
     */
    private static final Map<String, AddArgument> NAME_TO_ARGUMENT_MAP;

    /**
     * Map the object of the parsed value to {@code EditArgument} instance.
     */
    private EnumMap<AddArgument, Object> argMap;

    /**
     * Populate {@code NAME_TO_ARGUMENT_MAP} with short name and long name as
     * key and the respective {@code AddArgument} instance as value.
     */
    static {
        Map<String, AddArgument> map = new HashMap<>();
        for (AddArgument instance : AddArgument.values()) {
            map.put(instance.getShortName(), instance);
            map.put(instance.getLongName(), instance);
        }
        NAME_TO_ARGUMENT_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Parses {@code args} in the context of {@code AddModuleCommand}
     * returns {@code AddModuleCommand} for execution.
     * <p>
     * Throws {@code ParseException} when:
     * <ul>
     *     <li>Number of argument is either 8 or 10.</li>
     *     <li>Argument is not in name-value pair format</li>
     *     <li>Argument contains illegal name</li>
     *     <li>Same name appeared more than once</li>
     * </ul>
     *
     * @param argsInString String that contains all the argument
     * @return {@code AddModuleCommand} object for execution
     * @throws ParseException thrown when user input does not conform to the
     * expected format
     */
    public AddModuleCommand parse(String argsInString) throws ParseException {
        // Converts argument string to tokenize argument array.
        String[] args = ParserUtil.tokenize(argsInString);

        // Initialize set of allowed argument size.
        Set<Integer> allowedSize = new HashSet<>();
        allowedSize.add(8);
        allowedSize.add(10);

        // Size of argument should be 8 or 10.
        // Arguments should be in name-value pair.
        // Name should be legal.
        // No duplicate name.
        argsWithBounds(args, allowedSize);
        argsAreNameValuePair(args, MESSAGE_INVALID_FORMAT);
        validateName(args, NAME_TO_ARGUMENT_MAP, MESSAGE_INVALID_FORMAT);

        // Parse values.
        parseValues(args);

        // Return add module command for execution.
        Module toAdd = getModuleBuiltWithArgMap();
        return new AddModuleCommand(toAdd);
    }

    /**
     * Parse the value into its relevant object and put it in {@code argMap}.
     *
     * @param args array of name-value pair arguments
     * @throws ParseException thrown when the value cannot be parsed
     */
    private void parseValues(String[] args) throws ParseException {
        // Setup argument map.
        argMap = new EnumMap<>(AddArgument.class);

        for (int index = 0; index < args.length; index = index + 2) {
            String nameInString = args[index];
            String valueInString = args[index + 1];

            AddArgument name = NAME_TO_ARGUMENT_MAP.get(nameInString);
            Object value = name.getValue(valueInString);
            argMap.put(name, value);
        }
    }

    /**
     * Returns module built using values in {@code argMap}.
     *
     * @return module using values in {@code argMap}
     */
    private Module getModuleBuiltWithArgMap() {
        Code code = (Code) argMap.get(AddArgument.CODE);
        Year year = (Year) argMap.get(AddArgument.YEAR);
        Semester semester = (Semester) argMap.get(AddArgument.SEMESTER);
        Credit credit = (Credit) argMap.get(AddArgument.CREDIT);
        Grade grade = (Grade) argMap.get(AddArgument.GRADE);

        return new ModuleBuilder().withCode(code)
                .withYear(year)
                .withSemester(semester)
                .withCredit(credit)
                .withGrade(grade)
                .build();
    }
}
