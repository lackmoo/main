package spinbox.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.stage.Screen;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import javafx.util.Pair;
import spinbox.DateTime;
import spinbox.SpinBox;
import spinbox.containers.ModuleContainer;
import spinbox.containers.lists.FileList;
import spinbox.containers.lists.GradeList;
import spinbox.containers.lists.TaskList;
import spinbox.entities.Module;
import spinbox.entities.items.File;
import spinbox.entities.items.GradedComponent;
import spinbox.entities.items.tasks.Exam;
import spinbox.entities.items.tasks.Schedulable;
import spinbox.entities.items.tasks.Task;
import spinbox.entities.items.tasks.TaskType;
import spinbox.exceptions.DataReadWriteException;
import spinbox.exceptions.SpinBoxException;
import spinbox.exceptions.CalendarSelectorException;
import spinbox.exceptions.InvalidIndexException;
import spinbox.exceptions.FileCreationException;
import spinbox.gui.boxes.FileBox;
import spinbox.gui.boxes.GradedComponentBox;
import spinbox.gui.boxes.ModuleBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends GridPane {
    private static final String WHITESPACE = "    ";
    private static final String TASKS = "Tasks";
    private static final String FILES = "Files";
    private static final String GRADES = "Grades";
    private static final String HELP_POPUP = "Welcome to the help page.";
    private static final String HELP_PAGE_POPUP = "Example:";
    private static final String NO_DATA = "We notice you have no existing data."
            + " Type \"populate\" into this input box to load sample data.";

    @FXML
    private TabPane tabPane;
    @FXML
    private VBox overallTasksView;
    @FXML
    private TextField userInput;
    @FXML
    private Button submitButton;
    @FXML
    private GridPane modulesTabContainer;
    @FXML
    private StackPane calendarView;
    @FXML
    private VBox examsList;

    private SpinBox spinBox;
    private String specificModuleCode;
    private String subTab;
    private Popup popup = new Popup();
    private ArrayList<String> commandHistory = new ArrayList<>();
    private int commandCount = 0;
    private List<Pair<String, Task>> allTasks;

    /**
     * FXML method that is used as a post-constructor function to initialize variables and tabbed views.
     */
    @FXML
    public void initialize()  {
        this.spinBox = new SpinBox();
        this.specificModuleCode = null;
        this.subTab = null;

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                switch (newValue.intValue()) {
                case 0:
                    try {
                        updateMain();
                    } catch (SpinBoxException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        updateCalendar();
                    } catch (CalendarSelectorException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    updateModules();
                }
            }
        });
    }

    /**
     * Uses a list of strings and a listener to cycle through user commands using the keyboard.
     */
    private void enableCommandHistory() {
        userInput.setOnKeyPressed(event -> {
            switch (event.getCode()) {
            case UP:
                if (commandCount < commandHistory.size()) {
                    userInput.setText(commandHistory.get(commandCount));
                    commandCount += 1;
                }
                userInput.end();
                break;
            case DOWN:
                commandCount -= 1;
                if (commandCount > 0) {
                    commandCount -= 1;
                    userInput.setText(commandHistory.get(commandCount));
                } else {
                    userInput.clear();
                    commandCount = 0;
                }
                userInput.end();
                break;
            default:
                break;
            }
        });
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing SpinBox's reply and then appends
     * them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput()
            throws InvalidIndexException, DataReadWriteException, FileCreationException, CalendarSelectorException {
        commandHistory.add(0, userInput.getText());
        commandCount = 0;
        String input = userInput.getText();
        String response = spinBox.getResponse(input, true);
        String[] responseFragments = response.split("/");

        String comparator;

        if (responseFragments.length > 1) {
            comparator = responseFragments[1];
        } else {
            comparator = response;
        }

        switch (comparator) {
        case "main":
            tabPane.getSelectionModel().select(0);
            break;
        case "calendar":
            tabPane.getSelectionModel().select(1);
            break;
        case "modules":
            tabPane.getSelectionModel().select(2);

            if (responseFragments.length == 4) {
                this.specificModuleCode = responseFragments[2];
                this.subTab = responseFragments[3].split(" ")[0];
            } else {
                this.specificModuleCode = null;
                this.subTab = null;
            }
            updateModules();
            break;
        default:
            updateAll();
            getPopup(response);
            break;
        }
        userInput.clear();
        suggestPopulate();
        if (spinBox.isShutdown()) {
            System.exit(0);
        }
    }

    /**
     * Initializes the contents of the Main tab, which is the default upon startup.
     * @throws DataReadWriteException should be displayed.
     * @throws FileCreationException should be displayed.
     * @throws InvalidIndexException should be displayed.
     */
    public void initializeGui() throws DataReadWriteException, FileCreationException, InvalidIndexException {
        this.setPopup(popup);
        this.suggestPopulate();
        this.updateMain();
        this.enableCommandHistory();
    }

    private void suggestPopulate() {
        if (spinBox.getModuleContainer().getModules().isEmpty()) {
            userInput.setPromptText(NO_DATA);
            userInput.setStyle("-fx-prompt-text-fill: #FF0000; -fx-font-weight: BOLD");
        } else {
            userInput.setPromptText("");
            userInput.setStyle("-fx-font-weight: normal");
        }
    }

    private void updateAll()
            throws DataReadWriteException, FileCreationException, InvalidIndexException, CalendarSelectorException {
        updateMain();
        updateModules();
        updateCalendar();
    }

    private void updateMain() throws InvalidIndexException, DataReadWriteException, FileCreationException {
        updateOverallTasksView();
        updateExams();
    }

    private void updateModules() {
        modulesTabContainer.getChildren().clear();

        if (this.specificModuleCode != null && this.subTab != null) {
            updateSpecificModule(this.specificModuleCode, this.subTab);
        } else {
            updateModulesList();
        }
    }

    private void updateOverallTasksView() throws DataReadWriteException, InvalidIndexException, FileCreationException {

        allTasks = new ArrayList<>();
        overallTasksView.getChildren().clear();
        overallTasksView.getChildren().add(addHeader("URGENT TASK"));
        ModuleContainer moduleContainer = spinBox.getModuleContainer();
        HashMap<String, Module> modules = moduleContainer.getModules();
        for (Map.Entry module : modules.entrySet()) {
            String moduleCode = (String) module.getKey();
            Module moduleObject = (Module) module.getValue();
            TaskList tasks = moduleObject.getTasks();
            for (Task task : tasks.getList()) {
                if (!task.getDone()) {
                    allTasks.add(new Pair<>(moduleCode, task));
                }
            }
        }

        allTasks.sort((o1, o2) -> {
            Task a = o1.getValue();
            Task b = o2.getValue();

            DateTime startDateA = null;
            DateTime startDateB = null;

            if (!a.getDone() && b.getDone()) {
                return -1;
            } else if (a.getDone() && !b.getDone()) {
                return 1;
            }

            if (a.isSchedulable()) {
                startDateA = ((Schedulable) a).getStartDate();
            }
            if (b.isSchedulable()) {
                startDateB = ((Schedulable) b).getStartDate();
            }

            if (startDateA == null && startDateB == null) {
                return a.getName().compareToIgnoreCase(b.getName());
            } else if (startDateA == null) {
                return 1;
            } else if (startDateB == null) {
                return -1;
            } else {
                return startDateA.compareTo(startDateB);
            }
        });

        int boxes;
        if (allTasks.size() < 5) {
            boxes = allTasks.size();
        }  else {
            boxes = 5;
        }

        int count = 0;
        int index = 0;
        while (count < boxes && index < allTasks.size()) {
            Task addTask = allTasks.get(index).getValue();
            if (addTask.getTaskType() != TaskType.EXAM) {
                String moduleCode = allTasks.get(index).getKey();
                String description = addTask.getTaskType().name();
                description += ": " + addTask.getName();
                String dates = "";
                if (addTask.isSchedulable()) {
                    Schedulable task = ((Schedulable)addTask);
                    dates += task.getStartDate().toString();
                    if (TaskType.taskWithBothDates().contains(task.getTaskType())) {
                        dates += " " + task.getEndDate().toString();
                        dates = "At: " + dates;
                    } else {
                        dates = "By: " + dates;
                    }
                }
                overallTasksView.getChildren().add(TaskBox.getTaskBox(description, moduleCode, dates));
                count += 1;
            }
            index += 1;
        }
    }

    private void updateExams() {
        examsList.getChildren().clear();
        examsList.getChildren().add(addHeader("EXAM"));
        for (Pair item : allTasks) {
            Task addTask = (Task) item.getValue();
            if (addTask.getTaskType() == TaskType.EXAM) {
                String description = addTask.getTaskType().name();
                description += ": " + addTask.getName();
                String dates = "";
                Exam task = ((Exam)addTask);
                dates += task.getStartDate().toString();
                dates += " " + task.getEndDate().toString();
                dates = "At: " + dates;
                String moduleCode = (String) item.getKey();
                examsList.getChildren().add(TaskBox.getTaskBox(description, moduleCode, dates));
            }
        }
    }

    private HBox addHeader(String label) {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 0, 0, 0));
        header.setAlignment(Pos.BOTTOM_CENTER);
        Label headerText = new Label(label);
        headerText.setStyle("-fx-font-size:20px");
        headerText.setTextFill(Color.WHITE);
        header.getChildren().add(headerText);
        return header;
    }

    private void updateModulesList() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        modulesTabContainer.add(scrollPane, 1, 0, 1, 2);

        VBox modulesList = new VBox();
        modulesList.setStyle("-fx-background-color: #25274D");

        scrollPane.setContent(modulesList);

        ModuleContainer moduleContainer = spinBox.getModuleContainer();
        HashMap<String, Module> modules = moduleContainer.getModules();

        for (Map.Entry module : modules.entrySet()) {
            Module currentModule = (Module) module.getValue();
            ModuleBox wrappedModule = ModuleBox.getModuleListBox(currentModule.getModuleCode(),
                    currentModule.getModuleName());

            wrappedModule.setOnMouseClicked(event -> {
                userInput.setText("view / modules " + currentModule.getModuleCode() + " tasks");
                try {
                    handleUserInput();
                } catch (SpinBoxException e) {
                    e.printStackTrace();
                }
            });
            modulesList.getChildren().add(wrappedModule);
        }
    }

    private void updateSpecificModule(String moduleCode, String subTab) {
        ModuleContainer moduleContainer = spinBox.getModuleContainer();
        Module currentModule = moduleContainer.getModule(moduleCode);

        updateSpecificModuleHeader(currentModule, subTab);
        updateSpecificModuleNotes(currentModule);
        updateSpecificModuleList(currentModule, subTab);
    }

    private void updateSpecificModuleHeader(Module currentModule, String subTabName) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(10, 10, 10, 10));
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setLineSpacing(5.0);
        textFlow.setStyle("-fx-background-color: #464866");

        Text modCode = new Text(currentModule.getModuleCode());
        modCode.setFont(new Font("Roboto", 18.0));
        modCode.setFill(Color.WHITE);
        modCode.setStyle("-fx-font-weight: bold");
        textFlow.getChildren().add(modCode);

        textFlow.getChildren().add(new Text(WHITESPACE));

        Text modName = new Text(currentModule.getModuleName());
        modName.setFont(new Font("Roboto", 14.0));
        modName.setFill(Color.WHITE);
        textFlow.getChildren().add(modName);

        textFlow.getChildren().add(new Text(System.lineSeparator()));

        Text taskSubHeader = new Text((TASKS));
        Text fileSubHeader = new Text((FILES));
        Text gradeSubHeader = new Text((GRADES));

        taskSubHeader.setFill(Color.WHITE);
        fileSubHeader.setFill(Color.WHITE);
        gradeSubHeader.setFill(Color.WHITE);

        switch (subTabName) {
        case "grades":
            gradeSubHeader.setStyle("-fx-font-weight: bold");
            gradeSubHeader.setFill(Color.AQUA);
            break;

        case "files":
            fileSubHeader.setStyle("-fx-font-weight: bold");
            fileSubHeader.setFill(Color.AQUA);
            break;

        default:
            taskSubHeader.setStyle("-fx-font-weight: bold");
            taskSubHeader.setFill(Color.AQUA);
        }

        textFlow.getChildren().add(taskSubHeader);
        textFlow.getChildren().add(new Text(WHITESPACE));
        textFlow.getChildren().add(fileSubHeader);
        textFlow.getChildren().add(new Text(WHITESPACE));
        textFlow.getChildren().add(gradeSubHeader);

        modulesTabContainer.add(textFlow, 0, 0,  2, 1);
    }

    private void updateSpecificModuleNotes(Module currentModule) {
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #AAABB8");
        textFlow.setPadding(new Insets(5, 5, 5, 15));
        textFlow.setLineSpacing(5.0);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);

        Text header = new Text(currentModule.getModuleCode() + " Notes");
        header.setStyle("-fx-font-weight: bold; -fx-font-family: 'Roboto Light'; -fx-font-size: 20.0;");
        textFlow.getChildren().add(header);
        textFlow.getChildren().add(new Text(System.lineSeparator()));


        List<String> notes = currentModule.getNotepad().getNotes();
        for (int i = 0; i < notes.size(); i++) {
            textFlow.getChildren().add(new Text(Integer.toString(i + 1) + ". " + notes.get(i)));
            textFlow.getChildren().add(new Text(System.lineSeparator()));
        }

        modulesTabContainer.add(textFlow, 0, 1, 1, 1);
    }

    private void updateSpecificModuleList(Module currentModule, String subTab) {
        switch (subTab) {
        case "files":
            updateSpecificModuleFileList(currentModule);
            break;

        case "grades":
            updateSpecificModuleGradeList(currentModule);
            break;

        default:
            updateSpecificModuleTaskList(currentModule);
        }
    }

    private void updateSpecificModuleGradeList(Module currModule) {
        GradeList gradeList = currModule.getGrades();
        List<GradedComponent> gradedComponents = gradeList.getList();

        ScrollPane scrollPane = this.createScrollPane();
        modulesTabContainer.add(scrollPane, 1, 1, 1, 1);

        VBox gradesList = new VBox();
        for (int i = 0; i < gradedComponents.size(); i += 1) {
            GradedComponent gradedComponent = gradedComponents.get(i);
            GradedComponentBox wrappedGradedComponent = GradedComponentBox.getGradedComponentsBox(gradedComponent,
                    (i + 1));
            gradesList.getChildren().add(wrappedGradedComponent);
        }

        gradesList.setStyle("-fx-background-color: #25274D");
        scrollPane.setContent(gradesList);
    }

    private void updateSpecificModuleTaskList(Module currModule) {
        ScrollPane scrollPane = this.createScrollPane();
        modulesTabContainer.add(scrollPane, 1, 1, 1, 1);

        VBox tasksList = new VBox();
        tasksList.setStyle("-fx-background-color: #25274D");

        scrollPane.setContent(tasksList);

        TaskList taskList = currModule.getTasks();
        List<Task> tasks = taskList.getList();
        for (int i = 0; i < tasks.size(); i += 1) {
            Task task = tasks.get(i);
            String doneStatus = (task.getDone()) ? "[DONE] " : "[NOT DONE] ";
            String description = (i + 1) + ". " + doneStatus + task.getTaskType().name();
            description += ": " + task.getName();
            String dates = "";
            if (task.isSchedulable()) {
                Schedulable schedulable = ((Schedulable) task);
                dates += schedulable.getStartDate().toString();
                if (TaskType.taskWithBothDates().contains(task.getTaskType())) {
                    dates += " " + schedulable.getEndDate().toString();
                    dates = "At: " + dates;
                } else {
                    dates = "By: " + dates;
                }
            }

            TaskBox wrappedTask = TaskBox.getTaskBox(description, "", dates);
            tasksList.getChildren().add(wrappedTask);
        }
    }

    private void updateSpecificModuleFileList(Module currModule) {
        ScrollPane scrollPane = this.createScrollPane();
        modulesTabContainer.add(scrollPane, 1, 1, 1, 1);

        VBox filesList = new VBox();
        filesList.setStyle("-fx-background-color: #25274D");

        scrollPane.setContent(filesList);

        FileList fileList = currModule.getFiles();
        List<File> files = fileList.getList();
        for (int i = 0; i < files.size(); i += 1) {
            File file = files.get(i);
            FileBox wrappedFile = FileBox.getFileBox(file, (i + 1));
            filesList.getChildren().add(wrappedFile);
        }
    }

    private void updateCalendar() throws CalendarSelectorException {
        CalendarMonthBox monthBox = new CalendarMonthBox("today", allTasks);
        calendarView.getChildren().clear();
        calendarView.getChildren().add(monthBox);
    }

    private void setPopup(Popup popup) {
        popup.setAutoHide(true);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
    }

    /**
     * Retrieve a popup to display text in a styled manner.
     * @param displayText the String to be displayed to the user.
     */
    private void getPopup(String displayText) {
        popup.getContent().clear();
        GridPane grid = new GridPane();
        Label response = new Label();
        response.setText(displayText);
        grid.setStyle("-fx-background-color:white;"
                + "-fx-border-color: black;"
                + "-fx-border-width:2;"
                + "-fx-border-radius:3;"
                + "-fx-hgap:3;"
                + "-fx-vgap:5;");
        grid.getChildren().add(response);
        popup.getContent().add(grid);
        Window window = tabPane.getScene().getWindow();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        if (displayText.contains(HELP_POPUP) || displayText.contains(HELP_PAGE_POPUP)) {
            popup.setX(primaryScreenBounds.getMinX());
            popup.setY(primaryScreenBounds.getMinY());
        } else {
            popup.setX(((primaryScreenBounds.getMaxX() + primaryScreenBounds.getMinX()) / 2) - 200);
            popup.setY(primaryScreenBounds.getMaxY());
        }
        popup.show(window);
    }

    private ScrollPane createScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}