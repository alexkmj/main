package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.function.Predicate;

import javafx.collections.ObservableList;

import seedu.address.model.capgoal.CapGoal;
import seedu.address.model.module.Code;
import seedu.address.model.module.Grade;
import seedu.address.model.module.Module;
import seedu.address.model.module.UniqueModuleList;
import seedu.address.model.module.exceptions.ModuleCompletedException;

//@@author alexkmj
/**
 * Wraps all data at the transcript level
 * Duplicates are not allowed (by .isSameModule comparison)
 */
public class Transcript implements ReadOnlyTranscript {

    private final UniqueModuleList modules;
    private CapGoal capGoal;
    private double currentCap;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */

    {
        modules = new UniqueModuleList();
    }

    public Transcript() {
        capGoal = new CapGoal();
        currentCap = 0;
    }

    /**
     * Creates an Transcript using the Modules in the {@code toBeCopied}
     */
    public Transcript(ReadOnlyTranscript toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the module list with {@code modules}.
     * {@code modules} must not contain duplicate modules.
     */
    public void setModules(List<Module> modules) {
        this.modules.setModules(modules);
        modulesUpdated();
    }

    /**
     * Resets the existing data of this {@code Transcript} with {@code newData}.
     */
    public void resetData(ReadOnlyTranscript newData) {
        requireNonNull(newData);

        setModules(newData.getModuleList());
        setCapGoal(newData.getCapGoal());
    }

    //// module-level operations

    /**
     * Returns true if a module with the same identity as {@code module} exists in the transcript.
     */
    public boolean hasModule(Module module) {
        requireNonNull(module);
        return modules.contains(module);
    }

    /**
     * Returns true if a module with the same identity as {@code module} exists in the transcript.
     */
    public boolean hasMultipleInstances(Code code) {
        requireNonNull(code);
        return modules.hasMultipleInstances(code);
    }

    /**
     * Adds a module to the transcript.
     * The module must not already exist in the transcript.
     */
    public void addModule(Module p) {
        modules.add(p);
        modulesUpdated();
    }

    /**
     * Replaces the given module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the transcript.
     * The module identity of {@code editedModule} must not be the same as another existing module in the transcript.
     */
    public void updateModule(Module target, Module editedModule) {
        requireNonNull(editedModule);
        modules.setModule(target, editedModule);
        modulesUpdated();
    }

    /**
     * Removes {@code key} from this {@code Transcript}.
     * {@code key} must exist in the transcript.
     */
    public void removeModule(Module key) {
        modules.remove(key);
        modulesUpdated();
    }

    /**
     * Removes {@code key} from this {@code Transcript}.
     */
    public void removeModule(Predicate<Module> predicate) {
        modules.remove(predicate);
        modulesUpdated();
    }

    //@@author jeremiah-ang
    @Override
    public double getCurrentCap() {
        return currentCap;
    }

    @Override
    public ObservableList<Module> getCompletedModuleList() {
        return getModuleList().filtered(Module::hasCompleted);
    }

    @Override
    public ObservableList<Module> getIncompleteModuleList() {
        return getModuleList().filtered(module -> !module.hasCompleted());
    }

    private void updateCurrentCap() {
        currentCap = calculateCap();
    }

    /**
     * Calculate CAP Score based on modules with scores
     *
     * @return cap: cap score
     */
    private double calculateCap() {

        ObservableList<Module> gradedModulesList = getGradedModulesList();
        double totalModulePoint = calculateTotalModulePoint(gradedModulesList);
        double totalModuleCredit = calculateTotalModuleCredit(gradedModulesList);

        double cap = 0;
        if (totalModuleCredit > 0) {
            cap = totalModulePoint / totalModuleCredit;
        }

        return cap;
    }

    /**
     * Calculates the total module point from the list of modules
     * @param modules
     * @return
     */
    private double calculateTotalModulePoint(ObservableList<Module> modules) {
        double totalPoint = 0;
        for (Module module : modules) {
            totalPoint += module.getGrade().getPoint() * module.getCredits().value;
        }
        return totalPoint;
    }

    /**
     * Calculates the total module credit from the list of modules
     * @param modules
     * @return
     */
    private double calculateTotalModuleCredit(ObservableList<Module> modules) {
        int totalModuleCredit = 0;
        for (Module module : modules) {
            totalModuleCredit += module.getCredits().value;
        }
        return totalModuleCredit;
    }

    /**
     * Filters for modules that is to be used for CAP calculation
     *
     * @return list of modules used for CAP calculation
     */
    private ObservableList<Module> getGradedModulesList() {
        return modules.getFilteredModules(this::moduleIsUsedForCapCalculation);
    }

    /**
     * Filters for modules that is to be assigned a target grade
     * @return gradedModulesList: a list of modules used for CAP calculation
     */
    private ObservableList<Module> getTargetableModulesList() {
        return modules.getFilteredModules(Module::isTargetable);
    }

    /**
     * Filters for modules that have target grades
     * @return gradedModulesList: a list of modules used for CAP calculation
     */
    protected ObservableList<Module> getTargetedModulesList() {
        return modules.getFilteredModules(Module::isTargetted);
    }

    /**
     * Check if the given module should be considered for CAP Calculation
     *
     * @param module
     * @return true if yes, false otherwise
     */
    private boolean moduleIsUsedForCapCalculation(Module module) {
        return module.hasCompleted() && module.isAffectCap();
    }

    /**
     * Calls relevant methods when the modules list is updated
     */
    private void modulesUpdated() {
        updateTargetModuleGrades();
        updateCurrentCap();
    }

    /**
     * Replaces targetable module with an updated target grade
     */
    private void updateTargetModuleGrades() {
        boolean shouldSkip = !capGoal.isSet();
        if (shouldSkip) {
            return;
        }
        ObservableList<Module> targetableModules = getTargetableModulesList();
        List<Module> newTargetModules = getNewTargetModuleGrade(targetableModules);
        if (newTargetModules == null) {
            makeCapGoalImpossible();
            return;
        }
        makeCapGoalPossible();
        replaceTargetModules(targetableModules, newTargetModules);
    }

    /**
     * Replaces Modules used to calculate target grade with new Modules with those target grades
     * @param targetableModules
     * @param newTargetModules
     */
    private void replaceTargetModules(
            List<Module> targetableModules, List<Module> newTargetModules) {
        if (targetableModules.isEmpty()) {
            return;
        }
        modules.removeAll(targetableModules);
        modules.addAll(newTargetModules);
    }


    /**
     * Calculates target module grade in order to achieve target goal
     * @return a list of modules with target grade if possible. null otherwise
     */
    private List<Module> getNewTargetModuleGrade(ObservableList<Module> targetableModules) {
        ObservableList<Module> gradedModules = getGradedModulesList();
        ObservableList<Module> adjustedModules = getGradedAdjustedModulesList();
        ObservableList<Module> sortedTargetableModules = targetableModules.sorted(
                Comparator.comparingInt(Module::getCreditsValue));

        double totalUngradedModuleCredit = calculateTotalModuleCredit(sortedTargetableModules);
        double totalMc = calculateTotalModuleCredit(gradedModules)
                + calculateTotalModuleCredit(adjustedModules) + totalUngradedModuleCredit;
        double currentTotalPoint = calculateTotalModulePoint(gradedModules)
                + calculateTotalModulePoint(adjustedModules);

        if (totalUngradedModuleCredit == 0) {
            if (totalMc == 0) {
                return null;
            }
            double adjustedCap = currentTotalPoint / totalMc;
            if (capGoal.getValue() > adjustedCap) {
                return null;
            }
            return new ArrayList<>();
        }

        return createNewTargetModuleGrade(
                sortedTargetableModules,
                totalUngradedModuleCredit, totalMc, currentTotalPoint);
    }

    /**
     * Creates the new list of modules with target grade
     * @param sortedTargetableModules
     * @param totalUngradedModuleCredit
     * @param totalMc
     * @param currentTotalPoint
     * @return the new list of modules with target grade
     * @throws IllegalArgumentException  if totalUngradedModuleCredit is zero or negative
     */
    private List<Module> createNewTargetModuleGrade(
            ObservableList<Module> sortedTargetableModules,
            double totalUngradedModuleCredit, double totalMc, double currentTotalPoint) {
        if (totalUngradedModuleCredit <= 0) {
            throw new IllegalArgumentException("totalUngradedModuleCredit cannot be zero or negative");
        }
        double totalScoreToAchieve = capGoal.getValue() * totalMc - currentTotalPoint;
        return calculateAndCreateNewTargetModuleGrade(
                sortedTargetableModules,
                totalUngradedModuleCredit, totalScoreToAchieve);
    }

    /**
     * Calculates and creates the new list of modules with target grade
     * @param sortedTargetableModules
     * @param givenTotalUngradedModuleCredit
     * @param givenTotalScoreToAchieve
     * @return the new list of modules with target grade
     * @throws IllegalArgumentException  if totalUngradedModuleCredit is zero or negative
     */
    private List<Module> calculateAndCreateNewTargetModuleGrade(
            ObservableList<Module> sortedTargetableModules,
            double givenTotalUngradedModuleCredit, double givenTotalScoreToAchieve) {
        double totalUngradedModuleCredit = givenTotalUngradedModuleCredit;
        double totalScoreToAchieve = givenTotalScoreToAchieve;
        double unitScoreToAchieve = Math.ceil(totalScoreToAchieve / totalUngradedModuleCredit * 2) / 2.0;
        if (totalUngradedModuleCredit <= 0) {
            throw new IllegalArgumentException("totalUngradedModuleCredit cannot be zero or negative");
        }
        if (unitScoreToAchieve > 5) {
            return null;
        }
        List<Module> targetModules = new ArrayList<>();
        Module newTargetModule;
        for (Module targetedModule : sortedTargetableModules) {
            if (unitScoreToAchieve <= 0.5) {
                unitScoreToAchieve = 1.0;
            }
            newTargetModule = targetedModule.updateTargetGrade(unitScoreToAchieve);
            targetModules.add(newTargetModule);
            totalScoreToAchieve -= newTargetModule.getCreditsValue() * unitScoreToAchieve;
            totalUngradedModuleCredit -= newTargetModule.getCreditsValue();
            unitScoreToAchieve = Math.ceil(totalScoreToAchieve / totalUngradedModuleCredit * 2) / 2.0;
        }

        return targetModules;
    }

    private ObservableList<Module> getGradedAdjustedModulesList() {
        return modules.getFilteredModules(module -> module.isAdjusted() && module.isAffectCap());
    }

    @Override
    public CapGoal getCapGoal() {
        return capGoal;
    }

    private void setCapGoal(CapGoal capGoal) {
        this.capGoal = capGoal;
    }

    public void setCapGoal(double capGoal) {
        this.capGoal = new CapGoal(capGoal);
        updateTargetModuleGrades();
    }

    /**
     * Sets the capGoal impossible
     */
    private void makeCapGoalImpossible() {
        capGoal = capGoal.makeIsImpossible();
    }

    /**
     * Sets the capGoal as possible
     */
    private void makeCapGoalPossible() {
        assert capGoal.getValue() > 0;
        capGoal = new CapGoal(capGoal.getValue());
    }

    /**
     * Tells if the value is no longer possible
     * @return true if yes, false otherwise
     */
    public boolean isCapGoalImpossible() {
        return capGoal.isImpossible();
    }

    /**
     * Finds module that matches module to find with isSameModule
     * @param moduleToFind
     * @return the Module that matches; null if not matched
     */
    public Module findModule(Module moduleToFind) {
        return modules.find(moduleToFind);
    }

    /**
     * Finds the first instance of the module that has the same moduleCodeToFind
     * @param moduleCodeToFind
     * @return module that return true; null if not matched
     */
    public Module findModule(Code moduleCodeToFind) {
        return modules.find(moduleCodeToFind);
    }

    /**
     * Adjust the target Module to the desired Grade
     * @param targetModule
     * @param adjustGrade
     * @return adjusted Module
     */
    public Module adjustModule(Module targetModule, Grade adjustGrade) {
        requireNonNull(targetModule);
        requireNonNull(adjustGrade);
        if (targetModule.hasCompleted()) {
            throw new ModuleCompletedException();
        }

        Module adjustedModule = targetModule.adjustGrade(adjustGrade);
        //TODO: Use updateModule when fixed
        modules.remove(targetModule);
        modules.add(adjustedModule);
        modulesUpdated();
        return adjustedModule;
    }

    //@@author
    //// util methods

    @Override
    public String toString() {
        return modules.asUnmodifiableObservableList().size() + " modules";
        // TODO: refine later
    }

    @Override
    public ObservableList<Module> getModuleList() {
        return modules.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Transcript // instanceof handles nulls
                && modules.equals(((Transcript) other).modules));
    }

    @Override
    public int hashCode() {
        return modules.hashCode();
    }
}
