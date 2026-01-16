/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.studyplanner.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sandeep
 */
public class DashboardUI extends javax.swing.JFrame {
    
    // --- CLASS LEVEL VARIABLES ---
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter; // Moved Sorter here to be accessible globally

    // --- CONSTRUCTOR ---
    public DashboardUI() {
        initComponents(); // Load the Design
        
        // 1. Initialize Model
        model = (DefaultTableModel) jTable1.getModel();
        
        // 2. Initialize Sorter and attach to Table
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);
        
        // 3. Custom Highlight Colors (Yellow Background for Selection)
        jTable1.setSelectionBackground(Color.YELLOW);
        jTable1.setSelectionForeground(Color.BLACK);
        
        initController(); // Load the Logic (Buttons)
        updateCardCounts(); // Update the numbers on the cards
        
        // Window Settings
        this.setTitle("GyanYogana - Student Dashboard");
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        
    }

    private void initController() {
        
        // 1. Overview Button
        btnOverview.addActionListener(e -> {
            card.setVisible(true); // Show cards
            JOptionPane.showMessageDialog(this, "Overview: Cards are visible.");
        });

        // 2. My Task Button
        btnTask.addActionListener(e -> {
            // card.setVisible(false); 
            JOptionPane.showMessageDialog(this, "Task View: Focused on Table.");
        });

        // 3. New Task Button
        btnNewTask.addActionListener(e -> {
        // 1. Create the input fields once (so we can reuse them if validation fails)
        JTextField idF = new JTextField();
        JTextField topicF = new JTextField();
        JTextField subF = new JTextField();
        JTextField dateF = new JTextField("2026-02-10"); // Default format hint
        JComboBox<String> statusF = new JComboBox<>(new String[]{"pending", "done", "overdue"});

        Object[] message = {
            "ID:", idF, 
            "Topic:", topicF, 
            "Subject:", subF, 
            "Deadline (YYYY-MM-DD):", dateF, 
            "Status:", statusF
        };

        // 2. Loop until the user provides valid input or clicks Cancel
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);

            if (option != JOptionPane.OK_OPTION) {
                break; // User cancelled, stop everything
            }

            // 3. Get the input
            String id = idF.getText().trim();
            String topic = topicF.getText().trim();
            String sub = subF.getText().trim();
            String date = dateF.getText().trim();
            String status = (String) statusF.getSelectedItem();

            // 4. VALIDATION LOGIC
            if (id.isEmpty() || topic.isEmpty() || sub.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Error: All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue; // Re-opens the dialog with previous text intact
            }

            // Optional: Check Date Format (Simple Regex for YYYY-MM-DD)
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Error: Date must be YYYY-MM-DD (e.g., 2026-02-10)", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                continue; 
            }

            // Optional: Check if ID already exists
            boolean idExists = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(id)) {
                    idExists = true;
                    break;
                }
            }

            if (idExists) {
                JOptionPane.showMessageDialog(this, "Error: Task ID " + id + " already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // 5. Success! Add the row and exit loop
            model.addRow(new Object[]{id, topic, sub, date, status});
            updateCardCounts();
            JOptionPane.showMessageDialog(this, "Task Added Successfully!");
            break; 
        }
    });

        // 4. Sort Deadline (Sorts by Column 3)
        btnSort.addActionListener(e -> {
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            // 3 = Deadline Column, Sort Ascending
            sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING)); 
            sorter.setSortKeys(sortKeys);
            sorter.sort();
            
            JOptionPane.showMessageDialog(this, "Table Sorted by Deadline.");
        });

        // 5. Binary Search (Search by ID) - WITH HIGHLIGHT FIX
        btnBinarySearch.addActionListener(e -> {
            String searchId = JOptionPane.showInputDialog(this, "Enter Task ID to Search:");
            if (searchId == null || searchId.trim().isEmpty()) return;

            boolean found = false;
            int rowCount = model.getRowCount();
            
            // Clear previous selection
            jTable1.clearSelection();

            for (int i = 0; i < rowCount; i++) {
                String id = (String) model.getValueAt(i, 0); // Col 0 is ID
                
                if (id.equalsIgnoreCase(searchId)) {
                    // CRITICAL: Convert Model Index to View Index
                    // If table is sorted, View Index != Model Index
                    int viewIndex = jTable1.convertRowIndexToView(i);
                    
                    if (viewIndex != -1) {
                        // Select the row (Highlights it Yellow)
                        jTable1.setRowSelectionInterval(viewIndex, viewIndex);
                        // Scroll to the row so user can see it
                        jTable1.scrollRectToVisible(jTable1.getCellRect(viewIndex, 0, true));
                        found = true;
                    }
                    break; 
                }
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, "Task ID '" + searchId + "' not found.");
            }
        });

        // 6. Search Bar Logic (Filters/Highlights matching rows)
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0 || text.equals("Search...")) {
                    sorter.setRowFilter(null); // Reset filter
                } else {
                    // Case-insensitive regex filter
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // 7. Edit Selected
        btnEdit.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }

            // Convert View Index to Model Index (in case table is sorted)
            int modelRow = jTable1.convertRowIndexToModel(selectedRow);

            String currentTopic = (String) model.getValueAt(modelRow, 1);
            String currentSub = (String) model.getValueAt(modelRow, 2);
            String currentDate = (String) model.getValueAt(modelRow, 3);
            String currentStatus = (String) model.getValueAt(modelRow, 4);

            JTextField topicF = new JTextField(currentTopic);
            JTextField subF = new JTextField(currentSub);
            JTextField dateF = new JTextField(currentDate);
            JComboBox<String> statusF = new JComboBox<>(new String[]{"pending", "done", "overdue"});
            statusF.setSelectedItem(currentStatus);

            Object[] message = {
                "Topic:", topicF, "Subject:", subF, "Deadline:", dateF, "Status:", statusF
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                model.setValueAt(topicF.getText(), modelRow, 1);
                model.setValueAt(subF.getText(), modelRow, 2);
                model.setValueAt(dateF.getText(), modelRow, 3);
                model.setValueAt(statusF.getSelectedItem(), modelRow, 4);
                updateCardCounts();
            }
        });

        // 8. Delete
        btnDelete.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this task?", "Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(modelRow);
                    updateCardCounts();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        // 9. Logout
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) this.dispose();
        });
    }

    // Updates the summary cards
    private void updateCardCounts() {
        if (model == null) return;
        
        int totalC = model.getRowCount();
        int pendingC = 0;
        int doneC = 0;
        int overdueC = 0;

        for (int i = 0; i < totalC; i++) {
            String status = (String) model.getValueAt(i, 4); 
            if (status != null) {
                if (status.equalsIgnoreCase("pending")) pendingC++;
                else if (status.equalsIgnoreCase("done")) doneC++;
                else if (status.equalsIgnoreCase("overdue")) overdueC++;
            }
        }

        totalValue.setText(String.valueOf(totalC));
        pendingValue.setText(String.valueOf(pendingC));
        doneValue.setText(String.valueOf(doneC));
        overdueValue.setText(String.valueOf(overdueC));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnOverview = new javax.swing.JButton();
        btnTask = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();
        mainRpannel = new javax.swing.JPanel();
        head = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSort = new javax.swing.JButton();
        btnNewTask = new javax.swing.JButton();
        card = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        total = new javax.swing.JLabel();
        totalValue = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        pending = new javax.swing.JLabel();
        pendingValue = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        done = new javax.swing.JLabel();
        doneValue = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        overdue = new javax.swing.JLabel();
        overdueValue = new javax.swing.JLabel();
        foot = new javax.swing.JPanel();
        btnBinarySearch = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        dataTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane1.getViewport().setBackground(new java.awt.Color(31, 31, 48));
        jTable1 = new javax.swing.JTable();
        jTable1.getTableHeader().setBackground(new java.awt.Color(0, 0, 51)); jTable1.getTableHeader().setForeground(java.awt.Color.WHITE); jTable1.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(0, 0, 51));
        setMaximumSize(new java.awt.Dimension(1982, 782));
        setPreferredSize(new java.awt.Dimension(1982, 782));

        leftPanel.setBackground(new java.awt.Color(31, 31, 48));
        leftPanel.setPreferredSize(new java.awt.Dimension(250, 782));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("GyanYogana");

        btnOverview.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnOverview.setForeground(new java.awt.Color(255, 255, 255));
        btnOverview.setText("Overview");
        btnOverview.setContentAreaFilled(false);
        btnOverview.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOverview.addActionListener(this::btnOverviewActionPerformed);

        btnTask.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnTask.setForeground(new java.awt.Color(255, 255, 255));
        btnTask.setText("My Task");
        btnTask.setContentAreaFilled(false);
        btnTask.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnLogout.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("logout");
        btnLogout.setContentAreaFilled(false);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOverview)
                    .addComponent(jLabel1)
                    .addComponent(btnTask)
                    .addComponent(btnLogout))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addGap(87, 87, 87)
                .addComponent(btnOverview)
                .addGap(18, 18, 18)
                .addComponent(btnTask)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 461, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(65, 65, 65))
        );

        getContentPane().add(leftPanel, java.awt.BorderLayout.LINE_START);

        rightPanel.setBackground(new java.awt.Color(0, 0, 51));
        rightPanel.setAutoscrolls(true);
        rightPanel.setMaximumSize(new java.awt.Dimension(950, 782));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new java.awt.Dimension(950, 782));

        mainRpannel.setBackground(new java.awt.Color(0, 0, 51));

        head.setBackground(new java.awt.Color(0, 0, 51));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Welcome, user");

        txtSearch.setText("Search...");
        txtSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtSearch.setMargin(new java.awt.Insets(6, 6, 6, 6));

        btnSort.setBackground(new java.awt.Color(0, 255, 255));
        btnSort.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnSort.setForeground(new java.awt.Color(255, 255, 255));
        btnSort.setText("Sort Deadline");
        btnSort.setAlignmentX(1.0F);
        btnSort.setAlignmentY(1.0F);
        btnSort.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        btnSort.setBorderPainted(false);
        btnSort.setFocusPainted(false);

        btnNewTask.setBackground(new java.awt.Color(153, 153, 255));
        btnNewTask.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnNewTask.setForeground(new java.awt.Color(255, 255, 255));
        btnNewTask.setText("+ New Task");
        btnNewTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 255), new java.awt.Color(153, 153, 255), new java.awt.Color(153, 153, 255), new java.awt.Color(153, 153, 255)));
        btnNewTask.setBorderPainted(false);
        btnNewTask.setFocusPainted(false);
        btnNewTask.addActionListener(this::btnNewTaskActionPerformed);

        javax.swing.GroupLayout headLayout = new javax.swing.GroupLayout(head);
        head.setLayout(headLayout);
        headLayout.setHorizontalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSort, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNewTask, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headLayout.setVerticalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSort, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewTask, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        card.setBackground(new java.awt.Color(0, 0, 51));
        card.setLayout(new java.awt.GridLayout(1, 4, 20, 20));

        jPanel1.setBackground(new java.awt.Color(31, 31, 48));

        total.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        total.setForeground(new java.awt.Color(255, 255, 255));
        total.setText("Total");

        totalValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalValue.setForeground(new java.awt.Color(255, 255, 255));
        totalValue.setText("4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(totalValue))
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(total)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalValue)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        card.add(jPanel1);

        jPanel2.setBackground(new java.awt.Color(31, 31, 48));

        pending.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pending.setForeground(new java.awt.Color(255, 255, 255));
        pending.setText("Pending");

        pendingValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        pendingValue.setForeground(new java.awt.Color(255, 255, 255));
        pendingValue.setText("4");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(pending, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(78, 78, 78)
                    .addComponent(pendingValue)
                    .addContainerGap(249, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(pending)
                .addContainerGap(81, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(pendingValue)
                    .addContainerGap(54, Short.MAX_VALUE)))
        );

        card.add(jPanel2);

        jPanel3.setBackground(new java.awt.Color(31, 31, 48));

        done.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        done.setForeground(new java.awt.Color(255, 255, 255));
        done.setText("Done");

        doneValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        doneValue.setForeground(new java.awt.Color(255, 255, 255));
        doneValue.setText("4");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(done, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(78, 78, 78)
                    .addComponent(doneValue)
                    .addContainerGap(249, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(done)
                .addContainerGap(79, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(doneValue)
                    .addContainerGap(54, Short.MAX_VALUE)))
        );

        card.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(31, 31, 48));

        overdue.setBackground(new java.awt.Color(102, 102, 102));
        overdue.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        overdue.setForeground(new java.awt.Color(255, 255, 255));
        overdue.setText("Overdue");

        overdueValue.setBackground(new java.awt.Color(102, 102, 102));
        overdueValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        overdueValue.setForeground(new java.awt.Color(255, 255, 255));
        overdueValue.setText("4");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(overdue, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(overdueValue)))
                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(overdue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(overdueValue)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        card.add(jPanel4);

        foot.setBackground(new java.awt.Color(0, 0, 51));

        btnBinarySearch.setBackground(new java.awt.Color(102, 153, 255));
        btnBinarySearch.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnBinarySearch.setForeground(new java.awt.Color(255, 255, 255));
        btnBinarySearch.setText("Binary Search");
        btnBinarySearch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(102, 153, 255), new java.awt.Color(102, 153, 255), new java.awt.Color(102, 153, 255), new java.awt.Color(102, 153, 255)));
        btnBinarySearch.setBorderPainted(false);
        btnBinarySearch.setFocusPainted(false);

        btnDelete.setBackground(new java.awt.Color(255, 102, 102));
        btnDelete.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 102, 102), new java.awt.Color(255, 102, 102), new java.awt.Color(255, 102, 102), new java.awt.Color(255, 102, 102)));
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);

        btnEdit.setBackground(new java.awt.Color(255, 153, 102));
        btnEdit.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit Selected");
        btnEdit.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 153, 102), new java.awt.Color(255, 153, 102), new java.awt.Color(255, 153, 102), new java.awt.Color(255, 153, 102)));
        btnEdit.setBorderPainted(false);
        btnEdit.setFocusPainted(false);

        javax.swing.GroupLayout footLayout = new javax.swing.GroupLayout(foot);
        foot.setLayout(footLayout);
        footLayout.setHorizontalGroup(
            footLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, footLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnBinarySearch, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        footLayout.setVerticalGroup(
            footLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(footLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBinarySearch, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        dataTable.setBackground(new java.awt.Color(0, 0, 51));

        jScrollPane1.setBackground(new java.awt.Color(0, 0, 51));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBackground(new java.awt.Color(31, 31, 48));
        jTable1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "Drawing", "Art", "2026-02-05", "pending"},
                {"2", "Algebra", "Math", "2025-12-28", "done"},
                {"3", "History Essay", "History", "2025-12-30", "overdue"},
                {"4", "Physics Lab", "Science", "2026-02-06", "pending"}
            },
            new String [] {
                "ID", "Topic", "Subject", "Deadline", "Status"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(102, 102, 102));
        jTable1.setRowHeight(30);
        jTable1.setSelectionBackground(new java.awt.Color(102, 153, 255));
        jTable1.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout dataTableLayout = new javax.swing.GroupLayout(dataTable);
        dataTable.setLayout(dataTableLayout);
        dataTableLayout.setHorizontalGroup(
            dataTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataTableLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1419, Short.MAX_VALUE)
                .addContainerGap())
        );
        dataTableLayout.setVerticalGroup(
            dataTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataTableLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainRpannelLayout = new javax.swing.GroupLayout(mainRpannel);
        mainRpannel.setLayout(mainRpannelLayout);
        mainRpannelLayout.setHorizontalGroup(
            mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1440, Short.MAX_VALUE)
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(head, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(3, 3, 3)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(card, javax.swing.GroupLayout.PREFERRED_SIZE, 1403, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(16, Short.MAX_VALUE)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(foot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(3, 3, 3)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dataTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        mainRpannelLayout.setVerticalGroup(
            mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 959, Short.MAX_VALUE)
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(head, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(864, Short.MAX_VALUE)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(101, 101, 101)
                    .addComponent(card, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(742, Short.MAX_VALUE)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(830, 830, 830)
                    .addComponent(foot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(15, Short.MAX_VALUE)))
            .addGroup(mainRpannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainRpannelLayout.createSequentialGroup()
                    .addGap(261, 261, 261)
                    .addComponent(dataTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(130, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainRpannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(mainRpannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(rightPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOverviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverviewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOverviewActionPerformed

    private void btnNewTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTaskActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNewTaskActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new DashboardUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBinarySearch;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNewTask;
    private javax.swing.JButton btnOverview;
    private javax.swing.JButton btnSort;
    private javax.swing.JButton btnTask;
    private javax.swing.JPanel card;
    private javax.swing.JPanel dataTable;
    private javax.swing.JLabel done;
    private javax.swing.JLabel doneValue;
    private javax.swing.JPanel foot;
    private javax.swing.JPanel head;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel mainRpannel;
    private javax.swing.JLabel overdue;
    private javax.swing.JLabel overdueValue;
    private javax.swing.JLabel pending;
    private javax.swing.JLabel pendingValue;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel total;
    private javax.swing.JLabel totalValue;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
