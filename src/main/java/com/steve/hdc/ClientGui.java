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
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField recipientField;
    private JButton signUpButton;
    private JButton getFileButton;
    private JTextField fileNameField;

    public ClientGui(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String recipient = recipientField.getText();
                String toTextBox = null;

                String response = Client.sendMsg(username, password, new Message(username, recipient, sendMessageBox.getText()));

                if(response == null){
                    toTextBox = username + ": " + sendMessageBox.getText();
                }
                else if(response.equals("Invalid Username/Password.")){
                    toTextBox = "Please Check your Username and Password";
                }
                else if(response.equals("Invalid Sender/Recipient.")){
                    toTextBox = "Unknown Recipient";
                }
                else{
                    toTextBox = "Something went wrong. Please try again later.";
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                Document sendDoc = sendMessageBox.getDocument();
                try {
                    sendDoc.remove(0,sendDoc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String toTextBox = null;

                String response;

                response = Client.signup(username, password);

                if(response == null){
                    toTextBox = username + " signed up.";
                }
                else{
                    toTextBox = response;
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        });

        getFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = usernameField.getText();
                char[] passwordArray = passwordField.getPassword();
                String password = passwordArray.toString();
                String filename = fileNameField.getText();
                String toTextBox;

                Message response = Client.getFile(username, password, filename);

                if(response != null){
                    toTextBox = response.getContent().toString();
                }
                else {
                    toTextBox = "File Retrieval Failed";
                }

                Document doc = recvMessagePane.getDocument();
                try {
                    doc.insertString(doc.getLength(),toTextBox + "\n",null);
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


