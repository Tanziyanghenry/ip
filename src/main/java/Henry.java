import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * a Personal Assistant Chatbot that helps a person
 * to keep track of various things.
 */
public class Henry {

    /**
     * Prints greetings
     *
     */
    public static void greetings() {
        String greetings = "Hello! I'm Henry\n"
                + "What can I do for you?\n";
        System.out.println(greetings);
    }

    /**
     * Prints exit
     *
     */
    public static void bye() {
        String bye = "Bye. Hope to see you again soon!";
        System.out.println(bye);
    }

    /**
     * Prints all the tasks recorded
     *
     * @param tasks array of tasks recorded
     * @param index number of tasks recorded
     */
    public static void printList(ArrayList<Task> tasks, int index)
            throws HenryException {
        //check if there is any task to print
        if (index == 0) {
            throw new HenryException("You do not have any tasks!");
        }
        System.out.println("\nHere are the tasks in your list:");
        for (int i = 0; i < index; i++) {
            System.out.println(i + 1
                    + "."
                    + tasks.get(i).toString());
        }
        System.out.println();
    }

    /**
     * Returns date in the form of MMM dd yyyy
     *
     * @param date date in the form of YYYY-MM-DD
     * @return date in the form of MMM dd yyyy
     */
    public static String convertDateTime(String dateTime) throws HenryException {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh.mm a");
            return localDateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw new HenryException("Please write the date and time in " +
                    "the following format: YYYY-MM-DD HHmm");
        }
    }

    /**
     * Adds new task into the array of tasks recorded and split them into todo, event and deadline
     *
     * @param tasks array of tasks recorded
     * @param index number of tasks recorded
     * @param input name of task
     */
    public static void addTask(ArrayList<Task> tasks, int index, String input)
            throws HenryException  {
        String[] words = input.split(" ");
        String task = words[0].toLowerCase();
        String activityAndTime = input.replaceFirst(words[0] + " ", "");
        String[] activityAndTimeList = activityAndTime.split(" /");
        String activity = activityAndTimeList[0];
        if (task.equals("todo")) {
            //check if todo description is valid
            if (words.length == 1 ) {
                throw new HenryException("The todo description is wrong!! " +
                        "Ensure that you have included the activity. " +
                        "Example: todo read book");
            }
            tasks.add(new Todo(activity));
        } else if (task.equals("deadline")) {
            //check if deadline description is valid
            if (activityAndTimeList.length != 2 ) {
                throw new HenryException("The deadline description is wrong!! " +
                        "Ensure that you have included the activity, " +
                        "followed by the deadline. " +
                        "Example: deadline return book /by Sunday");
            }
            String dateTime = activityAndTimeList[1]
                    .replaceFirst("by ", "");
            String convertedDateTime = convertDateTime(dateTime);
            tasks.add(new Deadline(activity, convertedDateTime));
        } else if (task.equals("event")) {
            //check if event description is valid
            if (activityAndTimeList.length != 3 ) {
                throw new HenryException("The event description is wrong!! " +
                        "Ensure that you have included the activity, " +
                        "followed by the start time and end time. " +
                        "Example: event project meeting /from Mon 2pm /to 4pm");
            }
            String startTime = activityAndTimeList[1]
                    .replaceFirst("from ", "");
            String endTime = activityAndTimeList[2]
                    .replaceFirst("to ", "");
            tasks.add(new Event(activity, startTime, endTime));
        } else {
            //check for invalid input
            throw new HenryException("This is not a task!! " +
                    "To write a task, start with "
                    + "\"" + "todo" +"\","
                    + " \"" + "deadline" +"\" or"
                    + " \"" + "event" +"\"");
        }
        System.out.println("\nGot it. I've added this task:\n"
                + tasks.get(index).toString()
                + "\nNow you have "
                + (index + 1)
                + (index + 1 <= 1 ? " task" : " tasks")
                + " in the list.\n");
    }

    /**
     * Changes status of task
     *
     * @param tasks array of tasks recorded
     * @param words user input
     */
    public static void changeTaskStatus(ArrayList<Task> tasks, String[] words, int index)
            throws HenryException {
        //check for invalid number
        try {
            int number = Integer.parseInt(words[1]);
            //check if number is out of range
            if (number > index) {
                throw new HenryException("There "
                        + (index <= 1 ? "is " : "are ")
                        + "only "
                        + index
                        + (index <= 1 ? " task" : " tasks")
                        + "!");
            } else if (number <= 0) {
                throw new HenryException("Number must be greater than zero!");
            }
            if (words[0].equals("mark")) {
                //check if task is already marked
                if (tasks.get(number - 1).isDone()) {
                    throw new HenryException("The task is already marked!");
                }
                tasks.get(number - 1).mark();
                System.out.println("\nNice! I've marked this task as done:\n"
                        + tasks.get(number - 1).toString()
                        + "\n");
            } else {
                //check if task is already unmarked
                if (!tasks.get(number - 1).isDone()) {
                    throw new HenryException("The task is already unmarked!");
                }
                tasks.get(number - 1).unmark();
                System.out.println("\nOK, I've marked this task as not done yet:\n"
                        + tasks.get(number - 1).toString()
                        + "\n");
            }
        } catch(NumberFormatException e) {
            throw new HenryException("This is not a number!!");
        }
    }

    /**
     * Deletes task into the array of tasks recorded
     *
     * @param tasks array of tasks recorded
     * @param num task number to be deleted
     * @param index number of tasks recorded
     */
    public static void deleteTask(ArrayList<Task> tasks, String num, int index)
            throws HenryException {
        //check for invalid number
        try {
            int number = Integer.parseInt(num);
            //check if number is out of range
            if (number > index) {
                throw new HenryException("There "
                        + (index <= 1 ? "is " : "are ")
                        + "only "
                        + index
                        + (index <= 1 ? " task" : " tasks")
                        + "!");
            } else if (number <= 0) {
                throw new HenryException("Number must be greater than zero!");
            }
            System.out.println("\nNoted. I've removed this task:\n"
                    + tasks.get(number - 1).toString()
                    + "\nNow you have "
                    + (index - 1)
                    + (index - 1 <= 1 ? " task" : " tasks")
                    + " in the list.\n");
            tasks.remove(number - 1);
        } catch(NumberFormatException e) {
            throw new HenryException("This is not a number!!");
        }
    }

    /**
     * Writes text into file
     *
     * @param filePath path of the file where it is saved
     * @param textToAdd text to be added in the file
     * @param index number of tasks recorded
     */
    private static void writeToFile(String filePath, String textToAdd, int index)
            throws IOException {
        FileWriter fw = new FileWriter(filePath, index != 0);
        fw.write(textToAdd);
        fw.close();
    }

    public static void main(String[] args) throws IOException {
        greetings();

        int index = 0;

        ArrayList<Task> tasks = new ArrayList<Task>();

        String pathName = "./data/Henry.txt";
        File file = new File(pathName);
        //if file does not exist, create new file and directory
        if (!file.exists()) {
            file.createNewFile();
            file.mkdirs();
        } else {
            // create a Scanner using the File as the source
            Scanner scanner1 = new Scanner(file);
            while (scanner1.hasNext()) {
                String input = scanner1.nextLine();
                String[] words = input.split(" \\| ");
                if(words[0].equals("T")) {
                    tasks.add(new Todo(words[2],
                            (Integer.parseInt(words[1]) != 0)));
                } else if (words[0].equals("D")) {
                    tasks.add(new Deadline(words[2], words[3],
                            (Integer.parseInt(words[1]) != 0)));
                } else if (words[0].equals("E")) {
                    String[] duration = words[3].split("-");
                    String startTime = duration[0];
                    String endTime = duration[1];
                    tasks.add(new Event(words[2], startTime,
                            endTime, (Integer.parseInt(words[1]) != 0)));
                }
                index++;
            }
        }

        Scanner scanner = new Scanner(System.in);
        do {
            try {
                String input = scanner.nextLine();
                if (input.equals("bye")) {
                    System.out.println();
                    bye();
                    break;
                } else if (input.equals("list")) {
                    printList(tasks, index);
                } else {
                    String[] words = input.split(" ");
                    String command = words[0].toLowerCase();
                    if (command.equals("mark") || command.equals("unmark")) {
                        changeTaskStatus(tasks, words, index);
                    } else if (command.equals("delete")) {
                        deleteTask(tasks, words[1], index);
                        index--;
                    } else {
                        addTask(tasks, index, input);
                        index++;
                    }
                }
                //update the file
                try {
                    for (int i = 0; i < index; i++){
                        writeToFile(pathName, tasks.get(i).summary()
                                + System.lineSeparator(), i);
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                }
            } catch (HenryException e) {
                System.out.println("\nSorry! " + e.getMessage() + "\n");
            }
        } while (true);
    }
}
