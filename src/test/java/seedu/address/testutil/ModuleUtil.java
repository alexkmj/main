package seedu.address.testutil;

import seedu.address.logic.commands.AddModuleCommand;
import seedu.address.logic.commands.DeleteModuleCommand;
import seedu.address.logic.commands.EditModuleCommand;
import seedu.address.logic.parser.arguments.DeleteArgument;
import seedu.address.logic.parser.arguments.EditArgument;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credit;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Module;
import seedu.address.model.module.Semester;
import seedu.address.model.module.Year;

//@@author alexkmj
/**
 * A utility class for Module.
 */
public class ModuleUtil {

    /**
     * Returns an add command string for adding the {@code newModule}.
     *
     * @param newModule module to be added
     * @return add command string for adding {@code newModule}
     */
    public static String getAddModuleCommand(Module newModule) {
        return AddModuleCommand.COMMAND_WORD
                + " "
                + newModule.getCode().value
                + " "
                + newModule.getYear().value
                + " "
                + newModule.getSemester().value
                + " "
                + newModule.getCredits().value
                + " "
                + newModule.getGrade().value;
    }

    /**
     * Returns a delete command string for deleting {@code target}.
     *
     * @param target module to be deleted
     * @return delete command string for deleting {@code target}
     */
    public static String getDeleteModuleCommand(Module target) {

        return DeleteModuleCommand.COMMAND_WORD
                + " " + DeleteArgument.TARGET_CODE.getShortName()
                + " " + target.getCode().value
                + " " + DeleteArgument.TARGET_YEAR.getShortName()
                + " " + target.getYear().value
                + " " + DeleteArgument.TARGET_SEMESTER.getShortName()
                + " " + target.getSemester().value;
    }

    /**
     * Returns a edit command string for editing {@code target}.
     *
     * @param target module to be edited
     * @param code new {@code Code} object
     * @param year new {@code Year} object
     * @param semester new {@code Semester} object
     * @param credit new {@code Credit} object
     * @param grade new {@code Grade} object
     * @return edit command string for editing {@code target}
     */
    public static String getEditModuleCommand(Module target, Code code,
            Year year, Semester semester, Credit credit, Grade grade) {
        String command = EditModuleCommand.COMMAND_WORD
                + " " + EditArgument.TARGET_CODE.getShortName()
                + " " + target.getCode().value
                + " " + EditArgument.TARGET_YEAR.getShortName()
                + " " + target.getYear().value
                + " " + EditArgument.TARGET_SEMESTER.getShortName()
                + " " + target.getSemester().value;

        command += " ";

        if (code != null) {
            command += EditArgument.NEW_CODE.getShortName()
                    + " " + code.value;
        }

        if (year != null) {
            command += EditArgument.NEW_YEAR.getShortName()
                    + " " + year.value;
        }

        if (semester != null) {
            command += EditArgument.NEW_SEMESTER.getShortName()
                    + " " + semester.value;
        }

        if (credit != null) {
            command += EditArgument.NEW_CREDIT.getShortName()
                    + " " + credit.value;
        }

        if (grade != null) {
            command += EditArgument.NEW_GRADE.getShortName()
                    + " " + grade.value;
        }

        return command;
    }
}
