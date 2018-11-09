package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.Objects;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;

//@@author alexkmj
/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteModuleCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the module identified by the module code, year, and"
            + " semester.\n"
            + "Parameters: -t MODULE_CODE [-e YEAR -z SEMESTER]\n"
            + "Example: " + COMMAND_WORD + " -t CS2103 -e 4 -z 2";

    public static final String MESSAGE_DELETE_MODULE_SUCCESS = "Deleted Module";

    private final Code targetCode;
    private final Year targetYear;
    private final Semester targetSemester;

    /**
     * Constructor that instantiates {@code DeleteModuleCommand}.
     * <p>
     * Sets target field used to find and delete the module.
     * <p>
     * Assumes that:
     * <ul>
     *     <li>{@code targetCode} is not null.</li>
     *     <li>
     *         {@code targetYear} is null if and only if {@code targetSemester}
     *         is null
     *     </li>
     * </ul>
     *
     * @param argMap Contains the name-value pair mapping of the arguments
     */
    public DeleteModuleCommand(EnumMap<DeleteArgument, Object> argMap) {
        requireNonNull(argMap);

        // Instantiate target fields.
        targetCode = (Code) argMap.get(DeleteArgument.TARGET_CODE);
        targetYear = (Year) argMap.get(DeleteArgument.TARGET_YEAR);
        targetSemester = (Semester) argMap.get(DeleteArgument.TARGET_SEMESTER);

        // Already handled by DeleteModuleCommandParser:
        // 1) Target code cannot be null.
        // 2) Target year is null if and only if target semester is null.
        requireNonNull(targetCode);
        assert !(targetYear == null ^ targetSemester == null);
    }

    /**
     * Deletes the targeted module in the module list of transcript.
     * <p>
     * Throws {@code CommandException} when:
     * <ul>
     *     <li>There is no module that matches the target code.</li>
     *     <li>There exists more than one matching module.</li>
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
        // Model cannot be null.
        requireNonNull(model);

        // Get target module.
        // Throws CommandException if module does not exists.
        // Throws CommandException if there is more than one matching module.
        Module target = CommandUtil.getUniqueTargetModule(model, targetCode,
                targetYear, targetSemester);

        // Delete module and commit.
        model.deleteModule(target);
        model.commitAddressBook();

        // Return success message.
        String successMsg = String.format(MESSAGE_DELETE_MODULE_SUCCESS);
        return new CommandResult(successMsg);
    }

    /**
     * Returns true if all field matches.
     *
     * @param other the other object compared against
     * @return true if all field matches
     */
    @Override
    public boolean equals(Object other) {
        // Short circuit if same object.
        if (other == this) {
            return true;
        }

        // instanceof handles nulls.
        if (!(other instanceof DeleteModuleCommand)) {
            return false;
        }

        // State check.
        DeleteModuleCommand e = (DeleteModuleCommand) other;
        return Objects.equals(targetCode, e.targetCode)
                && Objects.equals(targetYear, e.targetYear)
                && Objects.equals(targetSemester, e.targetSemester);
    }
}
