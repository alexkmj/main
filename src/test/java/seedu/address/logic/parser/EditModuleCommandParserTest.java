package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalModules.DATA_STRUCTURES;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;

import java.util.EnumMap;

import org.junit.Test;

import seedu.address.logic.commands.EditModuleCommand;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.testutil.Assert;

/**
 * Unit testing for {@code EditModuleCommandParser}.
 * <p>
 * Tests for the following:
 * <ul>
 *     <li>Argument should not be null</li>
 *     <li>Number of arguments should be between 3 and 13</li>
 *     <li>Target code cannot be null</li>
 *     <li>
 *         Target year is non-null if and only if target semester is non-null
 *     </li>
 *     <li>One of the new fields should be non-null</li>
 * </ul>
 */
public class EditModuleCommandParserTest {
    /**
     * {@code EditModuleCommandParser} instance used for parsing arguments in
     * {@code EditModuleCommandParserTest}.
     */
    private EditModuleCommandParser parser = new EditModuleCommandParser();

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
     * Number of arguments for edit should be more than or equal to 4 and less
     * than or equal to 16.
     */
    @Test
    public void parseInvalidNumOfArgumentFails() {
        String exceptionMsg = "Invalid number of arguments!"
                + " Number of arguments should be 16, 4, 6, 8, 10, 12, 14";

        assertParseFailure(parser, "", exceptionMsg);
        assertParseFailure(parser, "-t CS1231", exceptionMsg);
        assertParseFailure(parser, "-t CS1231 -y", exceptionMsg);
        assertParseFailure(parser, "-t CS1231 1", exceptionMsg);
        assertParseFailure(parser,
                "-t CS1231 -e 1 -z 1 -m CS1232 -y 1 -s 1 -c 4 -g 5 "
                        + " extra",
                exceptionMsg);
    }

    /**
     * Arguments should be in name-value pair format.
     */
    @Test
    public void parseNotNameValuePairFails() {
        assertParseFailure(parser,
                "-t CS1231 A+ -g",
                EditModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments cannot contain illegal name.
     */
    @Test
    public void parseIllegalNameFails() {
        assertParseFailure(parser,
                "-t CS1231 -g A+ -random random",
                EditModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Arguments should not contain duplicate name.
     */
    @Test
    public void parseSameNameAppearMoreThanOnceFails() {
        assertParseFailure(parser,
                "-t CS1231 -g A+ -g B",
                EditModuleCommandParser.MESSAGE_INVALID_FORMAT);

        assertParseFailure(parser,
                "-t CS1231 -g A+ --grade B",
                EditModuleCommandParser.MESSAGE_INVALID_FORMAT);
    }

    /**
     * Target code cannot be null.
     */
    @Test
    public void parseTargetCodeNullFails() {
        assertParseFailure(parser,
                "-m CS1232 -g A+",
                EditModuleCommandParser.MESSAGE_TARGET_CODE_REQUIRED);
    }

    /**
     * Target year is null if and only if target semester is also null.
     */
    @Test
    public void parseTargetYearTargetSemesterExclusiveNullFails() {
        assertParseFailure(parser,
                "-t CS1231 -e 1 -m CS1232",
                EditModuleCommandParser.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);

        assertParseFailure(parser,
                "-t CS1231 -z 1 -m CS1232",
                EditModuleCommandParser.MESSAGE_YEAR_AND_SEMESTER_XOR_NULL);
    }

    /**
     * There should be at least one value being updated.
     */
    @Test
    public void parseNoNewValueFails() {
        assertParseFailure(parser,
                "-t CS1231 -e 1 -z 1",
                EditModuleCommandParser.MESSAGE_NO_NEW_VALUE);
    }

    /**
     * Parse in this unit test should pass.
     */
    @Test
    public void parseSuccess() {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, DATA_STRUCTURES.getCode());
        argMap.put(EditArgument.NEW_CODE, DISCRETE_MATH.getCode());

        assertParseSuccess(parser,
                "-t CS2040 -m CS1231",
                new EditModuleCommand(argMap));

        argMap.put(EditArgument.TARGET_YEAR, DATA_STRUCTURES.getYear());
        argMap.put(EditArgument.TARGET_SEMESTER, DATA_STRUCTURES.getSemester());
        assertParseSuccess(parser,
                "-t CS2040 -e 3 -z s1 -m CS1231",
                new EditModuleCommand(argMap));
    }
}
