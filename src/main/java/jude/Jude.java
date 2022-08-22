package jude;

import java.io.IOException;

/**
 * Jude is a task tracker which appears like a command line interface. The name is a Beatles
 * reference, referring to the hit song Hey Jude.
 * When > shows up, you can type a command.
 *
 * There are three types of tasks, namely the todo, deadline and event.
 * Todo tasks are tasks without an associated date/time.
 * Deadline tasks have a specific deadline by which the task must be completed.
 * Event tasks have a start time and an end time.
 *
 * Here are the list of commands:
 * todo (description) - adds a todo task with some description
 * deadline (description) /by (deadline) - adds a deadline task with the specified description and
 *   deadline. The deadline needs to be a valid date (e.g., 21 Aug 2022, Aug 21 2022 or
 *   2022-08-21), with time optional. If time is provided, it must be provided after the date,
 *   with exactly one space in between.
 * event (description) /at (daterange) - adds an event task with start time and end time as part of
 *   daterange parameter
 * list - lists all tasks
 * mark - mark the task with a specified index (from list command) as done
 *   e.g. mark 2 marks second task as done
 * unmark - mark the task with a specified index (from list command) as undone,
 *   e.g. unmark 2 marks second task as undone
 * delete - delete the task corresponding to a specified index (from list command)
 *   e.g. delete 2 deletes second task
 * bye - exits the program
 *
 * If the command does not have these prefixes, an error will be thrown saying that the bot does
 * not understand.
 */
public class Jude {
    // Overall project structure code for main class adapted from
    // https://nus-cs2103-ay2223s1.github.io/website/schedule/week3/project.html
    private TaskList tasks = new TaskList();
    private Storage storage;
    private Ui ui = new Ui();
    private Parser parser;

    /**
     * Creates an instance of Jude chatbot.
     *
     * @param filePath The file path in which the list of tasks is stored.
     * @throws IOException When system I/O fails.
     */
    public Jude(String filePath) throws IOException {
        storage = new Storage(filePath);
    }

    /**
     * Runs the chatbot.
     *
     * @throws IOException When system I/O fails.
     */
    public void run() throws IOException {
        tasks = storage.load();
        parser = new Parser(tasks, storage);
        ui.showWelcome();

        while (true) {
            ui.showCommandReadReady();
            String str = ui.readCommand();
            boolean canContinue = parser.parse(str);
            if (!canContinue) {
                break;
            }
        }
    }

    /**
     * Runs the task tracker chatbot.
     *
     * @param args Not used for now.
     */
    public static void main(String[] args) throws IOException {
        new Jude("data/tasks.txt").run();
    }
}
