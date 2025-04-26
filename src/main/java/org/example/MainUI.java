package org.example;

import javax.swing.*;

public class MainUI {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}