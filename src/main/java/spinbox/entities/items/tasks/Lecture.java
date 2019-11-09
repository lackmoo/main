package spinbox.entities.items.tasks;

import spinbox.DateTime;
import spinbox.exceptions.ScheduleDateException;

public class Lecture extends Schedulable {
    /**
     * Constructor for SpinBox.Tasks.Lecture object.
     * @param description name of the lecture.
     * @param startDate Date object for start DateTime.
     * @param endDate Date object for end DateTime.
     */
    public Lecture(String description, DateTime startDate, DateTime endDate) throws ScheduleDateException {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
        checkValidEndDate();
        taskType = TaskType.LECTURE;
    }

    /**
     * This constructor is used for recreation of SpinBox.Tasks.Lecture from storage.
     * @param done  1 if task has been marked complete, 0 otherwise.
     * @param description the name or description of the lecture.
     * @param startDate Date object for start DateTime.
     * @param endDate Date object for end DateTime.
     */
    public Lecture(int done, String description, DateTime startDate, DateTime endDate) throws ScheduleDateException {
        super(description);
        this.updateDone(done == 1);
        this.startDate = startDate;
        this.endDate = endDate;
        checkValidEndDate();
        taskType = TaskType.LECTURE;
    }

    public Lecture() {
        taskType = TaskType.LECTURE;
    }

    @Override
    public String storeString() {
        return "LEC | " + super.storeString() + " | " + this.getStartDateString() + " | " + this.getEndDateString();
    }

    @Override
    String getStartDateString() {
        return this.startDate.toString();
    }

    @Override
    String getEndDateString() {
        return this.endDate.toString();
    }

    @Override
    public String toString() {
        return "[LEC]" + super.toString() + " (at: " + this.getStartDateString() + " to " + this.getEndDateString()
                + ")";
    }

    /**
     * Check if date given is within event period.
     * @param inputDate the date to be compared.
     * @return true if within, false if not.
     */
    @Override
    public boolean compareEquals(DateTime inputDate) {
        boolean isAfterStartDate = (this.startDate.compareTo(inputDate) <= 0);
        boolean isBeforeEndDate = (this.endDate.compareTo(inputDate) >= 0);

        return (isAfterStartDate && isBeforeEndDate);
    }

    @Override
    public Boolean isOverlapping(DateTime startTime, DateTime endTime) {
        return startTime.before(endDate) && startDate.before(endTime);
    }
}