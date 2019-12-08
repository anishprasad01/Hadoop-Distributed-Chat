package com.steve.hdc.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {
    private JPanel panel1;
    private JButton sendMessageButton;

    public ClientGUI(){
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(panel1, "You clicked the Send button!");
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("HDC Client");
        frame.setContentPane(new ClientGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



}


