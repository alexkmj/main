package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;

//@@author alexkmj
/**
 * Contains utility methods used for executing commands in the various
 * Command classes.
 */
public class CommandUtil {
    public static final String MESSAGE_NO_SUCH_MODULE = "No such module.";
    public static final String MESSAGE_MULTIPLE_MODULE_ENTRIES = "There exists"
            + " more than one module that matches the target code, and target"
            + " year and target semester is not specified.";

    /**
     * Returns the targeted module.
     * <p>
     * Checks if module specified by {@code targetCode} exist. If multiple
     * module entries matches {@code targetCode}, check if {@code targetYear}
     * and {@code targetSemester} has been specified. If all check passes, the
     * targeted module is returned.
     *
     * @param model {@code model} containing the transcript
     * @return targeted module
     * @throws CommandException thrown when specified module does not exist or
     * there are multiple module entries matching the {@code targetCode} but
     * {@code targetYear} or {@code targetSemester} was not specified
     */
    public static Module getUniqueTargetModule(Model model, Code targetCode,
            Year targetYear, Semester targetSemester) throws CommandException {
        requireNonNull(targetCode);

        // Returns the number of modules with target code, year, and semester.
        List<Module> filteredModule = model.getFilteredModuleList()
                .stream()
                .filter(module -> isTarget(module, targetCode,
                        targetYear, targetSemester))
                .collect(Collectors.toList());

        // Throws exception when targeted module does not exist.
        if (filteredModule.size() == 0) {
            throw new CommandException(MESSAGE_NO_SUCH_MODULE);
        }

        // Throws exception when more than one module matches target.
        if (filteredModule.size() > 1) {
            throw new CommandException(MESSAGE_MULTIPLE_MODULE_ENTRIES);
        }

        // Returns the targeted module.
        return filteredModule.get(0);
    }

    /**
     * Returns true if module matches all non-null target fields.
     *
     * @param module module {@code Module} to check
     * @param targetCode {@code Code} to check against
     * @param targetYear {@code Year} to check against
     * @param targetSemester {@code Semester} to check against
     * @return true if module matches all non-null target fields
     */
    private static boolean isTarget(Module module, Code targetCode,
            Year targetYear, Semester targetSemester) {
        requireNonNull(module);
        requireNonNull(targetCode);

        if (targetYear == null && targetSemester == null) {
            return module.getCode().equals(targetCode);
        }

        return module.getCode().equals(targetCode)
                && module.getYear().equals(targetYear)
                && module.getSemester().equals(targetSemester);
    }
}
