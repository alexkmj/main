package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.GoalCommand;

//@@author jeremiah-ang
public class GoalCommandParserTest {
    private GoalCommandParser parser = new GoalCommandParser();

    @Test
    public void parseValidCommandSuccess() {
        String userInput = "0";
        GoalCommand expectedCommand = new GoalCommand(0);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = "4.5";
        expectedCommand = new GoalCommand(4.5);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = "5";
        expectedCommand = new GoalCommand(5);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parseInvalidNumberFormatFailure() {
        String userInput = "4.5 3.5";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, GoalCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parseOutOfRangeFailure() {
        String userInput = "5.5";
        String expectedMessage = GoalCommandParser.MESSAGE_OUT_OF_RANGE;
        assertParseFailure(parser, userInput, expectedMessage);

        userInput = "-1";
        expectedMessage = GoalCommandParser.MESSAGE_OUT_OF_RANGE;
        assertParseFailure(parser, userInput, expectedMessage);
    }
}
