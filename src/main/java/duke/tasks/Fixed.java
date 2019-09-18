package duke.tasks;

public class Fixed extends Task {
    private String fixedDuration;

    public Fixed(String taskName, String fixedDuration) {
        super(taskName);
        this.fixedDuration = fixedDuration;
    }

    public Fixed(int done, String taskName, String fixedDuration) {
        super(taskName);
        this.isDone = (done == 1);
        this.fixedDuration = fixedDuration;
    }

    @Override
    public String toString() {
        return "[F]" + super.toString() + " (needs: " + getFixedDuration() + ")";
    }

    @Override
    public String storeString() {
        return "F | " + super.storeString() + " | " + getFixedDuration();
    }

    public String getFixedDuration() {
        return this.fixedDuration;
    }
}