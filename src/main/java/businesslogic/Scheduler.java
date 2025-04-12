package businesslogic;

import model.Queue;
import model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {

    private static final Strategy SHORTEST_STRATEGY = new ShortestQueueStrategy();
    private static final Strategy TIME_STRATEGY = new TimeStrategy();

    private final List<Queue> queues;
    private Strategy strategy = SHORTEST_STRATEGY;
    private Integer maxNumberOfServers;
    private Integer maxNumberOfTasksForServer;

    private ExecutorService executor;

    public Scheduler(Integer maxNumberOfServers, Integer maxNumberOfTasksForServer) {
        queues = new ArrayList<>();
        for (int i = 0; i < maxNumberOfServers; i++) {
            queues.add(new Queue(maxNumberOfTasksForServer));
        }
        this.maxNumberOfServers = maxNumberOfServers;
        this.maxNumberOfTasksForServer = maxNumberOfTasksForServer;
    }

    public void startQueues() {
        executor = Executors.newFixedThreadPool(queues.size());
        for (Queue queue : queues) {
            executor.execute(queue); //se pornesc threadurile
        }
    }

    public void stopQueues() {
        for (Queue queue : queues) {
            queue.stopQueue(); //sse opresc threadurile
        }
        executor.shutdown();
    }


    public void  changeStrategy(SelectionPolicy selectionPolicy) {
        if (selectionPolicy == SelectionPolicy.SHORTHEST_TIME) {
            strategy = SHORTEST_STRATEGY;
        } else {
            strategy = TIME_STRATEGY;
        }
    }

    public String getQueueInfo() {
        String display = "";
        int numberOfQueue = 1;
        for (Queue queue : queues) {
            display += "Queue " + numberOfQueue + ": " + queue.toString() + "\n";
            numberOfQueue++;
        }

        return display;
    }

    public void dispatchTask(Client client) {
        strategy.addTask(queues, client); // in functie de strategie adaugam taskul la o coada
    }

    public void enableAllQueues() {
        for (Queue queue : queues) {
            queue.setEnable(true);
        }
    }

    public boolean allQueuesAreDisabled() {
        for (Queue queue : queues) {
            if (queue.getEnable()) {
                return false;
            }
        }

        return true;
    }

    public List<Queue> getQueues() {
        return queues;
    }

}

