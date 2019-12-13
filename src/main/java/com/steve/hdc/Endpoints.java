package com.steve.hdc;

import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * A class which handles the communication from the server to the clients.
 * It handles requests/responses and provides an easy API to use for
 * the client to connect to. The client can send messages, signup, recieve
 * messages, and files from the server. These are based on agreed transfer
 * protocols between the client and the server.
 * TODO: Add basic HTTP authentication support.
 *
 * @author Ardalan Ahanchi
 * @version 0.1
 */
public class Endpoints {


    /**
     * The default (Root directory) endpoint, which just prints the program name.
     *
     * @param req The HTTP request Object.
     * @param res The HTTP response Object.
     */
    @DynExpress() // Default is context="/"
    public void getDefault(Request req, Response res) {
        res.send("Hadoop Distributed Chat Server.");
    }


    /**
     * The signup endpoint, which creates a new user. It is passed a username,
     * and password for creating the user. It communicates with the client with
     * the agreed upon protocol.
     *
     * @param req The HTTP request Object.
     * @param res The HTTP response Object.
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


        //Check if Username is invalid.
        if (!Server.signupCheckUser(user)) {
            res.setStatus(Status._409);                     //Code: Conflict
            res.send();                                     //Send response back.
            return;
        }

        //Check if Password is invalid.
        if (!Server.signupCheckPass(pass)) {
            res.setStatus(Status._409);                     //Code: Not Acceptable
            res.send();                                     //Send response back.
            return;
        }

        //Create the user, and check it's status.
        if (!Server.signup(user, pass)) {
            res.setStatus(Status._400);                     //Code: Bad Request
            res.send();                                     //Send response back.
            return;
        }

        //If we get here everythin wen't according to the plan.
        System.err.println("Server: Created new user. Username=" + user);

        //Send success response
        res.setStatus(Status._201);                         //Code: Successfully Created

        //Send the response back.
        res.send();
    }


    /**
     * The send endpoint, is used by the clients to send messages. It is passed
     * a JSON object (Message serialized). It reads it and routes it to the right
     * recipient. It communicates with the client with the agreed upon protocol.
     * TODO: Get user/pass from basic Auth.
     *
     * @param req The HTTP request Object.
     * @param res The HTTP response Object.
     */
    @DynExpress(context = "/send", method = RequestMethod.POST)
    public void sendMsg(Request req, Response res) {
        System.err.println("Server: Message sent from client " + req.getIp());

        //Get the body of the request, convert it to a string for parsing.
        InputStream stream = req.getBody();
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String body = s.hasNext() ? s.next() : "";

        //Get Authorization Info
        String[] authArray = Endpoints.getAuthInfo(req);
        String user = authArray[0];
        String pass = authArray[1];

        //Check if authentication is unsuccessful.
        if (!Server.auth(user, pass)) {
            res.setStatus(Status._403);                     //Code: Forbidden
            res.send();                                     //Send response back.
            return;
        }

        //Create a message object from the JSON of the body.
        Message msg = new Message(body);

        //Check if the recipient exists (is a valid user), and the sender
        if (Server.userExists(msg.getReciever()) && msg.getSender().equals(user)) {
            Server.route(msg);          //Route the message to the right user/s.
        } else {
            //If the message is not acceptable (sender/recipient are invalid).
            res.setStatus(Status._406);                     //Code: Not Acceptable
            res.send();                                     //Send response back.
            return;
        }

        //If everything went alright.
        res.setStatus(Status._201);                         //Code: Successfully Created
        res.send();                                         //Send the response back.
    }


    /**
     * The recieve endpoint, is used by the clients to read messages. It is passed
     * a timestamp. It groups all the messagaes from that timestamp into a JSON
     * text, and sends it over to the client.  It communicates with the client
     * with the agreed upon protocol.
     * TODO: Get user/pass from basic Auth.
     *
     * @param req The HTTP request Object.
     * @param res The HTTP response Object.
     */
    @DynExpress(context = "/recieve")
    public void getMsg(Request req, Response res) {
        System.err.println("Server: Recieve Message requested from client " + req.getIp());

        //Get Authorization Info
        String[] authArray = Endpoints.getAuthInfo(req);
        String user = authArray[0];
        String pass = authArray[1];

        //Check if authentication is unsuccessful.
        if (!Server.auth(user, pass)) {
            res.setStatus(Status._403);                     //Code: Forbidden
            res.send();                                     //Send response back.
            return;
        }

        //Get the time-from parameter from the request.
        long timeFrom = Long.parseLong((String) req.getHeader("Time-From").get(0));

        //Retrieve messages from the server.
        MessageList messages = Server.getMessages(user, timeFrom);

        //If we get here everything went alright.
        res.setStatus(Status._200);                         //Code: OK
        res.send(messages.toJSON());                        //Send message and response.
    }


    /**
     *  A function which gets the Auth header from the request, parses the
     *  username and password. And retunrs an array with username and password.
     *
     *  @param req The HTTP request recieved.
     *  @return An array with the first string being the username, second passowrd.
     */
    public static String[] getAuthInfo(Request req) {
        return req.getHeader("Auth").get(0).split("_");
    }


    /**
     * The file recieve endpoint, is used by the clients to read file messages.
     * It is passed a filename. It reads the message into a JSON string and
     * sends it over to the client.  It communicates with the client
     * with the agreed upon protocol.
     * TODO: Get user/pass from basic Auth.
     *
     * @param req The HTTP request Object.
     * @param res The HTTP response Object.
     */
    @DynExpress(context = "/file")
    public void getFile(Request req, Response res) {
        System.err.println("Server: File requested from client " + req.getIp());

        //Get the filename parameter from the request.
        String fileName = (String) req.getHeader("File-Name").get(0);

        //Get Authorization Info
        String[] authArray = Endpoints.getAuthInfo(req);
        String user = authArray[0];
        String pass = authArray[1];

        //Check if authentication is unsuccessful, or filename is invalid.
        if (!Server.auth(user, pass) || fileName.contains("..")) {
            res.setStatus(Status._403);                     //Code: Forbidden
            res.send();                                     //Send response back.
            return;
        }

        //Retrieve the file from the server and make sure it exists.
        Message file = Server.getFile(user, fileName);

        //Check if file doesn't exist.
        if (file == null) {
            res.setStatus(Status._404);                     //Code: Not Found.
            res.send();                                     //Send response back.
            return;
        }

        //If we get here everything went fine.
        res.setStatus(Status._200);                         //Code: OK
        res.send(file.toJSON());                            //Send message and response.
    }
}
