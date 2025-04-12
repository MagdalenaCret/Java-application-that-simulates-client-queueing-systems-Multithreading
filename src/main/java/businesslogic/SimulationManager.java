package businesslogic;

import model.Client;
import model.Queue;
import exception.ValidationException;
import guiinterface.Gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class SimulationManager implements Runnable {

    //data read from UI
    public Integer timeLimit = 100;
    public Integer maxArrivalTime = 10;
    public Integer minArrivalTime = 1;

    public Integer maxServiceTime = 10;
    public Integer minServiceTime = 2;
    public Integer numberOfQueues = 3;
    public Integer maxNumberOfTaskPerQueue = 100;
    public Integer numberOfClients = 10;

    public Double medWaitingTime = 0.0;
    public Double medServiceTime = 0.0;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTHEST_TIME;

    private Scheduler scheduler;
    private Gui frame;
    private List<Client> generatedClients;

    public SimulationManager(Gui frame) {
        this.frame = frame;
    }

    public void startSimulation() {
        generateNRandomClients();
        scheduler.startQueues();
        frame.textArea.setText("");
        frame.textFieldAverageServiceTime.setText("");
        frame.textFieldAverageWaitingTime.setText("");
        frame.textFieldPeakHour.setText("");
    }

    private void generateNRandomClients() {
        Random random = new Random();
        generatedClients = new ArrayList<>();
        for (int i = 0; i < numberOfClients; i++) {
            Integer arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime;
            Integer serviceTime = random.nextInt(maxServiceTime - minServiceTime) + minServiceTime;
            Client client = new Client(i + 1, arrivalTime, serviceTime);
            generatedClients.add(client);
        }
        Collections.sort(generatedClients);
    }

    @Override
    public void run() {
        int currentTime = 0;
        startSimulation();

        Integer peakTime = 0;
        int numberMaxOfClientsOnQueueInSimulation = 0;
        while (currentTime < timeLimit && existsClients()) {

            boolean ok = true;
            while (ok && !generatedClients.isEmpty()) {
                Client client = generatedClients.get(0);
                if (client.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(client);   //se face remove cat timp sunt clienti care au ArrivalTime() == currentTime
                    generatedClients.remove(client);
                } else {
                    ok = false;
                }
            }

            // Enable Queue processing
            // Wait that all queues has proceeded the client, but only for 1 second
            scheduler.enableAllQueues();
            while (!scheduler.allQueuesAreDisabled()) {
                try {
                    Thread.sleep(5); //astept pana toate cozile au procesat o secunda, sleep-ul pentru fiecare thread
                } catch (InterruptedException e) {
                    System.out.println("Error at sleeping the simulation manager for waiting to process queues");
                }
            }

            Integer allNumberOfClients = geNumberOfClientsInQueues();
            if (numberMaxOfClientsOnQueueInSimulation < allNumberOfClients) {
                numberMaxOfClientsOnQueueInSimulation = allNumberOfClients;
                peakTime = currentTime;
            }

            String messageToDisplay = getDisplayInfo(currentTime);
            System.out.println(messageToDisplay);
            frame.textArea.setText(frame.textArea.getText() + "\n" + messageToDisplay);
            currentTime++;
        }

        scheduler.stopQueues();


        Integer sumWaiting = 0;
        for (Queue queue : scheduler.getQueues()) {
            sumWaiting += queue.getSumOfWaitingTime().get();
        }
        medWaitingTime = 1.0 * sumWaiting / numberOfClients;
        frame.textFieldAverageWaitingTime.setText(medWaitingTime.toString());  //afisez average waintingTime


        Integer sumService = 0;
        for (Queue queue : scheduler.getQueues()) {
            sumService += queue.getSumOfServiceTime().get();
        }
        medServiceTime = 1.0 * sumService / numberOfClients;
        frame.textFieldAverageServiceTime.setText(medServiceTime.toString()); //afisez average serviceTime
        frame.textFieldPeakHour.setText(peakTime.toString()); //peak hour for the simulation interval

        String logs = frame.textArea.getText();
        logs+="\n"+"Average Waiting Time: "+ medWaitingTime;
        logs+="\n"+"Average Service Time: "+ medServiceTime;
        logs+="\n"+"Peak Hour: "+ peakTime;

        WriteFile.writeSimulationLogs(logs);
    }

    // numarul total de clienti din toate cozile, implementata pentru peakTime
    // peak hour: iau primul timp pentru care cozile au cel mai mare numar de clienti
    public Integer geNumberOfClientsInQueues() {
        int allNrClients = 0;
        for (Queue queue : scheduler.getQueues()) {
            allNrClients += queue.getNumberOfClients();
        }
        return allNrClients;
    }

    //verific daca mai exista clienti la coada
    public boolean existsClients() {
        if (generatedClients.isEmpty()) {
            for (Queue queue : scheduler.getQueues()) {
                if (queue.getNumberOfClients() != 0) { //mai exista clienti in coada
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public String getDisplayInfo(Integer currentTime) {
        String display = "Time: " + currentTime + "\n";
        display += "Waiting clients: ";

        for (Client client : generatedClients) {
            display += client.toString() + "; ";
        }
        display += "\n";
        display += scheduler.getQueueInfo();
        return display;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Gui frame = new Gui();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setSize(800, 800);

                Gui finalFrame = frame;
                frame.startSimulationButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {

                            SimulationManager simulationManager = new SimulationManager(finalFrame);
                            simulationManager.numberOfClients = finalFrame.getNumberOfClients();
                            simulationManager.numberOfQueues = finalFrame.getNumberOfQueues();
                            simulationManager.timeLimit = finalFrame.getSimulationInterval();
                            simulationManager.minArrivalTime = finalFrame.getMinArrivalTime();
                            simulationManager.maxArrivalTime = finalFrame.getMaxArrivalTime();
                            simulationManager.minServiceTime = finalFrame.getMinServiceTime();
                            simulationManager.maxServiceTime = finalFrame.getMaxServiceTime();

                            if (simulationManager.minArrivalTime > simulationManager.maxArrivalTime) {
                                throw new ValidationException("Min arrival time must be < max arrival time");
                            }

                            if (simulationManager.minServiceTime > simulationManager.maxServiceTime) {
                                throw new ValidationException("Min service time must be < max service time");
                            }

                            simulationManager.scheduler = new Scheduler(simulationManager.numberOfQueues, simulationManager.maxNumberOfTaskPerQueue);
                            simulationManager.scheduler.changeStrategy(finalFrame.getStrategy());
                            simulationManager.startSimulation();

                            // pornesc un nou thread care la start va executa metoda run
                            // este threadul principal care ia informatii de pe cozi si le afiseaza in UI
                            // celelalte threaduri reprezinta cozile, sunt n astfel de threaduri
                            Thread thread = new Thread(simulationManager);
                            thread.start();

                        } catch (ValidationException validationException) {
                            JOptionPane.showMessageDialog(finalFrame, validationException.getMessage());
                        }
                    }
                });
            }
        });


    }
}
