package businesslogic;

import model.Queue;
import model.Client;

import java.util.List;

public class TimeStrategy implements Strategy {

    // Add task to server with minimum waiting time;
    @Override
    public void addTask(List<Queue> queues, Client client) {
        if (!queues.isEmpty()) {
            int minTime = Integer.MAX_VALUE;
            Queue queueWhereTaskIsAdded = null;

            for (Queue queue : queues) {
                if (minTime > queue.getWaitingTime()) {
                    minTime = queue.getWaitingTime();
                    queueWhereTaskIsAdded = queue;
                }
            }


            queueWhereTaskIsAdded.addTask(client);
        }
    }
}
