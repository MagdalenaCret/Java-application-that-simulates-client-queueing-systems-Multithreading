package businesslogic;

import model.Queue;
import model.Client;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {

    // Add task to server with minimum number of tasks;
    @Override
    public void addTask(List<Queue> queues, Client client) {
        if (!queues.isEmpty()) {
            int minNumberOfTasks = Integer.MAX_VALUE;
            Queue queueWhereTaskIsAdded = null;

            for (Queue queue : queues) {
                if (minNumberOfTasks > queue.getNumberOfClients()) {
                    minNumberOfTasks = queue.getNumberOfClients();
                    queueWhereTaskIsAdded = queue;
                }
            }

            queueWhereTaskIsAdded.addTask(client);
        }
    }
}
