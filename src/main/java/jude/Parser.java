package jude;

import jude.task.Deadline;
import jude.task.Event;
import jude.task.Task;
import jude.task.Todo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * The {@code Parser} class parses user commands in the task tracker chatbot and executes them.
 */
public class Parser {

    private TaskList tasks;
    private Storage storage;

    /**
     * Creates a new Parser which will parse user commands in the chatbot and execute them.
     *
     * @param tasks The initial list of tasks which will be used by the parser.
     * @param storage The file which the list of tasks will be saved to.
     */
    public Parser(TaskList tasks, Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
    }

    /**
     * Parses date in ISO 8601 format or DD MMM YYYY format (e.g. 21 Aug 2022) or MMM DD YYYY
     * format (e.g. Aug 21 2022) and returns a string in DD MMM YYYY format. If date parse fails,
     * throws IllegalCommandException.
     * Time, if provided, should be given in 12 or 24-hour format, and provided after the date
     * string, which is not in ISO format.
     * This parser is not case-sensitive.
     */
    String convertToDate(String date) {
        // Solution adapted from
        // https://nus-cs2103-ay2223s1.github.io/website/schedule/week3/project.html
        if (date == null) {
            throw new IllegalCommandException("Date cannot be empty.");
        }
        LocalDateTime dateObject = null;
        try {
            dateObject = LocalDateTime.parse(date);
        } catch (DateTimeParseException ex) {
            boolean valid = false;
            String[] dateTimeFormats = {
                    "yyyy-MM-dd",
                    "yyyy-MM-dd H:mm",
                    "yyyy-MM-dd h:mm a",
                    "d MMM yyyy",
                    "d MMM yyyy H:mm",
                    "d MMM yyyy h:mm a",
                    "MMM d yyyy",
                    "MMM d yyyy H:mm",
                    "MMM d yyyy h:mm a",
            };
            boolean[] isDateFormats = { true, false, false, true, false, false, true, false, false };
            for (int i = 0; i < dateTimeFormats.length; i++) {
                // Solution below adapted from
                // https://stackoverflow.com/questions/44925840/
                // java-time-format-datetimeparseexception-text-could-not-be-parsed-at-index-3
                DateTimeFormatter dateFormat = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern(dateTimeFormats[i])
                        .toFormatter(Locale.ENGLISH);
                try {
                    if (isDateFormats[i]) {
                        // Solution below adapted from
                        // https://stackoverflow.com/questions/27454025/
                        // unable-to-obtain-localdatetime-from-temporalaccessor-when-parsing-localdatetime
                        dateObject = LocalDate.parse(date, dateFormat).atStartOfDay();
                    } else {
                        dateObject = LocalDateTime.parse(date, dateFormat);
                    }
                    valid = true;
                    break;
                } catch (DateTimeParseException ex2) {
                }
            }
            if (!valid) {
                throw new IllegalCommandException("Please input a valid date, e.g. 21 Aug 2022, " +
                        "Aug 21 2022 or 2022-08-21.");
            }
        }
        return dateObject.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /**
     * Parses the command provided and executes it.
     *
     * @param command The command which will be provided to the parser.
     * @return true if the command terminates the program, i.e. bye command, false otherwise.
     * @throws IOException When system I/O fails.
     */
    public boolean parse(String command) throws IOException {
        String[] tokens = command.split(" ", 2);
        try {
            if (tokens[0].equals("todo") || tokens[0].equals("deadline") ||
                    tokens[0].equals("event")) {
                Task taskAdded = null;
                if (tokens.length == 1 || tokens[1].isBlank()) {
                    throw new IllegalCommandException(
                            String.format("Description of %s cannot be empty.", tokens[0]));
                } else if (tokens[0].equals("todo")) {
                    String description = tokens[1];
                    taskAdded = new Todo(description, false);
                } else if (tokens[0].equals("deadline")) {
                    String remText = tokens[1];
                    String[] remTextTokens = "  ".concat(remText).split(" /by ", 2);
                    if (remTextTokens.length == 2) {
                        String description = remTextTokens[0].trim();
                        String deadline = remTextTokens[1].trim();
                        if (description.isBlank() || description.isEmpty()) {
                            throw new IllegalCommandException(
                                    "Description of deadline task cannot be empty.");
                        } else if (deadline.isBlank()) {
                            throw new IllegalCommandException("A deadline task must have a " +
                                    "deadline.");
                        }
                        deadline = convertToDate(deadline);
                        taskAdded = new Deadline(description, false, deadline);
                    } else {
                        throw new IllegalCommandException("A deadline task must have a " +
                                "deadline.");
                    }
                } else if (tokens[0].equals("event")) {
                    String remText = tokens[1];
                    String[] remTextTokens = "  ".concat(remText).split(" /at ", 2);
                    if (remTextTokens.length == 2) {
                        String description = remTextTokens[0].strip();
                        String when = remTextTokens[1].strip();
                        if (description.isBlank()) {
                            throw new IllegalCommandException(
                                    "Description of event task cannot be empty.");
                        } else if (when.isBlank()) {
                            throw new IllegalCommandException("An event task must have a " +
                                    "time at which the event takes place.");
                        }
                        taskAdded = new Event(description, false, when);
                    } else {
                        throw new IllegalCommandException("An event task must have a time at " +
                                "which the event takes place.");
                    }
                }

                if (taskAdded != null) {
                    tasks.add(taskAdded);
                    storage.save(tasks);
                    System.out.printf("The following %s task has been added:\n  ", tokens[0]);
                    System.out.println(taskAdded);
                    System.out.printf("The task list now contains %d task(s).\n", tasks.size());
                }
            } else if (tokens[0].equals("mark")) {
                int index = Integer.parseInt(tokens[1]);
                Task task = tasks.get(index);

                // Solution below adapted from
                // https://nus-cs2103-ay2223s1.github.io/website/schedule/week2/project.html
                task.markAsDone();
                storage.save(tasks);

                System.out.println("The following task has been marked as done");
                System.out.println(task);
            } else if (tokens[0].equals("unmark")) {
                int index = Integer.parseInt(tokens[1]);
                Task task = tasks.get(index);
                task.markAsUndone();
                storage.save(tasks);
                System.out.println("The following task has been marked as undone");
                System.out.println(task);
            } else if (tokens[0].equals("delete")) {
                int index = Integer.parseInt(tokens[1]);
                Task task = tasks.get(index);
                tasks.delete(index);
                storage.save(tasks);
                System.out.println("The following task has been removed:");
                System.out.println(task);
                System.out.printf("The task list now contains %d task(s).\n", tasks.size());
            } else if (command.equals("list")) {
                for (int i = 1; i <= tasks.size(); i++) {
                    Task task = tasks.get(i);
                    System.out.printf("%d.%s\n", i, task);
                }
            } else if (command.equals("bye")) {
                System.out.println("Goodbye! Have a nice day!");
                return false;
            } else {
                System.out.println("Sorry, I don't understand what this means!");
            }
        } catch (IllegalCommandException ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }
}
