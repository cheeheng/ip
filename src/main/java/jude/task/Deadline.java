package jude.task;

/**
 * A {@code Deadline} object is a Task object that has an associated deadline by which the task
 * should be completed.
 */
public class Deadline extends Task {

    private final String deadline;

    /**
     * Creates a new {@code Deadline} object with a given description, whether it has been done
     * and the corresponding deadline.
     *
     * @param description the description of the task
     * @param isDone      whether the task is marked as done
     * @param deadline    the date/time by which the task should be completed
     */
    public Deadline(String description, boolean isDone, String deadline) {
        super(description, isDone);
        this.deadline = deadline;
    }

    /**
     * Returns the deadline of the task the {@code Deadline} object is associated with
     *
     * @return the deadline of the task the {@code Deadline} object is associated with
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * The task type code for a {@code Deadline} object is "D". Hence, this method returns "D".
     *
     * @return "D"
     */
    @Override
    public String getTaskTypeCode() {
        return "D";
    }

    /**
     * Returns the String representation of the {@code Deadline} object, i.e.
     *   a string in the format "[task type code][get status icon] description (by: deadline)".
     *
     * @return String representation of the {@code Deadline} object
     */
    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), deadline);
    }

    /**
     * Returns a String representation of the {@code Deadline} object which in a format convenient
     * to save and load files.
     *
     * The string returned is in the following format (with newlines in between components and in
     * the end):
     * Task Type Code, i.e. "D" for {@code Deadline} objects
     * Description
     * 1 if the task is done, and 0 otherwise
     * Deadline by which the task should be completed
     *
     * @return The String representation of the {@code Deadline} object.
     */
    @Override
    public String toFileSaveString() {
        return String.format("%s%s\n", super.toFileSaveString(), deadline);
    }
}
