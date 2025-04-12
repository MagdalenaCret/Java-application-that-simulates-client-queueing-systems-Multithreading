package businesslogic;

import model.Queue;
import model.Client;

import java.util.List;

public interface Strategy {

    public void addTask(List<Queue> queues, Client client);
}
