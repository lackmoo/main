package spinbox.commands;

import spinbox.containers.ModuleContainer;
import spinbox.containers.lists.FileList;
import spinbox.containers.Notepad;
import spinbox.entities.items.File;
import spinbox.entities.Module;
import spinbox.exceptions.DataReadWriteException;
import spinbox.exceptions.InvalidIndexException;
import spinbox.exceptions.SpinBoxException;
import spinbox.exceptions.InputException;
import spinbox.entities.items.tasks.Task;
import spinbox.Ui;
import spinbox.containers.lists.TaskList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RemoveMultipleCommand extends Command {
    private static final String NON_EXISTENT_MODULE = "This module does not exist.";
    private static final String NOTES_REMOVED = "The specified notes have been successfully removed from ";
    private static final String PROVIDE_INDEX = "Please provide the indexes to be removed.";
    private static final String INVALID_INDEX = "Please enter a valid index.";
    private static final String REMOVE_SINGLE_TASK = "To remove a single task, provide the input in this "
            + "format instead: remove <pageContent> / <type> <one index in integer form>.";
    private static final String INDEX_SEPARATION = "Ensure that the indexes are separated by ',' without any spacing. "
            + "E.g. remove-* <pageContent> / <type> 2,3,4";
    private static final String INVALID_REMOVE_FORMAT = "Please use valid multiple removal format:\n"
            + "remove-* <pageContent> : <type> <index>";
    private static final String REMOVED_MULTIPLE_FILES = "Noted. I've removed these files:";
    private static final String REMOVED_MULTIPLE_TASKS = "Noted. I've removed these tasks:";

    private String type;

    private String moduleCode;
    private String content;
    private String outputMessage = "";

    /**
     * Constructor for initialization of variables to support removal of entities.
     * @param pageDataComponents page data components.
     * @param content A string containing the content of the processed user input.
     */
    public RemoveMultipleCommand(String[] pageDataComponents, String content) {
        if (pageDataComponents.length > 1) {
            this.moduleCode = pageDataComponents[1];
        }
        this.content = content;
        this.type = content.split(" ")[0].toLowerCase();
    }

    /**
     * Method to remove multiple files from the file list.
     * @param finalIndexes Indexes of the files to be removed.
     * @param files The list of files in the storage.
     * @param outputMessage The message to be output to the screen.
     * @return outputMessage to be printed to the screen.
     * @throws InvalidIndexException If an invalid index is accessed.
     * @throws DataReadWriteException If there is an error reading/writing to the file.
     */
    private String removeMultipleFile(List<Integer> finalIndexes, FileList files, String outputMessage)
            throws InvalidIndexException, DataReadWriteException {
        for (int i = 0; i < finalIndexes.size(); i++) {
            File fileRemoved = files.remove(finalIndexes.get(i));
            if (i == 0) {
                outputMessage = outputMessage.concat(HORIZONTAL_LINE + "\n" + REMOVED_MULTIPLE_FILES + "\n");
            }
            outputMessage = outputMessage.concat(fileRemoved.toString() + "\n");
        }
        return outputMessage;
    }

    /**
     * Method to remove multiple tasks from the task list.
     * @param finalIndexes Indexes of the tasks to be removed.
     * @param tasks The list of tasks in the storage.
     * @param outputMessage The message to be output to the screen.
     * @return outputMessage to be printed to the screen.
     * @throws InvalidIndexException If an invalid index is accessed.
     * @throws DataReadWriteException If there is an error reading/writing to the file.
     */
    private String removeMultipleTask(List<Integer> finalIndexes, TaskList tasks, String outputMessage)
            throws InvalidIndexException, DataReadWriteException {
        for (int i = 0; i < finalIndexes.size(); i++) {
            Task taskRemoved = tasks.remove(finalIndexes.get(i));
            if (i == 0) {
                outputMessage = outputMessage.concat(HORIZONTAL_LINE + "\n" + REMOVED_MULTIPLE_TASKS + "\n");
            }
            outputMessage = outputMessage.concat(taskRemoved.toString() + "\n");
        }
        return outputMessage;
    }

    @Override
    public String execute(ModuleContainer moduleContainer, ArrayDeque<String> pageTrace, Ui ui, boolean guiMode) throws
            SpinBoxException {
        int inputSize = content.split(" ").length;

        if (inputSize > 2) {
            throw new InputException(INDEX_SEPARATION);
        }
        try {
            String[] splitIndexes = content.replace(type.concat(" "), "").split(",");
            if ((type.equals("file") || type.equals("note") || type.equals("task")) && (splitIndexes.length == 1)
                && splitIndexes[0].matches("\\d+")) {
                throw new InputException(REMOVE_SINGLE_TASK);
            } else if ((type.equals("file") || type.equals("note") || type.equals("task"))
                    && (splitIndexes.length == 1)) {
                throw new InputException(PROVIDE_INDEX);
            } else if (!type.equals("file") && !type.equals("note") && !type.equals("task")) {
                throw new InputException(INVALID_REMOVE_FORMAT);
            }
            List<Integer> finalIndexes = new ArrayList<>();
            for (String convert : splitIndexes) {
                finalIndexes.add(Integer.parseInt(convert) - 1);
            }
            finalIndexes.sort(Collections.reverseOrder());
            switch (type) {
            case "file":
                checkIfOnModulePage(moduleCode);
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    FileList files = module.getFiles();
                    if (inputSize == 1) {
                        throw new InputException(PROVIDE_INDEX);
                    }
                    outputMessage = removeMultipleFile(finalIndexes, files, outputMessage).concat(
                            "You currently have " + files.getList().size()
                            + ((files.getList().size() == 1) ? " file in the list." : " files in the list.") + "\n"
                                    + HORIZONTAL_LINE);
                    return outputMessage;
                } else {
                    return NON_EXISTENT_MODULE;
                }

            case "note":
                checkIfOnModulePage(moduleCode);
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    Notepad notepad = module.getNotepad();
                    if (inputSize == 1) {
                        throw new InputException(PROVIDE_INDEX);
                    }
                    for (Integer finalIndex : finalIndexes) {
                        notepad.removeLine(finalIndex);
                    }
                    return NOTES_REMOVED + moduleCode;
                } else {
                    return NON_EXISTENT_MODULE;
                }

            case "task":
                checkIfOnModulePage(moduleCode);
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    TaskList tasks = module.getTasks();
                    if (inputSize == 1) {
                        throw new InputException(PROVIDE_INDEX);
                    }
                    outputMessage = removeMultipleTask(finalIndexes, tasks, outputMessage).concat(
                            "You currently have " + tasks.getList().size()
                            + ((tasks.getList().size() == 1) ? " task in the list." : " tasks in the list.") + "\n"
                                    + HORIZONTAL_LINE);
                    return outputMessage;
                } else {
                    return NON_EXISTENT_MODULE;
                }

            default:
                throw new InputException(INVALID_REMOVE_FORMAT);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new InputException(INVALID_INDEX);
        }
    }
}