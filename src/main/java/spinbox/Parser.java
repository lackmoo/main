package spinbox;

import spinbox.commands.AddCommand;
import spinbox.commands.Command;
import spinbox.commands.ExitCommand;
import spinbox.commands.FindCommand;
import spinbox.commands.HelpCommand;
import spinbox.commands.RemoveCommand;
import spinbox.commands.RemoveMultipleCommand;
import spinbox.commands.SetDateCommand;
import spinbox.commands.ScoreCommand;
import spinbox.commands.SetNameCommand;
import spinbox.commands.UpdateCommand;
import spinbox.commands.UpdateMultipleCommand;
import spinbox.commands.ViewCommand;
import spinbox.commands.PopulateCommand;
import spinbox.commands.ExportCommand;
import spinbox.exceptions.SpinBoxException;
import spinbox.exceptions.InputException;

import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final String INVALID_COMMAND = "Please provide a valid command:\n"
            + "'<action> <page> / <content>' or 'bye'";
    private static final int PAGEDATA_COMPONENT_MAX = 10;

    private static ArrayDeque<String> pageTrace;
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /**
     * Static function to update the current page trace to determine context.
     * @param pageTraceNew An arraydeque of strings to overwrite the page trace with.
     */
    public static void setPageTrace(ArrayDeque<String> pageTraceNew) {
        LOGGER.setUseParentHandlers(true);
        LOGGER.setLevel(Level.WARNING);
        LOGGER.info("Page trace set");
        for (String string : pageTraceNew) {
            LOGGER.fine(string);
        }
        pageTrace = pageTraceNew;
    }

    /**
     * Appends the page or page and "modules" from pageTrace.
     * @param pageData The original empty page data.
     * @param tempPageTrace The cloned version of the original ArrayDeque pageTrace.
     * @param lastElement The last element of the ArrayDeque pageTrace.
     * @return The full page data.
     */
    private static String emptyPageComponentAppender(String pageData, ArrayDeque<String> tempPageTrace,
                                                     String lastElement) {
        String fullPageData;
        fullPageData = pageData.concat(lastElement);
        tempPageTrace.removeLast();
        if (tempPageTrace.size() != 0) {
            String lastComponent = tempPageTrace.getLast();
            String finalFullPageData;
            finalFullPageData = fullPageData.concat(" " + lastComponent.toUpperCase());
            LOGGER.info("Exiting static method emptyPageComponentAppender with pageData: " + finalFullPageData);
            return finalFullPageData;
        } else {
            LOGGER.info("Exiting static method emptyPageComponentAppender with pageData: " + fullPageData);
            return fullPageData;
        }
    }

    /**
     * Appends page from input and maybe module code from pageTrace to the full page data.
     * @param pageData The original empty page data.
     * @param firstPageComponent The first component of the split input page data.
     * @param tempPageTrace The cloned version of the original ArrayDeque pageTrace.
     * @return The full page data.
     */
    private static String singlePageComponentAppender(String pageData, String firstPageComponent,
                                                      ArrayDeque<String> tempPageTrace) {
        String fullPageData;
        switch (firstPageComponent) {
        case "main":
        case "calendar":
            fullPageData = pageData.concat(firstPageComponent);
            break;
        case "modules":
            fullPageData = emptyPageComponentAppender(pageData, tempPageTrace, firstPageComponent);
            break;
        default:
            // pageData is a module code
            fullPageData = pageData.concat("modules " + firstPageComponent.toUpperCase());
        }
        LOGGER.info("Exiting static method singlePageComponentAppender with pageData: " + fullPageData);
        return fullPageData;
    }

    /**
     * Appends both "modules" and the module code to form the full page data.
     * @param firstPageComponent The first component of the split input page data.
     * @param secondPageComponent The second component of the split input page data.
     * @return The full page data.
     * @throws InputException If the input is an invalid command.
     */
    private static String fullPageComponentAppender(String pageData, String firstPageComponent,
                                                    String secondPageComponent) throws InputException {
        String fullPageData;
        if (firstPageComponent.equals("modules")) {
            fullPageData = pageData.concat("modules " + secondPageComponent.toUpperCase());
        } else {
            LOGGER.warning("Invalid command entered, error propagated upwards.");
            throw new InputException(INVALID_COMMAND);
        }
        LOGGER.info("Exiting static method fullPageComponentAppender with pageData: " + fullPageData);
        return fullPageData;
    }

    /**
     * Builds the required page data for command input. Return page and maybe moduleCode.
     * @param inputPageData The page data input.
     * @return The full page data.
     * @throws InputException If the input is invalid.
     */
    private static String commandBuilder(String inputPageData) throws InputException {
        LOGGER.info("Entering static method commandBuilder");
        String pageData = "";
        String[] pageComponent = inputPageData.split(" ");
        String firstPageComponent = pageComponent[0];
        ArrayDeque<String> tempPageTrace = pageTrace.clone();

        if (firstPageComponent.isEmpty()) {
            pageData = emptyPageComponentAppender(pageData, tempPageTrace, tempPageTrace.getLast());
        } else if (pageComponent.length == 1) {
            pageData = singlePageComponentAppender(pageData, firstPageComponent, tempPageTrace);
        } else if (pageComponent.length == 2) {
            String secondPageComponent = pageComponent[1];
            pageData = fullPageComponentAppender(pageData, firstPageComponent, secondPageComponent);
        } else {
            LOGGER.warning("Invalid command entered, error propagated upwards.");
            throw new InputException(INVALID_COMMAND);
        }
        LOGGER.info("Exiting static method commandBuilder with pageData: " + pageData);
        return pageData;
    }

    /**
     * Decides the action of the user based on the first word in the user input.
     * @param actionInputTrimmed The trimmed first word of the user input.
     * @return The intended action of the user.
     * @throws InputException If the input is invalid.
     */
    private static String actionDecider(String actionInputTrimmed) throws InputException {
        String action;
        if (actionInputTrimmed.equals("bye")) {
            action = "bye";
        } else if (actionInputTrimmed.equals("help")) {
            action = "help";
        } else if (actionInputTrimmed.equals("populate")) {
            action = "populate";
        } else {
            LOGGER.warning("Invalid command entered, error propagated upwards.");
            throw new InputException(INVALID_COMMAND);
        }
        return action;
    }

    /**
     * Parses an input string into a workable command.
     * @param input The user input.
     * @return A full command that can be executed.
     * @throws SpinBoxException If there are storage errors or input errors.
     */
    public static Command parse(String input) throws SpinBoxException {
        LOGGER.info("Parsing input into command: " + input);
        Command command;
        String action;
        String content;
        String pageData;
        String[] pageDataComponents = new String[PAGEDATA_COMPONENT_MAX];
        String[] slashSeparate = input.split(" / ", 2);

        try {
            String actionInput = slashSeparate[0].toLowerCase();
            String actionInputTrimmed = actionInput.trim();
            if (slashSeparate.length == 1) {
                action = actionDecider(actionInputTrimmed);
                content = "";
            } else if (actionInputTrimmed.equals("help") && slashSeparate.length == 2) {
                action = actionDecider(actionInputTrimmed);
                content = slashSeparate[1].trim();
            } else {
                content = slashSeparate[1].trim();
                String[] frontComponents = slashSeparate[0].split(" ");
                action = frontComponents[0].toLowerCase();
                pageData = slashSeparate[0].replace(action, "");
                String pageDataTrimmed = pageData.trim();
                String fullPageData = commandBuilder(pageDataTrimmed.toLowerCase());
                pageDataComponents = fullPageData.split(" ");
            }
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            LOGGER.warning("Invalid command entered, error propagated upwards.");
            throw new InputException(INVALID_COMMAND);
        }

        LOGGER.info("Input: " + input + " associated with action: " + action.toLowerCase());
        switch (action) {
        case "bye":
            command = new ExitCommand();
            break;
        case "populate":
            command = new PopulateCommand();
            break;
        case "view":
            command = new ViewCommand(pageDataComponents, content);
            break;
        case "add":
            command = new AddCommand(pageDataComponents, content);
            break;
        case "remove":
            command = new RemoveCommand(pageDataComponents, content);
            break;
        case "update":
            command = new UpdateCommand(pageDataComponents, content);
            break;
        case "export":
            command = new ExportCommand(pageDataComponents, content);
            break;
        case "remove-*":
            command = new RemoveMultipleCommand(pageDataComponents, content);
            break;
        case "update-*":
            command = new UpdateMultipleCommand(pageDataComponents, content);
            break;
        case "score":
            command = new ScoreCommand(pageDataComponents, content);
            break;
        case "find":
            command = new FindCommand(pageDataComponents, content);
            break;
        case "set-date":
            command = new SetDateCommand(pageDataComponents, content);
            break;
        case "set-name":
            command = new SetNameCommand(pageDataComponents, content);
            break;
        case "help":
            command = new HelpCommand(content);
            break;
        default:
            LOGGER.warning("Invalid command entered, error propagated upwards.");
            throw new InputException(INVALID_COMMAND);
        }
        return command;
    }
}