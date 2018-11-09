package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalModules.ASKING_QUESTIONS;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;
import static seedu.address.testutil.TypicalModules.getTypicalTranscript;

import java.util.EnumMap;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the {@code Model},
 * {@code UndoCommand} and {@code RedoCommand}) and unit tests for
 * {@code DeleteModuleCommandTest}.
 */
public class DeleteModuleCommandTest {

    private Model model = new ModelManager(getTypicalTranscript(),
            new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    /**
     * Target should be in the transcript.
     */
    @Test
    public void executeTargetNotInTranscriptFail() {
        EnumMap<DeleteArgument, Object> argMap;
        argMap = new EnumMap<>(DeleteArgument.class);
        argMap.put(DeleteArgument.TARGET_CODE, ASKING_QUESTIONS.getCode());
        argMap.put(DeleteArgument.TARGET_YEAR, null);
        argMap.put(DeleteArgument.TARGET_SEMESTER, null);

        DeleteModuleCommand command = new DeleteModuleCommand(argMap);

        assertCommandFailure(command, model, commandHistory,
                CommandUtil.MESSAGE_NO_SUCH_MODULE);
    }

    /**
     * Command should execute successfully.
     */
    @Test
    public void executeEditCommandSuccess() {
        EnumMap<DeleteArgument, Object> argMap;
        argMap = new EnumMap<>(DeleteArgument.class);
        argMap.put(DeleteArgument.TARGET_CODE, DISCRETE_MATH.getCode());
        argMap.put(DeleteArgument.TARGET_YEAR, null);
        argMap.put(DeleteArgument.TARGET_SEMESTER, null);

        DeleteModuleCommand command = new DeleteModuleCommand(argMap);

        ModelManager expectedModel = new ModelManager(model.getTranscript(),
                new UserPrefs());
        expectedModel.deleteModule(DISCRETE_MATH);
        expectedModel.commitTranscript();

        assertCommandSuccess(command, model, commandHistory,
                DeleteModuleCommand.MESSAGE_DELETE_MODULE_SUCCESS,
                expectedModel);
    }
}
