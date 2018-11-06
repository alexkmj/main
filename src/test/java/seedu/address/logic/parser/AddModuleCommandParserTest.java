package seedu.address.logic.parser;

import static seedu.address.logic.parser.AddModuleCommandParser.MESSAGE_INVALID_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;

import org.junit.Test;

import seedu.address.logic.parser.arguments.AddArgument;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credit;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;
import seedu.address.testutil.Assert;
import seedu.address.testutil.TypicalModules;

//@@author alexkmj
/**
 * Unit testing for {@code AddModuleCommandParser}.
 * <p>
 * Tests for the following:
 * <ul>
 *     <li>Argument should not be null</li>
 *     <li>Number of arguments should be either 8 or 10</li>
 *     <li>All values can be parsed </li>
 * </ul>
 */
public class AddModuleCommandParserTest {
    /**
     * {@code AddModuleCommandParser} instance used for parsing arguments in
     * {@code AddModuleCommandParserTest}.
     */
    private AddModuleCommandParser parser = new AddModuleCommandParser();

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
     * Number of arguments for edit should be either 8 or 10.
     */
    @Test
    public void parseInvalidNumOfArgumentFails() {
        String exceptionMsg = "Invalid number of arguments! Number of arguments"
                + " should be 8, 10";

        String commandA = getCommandString(DISCRETE_MATH.getCode(), null, null,
                null, null);

        String commandB = commandA + getCommandString(null,
                DISCRETE_MATH.getYear(), null, null, null);

        String commandC = commandB + getCommandString(null, null,
                DISCRETE_MATH.getSemester(), null, null);

        String commandD = commandC + getCommandString(null, null, null,
                DISCRETE_MATH.getCredits(), null) + "-randomName";

        String commandE = commandC + getCommandString(null, null, null,
                DISCRETE_MATH.getCredits(), null) + "randomValue";

        String commandF = commandC + getCommandString(null, null, null,
                DISCRETE_MATH.getCredits(), DISCRETE_MATH.getGrade())
                + "randomValue";

        assertParseFailure(parser, "", exceptionMsg);
        assertParseFailure(parser, commandA, exceptionMsg);
        assertParseFailure(parser, commandB, exceptionMsg);
        assertParseFailure(parser, commandC, exceptionMsg);
        assertParseFailure(parser, commandD, exceptionMsg);
        assertParseFailure(parser, commandE, exceptionMsg);
        assertParseFailure(parser, commandF, exceptionMsg);
    }

    /**
     * Arguments should be in name-value pair format.
     */
    @Test
    public void parseNotNameValuePairFails() {
        String commandA = getCommandString(DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester(), null, null)
                + AddArgument.CREDIT.getShortName()
                + " "
                + AddArgument.CREDIT.getShortName();

        String commandB = getCommandString(DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester(), null, null)
                + TypicalModules.CREDIT_FOUR
                + " "
                + TypicalModules.CREDIT_FOUR;

        String commandC = getCommandString(DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester(), null, null)
                + TypicalModules.CREDIT_FOUR
                + " "
                + AddArgument.CREDIT.getShortName();

        assertParseFailure(parser, commandA, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, commandB, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, commandC, MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments cannot contain illegal name.
     */
    @Test
    public void parseIllegalNameFails() {
        String commandA = getCommandString(DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester(),
                DISCRETE_MATH.getCredits(), null) + "-random random";

        assertParseFailure(parser, commandA, MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments should not contain duplicate name.
     */
    @Test
    public void parseSameNameAppearMoreThanOnceFails() {
        String commandA = getCommandString(DISCRETE_MATH.getCode(),
                DISCRETE_MATH.getYear(),
                DISCRETE_MATH.getSemester(),
                DISCRETE_MATH.getCredits(), null);

        String commandB = commandA + getCommandString(DISCRETE_MATH.getCode(),
                null, null, null, null);

        assertParseFailure(parser, commandB, MESSAGE_INVALID_FORMAT);
    }

    /**
     * Parse in this unit test should pass.
     */
    @Test
    public void parseSuccess() {
    }

    /**
     * Builds the command with the given arguments.
     *
     * @param code {@code Code} of the new module
     * @param year {@code Year} of the new module
     * @param semester {@code Semester} of the new module
     * @param credit {@code Credit} of the new module
     * @param grade {@code Grade} of the new module
     * @return {@code String} which contains the command
     */
    private static String getCommandString(Code code, Year year,
            Semester semester, Credit credit, Grade grade) {
        String command = " ";

        if (code != null) {
            command += AddArgument.CODE.getShortName() + " " + code.value + " ";
        }

        if (year != null) {
            command += AddArgument.YEAR.getShortName() + " " + year.value + " ";
        }

        if (semester != null) {
            command += AddArgument.SEMESTER.getShortName() + " "
                    + semester.value + " ";
        }

        if (credit != null) {
            command += AddArgument.CREDIT.getShortName() + " " + credit.value
                    + " ";
        }

        if (grade != null) {
            command += AddArgument.GRADE.getShortName() + " " + grade.value
                    + " ";
        }

        return command;
    }
}
