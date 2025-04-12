package model;

public class Client implements Comparable<Client> {

    private final Integer id;
    private final Integer arrivalTime;
    private Integer serviceTime;

    private Integer waitingTime=0;

    public Client(Integer id, Integer arrivalTime, Integer serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public Integer getId() {
        return id;
    }

    public Integer getArrivalTime() {
        return arrivalTime;
    }

    public Integer getServiceTime() {
        return serviceTime;
    }

    public Integer getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Integer waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void decreaseServiceTime() {
        serviceTime--;
    }

    @Override
    public int compareTo(Client o) {
        return this.arrivalTime.compareTo(o.arrivalTime); //sort list with respect to arrivalTime
    }

    @Override
    public String toString() {
        return "(id=" + id + ", arrive=" + arrivalTime + ", period=" + serviceTime + ')';
    }
}
