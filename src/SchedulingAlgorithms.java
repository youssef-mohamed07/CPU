import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SchedulingAlgorithms {

    // First-Come, First-Served (FCFS) Scheduling Algorithm
    public static void fcfs(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization) {
        executeScheduling(processes, model, avgTurnaround, avgWaiting, throughput, cpuBurst, cpuUtilization, "FCFS");
    }

    // Shortest Job First (SJF) Scheduling Algorithm
    public static void sjf(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization) {
        executeScheduling(processes, model, avgTurnaround, avgWaiting, throughput, cpuBurst, cpuUtilization, "SJF");
    }

    // Priority Scheduling Algorithm
    public static void priorityScheduling(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization) {
        executeScheduling(processes, model, avgTurnaround, avgWaiting, throughput, cpuBurst, cpuUtilization, "Priority");
    }

    // Round Robin Scheduling Algorithm
    public static void roundRobin(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization, int timeQuantum) {
        resetProcessMetrics(processes);
        int currentTime = 0;
        double totalTurnaroundTime = 0, totalWaitingTime = 0;
        int totalBurstTime = 0; // Total CPU burst time
        int completedProcesses = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> remainingProcesses = new ArrayList<>(processes);

        model.setRowCount(0); // Clear the model for new entries
        while (completedProcesses < processes.size()) {
            addArrivedProcesses(remainingProcesses, currentTime, readyQueue);

            if (!readyQueue.isEmpty()) {
                Process process = readyQueue.remove(0);
                if (process.responseTime == -1) {
                    process.responseTime = currentTime - process.arrivalTime;
                }

                currentTime = Math.max(currentTime, process.arrivalTime);
                int executionTime = Math.min(timeQuantum, process.burstTime);
                process.burstTime -= executionTime;
                currentTime += executionTime;

                totalBurstTime += executionTime; // Accumulate burst time

                if (process.burstTime <= 0) {
                    process.completionTime = currentTime;
                    process.turnaroundTime = process.completionTime - process.arrivalTime;
                    process.waitingTime = process.turnaroundTime - (currentTime - process.arrivalTime);
                    completedProcesses++;

                    totalTurnaroundTime += process.turnaroundTime;
                    totalWaitingTime += process.waitingTime;

                    model.addRow(new Object[]{"RR (Q=" + timeQuantum + ")", process.id, process.responseTime, process.turnaroundTime, process.completionTime, process.waitingTime});
                } else {
                    readyQueue.add(process); // Re-add the process to the queue
                }
            } else {
                currentTime++; // Increment time if no processes are ready
            }
        }

        updateAverages(processes.size(), totalTurnaroundTime, totalWaitingTime, avgTurnaround, avgWaiting, throughput, currentTime);
        cpuBurst.setText(String.valueOf(totalBurstTime));
        cpuUtilization.setText(String.format("%.2f%%", (totalBurstTime / (double) currentTime) * 100));
    }

    // Common scheduling execution logic
    private static void executeScheduling(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization, String algorithm) {
        resetProcessMetrics(processes);
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        double totalTurnaroundTime = 0, totalWaitingTime = 0;
        int totalBurstTime = 0; // Total CPU burst time

        model.setRowCount(0); // Clear the model for new entries
        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime);
            process.responseTime = currentTime - process.arrivalTime;
            process.completionTime = currentTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;

            currentTime = process.completionTime;
            totalBurstTime += process.burstTime; // Accumulate burst time
            totalTurnaroundTime += process.turnaroundTime;
            totalWaitingTime += process.waitingTime;

            model.addRow(new Object[]{algorithm, process.id, process.responseTime, process.turnaroundTime, process.completionTime, process.waitingTime});
        }

        updateAverages(processes.size(), totalTurnaroundTime, totalWaitingTime, avgTurnaround, avgWaiting, throughput, currentTime);
        cpuBurst.setText(String.valueOf(totalBurstTime));
        cpuUtilization.setText(String.format("%.2f%%", (totalBurstTime / (double) currentTime) * 100));
    }

    // Helper Methods
    private static void resetProcessMetrics(ArrayList<Process> processes) {
        for (Process process : processes) {
            process.completionTime = 0;
            process.turnaroundTime = 0;
            process.waitingTime = 0;
            process.responseTime = -1; // Marker for first response time
        }
    }

    private static void addArrivedProcesses(ArrayList<Process> processes, int currentTime, ArrayList<Process> readyQueue) {
        for (Process process : processes) {
            if (process.arrivalTime <= currentTime && process.completionTime == 0 && !readyQueue.contains(process)) {
                readyQueue.add(process);
            }
        }
    }

    private static void updateAverages(int processCount, double totalTAT, double totalWT, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, int currentTime) {
        avgTurnaround.setText(String.format("%.2f", totalTAT / processCount));
        avgWaiting.setText(String.format("%.2f", totalWT / processCount));
        throughput.setText(String.format("%.2f", processCount / (double) currentTime));
    }
}