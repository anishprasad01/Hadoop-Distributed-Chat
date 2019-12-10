package com.steve.hdc;

import express.Express;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static final int PORT = 8082;

    /**
     * Minimum number of characters for the username.
     */
    public static final int MIN_CHARACTERS_USER = 4;

    /**
     * Minimum number of characters for the username and password.
     */
    public static final int MIN_CHARACTERS_PASS = 6;

    public static ConcurrentHashMap<String, String> users = null;

    public static ConcurrentHashMap getUsers() {
        return users;
    }

    //Start the server (Listen to clients).
    public static void init() {
        //Initialize the user's hashmap.

        //TODO: Check if serialized users file is found on server root.
        //      If it is, read it into the users hashmap.
        //      If not, just initalize it like this:
        users = new ConcurrentHashMap<>();

        //Run the Server and listen to connections (Start the endpoints).
        System.err.println("Hadoop Distributed Chat Server Started on Port " + PORT);
        Express app = new Express();
        app.bind(new Endpoints());
        app.listen(PORT);
    }

    //Write the ram stuff into the HDFS cluster via serialization.
    public static synchronized void sync() {
        //TODO: Serialize the users and write it to HDFS using DataManager.
        //      As it is, we have to serialize the users hashmap, write it to local
        //      disk, then call createFile to write it to hdfs. Then remove it from
        //      The local filesystem.
    }


    /**
     * A function which takes a message and saves it to the hdfs folder for the
     * sender and the recipient. As it is, it writes the data locally, and moves
     * it to the HDFS instance. Then the local data is deleted. The error checking
     * should be done by the calling instance (In this case, it is done by the
     * endpoints class).
     * TODO: Make it more efficient by not writing it to local storage (Optional).
     *
     * @param msg The message which we're trying to route.
     */
    public static void route(Message msg) {
        //Write the message to local disk.
        String fileName = msg.toDisk();

        //Create the filename for the message which will be saved.
        String recieverPath = msg.getReciever() + "/" + fileName;
        String senderPath = msg.getSender() + "/" + fileName;

        //Write the messages to HDFS for both the client and the server.
        DataManager.createFile(fileName, recieverPath);
        DataManager.createFile(fileName, senderPath);

        //Remove the local copy of the message.
        File file = new File(fileName);
        if (!file.delete()) {
            System.err.println("Server: Error: Can't delete file from local storage");
        }
    }

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
    public static Message getFile(String user, String fileName) {
        return new Message("a", "b", "c");
    }


    /**
     * A function which adds a user to the list of users (signs them up).
     * and creates a folder for their data in the Hadoop cluster.
     *
     * @param user The username which the user is trying to sign-up as.
     * @param pass The corresponding password (or hash of it) for that username.
     */
    public static boolean signup(String user, String pass) {
        //Check if this username is already used.
        if (Server.userExists(user)) {
            System.err.println("Server: Error: Signup error, user already exists.");
            return false;
        }

        //Create a folder for this user.
//        DataManager.createFolder(user);

        //If not, add the user to the users database.
        users.put(user, pass);
        if (users.containsKey(user)) {
            System.out.println("User added success");
        }

        //Synchronize the database to the file system.
        Server.sync();

        // ********* return true just to get working ***************
        return true;
    }


    /**
     * A function which checks the username which the user has requested to
     * sign up with. It checks if it exists, and if the username is valid.
     *
     * @param user The username which we're signing up with.
     * @return True if username is valid, false otherwise.
     */
    public static boolean signupCheckUser(String user) {
        //If the user is already signed up, return false.
        if (Server.userExists(user)) {
            return false;
        }

        //If the username is too short, return false.
        if (user.length() < MIN_CHARACTERS_USER) {
            return false;
        }

        //If we get here, the username is acceptable.
        return true;
    }


    /**
     * A function which checks the password which the user has requested to
     * sign up with. It checks if it exists, and if the password is valid.
     *
     * @param pass The password which we're signing up with.
     * @return True if password is valid, false otherwise.
     */
    public static boolean signupCheckPass(String pass) {
        //If the password is too short, return false.
        if (pass.length() < MIN_CHARACTERS_PASS) {
            return false;
        }

        //If we get here, the password is acceptable.
        return true;
    }


    /**
     * Checks to see if a username exists in the server, if it does it returns
     * ture, if not false.
     *
     * @param user The username which we're trying to check.
     * @return True if user exists, false otherwise.
     */
    public static boolean userExists(String user) {
        //If the user is valid, just return true.
        if (users.containsKey(user)) {
            return true;
        }

        //If not, return false.
        return false;
    }


    /**
     * A function which checks if the username and passowrd are valid
     * it checks if the usre is signed up already, and if the password
     * is correct.
     *
     * @param user The username which the user is trying to sign-in with.
     * @param pass The password which the user is trying to sign-in with.
     */
    public static boolean auth(String user, String pass) {
        //If the user does not exist, return false.
        if (!Server.userExists(user)) {
            System.out.println("here 1");
            return false;
        }

        //If the password is incorrect return false.
        if (!users.get(user).equals(pass)) {
            System.out.println("here 2");
            return false;
        }

        //If we get here, authentication was Successfully done.
        return true;
    }

    public static String[] getAuthInfo(List authHeader) {
        String authString = Arrays.toString(authHeader.toArray());
        int left = authString.indexOf("c");
        int right = authString.indexOf("]");
        String authInfo = authString.substring(left + 2, right);
        System.out.println("authinfo: " + authInfo);
        String decodedAuth = new String(Base64.getDecoder().decode(authInfo.getBytes()));
        return decodedAuth.split(" ");
    }


    public static void main(String[] args) {
        //Initialize and Start the server and wait for clients.
        Server.init();

        //TODO: Remove, add server code.
        //****************** CLIENT SAMPLES ************************************

        //Some example code on how to run and start a client
        //All functions are static, so you call them without an object.

        //Sample on how to sign up.
        Client.signup("Ardalan", "testpassword");

        //Sample on how to send a message.
        Message toSend = new Message("Me", "You", "Message text");
        Client.sendMsg("Ardalan", "testpassword", toSend);

        //Sample to get the list of messages from timestamp 1 (!)
        Message[] messages = Client.getMsg("Ardalan", "testpassword", 1);
        if (messages != null) {
            for (Message m : messages) {
                m.dump();
            }
        } else {
            System.err.println("No messages!!!");
        }


        //Sample to get a file from the server (Name should be the content of placeholder).
        Message testfile = Client.getFile("Ardalan", "testpassword", "SampleFile.txt");

    }
}
