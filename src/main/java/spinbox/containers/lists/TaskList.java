package spinbox.containers.lists;

import spinbox.DateTime;
import spinbox.storage.Storage;
import spinbox.exceptions.DataReadWriteException;
import spinbox.exceptions.FileCreationException;
import spinbox.entities.items.tasks.Deadline;
import spinbox.entities.items.tasks.Event;
import spinbox.entities.items.tasks.Exam;
import spinbox.entities.items.tasks.Lab;
import spinbox.entities.items.tasks.Lecture;
import spinbox.entities.items.tasks.Schedulable;
import spinbox.entities.items.tasks.Task;
import spinbox.entities.items.tasks.Todo;
import spinbox.entities.items.tasks.Tutorial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskList extends SpinBoxList<Task> {
    private static final String TASK_LIST_FILE_NAME = "/tasks.txt";
    private static final String DELIMITER_FILTER = " \\| ";

    public TaskList(String parentName) throws FileCreationException {
        super(parentName);
        localStorage = new Storage(DIRECTORY_NAME + this.getParentCode() + TASK_LIST_FILE_NAME);
    }

    public static class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task a, Task b) {
            DateTime startDateA = null;
            DateTime startDateB = null;

            if (!a.getDone() && b.getDone()) {
                return -1;
            } else if (a.getDone() && !b.getDone()) {
                return 1;
            }

            if (a.isSchedulable()) {
                startDateA = ((Schedulable)a).getStartDate();
            }
            if (b.isSchedulable()) {
                startDateB = ((Schedulable)b).getStartDate();
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
        }
    }

    public void sort() {
        list.sort(new TaskComparator());
    }

    @Override
    public void loadData() throws DataReadWriteException {
        List<String> savedData = localStorage.loadData();

        for (String datum : savedData) {
            String[] arguments = datum.split(DELIMITER_FILTER);
            switch (arguments[0]) {
            case "T":
                Todo todo = new Todo();
                todo.fromStoredString(datum);
                this.addFromStorage(todo);
                break;
            case "D":
                Deadline deadline = new Deadline();
                deadline.fromStoredString(datum);
                this.addFromStorage(deadline);
                break;
            case "E":
                Event event = new Event();
                event.fromStoredString(datum);
                this.addFromStorage(event);
                break;
            case "EXAM":
                Exam exam = new Exam();
                exam.fromStoredString(datum);
                this.addFromStorage(exam);
                break;
            case "LAB":
                Lab lab = new Lab();
                lab.fromStoredString(datum);
                this.addFromStorage(lab);
                break;
            case "LEC":
                Lecture lecture = new Lecture();
                lecture.fromStoredString(datum);
                this.addFromStorage(lecture);
                break;
            default:
                Tutorial tutorial = new Tutorial();
                tutorial.fromStoredString(datum);
                this.addFromStorage(tutorial);
            }
        }
    }

    @Override
    public void saveData() throws DataReadWriteException {
        List<String> dataToSave = new ArrayList<>();
        for (Task task: this.getList()) {
            dataToSave.add(task.storeString());
        }
        localStorage.saveData(dataToSave);
    }

    @Override
    public List<String> viewList() {
        List<String> output = new ArrayList<>();
        output.add("Here are the tasks in your module:");
        for (int i = 0; i < list.size(); i++) {
            output.add(((i + 1) + ". " + list.get(i).toString()));
        }
        return output;
    }

    @Override
    public List<String> containsKeyword(String keyword) {
        List<Task> contains = new ArrayList<>();
        for (Task task : this.getList()) {
            if (task.getName().toLowerCase().contains(keyword)) {
                contains.add(task);
            }
        }

        contains.sort(new TaskComparator());

        List<String> output = new ArrayList<>();
        output.add("Here are the tasks that contain " + keyword
                + " in your module:");
        for (int i = 0; i < contains.size(); i++) {
            output.add(((i + 1) + ". " + contains.get(i).toString()));
        }

        return output;
    }
}
