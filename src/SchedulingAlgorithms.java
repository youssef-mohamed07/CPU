import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SchedulingAlgorithms {

    // First-Come, First-Served (FCFS) Scheduling
    public static void fcfs(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        resetProcessMetrics(processes);
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;

        model.setRowCount(0);
        for (Process p : processes) {
            currentTime = Math.max(currentTime, p.arrivalTime);
            p.responseTime = currentTime - p.arrivalTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;

            currentTime = p.completionTime;
            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;

            model.addRow(new Object[]{"FCFS", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
        }

        updateAverages(processes.size(), totalTAT, totalWT, avgTurnaround, avgWaiting, throughput, currentTime);
    }

    // Shortest Job First (SJF) Scheduling
    public static void sjf(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        resetProcessMetrics(processes);
        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();

        model.setRowCount(0);
        while (completed < processes.size()) {
            addArrivedProcesses(processes, currentTime, readyQueue);

            if (!readyQueue.isEmpty()) {
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process p = readyQueue.remove(0);
                currentTime = Math.max(currentTime, p.arrivalTime);
                p.responseTime = currentTime - p.arrivalTime;
                p.completionTime = currentTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                currentTime = p.completionTime;
                completed++;

                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;

                model.addRow(new Object[]{"SJF", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
            } else {
                currentTime++;
            }
        }

        updateAverages(processes.size(), totalTAT, totalWT, avgTurnaround, avgWaiting, throughput, currentTime);
    }

    // Priority Scheduling
    public static void priorityScheduling(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        resetProcessMetrics(processes);
        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();

        model.setRowCount(0);
        while (completed < processes.size()) {
            addArrivedProcesses(processes, currentTime, readyQueue);

            if (!readyQueue.isEmpty()) {
                readyQueue.sort(Comparator.comparingInt(p -> p.priority));
                Process p = readyQueue.remove(0);
                currentTime = Math.max(currentTime, p.arrivalTime);
                p.responseTime = currentTime - p.arrivalTime;
                p.completionTime = currentTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                currentTime = p.completionTime;
                completed++;

                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;

                model.addRow(new Object[]{"Priority", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
            } else {
                currentTime++;
            }
        }

        updateAverages(processes.size(), totalTAT, totalWT, avgTurnaround, avgWaiting, throughput, currentTime);
    }

    // Round Robin Scheduling
    public static void roundRobin(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, int timeQuantum) {
        resetProcessMetrics(processes);
        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> remainingProcesses = new ArrayList<>(processes);

        model.setRowCount(0);
        while (completed < processes.size()) {
            addArrivedProcesses(remainingProcesses, currentTime, readyQueue);

            if (!readyQueue.isEmpty()) {
                Process p = readyQueue.remove(0);
                if (p.responseTime == -1) {
                    p.responseTime = currentTime - p.arrivalTime;
                }

                currentTime = Math.max(currentTime, p.arrivalTime);
                int executeTime = Math.min(timeQuantum, p.burstTime);
                p.burstTime -= executeTime;
                currentTime += executeTime;

                if (p.burstTime <= 0) {
                    p.completionTime = currentTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - (currentTime - p.arrivalTime);
                    completed++;

                    totalTAT += p.turnaroundTime;
                    totalWT += p.waitingTime;

                    model.addRow(new Object[]{"RR (Q=" + timeQuantum + ")", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
                } else {
                    readyQueue.add(p);
                }
            } else {
                currentTime++;
            }
        }

        updateAverages(processes.size(), totalTAT, totalWT, avgTurnaround, avgWaiting, throughput, currentTime);
    }

    // Helper Methods
    private static void resetProcessMetrics(ArrayList<Process> processes) {
        for (Process p : processes) {
            p.completionTime = 0;
            p.turnaroundTime = 0;
            p.waitingTime = 0;
            p.responseTime = -1; // Special marker for first response time
        }
    }

    private static void addArrivedProcesses(ArrayList<Process> processes, int currentTime, ArrayList<Process> readyQueue) {
        for (Process p : processes) {
            if (p.arrivalTime <= currentTime && p.completionTime == 0 && !readyQueue.contains(p)) {
                readyQueue.add(p);
            }
        }
    }

    private static void updateAverages(int processCount, double totalTAT, double totalWT, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, int currentTime) {
        avgTurnaround.setText(String.format("%.2f", totalTAT / processCount));
        avgWaiting.setText(String.format("%.2f", totalWT / processCount));
        throughput.setText(String.format("%.2f", processCount / (double) currentTime));
    }
}