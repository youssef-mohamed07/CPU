public class Process {
    int id, arrivalTime, burstTime, priority, completionTime, turnaroundTime, waitingTime, responseTime;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}