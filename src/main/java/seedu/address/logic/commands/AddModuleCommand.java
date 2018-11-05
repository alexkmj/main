package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Module;

/**
 * Adds a module to the transcript.
 */
public class AddModuleCommand extends Command {
    /**
     * Command word for {@code AddModuleCommand}.
     */
    public static final String COMMAND_WORD = "add";

    /**
     * Usage of <b>add</b>.
     * <p>
     * Provides the description and syntax of <b>edit</b>.
     */
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the details of the module specified by the module code."
            + " Existing values will be overwritten by the input values."
            + " \nParameters:"
            + " -m MODULE_CODE"
            + " -y YEAR"
            + " -s SEMESTER"
            + " -c CREDIT"
            + " [-g GRADE]";

    // Constants for CommandException.
    public static final String MESSAGE_ADD_SUCCESS = "Added module: %1$s";
    public static final String MESSAGE_MODULE_ALREADY_EXIST = "Module already"
            + " exist.";

    private final Module toAdd;

    /**
     * Creates an AddModuleCommand to add the specified {@code Module}
     */
    public AddModuleCommand(Module toAdd) {
        requireNonNull(toAdd);
        this.toAdd = toAdd;
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

        // Throws CommandException if module to add already exists.
        moduleToAddNotInTranscript(model);

        // Add module and commit the transcript.
        model.addModule(toAdd);
        model.commitTranscript();

        // Return success message.
        String successMsg = String.format(MESSAGE_MODULE_ALREADY_EXIST, toAdd);
        return new CommandResult(successMsg);
    }

    /**
     * {@code toAdd} should not be in module list of transcript.
     *
     * @param model {@code Model} that the command operates on.
     * @throws CommandException thrown when {@code toAdd} already exist in
     * module list of transcript
     */
    private void moduleToAddNotInTranscript(Model model)
            throws CommandException {
        boolean moduleToAddAlreadyExist = model.hasModule(toAdd);
        if (moduleToAddAlreadyExist) {
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
        // Short circuit if same object.
        if (other == this) {
            return true;
        }

        // instanceof handles nulls.
        if (!(other instanceof DeleteModuleCommand)) {
            return false;
        }

        // State check.
        AddModuleCommand e = (AddModuleCommand) other;
        Module moduleOfOtherAddModuleCommand = e.toAdd;
        return toAdd.equals(moduleOfOtherAddModuleCommand);
    }
}
