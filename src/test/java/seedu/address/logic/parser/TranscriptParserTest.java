package seedu.address.logic.parser;

import static org.junit.Assert.assertEquals;

import static seedu.address.testutil.TypicalModules.ASKING_QUESTIONS;
import static seedu.address.testutil.TypicalModules.DATA_STRUCTURES;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;

import java.util.EnumMap;

import org.junit.Test;

import seedu.address.logic.commands.AddModuleCommand;
import seedu.address.logic.commands.DeleteModuleCommand;
import seedu.address.logic.commands.EditModuleCommand;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.testutil.Assert;
import seedu.address.testutil.ModuleUtil;

//@@author alexkmj
/**
 * Unit testing for {@code TranscriptParser}.
 * <p>
 * Tests for the following:
 * <ul>
 *     <li>null command should fail</li>
 *     <li>empty command should fail</li>
 *     <li>invalid command should fail</li>
 *     <li>Add module command</li>
 *     <li>Delete module command</li>
 *     <li>Edit module command</li>
 * </ul>
 */
public class TranscriptParserTest {
    /**
     * {@code TranscriptParser} used for all tests in {@TranscriptParserTest}.
     */
    private final TranscriptParser parser = new TranscriptParser();

    /**
     * {@code null}, empty, or invalid command should fail.
     */
    @Test
    public void parseCommandInvalidModuleFail() {
        Assert.assertThrows(NullPointerException.class, () -> {
            parser.parseCommand(null);
        });

        Assert.assertThrows(ParseException.class, () -> {
            parser.parseCommand("");
        });

        Assert.assertThrows(ParseException.class, () -> {
            parser.parseCommand("random");
        });
    }

    /**
     * Tests for add module command.
     *
     * @throws ParseException parse exception that should not be thrown
     */
    @Test
    public void parseCommandAddModuleSuccess() throws ParseException {
        AddModuleCommand addModuleCommand = new AddModuleCommand(DISCRETE_MATH);
        String addModuleString = ModuleUtil.getAddModuleCommand(DISCRETE_MATH);
        AddModuleCommand command = (AddModuleCommand) parser
                .parseCommand(addModuleString);
        assertEquals(addModuleCommand, command);
    }

    /**
     * Test for delete module command.
     *
     * @throws ParseException parse exception that should not be thrown
     */
    @Test
    public void parseCommandDeleteModuleSuccess() throws ParseException {
        EnumMap<DeleteArgument, Object> argMap;
        argMap = new EnumMap<>(DeleteArgument.class);
        argMap.put(DeleteArgument.TARGET_CODE, DATA_STRUCTURES.getCode());
        argMap.put(DeleteArgument.TARGET_YEAR, DATA_STRUCTURES.getYear());
        argMap.put(
                DeleteArgument.TARGET_SEMESTER,
                DATA_STRUCTURES.getSemester());

        DeleteModuleCommand deleteModuleCommand =
                new DeleteModuleCommand(argMap);
        String deleteModuleString = ModuleUtil
                .getDeleteModuleCommand(DATA_STRUCTURES);
        DeleteModuleCommand command = (DeleteModuleCommand) parser
                .parseCommand(deleteModuleString);
        assertEquals(deleteModuleCommand, command);
    }

    /**
     * Test for edit module command.
     *
     * @throws ParseException
     */
    @Test
    public void parseCommandEditModuleSuccess() throws ParseException {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, DATA_STRUCTURES.getCode());
        argMap.put(EditArgument.TARGET_YEAR, DATA_STRUCTURES.getYear());
        argMap.put(EditArgument.TARGET_SEMESTER, DATA_STRUCTURES.getSemester());
        argMap.put(EditArgument.NEW_CODE, ASKING_QUESTIONS.getCode());

        EditModuleCommand editModuleCommand = new EditModuleCommand(argMap);
        String editModuleString = ModuleUtil
                .getEditModuleCommand(DATA_STRUCTURES,
                        ASKING_QUESTIONS.getCode(),
                        null,
                        null,
                        null,
                        null);

        EditModuleCommand command = (EditModuleCommand) parser
                .parseCommand(editModuleString);
        assertEquals(editModuleCommand, command);
    }
}
