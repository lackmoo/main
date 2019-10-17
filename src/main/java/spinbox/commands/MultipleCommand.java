package spinbox.commands;

import spinbox.containers.ModuleContainer;
import spinbox.containers.lists.FileList;
import spinbox.entities.Notepad;
import spinbox.entities.items.File;
import spinbox.entities.Module;
import spinbox.exceptions.SpinBoxException;
import spinbox.exceptions.InputException;
import spinbox.Storage;
import spinbox.entities.items.tasks.Task;
import spinbox.Ui;
import spinbox.containers.lists.TaskList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultipleCommand extends Command {
    private static final String NON_EXISTENT_MODULE = "This module does not exist.";
    private static final String NOTES_REMOVED = "The specified notes have been successfully removed from ";
    private static final String PROVIDE_INDEX = "Please provide an index to be removed.";
    private String type;

    private String moduleCode;
    private String content;

    /**
     * Constructor for initialization of variables to support removal of entities.
     * @param moduleCode A String denoting the module code.
     * @param content A string containing the content of the processed user input.
     */
    public MultipleCommand(String moduleCode, String content) {
        this.moduleCode = moduleCode;
        this.content = content;
        this.type = content.split(" ")[0];
    }

    @Override
    public String execute(ModuleContainer moduleContainer, ArrayDeque<String> pageTrace, Ui ui) throws
            SpinBoxException {
        int inputSize = content.split(" ").length;
        if (inputSize == 1) {
            throw new InputException(PROVIDE_INDEX);
        }
        if (inputSize > 2) {
            throw new InputException("Ensure that the indexes are separated by ',' without any spacing. "
                    + "E.g. remove-multiple <pageContent> : <type> 2,3,4");
        }
        try {
            String[] splitIndexes = content.replace(type.concat(" "), "").split(",");
            if (splitIndexes.length == 1) {
                throw new InputException("To remove a single task, provide the input in this "
                        + "format instead: remove <pageContent> : <type> <one index in integer form>.");
            }
            List<Integer> finalIndexes = new ArrayList<>();
            for (String convert : splitIndexes) {
                finalIndexes.add(Integer.parseInt(convert) - 1);
            }
            finalIndexes.sort(Collections.reverseOrder());
            StringBuilder outputMessage = new StringBuilder();
            switch (type) {
            case "file":
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    FileList files = module.getFiles();
                    for (int i = 0; i < finalIndexes.size(); i++) {
                        File fileRemoved = files.remove(finalIndexes.get(i));
                        if (i == 0) {
                            outputMessage.append("Noted. I've removed these tasks:\n");
                            outputMessage.append(fileRemoved.toString()).append("\n");
                        } else {
                            outputMessage.append(fileRemoved.toString()).append("\n");
                        }
                    }
                    outputMessage.append("You currently have ").append(files.getList().size()).append((
                            files.getList().size() == 1) ? " file in the list." : " files in the list.");
                    return outputMessage.toString();
                } else {
                    return NON_EXISTENT_MODULE;
                }

            case "note":
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    Notepad notepad = module.getNotepad();
                    for (Integer finalIndex : finalIndexes) {
                        notepad.removeLine(finalIndex);
                    }
                    return NOTES_REMOVED + moduleCode;
                } else {
                    return NON_EXISTENT_MODULE;
                }

            case "task":
                if (moduleContainer.checkModuleExists(moduleCode)) {
                    HashMap<String, Module> modules = moduleContainer.getModules();
                    Module module = modules.get(moduleCode);
                    TaskList tasks = module.getTasks();
                    for (int i = 0; i < finalIndexes.size(); i++) {
                        Task taskRemoved = tasks.remove(finalIndexes.get(i));
                        if (i == 0) {
                            outputMessage.append("Noted. I've removed these tasks:\n");
                            outputMessage.append(taskRemoved.toString()).append("\n");
                        } else {
                            outputMessage.append(taskRemoved.toString()).append("\n");
                        }
                    }
                    outputMessage.append("You currently have ").append(tasks.getList().size()).append((
                            tasks.getList().size() == 1) ? " task in the list." : " tasks in the list.");
                    return outputMessage.toString();
                } else {
                    return NON_EXISTENT_MODULE;
                }

            default:
                throw new InputException("Please use valid remove format:\n"
                        + "remove <pageContent> : <type> <index>");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new InputException("Invalid index entered.");
        } catch (NumberFormatException e) {
            throw new InputException("Invalid index entered. Please ensure the index is in integer form.");
        }
    }
}