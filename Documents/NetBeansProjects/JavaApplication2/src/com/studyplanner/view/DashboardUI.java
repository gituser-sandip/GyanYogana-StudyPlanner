package com.studyplanner.view;

import com.studyplanner.controller.PlannerController;
import com.studyplanner.model.StudyTask;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class DashboardUI extends JFrame {

    // Colors
    private final Color BG_DARK = new Color(16, 16, 30);       // #10101E
    private final Color SIDEBAR_BG = new Color(21, 21, 37);    // #151525
    private final Color CARD_BG = new Color(31, 31, 48);       // #1F1F30
    private final Color ACCENT_PURPLE = new Color(108, 99, 255); // #6C63FF
    private final Color ACCENT_PINK = new Color(255, 76, 97);    // #FF4C61
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color TEXT_GRAY = new Color(160, 160, 176);

    private PlannerController controller;
    private DefaultTableModel tableModel;
    private JTable taskTable;
    private JLabel lblTotal, lblPending, lblCompleted, lblOverdue;
    private JTextField searchField;

    public DashboardUI(String username) {
        try {
            controller = new PlannerController();
            
            // Frame Setup
            setTitle("GyanYogana - Student Planner");
            setSize(1200, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // --- 1. SIDEBAR ---
            JPanel sidebar = new JPanel();
            sidebar.setPreferredSize(new Dimension(250, 800));
            sidebar.setBackground(SIDEBAR_BG);
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel logo = new JLabel("GyanYogana");
            logo.setFont(new Font("SansSerif", Font.BOLD, 24));
            logo.setForeground(ACCENT_PURPLE);
            logo.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(logo);
            sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

            addMenuButton(sidebar, "Overview", true);
            addMenuButton(sidebar, "My Tasks", false);
            
            sidebar.add(Box.createVerticalGlue());
            JButton btnLogout = new JButton("Logout");
            styleSimpleButton(btnLogout);
            btnLogout.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });
            sidebar.add(btnLogout);
            
            add(sidebar, BorderLayout.WEST);

            // --- 2. MAIN CONTENT PANEL ---
            JPanel mainContent = new JPanel(new BorderLayout());
            mainContent.setBackground(BG_DARK);
            mainContent.setBorder(new EmptyBorder(30, 30, 30, 30));

            // Header (Search & Sort)
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            
            JLabel welcomeLabel = new JLabel("<html>Welcome, <b>" + username + "</b></html>");
            welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            welcomeLabel.setForeground(TEXT_WHITE);
            
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actionPanel.setOpaque(false);

            searchField = new JTextField(15);
            searchField.setText("Search...");
            searchField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    String query = searchField.getText();
                    if(!query.isEmpty() && !query.equals("Search...")) updateTableWithList(controller.filterTasks(query));
                    else refreshTable();
                }
            });

            ModernButton btnSort = new ModernButton("Sort Deadline", Color.CYAN.darker());
            btnSort.addActionListener(e -> { controller.sortTasksByDeadline(); refreshTable(); });

            ModernButton btnAdd = new ModernButton("+ New Task", ACCENT_PURPLE);
            btnAdd.addActionListener(e -> openAddTaskDialog());

            actionPanel.add(searchField);
            actionPanel.add(btnSort);
            actionPanel.add(btnAdd);
            
            headerPanel.add(welcomeLabel, BorderLayout.WEST);
            headerPanel.add(actionPanel, BorderLayout.EAST);
            mainContent.add(headerPanel, BorderLayout.NORTH);

            // Center (Stats & Table)
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Stats
            JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
            statsPanel.setOpaque(false);
            statsPanel.setPreferredSize(new Dimension(800, 100));
            statsPanel.setMaximumSize(new Dimension(2000, 100));
            
            lblTotal = new JLabel("0"); lblPending = new JLabel("0");
            lblCompleted = new JLabel("0"); lblOverdue = new JLabel("0");

            statsPanel.add(createStatCard("Total", lblTotal, ACCENT_PURPLE));
            statsPanel.add(createStatCard("Pending", lblPending, Color.ORANGE));
            statsPanel.add(createStatCard("Done", lblCompleted, Color.GREEN));
            statsPanel.add(createStatCard("Overdue", lblOverdue, ACCENT_PINK));
            centerPanel.add(statsPanel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

            // Table
            String[] cols = {"ID", "Topic", "Subject", "Deadline", "Status"};
            tableModel = new DefaultTableModel(cols, 0);
            taskTable = new JTable(tableModel);
            styleTable(taskTable);

            JScrollPane scroll = new JScrollPane(taskTable);
            scroll.getViewport().setBackground(CARD_BG);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            
            centerPanel.add(scroll);
            
            // --- FOOTER (Buttons: Search, Edit, Delete) ---
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setOpaque(false);
            
            ModernButton btnBinary = new ModernButton("Binary Search", Color.BLUE);
            btnBinary.addActionListener(e -> performBinarySearch());
            
            // --- I ADDED THIS BACK: EDIT BUTTON ---
            ModernButton btnEdit = new ModernButton("Edit Selected", Color.ORANGE);
            btnEdit.addActionListener(e -> openEditTaskDialog());

            ModernButton btnDelete = new ModernButton("Delete", ACCENT_PINK);
            btnDelete.addActionListener(e -> deleteSelectedTask());
            
            footer.add(btnBinary);
            footer.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer
            footer.add(btnEdit); // Added back!
            footer.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer
            footer.add(btnDelete);
            
            centerPanel.add(footer);

            mainContent.add(centerPanel, BorderLayout.CENTER);
            add(mainContent, BorderLayout.CENTER);

            // Initial Load
            refreshTable();
            setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading Dashboard: " + e.getMessage());
        }
    }

    // --- Helpers ---
    private void refreshTable() { updateTableWithList(controller.getAllTasks()); updateStats(); }
    
    private void updateTableWithList(List<StudyTask> tasks) {
        tableModel.setRowCount(0);
        for (StudyTask t : tasks) tableModel.addRow(new Object[]{t.getId(), t.getTopic(), t.getSubject(), t.getDeadline(), t.getStatus()});
    }

    private void updateStats() {
        if(lblTotal == null) return;
        lblTotal.setText(String.valueOf(controller.getTotalCount()));
        lblPending.setText(String.valueOf(controller.getPendingCount()));
        lblCompleted.setText(String.valueOf(controller.getCompletedCount()));
        lblOverdue.setText(String.valueOf(controller.getOverdueCount()));
    }
    
    private void performBinarySearch() {
        String input = JOptionPane.showInputDialog(this, "Enter Exact Topic Name:");
        if (input != null && !input.isEmpty()) {
            int index = controller.binarySearchByTopic(input);
            if (index != -1) {
                refreshTable();
                taskTable.setRowSelectionInterval(index, index);
                JOptionPane.showMessageDialog(this, "Found at row " + (index + 1));
            } else {
                JOptionPane.showMessageDialog(this, "Not Found");
            }
        }
    }

    private void openAddTaskDialog() {
        JTextField t = new JTextField(), s = new JTextField(), d = new JTextField("2025-01-01");
        Object[] msg = {"Topic:", t, "Subject:", s, "Deadline:", d};
        if (JOptionPane.showConfirmDialog(this, msg, "Add", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            controller.addTask(t.getText(), s.getText(), d.getText());
            refreshTable();
        }
    }

    // --- EDIT DIALOG FUNCTIONALITY ---
    private void openEditTaskDialog() {
        int row = taskTable.getSelectedRow();
        if (row >= 0) {
            JTextField topic = new JTextField((String)tableModel.getValueAt(row, 1));
            JTextField subject = new JTextField((String)tableModel.getValueAt(row, 2));
            JTextField date = new JTextField((String)tableModel.getValueAt(row, 3));
            JComboBox<String> status = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
            status.setSelectedItem(tableModel.getValueAt(row, 4));
            
            Object[] msg = {"Topic:", topic, "Subject:", subject, "Deadline:", date, "Status:", status};
            
            if (JOptionPane.showConfirmDialog(this, msg, "Edit Task", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                 controller.updateTask(row, topic.getText(), subject.getText(), date.getText(), (String)status.getSelectedItem());
                 refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row first!");
        }
    }

    private void deleteSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row >= 0) { controller.deleteTask(row); refreshTable(); }
    }

    // --- Styling ---
    private void addMenuButton(JPanel p, String t, boolean a) {
        JLabel l = new JLabel(t); l.setForeground(a ? TEXT_WHITE : TEXT_GRAY);
        l.setBorder(new EmptyBorder(10,10,10,10)); l.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(l);
    }
    private void styleSimpleButton(JButton b) {
        b.setBackground(SIDEBAR_BG); b.setForeground(TEXT_GRAY); b.setBorderPainted(false); b.setFocusPainted(false); b.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    private JPanel createStatCard(String t, JLabel v, Color c) {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(CARD_BG); p.setBorder(new EmptyBorder(10,10,10,10));
        JLabel title = new JLabel(t); title.setForeground(TEXT_GRAY);
        v.setForeground(TEXT_WHITE); v.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(title, BorderLayout.NORTH); p.add(v, BorderLayout.CENTER);
        return p;
    }
    private void styleTable(JTable t) {
        t.setBackground(CARD_BG); t.setForeground(TEXT_WHITE); t.setRowHeight(30);
        t.getTableHeader().setBackground(BG_DARK); t.getTableHeader().setForeground(TEXT_GRAY);
    }
    class ModernButton extends JButton {
        Color c; ModernButton(String t, Color c) { super(t); this.c = c; setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setForeground(Color.WHITE); }
        protected void paintComponent(Graphics g) { g.setColor(c); g.fillRoundRect(0,0,getWidth(),getHeight(),10,10); super.paintComponent(g); }
    }
}