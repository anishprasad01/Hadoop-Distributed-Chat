package com.steve.hdc;

import java.io.*;                           //For Streams.
import java.util.*;                         //For Scanner.
import org.json.JSONObject ;                //For JSON parsing.

import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;

/**
 *  A class which handles the communication from the server to the clients.
 *  It handles requests/responses and provides an easy API to use for
 *  the client to connect to. The client can send messages, signup, recieve
 *  messages, and files from the server. These are based on agreed transfer
 *  protocols between the client and the server.
 *  TODO: Add basic HTTP authentication support.
 *  TODO: Add routing, storage, and error checking.
 *
 *  @author Ardalan Ahanchi
 *  @version 0.1
 */
public class Endpoints {



    /**
     *  The default (Root directory) endpoint, which just prints the program name.
     *
     *  @param req The HTTP request Object.
     *  @param res The HTTP response Object.
     */
    @DynExpress() // Default is context="/"
    public void getDefault(Request req, Response res) {
        res.send("Hadoop Distributed Chat Server.");
    }



    /**
     *  The signup endpoint, which creates a new user. It is passed a username,
     *  and password for creating the user. It communicates with the client with
     *  the agreed upon protocol.
     *
     *  @param req The HTTP request Object.
     *  @param res The HTTP response Object.
     */
    @DynExpress(context = "/signup", method = RequestMethod.POST)
    public void signup(Request req, Response res) {
        System.err.println("Server: Signup called from client " + req.getIp());

        //Get the body of the request, convert it to a string for parsing.
        InputStream stream = req.getBody();
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String request = s.hasNext() ? s.next() : "";

        //Create a JSON object from the text of the request, and extract data.
        JSONObject obj = new JSONObject(request);
        String user = (String) obj.get("user");
        String pass = (String) obj.get("pass");

        //TODO: Remove This section, instead add logic.
        System.err.println("Server: New user requested, user=" + user + " pass=" + pass);

        //TODO: Check if user/pass are valid, and successfully signed up.
        if(true) {
            //TODO: Save the user/pass in the proper place.
            res.setStatus(Status._201); //Code: Successfully Created
        } else if (true) {              //If username is invalid.
            res.setStatus(Status._409); //Code: Conflict
        } else if (true) {              //If password is invalid
            res.setStatus(Status._406); //Code: Not Acceptable
        } else {                        //If it's unknown
            res.setStatus(Status._400); //Code: Bad Request
        }

        //Send the response back.
        res.send();
    }



    /**
     *  The send endpoint, is used by the clients to send messages. It is passed
     *  a JSON object (Message serialized). It reads it and routes it to the right
     *  recipient. It communicates with the client with the agreed upon protocol.
     *
     *  @param req The HTTP request Object.
     *  @param res The HTTP response Object.
     */
    @DynExpress(context = "/send", method = RequestMethod.POST)
    public void sendMsg(Request req, Response res){
        System.err.println("Server: Message sent from client " + req.getIp());

        //Get the body of the request, convert it to a string for parsing.
        InputStream stream = req.getBody();
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String body = s.hasNext() ? s.next() : "";

        //Create a message object from the JSON of the body.
        //TODO: Check for errors.
        Message m = new Message(body);

        //TODO: Check if the user/pass are correct for basic auth.
        //TODO: Check if sender/recipient are correct in message.

        //TODO: Remove (Only for testing).
        m.dump();   //Show message content.

        //TODO: Route the message to the right client.

        //TODO: Check if everything went alright.
        if(!true) {
            res.setStatus(Status._201); //Code: Successfully Created
        } else if (true) {              //If user/password is invalid
            res.setStatus(Status._403); //Code: Forbidden
        } else if (true) {              //If sender/recipient are invalid
            res.setStatus(Status._406); //Code: Not Acceptable
        } else {                        //If it's unknown
            res.setStatus(Status._400); //Code: Bad Request
        }

        //Send the response back.
        res.send();
    }



    /**
     *  The recieve endpoint, is used by the clients to read messages. It is passed
     *  a timestamp. It groups all the messagaes from that timestamp into a JSON
     *  text, and sends it over to the client.  It communicates with the client
     *  with the agreed upon protocol.
     *
     *  @param req The HTTP request Object.
     *  @param res The HTTP response Object.
     */
    @DynExpress(context = "/recieve")
    public void getMsg(Request req, Response res) {
        System.err.println("Server: Recieve Message requested from client " + req.getIp());

        //Get the time-from parameter from the request.
        long timeFrom = Long.parseLong((String) req.getHeader("Time-From").get(0));

        //TODO: Add some routing code to retrieve messages.
        //TODO: Remove the example code.
        MessageList testList = new MessageList();
        testList.add(new Message("B", "A", "Test Message 1"));
        testList.add(new Message("C", "A", "Test Message 2"));
        testList.add(new Message("D", "A", "Test Message 2"));

        res.setStatus(Status._200);
        res.send(testList.toJSON());

        //TODO: Add error checking.
        //TODO: Send 403 when auth error, 200 when ok.
    }



    /**
     *  The file recieve endpoint, is used by the clients to read file messages.
     *  It is passed a filename. It reads the message into a JSON string and
     *  sends it over to the client.  It communicates with the client
     *  with the agreed upon protocol.
     *
     *  @param req The HTTP request Object.
     *  @param res The HTTP response Object.
     */
    @DynExpress(context = "/file")
    public void getFile(Request req, Response res){
        System.err.println("Server: File requested from client " + req.getIp());

        //Get the filename parameter from the request.
        String fileName = (String) req.getHeader("File-Name").get(0);

        //TODO: Add some routing code to retrieve messages (files).
        //TODO: Remove the example code.
        Message test = new Message("A", "B", "Test Message 1");
        res.setStatus(Status._200);
        res.send(test.toJSON());

        //TODO: Add error checking.
        //TODO: Send 403 when auth error, 200 when ok.
    }
}
