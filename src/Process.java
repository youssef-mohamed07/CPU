public class Process {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int priority;
    
    // Add these fields
    public int completionTime;
    public int turnaroundTime;
    public int waitingTime;
    public int responseTime;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}