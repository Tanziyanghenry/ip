package henry.command;

import henry.HenryException;
import henry.util.TaskList;
import henry.util.Ui;

/**
 * Deals with printing tasks recorded in TaskList
 */
public class PrintCommand extends Command {

    /**
     * Prints all the tasks recorded
     *
     * @param taskList instance of a TaskList class that contains
     *                 an array of tasks
     * @param ui instance of an Ui class that interacts with the user
     */
    public String execute(TaskList taskList, Ui ui) throws HenryException {
        //check if there is any task to print
        int numOfTasks = taskList.getTasks().size();
        if (numOfTasks == 0) {
            throw new HenryException("You do not have any tasks!");
        }
        StringBuilder string = new StringBuilder("\nHere are the tasks in your list:\n");
        for (int i = 0; i < numOfTasks; i++) {
            string.append(i + 1).append(".").append(taskList.getTasks().get(i)
                    .toString()).append("\n");
        }
        return string.toString();
    }

}
