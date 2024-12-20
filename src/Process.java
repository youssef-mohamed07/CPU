import java.awt.Color;

public class Process {
    public static final int HIGH_PRIORITY = 0;
    public static final int MEDIUM_PRIORITY = 1;
    public static final int LOW_PRIORITY = 2;

    public int id;
    public int arrivalTime;
    public int burstTime;
    public int priority; // 0: High, 1: Medium, 2: Low
    
    // Metrics for scheduling
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
            case HIGH_PRIORITY: return Color.RED; // High
            case MEDIUM_PRIORITY: return Color.ORANGE; // Medium
            case LOW_PRIORITY: return Color.GREEN; // Low
            default: return Color.GRAY; // Default
        }
    }
}