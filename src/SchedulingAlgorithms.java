import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SchedulingAlgorithms {
    public static void fcfs(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        // Reset process metrics
        for (Process p : processes) {
            p.completionTime = 0;
            p.turnaroundTime = 0;
            p.waitingTime = 0;
            p.responseTime = 0;
        }

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;

        model.setRowCount(0);
        for (Process p : processes) {
            // Wait if process arrives later
            currentTime = Math.max(currentTime, p.arrivalTime);

            p.responseTime = currentTime - p.arrivalTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;

            currentTime = p.completionTime;

            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;

            model.addRow(new Object[]{"", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
        }

        avgTurnaround.setText(String.format("%.2f", totalTAT / processes.size()));
        avgWaiting.setText(String.format("%.2f", totalWT / processes.size()));
        throughput.setText(String.format("%.2f", processes.size() / (double)currentTime));
    }

    public static void sjf(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        // Reset process metrics
        for (Process p : processes) {
            p.completionTime = 0;
            p.turnaroundTime = 0;
            p.waitingTime = 0;
            p.responseTime = 0;
        }

        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();

        model.setRowCount(0);
        while (completed < processes.size()) {
            // Add arrived processes to ready queue
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.completionTime == 0 && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }

            if (!readyQueue.isEmpty()) {
                // Sort ready queue by burst time
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process p = readyQueue.remove(0);

                // Wait if process arrives later
                currentTime = Math.max(currentTime, p.arrivalTime);

                p.responseTime = currentTime - p.arrivalTime;
                p.completionTime = currentTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                currentTime = p.completionTime;
                completed++;

                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;

                model.addRow(new Object[]{"", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
            } else {
                currentTime++;
            }
        }

        avgTurnaround.setText(String.format("%.2f", totalTAT / processes.size()));
        avgWaiting.setText(String.format("%.2f", totalWT / processes.size()));
        throughput.setText(String.format("%.2f", processes.size() / (double)currentTime));
    }

    public static void priorityScheduling(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput) {
        // Reset process metrics
        for (Process p : processes) {
            p.completionTime = 0;
            p.turnaroundTime = 0;
            p.waitingTime = 0;
            p.responseTime = 0;
        }

        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();

        model.setRowCount(0);
        while (completed < processes.size()) {
            // Add arrived processes to ready queue
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.completionTime == 0 && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }

            if (!readyQueue.isEmpty()) {
                // Sort ready queue by priority (lower number = higher priority)
                readyQueue.sort(Comparator.comparingInt(p -> p.priority));
                Process p = readyQueue.remove(0);

                // Wait if process arrives later
                currentTime = Math.max(currentTime, p.arrivalTime);

                p.responseTime = currentTime - p.arrivalTime;
                p.completionTime = currentTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;

                currentTime = p.completionTime;
                completed++;

                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;

                model.addRow(new Object[]{"", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
            } else {
                currentTime++;
            }
        }

        avgTurnaround.setText(String.format("%.2f", totalTAT / processes.size()));
        avgWaiting.setText(String.format("%.2f", totalWT / processes.size()));
        throughput.setText(String.format("%.2f", processes.size() / (double)currentTime));
    }

    public static void roundRobin(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, int timeQuantum) {
        // Reset process metrics
        for (Process p : processes) {
            p.completionTime = 0;
            p.turnaroundTime = 0;
            p.waitingTime = 0;
            p.responseTime = -1; // Special marker for first response time
        }

        int currentTime = 0;
        double totalTAT = 0, totalWT = 0;
        int completed = 0;
        ArrayList<Process> readyQueue = new ArrayList<>();
        ArrayList<Process> remainingProcesses = new ArrayList<>(processes);

        model.setRowCount(0);
        while (completed < processes.size()) {
            // Add arrived processes to ready queue
            for (Process p : remainingProcesses) {
                if (p.arrivalTime <= currentTime && !readyQueue.contains(p) && p.completionTime == 0) {
                    readyQueue.add(p);
                }
            }

            if (!readyQueue.isEmpty()) {
                Process p = readyQueue.remove(0);

                // Set first response time
                if (p.responseTime == -1) {
                    p.responseTime = currentTime - p.arrivalTime;
                }

                // Wait if process arrives later
                currentTime = Math.max(currentTime, p.arrivalTime);

                // Execute for time quantum or remaining burst time
                int executeTime = Math.min(timeQuantum, p.burstTime);
                p.burstTime -= executeTime;
                currentTime += executeTime;

                // If process completed
                if (p.burstTime <= 0) {
                    p.completionTime = currentTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - (currentTime - p.arrivalTime);
                    completed++;

                    totalTAT += p.turnaroundTime;
                    totalWT += p.waitingTime;

                    model.addRow(new Object[]{"", p.id, p.responseTime, p.turnaroundTime, p.completionTime, p.waitingTime});
                } else {
                    // Add back to queue if not completed
                    readyQueue.add(p);
                }
            } else {
                currentTime++;
            }
        }

        avgTurnaround.setText(String.format("%.2f", totalTAT / processes.size()));
        avgWaiting.setText(String.format("%.2f", totalWT / processes.size()));
        throughput.setText(String.format("%.2f", processes.size() / (double)currentTime));
    }
}