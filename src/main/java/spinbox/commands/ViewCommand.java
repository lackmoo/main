package spinbox.commands;

import spinbox.containers.ModuleContainer;
import spinbox.entities.Module;
import spinbox.Ui;
import spinbox.exceptions.InputException;
import spinbox.exceptions.SpinBoxException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewCommand extends Command {
    private static final String MISSING_PAGE_INPUT = "Please input the page you want to change to.";
    private static final String SPECIFY_PAGE = "Please specify module before tab.\n"
            + "E.g. 'view / <moduleCode> <tab>'";
    private static final String INCORRECT_VIEW_FORMAT = "Please input correct format for view command.";
    private static final String NON_EXISTENT_PAGE = "Sorry, that page does not exist."
            + " Please choose 'main', 'calendar', or 'modules'.";
    private static final String NON_EXISTENT_MODULE = "Sorry, that module or module tab does not exist. "
            + "These are the current "
            + "modules:";
    private static final String NON_EXISTENT_TAB = "Sorry, that tab does not exist."
            + " Please choose 'tasks', 'files', 'notes' or 'grades'.";
    private String page;
    private String moduleCode;
    private String tab;

    /**
     * Constructs by splitting the input and pageTrace and storing it in private variables.
     * @param pageDataComponents the page trace from parser.
     * @param content the content of input.
     * @throws InputException if invalid view command.
     */
    public ViewCommand(String[] pageDataComponents, String content) throws InputException {
        String[] contentComponents = content.toLowerCase().split(" ");

        if (contentComponents.length == 0) {
            throw new InputException(MISSING_PAGE_INPUT);
        // can be page, module, or tab
        } else if (contentComponents.length == 1) {
            switch (contentComponents[0]) {
            // content is page
            case "main":
                page = "main";
                break;
            case "calendar":
                page = "calendar";
                break;
            case "modules":
                page = "modules";
                break;
            // content is tab
            case "tasks":
            case "files":
            case "grades":
                // check if on a module page first
                try {
                    moduleCode = pageDataComponents[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new InputException(SPECIFY_PAGE);
                }
                page = "modules";
                moduleCode = pageDataComponents[1];
                tab = contentComponents[0];
                break;
            // content is module code
            default:
                page = "modules";
                moduleCode = contentComponents[0];
                tab = "tasks";
            }
        // can be 'modules <moduleCode>' or '<moduleCode> tab'
        } else if (contentComponents.length == 2) {
            if (contentComponents[0].equals("modules")) {
                page = "modules";
                moduleCode = contentComponents[1];
                tab = "tasks";
            } else if (contentComponents[1].equals("tasks") || contentComponents[1].equals("files")
                    || contentComponents[1].equals("grades")) {
                page = "modules";
                moduleCode = contentComponents[0];
                tab = contentComponents[1];
            } else {
                throw new InputException(INCORRECT_VIEW_FORMAT);
            }
        // modules <moduleCode> <tab>
        } else if (contentComponents.length == 3) {
            if (contentComponents[0].equals("modules")) {
                page = "modules";
                moduleCode = contentComponents[1];
                tab = contentComponents[2];
            }
        } else {
            throw new InputException(INCORRECT_VIEW_FORMAT);
        }

        if (moduleCode != null) {
            moduleCode = moduleCode.toUpperCase();
        }
    }

    /**
     * Replace pageTrace with the new pageTrace.
     * @param moduleContainer the modules stored.
     * @param pageTrace the current pageTrace.
     * @param ui the Ui instance.
     * @param guiMode boolean to check if in gui mode.
     * @return the display once been changed..
     * @throws SpinBoxException if page, module, or tab does not exist.
     */
    @Override
    public String execute(ModuleContainer moduleContainer, ArrayDeque<String> pageTrace, Ui ui, boolean guiMode)
            throws SpinBoxException {
        ArrayDeque<String> tempPageTrace = pageTrace.clone();
        StringBuilder oldTrace = new StringBuilder();
        while (tempPageTrace.size() > 0) {
            oldTrace.append("/").append(tempPageTrace.getLast());
            tempPageTrace.removeLast();
        }

        ArrayDeque<String> newPageTrace = new ArrayDeque<>();
        // add page
        if (page.equals("main") || page.equals("calendar") || page.equals("modules")) {
            newPageTrace.addFirst(page);
        } else {
            throw new InputException(NON_EXISTENT_PAGE);
        }

        // add module if exists
        if (page.equals("modules") && moduleCode != null) {
            // check if module exists
            if (moduleContainer.checkModuleExists(moduleCode)) {
                newPageTrace.addFirst(moduleCode);
            } else {
                String currentModules = "";
                for (HashMap.Entry<String, Module> entry : moduleContainer.getModules().entrySet()) {
                    currentModules = currentModules.concat(entry.getKey() + "\n");
                }
                throw new InputException(NON_EXISTENT_MODULE + "\n" + currentModules);
            }
        }

        List<String> outputList = new ArrayList<>();
        outputList.add("First line");
        // add tab
        if (page.equals("modules") && tab != null) {
            HashMap<String, Module> modules = moduleContainer.getModules();
            Module module = modules.get(moduleCode);
            switch (tab) {
            case "tasks":
                newPageTrace.addFirst(tab);
                outputList = module.getTasks().viewList();
                break;
            case "files":
                newPageTrace.addFirst(tab);
                outputList = module.getFiles().viewList();
                break;
            case "grades":
                newPageTrace.addFirst(tab);
                outputList = module.getGrades().viewList();
                break;
            case "notes":
                newPageTrace.addFirst(tab);
                outputList = module.getNotepad().viewList();
                break;
            default:
                throw new InputException(NON_EXISTENT_TAB);
            }
        }

        pageTrace.clear();

        StringBuilder newTrace = new StringBuilder();
        tempPageTrace = newPageTrace.clone();
        while (tempPageTrace.size() > 0) {
            newTrace.append("/").append(tempPageTrace.getLast());
            pageTrace.addFirst(tempPageTrace.getLast());
            tempPageTrace.removeLast();
        }

        if (guiMode) {
            outputList.set(0, newTrace.toString());
            return outputList.get(0);
        } else {
            outputList.set(0, "Changed from page "
                    + oldTrace.toString() + " to " + newTrace.toString());
        }

        return ui.showFormatted(outputList);
    }
}