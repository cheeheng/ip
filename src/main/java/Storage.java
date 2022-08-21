import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Storage {
    private String filePath;

    public Storage(String filePath) throws IOException {
        this.filePath = filePath;
        try {
            // Solution below adapted from
            // https://stackoverflow.com/questions/3634853/how-to-create-a-directory-in-java
            // https://stackoverflow.com/questions/8197049/
            // how-to-get-just-the-parent-directory-name-of-a-specific-file
            Files.createDirectories(Paths.get(filePath).getParent());
            File file = new File(filePath);

            // Solution below adapted from
            // https://www.w3schools.com/java/java_files_create.asp
            file.createNewFile();
        } catch (FileAlreadyExistsException ex) {
        }
    }

    /**
     * Loads file in order to load the tasks
     * The format is as follows (in separate lines, no extra newlines in between):
     * - typeOfTask (which can be 'T', 'D' or 'E'), representing todo, deadline and event tasks
     *   respectively.
     * - Name of task.
     * - Whether the task is marked as done, 1 if so and 0 otherwise.
     * - Any dates which may be required by the type. For events, the start date is stored on top
     *   of the end date.
     *
     * In between two tasks, there can be extra newlines.
     *
     * @return A {@code TaskList} object depicting the list of {@code Tasks} found by the loader.
     */
    public TaskList load() throws IOException {
        Scanner sc = new Scanner(Paths.get(filePath));
        TaskList tasks = new TaskList();
        while (sc.hasNextLine()) {
            String taskType = sc.nextLine();
            if (taskType.isBlank()) {
                // ignore blank lines in between tasks
                continue;
            }

            String taskName = sc.nextLine();
            String done = sc.nextLine();
            boolean isDone = Integer.parseInt(done) == 1;

            Task task = null;
            if (taskType.equals("T")) {
                task = new Todo(taskName, isDone);
            } else if (taskType.equals("D")) {
                String deadline = sc.nextLine();
                task = new Deadline(taskName, isDone, deadline);
            } else if (taskType.equals("E")){
                String eventTime = sc.nextLine();
                task = new Event(taskName, isDone, eventTime);
            } else {
                throw new RuntimeException("Jude cannot understand the input file.");
            }
            tasks.add(task);
        }
        return tasks;
    }

    // Saves list of tasks as a file.
    public void save(TaskList tasks) throws IOException {
        FileWriter fw = new FileWriter(Paths.get(filePath).toFile());
        for (int i = 1; i <= tasks.size(); i++) {
            Task task = tasks.get(i);
            fw.write(task.toFileSaveString());
        }
        fw.close();
    }
}