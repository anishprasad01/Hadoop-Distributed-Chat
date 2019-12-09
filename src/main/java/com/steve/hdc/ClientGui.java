package com.steve.hdc;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGui {
    private JButton sendButton;
    private JTextField sendMessageBox;
    private JTextPane recvMessagePane;
    private JPanel panel1;

    public ClientGui(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(), sendMessageBox.getText() + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hadoop Distributed Chat Client");
        frame.setContentPane(new ClientGui().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}


