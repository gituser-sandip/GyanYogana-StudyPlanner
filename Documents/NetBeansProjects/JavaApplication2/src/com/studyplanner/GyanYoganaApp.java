package com.studyplanner;

import com.studyplanner.view.LoginView;
import javax.swing.SwingUtilities;

public class GyanYoganaApp {
    public static void main(String[] args) {
        // Run on Event Dispatch Thread (Best Practice for Swing)
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}