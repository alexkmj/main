package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.Objects;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credit;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Module;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;
import seedu.address.model.util.ModuleBuilder;

/**
 * {@code EditModuleCommand} edit fields of existing module.
 */
public class EditModuleCommand extends Command {
    /**
     * Command word for {@code EditModuleCommand}.
     */
    public static final String COMMAND_WORD = "edit";

    /**
     * Usage of <b>edit</b>.
     * <p>
     * Provides the description and syntax of <b>edit</b>.
     */
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the details of the module specified by the module code."
            + " Existing values will be overwritten by the input values."
            + " \nParameters:"
            + " -t TARGET_MODULE_CODE"
            + " [-e TARGET_MODULE_YEAR -z TARGET_MODULE_SEMESTER]"
            + " [-m NEW_MODULE_CODE]"
            + " [-y NEW_YEAR]"
            + " [-s NEW_SEMESTER]"
            + " [-c NEW_CREDIT]"
            + " [-g NEW_GRADE]";

    // Constants for CommandException.
    public static final String MESSAGE_EDIT_SUCCESS = "Edited module: %1$s";
    public static final String MESSAGE_INCOMPLETE_MODULE_GRADE_CHANGE = "Cannot"
            + " change grade of incomplete modules. Use adjust to change grade"
            + " of incomplete modules.";
    public static final String MESSAGE_MODULE_ALREADY_EXIST = "Edited module"
            + "already exist.";

    // Target fields.
    private final Code targetCode;
    private final Year targetYear;
    private final Semester targetSemester;

    // New fields.
    private final Code newCode;
    private final Year newYear;
    private final Semester newSemester;
    private final Credit newCredit;
    private final Grade newGrade;

    /**
     * Constructor that instantiates {@code EditModuleCommand}.
     * <p>
     * Sets target field and new field used to find and editing the targeted
     * module.
     * <p>
     * Assumes that:
     * <ul>
     *     <li>{@code targetCode} is not null.</li>
     *     <li>
     *         {@code targetYear} is null if and only if {@code targetSemester}
     *         is null
     *     </li>
     *     <li>
     *         One of {@code newCode}, {@code newYear}, {@code newSemester},
     *         {@code newCredit}, or {@code newGrade} is not null.
     *     </li>
     * </ul>
     *
     * @param argMap Contains the name-value pair mapping of the arguments
     */
    public EditModuleCommand(EnumMap<EditArgument, Object> argMap) {
        requireNonNull(argMap);

        // Instantiate target fields.
        targetCode = (Code) argMap.get(EditArgument.TARGET_CODE);
        targetYear = (Year) argMap.get(EditArgument.TARGET_YEAR);
        targetSemester = (Semester) argMap.get(EditArgument.TARGET_SEMESTER);

        // Instantiate new fields.
        newCode = (Code) argMap.get(EditArgument.NEW_CODE);
        newYear = (Year) argMap.get(EditArgument.NEW_YEAR);
        newSemester = (Semester) argMap.get(EditArgument.NEW_SEMESTER);
        newCredit = (Credit) argMap.get(EditArgument.NEW_CREDIT);
        newGrade = (Grade) argMap.get(EditArgument.NEW_GRADE);

        // Already handled by EditModuleCommandParser:
        // 1) Target code cannot be null.
        // 2) Target year is null if and only if target semester is null.
        // 3) One of new field is not null.
        requireNonNull(targetCode);
        assert !(targetYear == null ^ targetSemester == null);
        assert newCode != null
                || newYear != null
                || newSemester != null
                || newCredit != null
                || newGrade != null;
    }

    /**
     * Edits the targeted module in the module list of transcript.
     * <p>
     * Throws {@code CommandException} when:
     * <ul>
     *     <li>Target module does not exist</li>
     *     <li>Target module is incomplete and edited module has new grade</li>
     *     <li>
     *         Another module in transcript already have the same module code,
     *         year, and semester of the edited module.
     *     </li>
     * </ul>
     *
     * @param model {@code Model} that the command operates on.
     * @param history {@code CommandHistory} that the command operates on.
     * @return result of the command
     * @throws CommandException thrown when command cannot be executed
     * successfully
     */
    @Override
    public CommandResult execute(Model model, CommandHistory history)
            throws CommandException {
        requireNonNull(model);

        // Get target module.
        // Throws CommandException if module does not exists.
        // Throws CommandException if module is incomplete and grade changed.
        Module target = CommandUtil.getUniqueTargetModule(model,
                targetCode,
                targetYear,
                targetSemester);
        moduleCompletedIfGradeChange(target);

        // Get edited module.
        // Throws CommandException if edited module already exist.
        Module editedModule = createEditedModule(target);
        editedModuleExist(model, editedModule);

        if (target.equals(editedModule)) {
            throw new CommandException("No changes");
        }

        // Update module and commit the transcript.
        model.updateModule(target, editedModule);
        model.commitTranscript();

        String successMsg = String.format(MESSAGE_EDIT_SUCCESS, editedModule);
        return new CommandResult(successMsg);
    }

    /**
     * Returns the edited version of the target module.
     *
     * @param target the module to be edited
     * @return the edited version of {@code target}
     */
    private Module createEditedModule(Module target) {
        ModuleBuilder moduleBuilder = new ModuleBuilder(target);

        if (newCode != null) {
            moduleBuilder = moduleBuilder.withCode(newCode);
        }

        if (newYear != null) {
            moduleBuilder = moduleBuilder.withYear(newYear);
        }

        if (newSemester != null) {
            moduleBuilder = moduleBuilder.withSemester(newSemester);
        }

        if (newCredit != null) {
            moduleBuilder = moduleBuilder.withCredit(newCredit);
        }

        if (newGrade != null) {
            moduleBuilder = moduleBuilder.withGrade(newGrade);
        }

        return moduleBuilder.build();
    }

    /**
     * Throws {@code CommandException} if target is an incomplete module and
     * grade has been changed.
     *
     * @param target targeted module to be updated
     * @throws CommandException thrown when target is an incomplete module and
     * grade has been changed
     */
    private void moduleCompletedIfGradeChange(Module target)
            throws CommandException {
        boolean targetIncomplete = !target.getGrade().isComplete();
        boolean newGradeNotNull = newGrade != null;

        if (targetIncomplete && newGradeNotNull) {
            throw new CommandException(MESSAGE_INCOMPLETE_MODULE_GRADE_CHANGE);
        }
    }

    /**
     * Throws {@code CommandException} if code, year, or semester has been
     * changed, and there exist a module in module list of transcript that
     * shares the same module code, year, and semester as the
     * {@code editedModule}.
     *
     * @param model {@code Model} that the command operates on.
     * @param editedModule module with updated fields
     * @throws CommandException thrown if current module list already contain a
     * module sharing the same module code, year, and semester as the
     * {@code editedModule}
     */
    private void editedModuleExist(Model model, Module editedModule)
            throws CommandException {
        boolean identifierNotChanged = newCode == null
                && newYear == null
                && newSemester == null;

        // No conflicts since identifier hasn't changed.
        if (identifierNotChanged) {
            return;
        }

        // Throw CommandException if module with same identifier already exist.
        if (model.hasModule(editedModule)) {
            throw new CommandException(MESSAGE_MODULE_ALREADY_EXIST);
        }
    }

    /**
     * Returns true if all field matches.
     *
     * @param other the other object compared against
     * @return true if all field matches
     */
    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditModuleCommand)) {
            return false;
        }

        // state check
        EditModuleCommand e = (EditModuleCommand) other;
        return targetCode.equals(e.targetCode)
                && Objects.equals(targetYear, e.targetYear)
                && Objects.equals(targetSemester, e.targetSemester)
                && Objects.equals(newYear, e.newYear)
                && Objects.equals(newSemester, e.newSemester)
                && Objects.equals(newCredit, e.newCredit)
                && Objects.equals(newGrade, e.newGrade);
    }
}
