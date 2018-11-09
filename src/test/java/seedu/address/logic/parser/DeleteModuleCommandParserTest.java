package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;

import java.util.EnumMap;

import org.junit.Test;

import seedu.address.logic.commands.DeleteModuleCommand;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.model.module.Code;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;
import seedu.address.testutil.Assert;

/**
 * Unit testing for {@code DeleteModuleCommandParser}.
 * <p>
 * Tests for the following:
 * <ul>
 *     <li>Argument should not be null</li>
 *     <li>Number of arguments should be between 2 and 6</li>
 *     <li>Target code cannot be null</li>
 *     <li>
 *         Target year is non-null if and only if target semester is non-null
 *     </li>
 * </ul>
 */
public class DeleteModuleCommandParserTest {
    /**
     * {@code DeleteModuleCommandParser} instance used for parsing arguments in
     * {@code DeleteModuleCommandParserTest}.
     */
    private DeleteModuleCommandParser parser = new DeleteModuleCommandParser();

    /**
     * Argument cannot be null.
     */
    @Test
    public void parseNullArgumentFails() {
        Assert.assertThrows(NullPointerException.class, () -> {
            parser.parse(null);
        });
    }

    /**
     * Number of arguments for edit should be more than or equal to 2 and less
     * than or equal to 6.
     */
    @Test
    public void parseInvalidNumOfArgumentFails() {
        String exceptionMsg = "Invalid number of arguments! Number of arguments"
                + " should be 2, 4, 6";

        assertParseFailure(parser, "", exceptionMsg);
        assertParseFailure(parser,
                DeleteArgument.TARGET_CODE.getShortName(),
                exceptionMsg);
        assertParseFailure(parser,
                DISCRETE_MATH.getCode().value,
                exceptionMsg);
        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(),
                        DISCRETE_MATH.getYear(),
                        DISCRETE_MATH.getSemester()) + " -exceed",
                exceptionMsg);
    }

    /**
     * Arguments should be in name-value pair format.
     */
    @Test
    public void parseNotNameValuePairFails() {
        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(), null, null)
                        + " " + DISCRETE_MATH.getYear()
                        + " " + DeleteArgument.TARGET_SEMESTER.getShortName(),
                DeleteModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments cannot contain illegal name.
     */
    @Test
    public void parseIllegalNameFails() {
        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(),
                        null, null)
                        + " -random random",
                DeleteModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments should not contain duplicate name.
     */
    @Test
    public void parseSameNameAppearMoreThanOnceFails() {
        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(),
                        null, null)
                        + getCommandString(DISCRETE_MATH.getCode(),
                        null, null),
                DeleteModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Target code cannot be null.
     */
    @Test
    public void parseTargetCodeNullFails() {
        assertParseFailure(parser,
                getCommandString(null,
                        DISCRETE_MATH.getYear(),
                        DISCRETE_MATH.getSemester()),
                DeleteModuleCommandParser.MESSAGE_TARGET_CODE_REQUIRED);
    }

    /**
     * Target year is null if and only if target semester is also null.
     */
    @Test
    public void parseTargetYearTargetSemesterExclusiveNullFails() {
        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(),
                        DISCRETE_MATH.getYear(),
                        null),
                DeleteModuleCommandParser.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);

        assertParseFailure(parser,
                getCommandString(DISCRETE_MATH.getCode(),
                        null,
                        DISCRETE_MATH.getSemester()),
                DeleteModuleCommandParser.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);
    }

    /**
     * Parse in this unit test should pass.
     */
    @Test
    public void parseSuccess() {
        String actualCommand = getCommandString(
                DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester());

        DeleteModuleCommand expectedResult = getDeleteModuleCommand(
                DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester());

        // leading and trailing whitespace
        assertParseSuccess(parser, actualCommand, expectedResult);

        actualCommand = getCommandString(DISCRETE_MATH.getCode(),
                null, null);

        expectedResult = getDeleteModuleCommand(
                DISCRETE_MATH.getCode(), null, null);

        // leading and trailing whitespace
        assertParseSuccess(parser, actualCommand, expectedResult);
    }

    /**
     * Builds the command with the given arguments.
     *
     * @param targetCode {@code Code} of the target module
     * @param targetYear {@code Year} of the target module
     * @param targetSemester {@code Semester} of the target module
     * @return {@code String} which contains the command
     */
    private static String getCommandString(Code targetCode, Year targetYear,
            Semester targetSemester) {
        String command = " ";

        if (targetCode != null) {
            command += DeleteArgument.TARGET_CODE.getShortName()
                    + " " + targetCode.value + " ";
        }

        if (targetYear != null) {
            command += DeleteArgument.TARGET_YEAR.getShortName()
                    + " " + targetYear.value + " ";
        }

        if (targetSemester != null) {
            command += DeleteArgument.TARGET_SEMESTER.getShortName()
                    + " " + targetSemester.value + " ";
        }

        return command;
    }

    /**
     * Builds the {@code DeleteModuleCommand} using the provided arguments.
     *
     * @param targetCode {@code Code} of the target module
     * @param targetYear {@code Year} of the target module
     * @param targetSemester {@code Semester} of the target module
     * @return {@code DeleteModuleCommand} for deleting the module with the
     * provided arguments
     */
    private static DeleteModuleCommand getDeleteModuleCommand(Code targetCode,
            Year targetYear, Semester targetSemester) {
        EnumMap<DeleteArgument, Object> argMap;
        argMap = new EnumMap<>(DeleteArgument.class);
        argMap.put(DeleteArgument.TARGET_CODE, targetCode);
        argMap.put(DeleteArgument.TARGET_YEAR, targetYear);
        argMap.put(DeleteArgument.TARGET_SEMESTER, targetSemester);

        return new DeleteModuleCommand(argMap);
    }
}
