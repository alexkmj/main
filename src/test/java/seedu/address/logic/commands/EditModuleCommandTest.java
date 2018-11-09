package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalModules.ASKING_QUESTIONS;
import static seedu.address.testutil.TypicalModules.DISCRETE_MATH;
import static seedu.address.testutil.TypicalModules.getTypicalTranscript;

import java.util.EnumMap;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Module;
import seedu.address.model.util.ModuleBuilder;
import seedu.address.testutil.TypicalModules;

/**
 * Contains integration tests (interaction with the {@code Model},
 * {@code UndoCommand} and {@code RedoCommand}) and unit tests for
 * {@code EditModuleCommandTest}.
 */
public class EditModuleCommandTest {

    private Model model = new ModelManager(getTypicalTranscript(),
            new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    /**
     * Target should be in the transcript.
     */
    @Test
    public void executeTargetNotInTranscriptFail() {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, ASKING_QUESTIONS.getCode());
        argMap.put(EditArgument.TARGET_YEAR, null);
        argMap.put(EditArgument.TARGET_SEMESTER, null);
        argMap.put(EditArgument.NEW_CODE, DISCRETE_MATH.getCode());
        argMap.put(EditArgument.NEW_YEAR, null);
        argMap.put(EditArgument.NEW_SEMESTER, null);
        argMap.put(EditArgument.NEW_CREDIT, null);
        argMap.put(EditArgument.NEW_GRADE, null);

        EditModuleCommand editModuleCommand = new EditModuleCommand(argMap);

        assertCommandFailure(editModuleCommand,
                model, commandHistory,
                CommandUtil.MESSAGE_NO_SUCH_MODULE);
    }

    /**
     * If there is a change in grade, target should not be incomplete
     */
    @Test
    public void executeTargetIsIncompleteGradeChangeFail() {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, DISCRETE_MATH.getCode());
        argMap.put(EditArgument.TARGET_YEAR, null);
        argMap.put(EditArgument.TARGET_SEMESTER, null);
        argMap.put(EditArgument.NEW_CODE, null);
        argMap.put(EditArgument.NEW_YEAR, null);
        argMap.put(EditArgument.NEW_SEMESTER, null);
        argMap.put(EditArgument.NEW_CREDIT, null);
        argMap.put(EditArgument.NEW_GRADE, ASKING_QUESTIONS.getGrade());

        Grade gradeGeneratedByTarget = DISCRETE_MATH.getGrade()
                .targetGrade(TypicalModules.GRADE_F);

        Module discreteMathIncomplete = new ModuleBuilder(DISCRETE_MATH)
                .withGrade(gradeGeneratedByTarget)
                .withCompleted(false)
                .build();

        model.updateModule(DISCRETE_MATH, discreteMathIncomplete);
        model.commitTranscript();

        EditModuleCommand editModuleCommand = new EditModuleCommand(argMap);

        assertCommandFailure(editModuleCommand,
                model, commandHistory,
                EditModuleCommand.MESSAGE_INCOMPLETE_MODULE_GRADE_CHANGE);

        model.updateModule(discreteMathIncomplete, DISCRETE_MATH);
        model.commitTranscript();
    }

    /**
     * Edited module can only share the same code, year, and semester as target
     * module. Edited module cannot share the same code, year, and semester with
     * any other module.
     */
    @Test
    public void executeEditedModuleAlreadyExistFail() {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, ASKING_QUESTIONS.getCode());
        argMap.put(EditArgument.TARGET_YEAR, null);
        argMap.put(EditArgument.TARGET_SEMESTER, null);
        argMap.put(EditArgument.NEW_CODE, DISCRETE_MATH.getCode());
        argMap.put(EditArgument.NEW_YEAR, DISCRETE_MATH.getYear());
        argMap.put(EditArgument.NEW_SEMESTER, DISCRETE_MATH.getSemester());
        argMap.put(EditArgument.NEW_CREDIT, DISCRETE_MATH.getCredits());
        argMap.put(EditArgument.NEW_GRADE, DISCRETE_MATH.getGrade());

        model.addModule(ASKING_QUESTIONS);
        model.commitTranscript();

        EditModuleCommand editModuleCommand = new EditModuleCommand(argMap);

        assertCommandFailure(editModuleCommand,
                model, commandHistory,
                EditModuleCommand.MESSAGE_MODULE_ALREADY_EXIST);

        model.deleteModule(ASKING_QUESTIONS);
        model.commitTranscript();
    }

    /**
     * Command should execute successfully.
     */
    @Test
    public void executeEditCommandSuccess() {
        EnumMap<EditArgument, Object> argMap;
        argMap = new EnumMap<>(EditArgument.class);
        argMap.put(EditArgument.TARGET_CODE, DISCRETE_MATH.getCode());
        argMap.put(EditArgument.TARGET_YEAR, null);
        argMap.put(EditArgument.TARGET_SEMESTER, null);
        argMap.put(EditArgument.NEW_CODE, null);
        argMap.put(EditArgument.NEW_YEAR, null);
        argMap.put(EditArgument.NEW_SEMESTER, null);
        argMap.put(EditArgument.NEW_CREDIT, null);
        argMap.put(EditArgument.NEW_GRADE, ASKING_QUESTIONS.getGrade());

        EditModuleCommand editModuleCommand = new EditModuleCommand(argMap);

        Module editedDiscreteMath = new ModuleBuilder(DISCRETE_MATH)
                .withGrade(ASKING_QUESTIONS.getGrade())
                .build();

        String expectedMessage = String.format(
                EditModuleCommand.MESSAGE_EDIT_SUCCESS,
                editedDiscreteMath);

        ModelManager expectedModel = new ModelManager(model.getTranscript(),
                new UserPrefs());
        expectedModel.updateModule(DISCRETE_MATH, editedDiscreteMath);
        expectedModel.commitTranscript();

        assertCommandSuccess(editModuleCommand,
                model, commandHistory,
                expectedMessage, expectedModel);
    }
}
