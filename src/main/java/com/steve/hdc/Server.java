package com.steve.hdc;

import express.Express;

public class Server {
    public static final int PORT = 8082;

    public static void main(String[] args) {
        //Run the Server (Start the endpoints).
        System.out.println("Hadoop Distributed Chat Server Started on Port " + PORT);
        Express app = new Express();
        app.bind(new Endpoints());
        app.listen(PORT);

        //TODO: Remove, add server code.
        //****************** CLIENT SAMPLES ************************************

        //Some example code on how to run and start a client.
        Client c = new Client();

        //Sample on how to sign up.
        c.signup("Ardalan", "testpassword");

        //Sample on how to send a message.
        Message toSend = new Message("Me", "You", "Message text");
        c.sendMsg("MyUser", "MyPass", toSend);

        //Sample to get the list of messages from timestamp 1 (!)
        Message[] messages = c.getMsg("Ardalan", "testpassword", 1);
        for(Message m : messages) {
            m.dump();
        }

        //Sample to get a file from the server (Name should be the content of placeholder).
        Message testfile = c.getFile("Ardalan", "testpassword", "SampleFile.txt");

    }
}
