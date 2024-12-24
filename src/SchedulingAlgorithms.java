import java.util.*;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SchedulingAlgorithms {

    // FCFS Implementation
    public static void fcfs(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, 
            JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization) {
        
        // Clear the model at start
        model.setRowCount(0);
        
        ArrayList<Process> remainingProcesses = deepCopyProcesses(processes);
        ArrayList<Process> readyQueue = new ArrayList<>();
        
        int currentTime = 0;
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        int totalBurstTime = 0;
        
        // Sort by arrival time
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Add arrived processes to ready queue
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }
            
            if (readyQueue.isEmpty()) {
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }
            
            // Take first process from ready queue (FCFS)
            Process selectedProcess = readyQueue.remove(0);
            
            selectedProcess.startTime = currentTime;
            selectedProcess.responseTime = currentTime - selectedProcess.arrivalTime;
            selectedProcess.completionTime = currentTime + selectedProcess.burstTime;
            selectedProcess.turnaroundTime = selectedProcess.completionTime - selectedProcess.arrivalTime;
            selectedProcess.waitingTime = currentTime - selectedProcess.arrivalTime;
            
            currentTime = selectedProcess.completionTime;
            totalBurstTime += selectedProcess.burstTime;
            totalTurnaroundTime += selectedProcess.turnaroundTime;
            totalWaitingTime += selectedProcess.waitingTime;
            
            model.addRow(new Object[]{
                "FCFS",
                "P" + selectedProcess.id,
                selectedProcess.responseTime,
                selectedProcess.turnaroundTime,
                selectedProcess.completionTime,
                selectedProcess.waitingTime
            });
        }
        
        updateMetrics(processes.size(), totalTurnaroundTime, totalWaitingTime, 
                currentTime, totalBurstTime, avgTurnaround, avgWaiting, throughput, 
                cpuBurst, cpuUtilization);
    }
    
    // SJF Implementation
    public static void sjf(ArrayList<Process> processes, DefaultTableModel model, JTextField avgTurnaround, 
            JTextField avgWaiting, JTextField throughput, JTextField cpuBurst, JTextField cpuUtilization) {
        
        // Clear the model at start
        model.setRowCount(0);
        
        ArrayList<Process> remainingProcesses = deepCopyProcesses(processes);
        ArrayList<Process> readyQueue = new ArrayList<>();
        
        int currentTime = 0;
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        int totalBurstTime = 0;
        
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Add arrived processes to ready queue
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }
            
            if (readyQueue.isEmpty()) {
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }
            
            // Find shortest job
            Process selectedProcess = null;
            int selectedIndex = -1;
            int shortestBurst = Integer.MAX_VALUE;
            
            for (int i = 0; i < readyQueue.size(); i++) {
                Process p = readyQueue.get(i);
                if (p.burstTime < shortestBurst) {
                    shortestBurst = p.burstTime;
                    selectedProcess = p;
                    selectedIndex = i;
                } else if (p.burstTime == shortestBurst && p.arrivalTime < selectedProcess.arrivalTime) {
                    selectedProcess = p;
                    selectedIndex = i;
                }
            }
            
            readyQueue.remove(selectedIndex);
            
            selectedProcess.startTime = currentTime;
            selectedProcess.responseTime = currentTime - selectedProcess.arrivalTime;
            selectedProcess.completionTime = currentTime + selectedProcess.burstTime;
            selectedProcess.turnaroundTime = selectedProcess.completionTime - selectedProcess.arrivalTime;
            selectedProcess.waitingTime = currentTime - selectedProcess.arrivalTime;
            
            currentTime = selectedProcess.completionTime;
            totalBurstTime += selectedProcess.burstTime;
            totalTurnaroundTime += selectedProcess.turnaroundTime;
            totalWaitingTime += selectedProcess.waitingTime;
            
            model.addRow(new Object[]{
                "SJF",
                "P" + selectedProcess.id,
                selectedProcess.responseTime,
                selectedProcess.turnaroundTime,
                selectedProcess.completionTime,
                selectedProcess.waitingTime
            });
        }
        
        updateMetrics(processes.size(), totalTurnaroundTime, totalWaitingTime, 
                currentTime, totalBurstTime, avgTurnaround, avgWaiting, throughput, 
                cpuBurst, cpuUtilization);
    }
    
    // Priority Scheduling Implementation
    public static void priorityScheduling(ArrayList<Process> processes, DefaultTableModel model, 
            JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, 
            JTextField cpuBurst, JTextField cpuUtilization) {
        
        // Clear the model at start
        model.setRowCount(0);
        
        ArrayList<Process> remainingProcesses = deepCopyProcesses(processes);
        ArrayList<Process> readyQueue = new ArrayList<>();
        
        int currentTime = 0;
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        int totalBurstTime = 0;
        
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Add arrived processes to ready queue
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }
            
            if (readyQueue.isEmpty()) {
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }
            
            // Find highest priority process
            Process selectedProcess = null;
            int selectedIndex = -1;
            int highestPriority = Integer.MAX_VALUE;
            
            for (int i = 0; i < readyQueue.size(); i++) {
                Process p = readyQueue.get(i);
                if (p.priority < highestPriority) {
                    highestPriority = p.priority;
                    selectedProcess = p;
                    selectedIndex = i;
                } else if (p.priority == highestPriority && p.arrivalTime < selectedProcess.arrivalTime) {
                    selectedProcess = p;
                    selectedIndex = i;
                }
            }
            
            readyQueue.remove(selectedIndex);
            
            selectedProcess.startTime = currentTime;
            selectedProcess.responseTime = currentTime - selectedProcess.arrivalTime;
            selectedProcess.completionTime = currentTime + selectedProcess.burstTime;
            selectedProcess.turnaroundTime = selectedProcess.completionTime - selectedProcess.arrivalTime;
            selectedProcess.waitingTime = selectedProcess.turnaroundTime - selectedProcess.burstTime;
            
            // Update totals
            currentTime = selectedProcess.completionTime;
            totalBurstTime += selectedProcess.burstTime;
            totalTurnaroundTime += selectedProcess.turnaroundTime;
            totalWaitingTime += selectedProcess.waitingTime;
            
            model.addRow(new Object[]{
                "Priority",
                "P" + selectedProcess.id,
                selectedProcess.responseTime,
                selectedProcess.turnaroundTime,
                selectedProcess.completionTime,
                selectedProcess.waitingTime
            });
        }
        
        updateMetrics(processes.size(), totalTurnaroundTime, totalWaitingTime, 
                currentTime, totalBurstTime, avgTurnaround, avgWaiting, throughput, 
                cpuBurst, cpuUtilization);
    }
    
    // Round Robin Implementation
    public static void roundRobin(ArrayList<Process> processes, DefaultTableModel model, 
            JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput, 
            JTextField cpuBurst, JTextField cpuUtilization, int timeQuantum) {
        
        model.setRowCount(0);
        
        ArrayList<Process> remainingProcesses = deepCopyProcesses(processes);
        Queue<Process> readyQueue = new LinkedList<>();
        Map<Integer, Integer> lastRunTime = new HashMap<>();
        
        int currentTime = 0;
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        int totalBurstTime = 0;
        
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        for (Process p : remainingProcesses) {
            p.remainingTime = p.burstTime;
            lastRunTime.put(p.id, p.arrivalTime);
        }
        
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.offer(remainingProcesses.remove(0));
            }
            
            if (readyQueue.isEmpty()) {
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }
            
            Process currentProcess = readyQueue.poll();
            
            if (currentProcess.responseTime == -1) {
                currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
            }
            
            // Calculate waiting time since last run
            currentProcess.waitingTime += currentTime - lastRunTime.get(currentProcess.id);
            
            int executeTime = Math.min(timeQuantum, currentProcess.remainingTime);
            currentProcess.remainingTime -= executeTime;
            currentTime += executeTime;
            totalBurstTime += executeTime;
            
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                Process p = remainingProcesses.remove(0);
                readyQueue.offer(p);
            }
            
            if (currentProcess.remainingTime > 0) {
                lastRunTime.put(currentProcess.id, currentTime);
                readyQueue.offer(currentProcess);
            } else {
                currentProcess.completionTime = currentTime;
                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                
                totalTurnaroundTime += currentProcess.turnaroundTime;
                totalWaitingTime += currentProcess.waitingTime;
                
                model.addRow(new Object[]{
                    "RR(Q=" + timeQuantum + ")",
                    "P" + currentProcess.id,
                    currentProcess.responseTime,
                    currentProcess.turnaroundTime,
                    currentProcess.completionTime,
                    currentProcess.waitingTime
                });
            }
        }
        
        updateMetrics(processes.size(), totalTurnaroundTime, totalWaitingTime, 
                currentTime, totalBurstTime, avgTurnaround, avgWaiting, throughput, 
                cpuBurst, cpuUtilization);
    }
    
    private static ArrayList<Process> deepCopyProcesses(ArrayList<Process> processes) {
        ArrayList<Process> copy = new ArrayList<>();
        for (Process p : processes) {
            Process newProcess = new Process(p.id, p.arrivalTime, p.burstTime, p.priority);
            newProcess.responseTime = -1;
            newProcess.waitingTime = 0;
            newProcess.remainingTime = p.burstTime;
            newProcess.completionTime = 0;
            newProcess.turnaroundTime = 0;
            copy.add(newProcess);
        }
        return copy;
    }
    
    private static void updateMetrics(int processCount, double totalTurnaroundTime, 
            double totalWaitingTime, int currentTime, int totalBurstTime,
            JTextField avgTurnaround, JTextField avgWaiting, JTextField throughput,
            JTextField cpuBurst, JTextField cpuUtilization) {
            
        avgTurnaround.setText(String.format("%.2f", totalTurnaroundTime / processCount));
        avgWaiting.setText(String.format("%.2f", totalWaitingTime / processCount));
        throughput.setText(String.format("%.2f", (double)processCount / currentTime));
        cpuBurst.setText(String.valueOf(totalBurstTime));
        cpuUtilization.setText(String.format("%.2f%%", (totalBurstTime / (double)currentTime) * 100));
    }
}