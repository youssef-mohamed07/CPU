import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class CPUSchedulingSimulator {

    public static void main(String[] args) {
        // Set global UI styles
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Enhanced CPU Scheduling Simulator");
        frame.setSize(1280, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Colors and Fonts for UI
        Color panelColor = new Color(245, 245, 245);
        Font titleFont = new Font("Arial", Font.BOLD, 16);
        Font labelFont = new Font("Arial", Font.PLAIN, 14);

        // Input Section
        JPanel inputPanel = new JPanel();
        inputPanel.setBounds(20, 20, 720, 240);
        inputPanel.setLayout(null);
        inputPanel.setBackground(panelColor);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                "Input Section",
                0,
                0,
                titleFont,
                new Color(0, 120, 215)
        ));

        String[] inputColumns = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        DefaultTableModel inputModel = new DefaultTableModel(inputColumns, 0);
        JTable inputTable = new JTable(inputModel);
        JScrollPane inputScrollPane = new JScrollPane(inputTable);
        inputScrollPane.setBounds(20, 30, 680, 100);
        inputPanel.add(inputScrollPane);

        JTextField txtArrivalTime = new JTextField();
        JTextField txtBurstTime = new JTextField();
        JTextField txtPriority = new JTextField();
        JCheckBox autoGenerateID = new JCheckBox("Auto-generate Process ID");
        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove");
        JButton btnClearAll = new JButton("Clear All");
        JButton btnSaveTest = new JButton("Save Test");
        JButton btnLoadTest = new JButton("Load Test");

        txtArrivalTime.setBounds(20, 140, 80, 40);
        txtArrivalTime.setBorder(BorderFactory.createTitledBorder("Arrival Time"));
        txtBurstTime.setBounds(110, 140, 80, 40);
        txtBurstTime.setBorder(BorderFactory.createTitledBorder("Burst Time"));
        txtPriority.setBounds(200, 140, 80, 40);
        txtPriority.setBorder(BorderFactory.createTitledBorder("Priority"));

        autoGenerateID.setBounds(290, 140, 200, 40);
        autoGenerateID.setBackground(panelColor);

        btnAdd.setBounds(20, 190, 100, 30);
        btnRemove.setBounds(130, 190, 100, 30);
        btnClearAll.setBounds(240, 190, 100, 30);
        btnSaveTest.setBounds(350, 190, 120, 30);
        btnLoadTest.setBounds(480, 190, 120, 30);

        inputPanel.add(txtArrivalTime);
        inputPanel.add(txtBurstTime);
        inputPanel.add(txtPriority);
        inputPanel.add(autoGenerateID);
        inputPanel.add(btnAdd);
        inputPanel.add(btnRemove);
        inputPanel.add(btnClearAll);
        inputPanel.add(btnSaveTest);
        inputPanel.add(btnLoadTest);

        // Enhanced Input Validation
        btnAdd.addActionListener(e -> {
            try {
                int arrivalTime = Integer.parseInt(txtArrivalTime.getText());
                int burstTime = Integer.parseInt(txtBurstTime.getText());
                int priority = Integer.parseInt(txtPriority.getText());

                // Validate inputs
                if (arrivalTime < 0 || burstTime <= 0 || priority < 0) {
                    throw new IllegalArgumentException("Values must be non-negative and burst time must be positive.");
                }

                String processID = autoGenerateID.isSelected() ? "P" + (inputModel.getRowCount() + 1) : "P" + inputModel.getRowCount();
                inputModel.addRow(new Object[]{processID, arrivalTime, burstTime, priority});

                // Clear input fields
                txtArrivalTime.setText("");
                txtBurstTime.setText("");
                txtPriority.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Confirmation dialog for removing a row
        btnRemove.addActionListener(e -> {
            int selectedRow = inputTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove this entry?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    inputModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Status bar for messages
        JLabel statusBar = new JLabel("Welcome to the CPU Scheduling Simulator");
        statusBar.setBounds(20, 850, 1220, 30);
        frame.add(statusBar);

        // Scheduling Method Panel
        JPanel schedulingPanel = new JPanel();
        schedulingPanel.setBounds(760, 20, 480, 240);
        schedulingPanel.setLayout(null);
        schedulingPanel.setBackground(panelColor);
        schedulingPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                "Select Scheduling Method",
                0,
                0,
                titleFont,
                new Color(0, 120, 215)
        ));

        JComboBox<String> schedulingMethods = new JComboBox<>(new String[]{
                "FCFS (First Come First Serve)",
                "SJF (Shortest Job First)",
                "Priority Scheduling",
                "Round Robin"
        });
        schedulingMethods.setBounds(20, 40, 440, 40);

        JTextField txtTimeQuantum = new JTextField();
        txtTimeQuantum.setBounds(20, 100, 440, 40);
        txtTimeQuantum.setBorder(BorderFactory.createTitledBorder("Time Quantum for Round Robin"));
        txtTimeQuantum.setEnabled(false);

        schedulingMethods.addActionListener(e -> {
            String selectedMethod = (String) schedulingMethods.getSelectedItem();
            txtTimeQuantum.setEnabled(selectedMethod != null && selectedMethod.equals("Round Robin"));
            if (!txtTimeQuantum.isEnabled()) {
                txtTimeQuantum.setText("");
            }
        });

        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setBounds(20, 160, 440, 40);

        schedulingPanel.add(schedulingMethods);
        schedulingPanel.add(txtTimeQuantum);
        schedulingPanel.add(btnCalculate);

        // Output Section
        JPanel outputPanel = new JPanel();
        outputPanel.setBounds(20, 280, 1220, 560);
        outputPanel.setLayout(null);
        outputPanel.setBackground(panelColor);
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                "Output Section",
                0,
                0,
                titleFont,
                new Color(0, 120, 215)
        ));

        String[] outputColumns = {"Algorithm", "Process ID", "Response Time", "Turnaround Time", "Completion Time", "Waiting Time"};
        DefaultTableModel outputModel = new DefaultTableModel(outputColumns, 0);
        JTable outputTable = new JTable(outputModel);
        JScrollPane outputScrollPane = new JScrollPane(outputTable);
        outputScrollPane.setBounds(20, 30, 1180, 300);
        outputPanel.add(outputScrollPane);

        JLabel lblAvgTurnaround = new JLabel("Avg. Turnaround Time:");
        lblAvgTurnaround.setFont(labelFont);
        lblAvgTurnaround.setBounds(20, 340, 200, 30);
        outputPanel.add(lblAvgTurnaround);

        JTextField avgTurnaroundField = new JTextField();
        avgTurnaroundField.setBounds(220, 340, 100, 30);
        avgTurnaroundField.setEditable(false);
        outputPanel.add(avgTurnaroundField);

        JLabel lblAvgWaiting = new JLabel("Avg. Waiting Time:");
        lblAvgWaiting.setFont(labelFont);
        lblAvgWaiting.setBounds(340, 340, 200, 30);
        outputPanel.add(lblAvgWaiting);

        JTextField avgWaitingField = new JTextField();
        avgWaitingField.setBounds(520, 340, 100, 30);
        avgWaitingField.setEditable(false);
        outputPanel.add(avgWaitingField);

        JLabel lblThroughput = new JLabel("Throughput:");
        lblThroughput.setFont(labelFont);
        lblThroughput.setBounds(640, 340, 200, 30);
        outputPanel.add(lblThroughput);

        JTextField throughputField = new JTextField();
        throughputField.setBounds(760, 340, 100, 30);
        throughputField.setEditable(false);
        outputPanel.add(throughputField);

        JButton btnExport = new JButton("Export to CSV");
        btnExport.setBounds(1050, 340, 140, 30);
        outputPanel.add(btnExport);

        JButton btnClearOutput = new JButton("Clear Output");
        btnClearOutput.setBounds(900, 340, 140, 30);
        outputPanel.add(btnClearOutput);

        btnClearOutput.setBackground(new Color(220, 53, 69)); 
         // Red color
        btnClearOutput.setFocusPainted(false);

        // Add Panels to Frame
        frame.add(inputPanel);
        frame.add(schedulingPanel);
        frame.add(outputPanel);

        // Event Listeners
        ArrayList<ArrayList<Process>> savedTests = new ArrayList<>();
        btnClearAll.addActionListener(e -> inputModel.setRowCount(0));
        btnSaveTest.addActionListener(e -> {
            ArrayList<Process> test = new ArrayList<>();
            for (int i = 0; i < inputModel.getRowCount(); i++) {
                String id = inputModel.getValueAt(i, 0).toString();
                int arrivalTime = Integer.parseInt(inputModel.getValueAt(i, 1).toString());
                int burstTime = Integer.parseInt(inputModel.getValueAt(i, 2).toString());
                int priority = Integer.parseInt(inputModel.getValueAt(i, 3).toString());
                test.add(new Process(Integer.parseInt(id.substring(1)), arrivalTime, burstTime, priority));
            }
            savedTests.add(test);
            JOptionPane.showMessageDialog(frame, "Test saved successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        btnLoadTest.addActionListener(e -> {
            if (savedTests.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No saved tests to load!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] testNames = new String[savedTests.size()];
            for (int i = 0; i < savedTests.size(); i++) {
                testNames[i] = "Test " + (i + 1);
            }

            String selectedTest = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select a test to load:",
                    "Load Test",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    testNames,
                    testNames[0]
            );

            if (selectedTest != null) {
                int index = Integer.parseInt(selectedTest.split(" ")[1]) - 1;
                ArrayList<Process> test = savedTests.get(index);
                inputModel.setRowCount(0);
                for (Process p : test) {
                    inputModel.addRow(new Object[]{"P" + p.id, p.arrivalTime, p.burstTime, p.priority});
                }
            }
        });
        btnCalculate.addActionListener(e -> {
            String selectedMethod = (String) schedulingMethods.getSelectedItem();
            if (selectedMethod == null) {
                JOptionPane.showMessageDialog(frame, "Please select a scheduling method!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create process selection dialog
            JDialog selectDialog = new JDialog(frame, "Select Processes", true);
            selectDialog.setLayout(new BorderLayout());
            selectDialog.setSize(300, 400);

            JPanel checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
            ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

            for (int i = 0; i < inputModel.getRowCount(); i++) {
                JCheckBox cb = new JCheckBox("Process " + inputModel.getValueAt(i, 0));
                cb.setSelected(true);
                checkBoxes.add(cb);
                checkBoxPanel.add(cb);
            }

            JButton btnConfirm = new JButton("Calculate Selected");
            btnConfirm.addActionListener(event -> {
                ArrayList<Process> selectedProcesses = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        int id = Integer.parseInt(inputModel.getValueAt(i, 0).toString().substring(1));
                        int arrivalTime = Integer.parseInt(inputModel.getValueAt(i, 1).toString());
                        int burstTime = Integer.parseInt(inputModel.getValueAt(i, 2).toString());
                        int priority = Integer.parseInt(inputModel.getValueAt(i, 3).toString());
                        selectedProcesses.add(new Process(id, arrivalTime, burstTime, priority));
                    }
                }

                if (selectedProcesses.isEmpty()) {
                    JOptionPane.showMessageDialog(selectDialog, "Please select at least one process!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Keep previous calculations in the output table
                try {
                    switch (selectedMethod) {
                        case "FCFS (First Come First Serve)":
                            SchedulingAlgorithms.fcfs(selectedProcesses, outputModel, avgTurnaroundField, avgWaitingField, throughputField);
                            for (int i = 0; i < outputModel.getRowCount(); i++) {
                                outputModel.setValueAt("FCFS", i, 0);
                            }
                            break;
                        case "SJF (Shortest Job First)":
                            SchedulingAlgorithms.sjf(selectedProcesses, outputModel, avgTurnaroundField, avgWaitingField, throughputField);
                            for (int i = 0; i < outputModel.getRowCount(); i++) {
                                outputModel.setValueAt("SJF", i, 0);
                            }
                            break;
                        case "Priority Scheduling":
                            SchedulingAlgorithms.priorityScheduling(selectedProcesses, outputModel, avgTurnaroundField, avgWaitingField, throughputField);
                            for (int i = 0; i < outputModel.getRowCount(); i++) {
                                outputModel.setValueAt("Priority", i, 0);
                            }
                            break;
                        case "Round Robin":
                            int timeQuantum = Integer.parseInt(txtTimeQuantum.getText());
                            SchedulingAlgorithms.roundRobin(selectedProcesses, outputModel, avgTurnaroundField, avgWaitingField, throughputField, timeQuantum);
                            for (int i = 0; i < outputModel.getRowCount(); i++) {
                                outputModel.setValueAt("RR (Q=" + timeQuantum + ")", i, 0);
                            }
                            break;
                        default:
                            JOptionPane.showMessageDialog(frame, "Invalid scheduling method!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Time Quantum for Round Robin!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectDialog.dispose();
            });

            selectDialog.add(new JScrollPane(checkBoxPanel), BorderLayout.CENTER);
            selectDialog.add(btnConfirm, BorderLayout.SOUTH);
            selectDialog.setLocationRelativeTo(frame);
            selectDialog.setVisible(true);
        });
        btnExport.addActionListener(e -> {
            try {
                // Add timestamp to filename
                String timestamp = String.format("%tF_%tH-%tM-%tS", 
                    System.currentTimeMillis(), 
                    System.currentTimeMillis(), 
                    System.currentTimeMillis(), 
                    System.currentTimeMillis());
                File file = new File("output_" + timestamp + ".csv");
                FileWriter writer = new FileWriter(file);

                for (int i = 0; i < outputModel.getColumnCount(); i++) {
                    writer.write(outputModel.getColumnName(i) + ",");
                }
                writer.write("\n");

                for (int i = 0; i < outputModel.getRowCount(); i++) {
                    for (int j = 0; j < outputModel.getColumnCount(); j++) {
                        writer.write(outputModel.getValueAt(i, j).toString() + ",");
                    }
                    writer.write("\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(frame, "Results exported successfully to " + file.getName() + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to export results!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add clear output button listener
        btnClearOutput.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to clear all output?", 
                "Confirm Clear", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                outputModel.setRowCount(0);
                avgTurnaroundField.setText("");
                avgWaitingField.setText("");
                throughputField.setText("");
            }
        });

        frame.setVisible(true);
    }
}
