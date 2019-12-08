package com.steve.hdc;

import express.Express;

public class Server {
    public static final int PORT = 8082;

    //Start the server (Listen to clients).
    public static void start() {
        //Run the Server and listen to connections (Start the endpoints).
        System.err.println("Hadoop Distributed Chat Server Started on Port " + PORT);
        Express app = new Express();
        app.bind(new Endpoints());
        app.listen(PORT);
    }

    //TODO: Implement.
    //Route the message to the right clients, and save to disk.
    public static void route(Message msg) { }

    //TODO: Implement.
    //Retrieve the messages from the server for the given user.
    public static MessageList getMessages(String user, long time) {
        MessageList testList = new MessageList();
        testList.add(new Message("B", "A", "Test Message 1"));
        testList.add(new Message("C", "A", "Test Message 2"));
        testList.add(new Message("D", "A", "Test Message 2"));
        return testList;
    }

    //TODO: Implement.
    //Retrieve a file from the server and send it to the user.
    public static Message getFile(String user, String fileName) { return new Message("a", "b", "c"); }

    //TODO: Implement.
    //Add the user to the list of users.
    public static boolean signup(String user, String pass) { return true; }

    //TODO: Implement.
    //Check the username for the signup.
    public static boolean signupCheckUser(String user) { return true; }

    //TODO: Implement.
    //Check the password for the signup.
    public static boolean signupCheckPass(String pass) { return true; }

    //TODO: Implement.
    //Check if a username is valid
    public static boolean userExists(String user) { return true; }

    //TODO: Implement.
    //Check the uesrname and password in the user database.
    public static boolean auth(String user, String pass) { return true; }


    public static void main(String[] args) {
        //Start the server and wait for clients.
        Server.start();

        //TODO: Remove, add server code.
        //****************** CLIENT SAMPLES ************************************

        //Some example code on how to run and start a client
        //All functions are static, so you call them without an object.

        //Sample on how to sign up.
        Client.signup("Ardalan", "testpassword");

        //Sample on how to send a message.
        Message toSend = new Message("Me", "You", "Message text");
        Client.sendMsg("MyUser", "MyPass", toSend);

        //Sample to get the list of messages from timestamp 1 (!)
        Message[] messages = Client.getMsg("Ardalan", "testpassword", 1);
        for(Message m : messages) {
            m.dump();
        }

        //Sample to get a file from the server (Name should be the content of placeholder).
        Message testfile = Client.getFile("Ardalan", "testpassword", "SampleFile.txt");

    }
}
