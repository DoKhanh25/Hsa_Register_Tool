package Swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingUI {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;

    public SwingUI() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUI swingUI = new SwingUI();
        swingUI.showButtonDemo();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Vi du Java Swing - JButton");
        mainFrame.setSize(400, 300);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(350, 100);
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showButtonDemo() {
        JButton okButton = new JButton("OK");
        JLabel excelLocationLabel = new JLabel("Url excel", JLabel.RIGHT);
        JLabel logFileLocationLabel = new JLabel("Log File: ", JLabel.CENTER);
        JTextField excelLocationText = new JTextField(40);
        JTextField logFileLocationText = new JTextField(40);

        excelLocationText.setSize(300, 50);
        logFileLocationText.setSize(300, 50);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        controlPanel.add(excelLocationLabel);
        controlPanel.add(excelLocationText);
        controlPanel.add(logFileLocationLabel);
        controlPanel.add(logFileLocationText);
        controlPanel.add(okButton);
        mainFrame.setVisible(true);
    }

}
