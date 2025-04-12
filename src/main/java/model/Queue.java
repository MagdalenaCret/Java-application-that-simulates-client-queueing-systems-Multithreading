package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable {

    private BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;

    private final Integer maxNumberOfTasks;

    private boolean enable = true;

    private boolean running = true;

    private Integer oldClientSize = 0;

    private AtomicInteger sumOfWaitingTime;
    private AtomicInteger sumOfServiceTime;

    public Queue(Integer maxNumberOfTasks) {
        clients = new LinkedBlockingDeque<>();
        waitingPeriod = new AtomicInteger(0);
        sumOfWaitingTime = new AtomicInteger(0);
        sumOfServiceTime = new AtomicInteger(0);
        this.maxNumberOfTasks = maxNumberOfTasks;
    }

    public void addTask(Client client) {
        client.setWaitingTime(this.getWaitingTime());
        sumOfWaitingTime.set(sumOfWaitingTime.get() + client.getWaitingTime()); //suma pentru waiting time pentru clientii dintr-o coada
        sumOfServiceTime.set(sumOfServiceTime.get() + client.getServiceTime());  //suma pentru service time pentru clientii dintr-o coada
        clients.add(client);
        waitingPeriod.set(waitingPeriod.get() + client.getServiceTime());
    }

    public int getWaitingTime() {
        return waitingPeriod.get();
    }

    public int getNumberOfClients() {
        return clients.size();
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void stopQueue() {
        running = false;
    }

    public boolean getEnable() {
        return enable;
    }

    public AtomicInteger getSumOfWaitingTime() {
        return sumOfWaitingTime;
    }

    public AtomicInteger getSumOfServiceTime() {
        return sumOfServiceTime;
    }

    // 1 second to another - verify if are clients
    @Override
    public void run() {
        try {
            while (running) {
                if (enable) {
                    // Daca am adugat recent un client, nu ii mai scad si perioada
                    if (!(oldClientSize == 0 && clients.size() == 1)) {
                        if (!clients.isEmpty()) {
                            Client client = clients.peek(); // Iau clientul din coada
                            client.decreaseServiceTime();
                            if (client.getServiceTime() == 0) {
                                clients.poll();  // Elimin clientul din coada
                            }
                            waitingPeriod.set(waitingPeriod.get() - 1);
                        }
                    }
                    oldClientSize = clients.size();
                    enable = false;
                }
                Thread.sleep(1000); //1 sec
            }

        } catch (InterruptedException e) {
            System.out.println("Error at stoping thread");
        }

    }


    @Override
    public String toString() {
        String message = "";
        if (clients.isEmpty()) {
            message = "Closed (No Clients)";
        } else {
            for (Client client : clients) {
                message += client.toString() + "; ";
            }
        }

        return message;
    }
}
