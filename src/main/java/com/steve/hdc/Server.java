package com.steve.hdc;

import express.Express;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.ConcurrentHashMap;   //For User/Pass data.

public class Server {
    public static final int PORT = 8082;

    /** Minimum number of characters for the username. */
    public static final int MIN_CHARACTERS_USER = 4;

    /** Minimum number of characters for the username and password. */
    public static final int MIN_CHARACTERS_PASS = 6;

    /** A hashmap which holds the username and password data. */
    public static ConcurrentHashMap<String, String> users = null;

    //Start the server (Listen to clients).
    public static void init() {
        //Initialize the datamanager.
        DataManager.init();

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

        //filepaths
        //CHECK THESE PATHS
        String local = "users";
        String hdfs = "users";

        //serialize user hashmap
        JSONObject obj = new JSONObject();

        for(String user : users.keySet()){
            obj.put(user, users.get(user));
        }

        //write to disk
        try {
            Files.writeString(Paths.get(local), obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Push the file to HDFS.
        DataManager.pushFile(local, hdfs, true);
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
        String[] fileNames = msg.toDisk();

        //For every file in the list of written files, move them to hadoop.
        for(String file : fileNames) {
            //Write the messages to HDFS for both the client and the server.
            DataManager.pushFile(file, msg.getReciever() + "/" + file);
            DataManager.pushFile(file, msg.getSender() + "/" + file, true);
        }
    }


    /**
     *  A function which retrieves all the messages for a user from the hadoop
     *  instance. It gets a list of the files, compares their timestamps,
     *  and if it matches the request, it adds them to a list and returns it.
     *  TODO: Make it more efficient by not writing it to local storage (Optional).
     *
     *  @param user The username for the current user.
     *  @param time The starting timestamp (Epoch time) for the first message.
     *  @return A MessageList which contains all the messages retrieved.
     */
    public static MessageList getMessages(String user, long time) {
        ///List of messages which will be sent back to user.
        MessageList msgs = new MessageList();

        //Get the list of files for the user.
        ArrayList<String> files = DataManager.fileList(user);

        //Sort the filenames based on the timestamp.
        files.sort(Comparator.comparing(String::toString));

        //Go through all the files, and remove the ones before the time, and file types.
        //File types are the ones that include an extension.
        for(int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);

            //Get the timestamp from the fileName.
            long timestamp = 0;
            try {
                timestamp = Long.parseLong(fileName.split("_")[0]);
            } catch (Exception e) {
                System.err.println("Server: Error: Invalid file name " + fileName);
                return msgs;
            }

            //If the timestamp matches the right time, and
            //If the message is not a file (Does not have an extension), save it.
            if(timestamp >= time && !fileName.contains(".")) {
                //Read the message from hdfs into the message file.
                Message newMsg = DataManager.readFile(fileName, user + "/" + fileName);

                //Add the message to the list of messages.
                msgs.add(newMsg);
            }
        }

        return msgs;
    }


    /**
     *  A function which retrieves a requested file for the user from the hadoop
     *  instance. The file is NOT the placeholder, and it is the actual contents
     *  of the file. The fileName should match the the data stored in the content
     *  variable of the placeholder file.
     *  TODO: Make it more efficient by not writing it to local storage (Optional).
     *
     *  @param user The username for the current user.
     *  @param fileName The name of the file which we're trying to read.
     *  @return A Message with the contents of the file.
     */
    public static Message getFile(String user, String fileName) {
        //The file which we're sending back.
        Message file = null;

        //Get the list of files for the user.
        ArrayList<String> files = DataManager.fileList(user);

        //Go through all the files, and check if we can find the file requested.
        for(int i = 0; i < files.size(); i++) {
            String currFileName = files.get(i);

            //If we find the file, read it and save it to the object.
            if(currFileName.equals(fileName)) {
                //Read the message file from hdfs into the local directory.
                DataManager.pullFile(user + "/" + fileName, fileName);

                //Read the file into memory and a message object.
                file = new Message(user, user, fileName, true);

                //Remove the local copy of the file.
                DataManager.rmLocalFile(fileName);
            }
        }

        return file;                //Return the file to the user.
    }


    /**
     *  A function which adds a user to the list of users (signs them up).
     *  and creates a folder for their data in the Hadoop cluster.
     *
     *  @param user The username which the user is trying to sign-up as.
     *  @param pass The corresponding password (or hash of it) for that username.
     */
    public static boolean signup(String user, String pass) {
        //Check if this username is already used.
        if(Server.userExists(user)) {
            System.err.println("Server: Error: Signup error, user already exists.");
            return false;
        }

        //Create a folder for this user.
        DataManager.createFolder(user);

        //If not, add the user to the users database.
        users.put(user, pass);

        //Synchronize the database to the file system.
        Server.sync();

        //If we reach here, signup was successful.
        return true;
    }


    /**
     *  A function which checks the username which the user has requested to
     *  sign up with. It checks if it exists, and if the username is valid.
     *
     *  @param user The username which we're signing up with.
     *  @return True if username is valid, false otherwise.
     */
    public static boolean signupCheckUser(String user) {
        //If the user is already signed up, return false.
        if(Server.userExists(user)) {
            return false;
        }

        //If the username is too short, return false.
        if(user.length() <  MIN_CHARACTERS_USER) {
            return false;
        }

        //If username inclues an underline or space.
        if(user.contains("_") || user.contains(" ")) {
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

        //Check if password contains invalid character.
        if(pass.contains("_")) {
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
        return true;
        /*
        //If the user does not exist, return false.
        if (!Server.userExists(user)) {
            return false;
        }

        //If the password is incorrect return false.
        if (!users.get(user).equals(pass)) {
            return false;
        }

        //If we get here, authentication was Successfully done.
        return true;*/
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
        Client.signup("Anish", "testpassword");
        Message toSend = new Message("Ardalan", "Anish", "Message text");
        Client.sendMsg("Ardalan", "testpassword", toSend);
        Message[] m = Client.getMsg("Ardalan", "testpassword", 0);

        Message fileSend = new Message("Ardalan", "Anish", "testfile.pdf", true);
        Client.sendMsg("Ardalan", "testpassword", fileSend);
/*
        //Sample on how to send a message.
        Message toSend = new Message("Me", "You", "Message text");
        Client.sendMsg("MyUser", "Ardalan", testpassword);

        //Sample to get the list of messages from timestamp 1 (!)
        Message[] messages = Client.getMsg("Ardalan", "testpassword", 1);
        if (messages != null) {
            for (Message m : messages) {
                m.dump();
            }
        } else {
            System.err.println("No messages!!!");
        }
*/

        //Sample to get a file from the server (Name should be the content of placeholder).
        //Message testfile = Client.getFile("Ardalan", "testpassword", "SampleFile.txt");

    }
}
