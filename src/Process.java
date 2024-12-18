import java.awt.Color;

public class Process {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int priority; // 0: High, 1: Medium, 2: Low
    
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

    public Color getPriorityColor() {
        switch (priority) {
            case 0: return Color.RED; // High
            case 1: return Color.ORANGE; // Medium
            case 2: return Color.GREEN; // Low
            default: return Color.GRAY; // Default
        }
    }
}