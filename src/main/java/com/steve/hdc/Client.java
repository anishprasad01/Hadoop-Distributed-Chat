package com.steve.hdc;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * A class which handles the communication from the client user-side to the
 * server. It handles requests/responses and provides an easy API to use for
 * the client. The user can send messages, signup, recieve messages, and files
 * from the server. These are based on agreed transfer protocols between the
 * client and the server.
 * TODO: Add basic HTTP authentication support.
 *
 * @author Ardalan Ahanchi
 * @version 0.5
 */
public class Client {


    /**
     * A constant which represents the address of the server.
     */
    public static final String ADDRESS = "0.0.0.0:8082";


    /**
     * A function for sigining up the user in the server. It uses HTTP calls to
     * send username and password to the server, and it returns the signup status.
     *
     * @param user The username for the signup.
     * @param pass The password for the user.
     * @return Returns null if signup was successful, returns the error message otherwise.
     */
    public static String signup(String user, String pass) {
        //For holding the results of the signup.
        String result = null;
        try {
            //Establish a HTTP connection.
            URL url = new URL("http://" + ADDRESS + "/signup");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // set the request method and properties.
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            //Put the username and pass in the JSON object.
            JSONObject obj = new JSONObject();
            obj.put("user", user);
            obj.put("pass", pass);

            // Send post request and write the JSON data.
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(obj.toString());
            wr.flush();
            wr.close();

            //Get the response code and check if it was successful.
            Integer response = con.getResponseCode();
            switch (response) {
                //When everything went successfully.
                case 201:
                    System.err.println("Client: Successfully signed up.");
                    break;

                //If password was not up to standard, a 409 is passed.
                case 409:
                    System.err.println("Client: Error signing up.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Invalid Password.");
                    break;

                //If username is duplicated, or invalid it returns a 406.
                case 406:
                    System.err.println("Client: Error signing up.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Duplicate/Invalid Username.");
                    break;

                //In all the other cases, it's just a bad request.
                default:
                    System.err.println("Client: Error signing up.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Bad Request.");
                    break;
            }

            con.disconnect();       //Disconnect from the server.
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Return null if everything went successfully, the error message otherwise.
        return result;
    }


    /**
     * A function for sending a message to another user in the server. It uses HTTP
     * calls to send the message content to the server, and it returns the status.
     * TODO: Add basic HTTP authentication support.
     *
     * @param user The username for the sending user (Itself), to be used in Auth.
     * @param pass The password for the sneding user (Itself), to be used in Auth.
     * @param msg  The message object which will be sent (To server, then recipient).
     * @return Returns null if signup was successful, returns the error message otherwise.
     */
    public static String sendMsg(String user, String pass, Message msg) {
        //For holding the results for the message sending.
        String result = null;
        String basicAuth = credentials(user, pass);
        try {
            //Establish a HTTP connection.
            URL url = new URL("http://" + ADDRESS + "/send");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // set the request method and properties.
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", basicAuth);

            // Send post request and write the JSON data from the message.
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(msg.toJSON());
            wr.flush();
            wr.close();

            //Get the response code and check if it was successful.
            Integer response = con.getResponseCode();
            switch (response) {
                //When sending was successfully accomplished.
                case 201:
                    System.err.println("Client: Message Successfully Sent.");
                    break;

                //If an error occured during authentication.
                case 403:
                    System.err.println("Client: Authentication error.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Invalid Username/Password.");
                    break;

                //If the sender/recipient were not valid.
                case 406:
                    System.err.println("Client: Bad Message Was Sent.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Invalid Sender/Recipient.");
                    break;

                //If a bad request was made.
                default:
                    System.err.println("Client: Error Sending Message.");
                    System.err.println("        " + con.getResponseMessage());
                    result = new String("Bad Request.");
                    break;
            }

            con.disconnect();       //Disconnect from the server.
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Return null if everything went fine, send error message back otherwise.
        return result;
    }


    /**
     * A function for recieving all messages from the server. It uses HTTP
     * calls to send the timestamp for the first message (timeFrom) to the server
     * and returns the status and retrieves a list of messages.
     * TODO: Add basic HTTP authentication support.
     *
     * @param user     The username for the sending user (Itself), to be used in Auth.
     * @param pass     The password for the sneding user (Itself), to be used in Auth.
     * @param timeFrom Epoch time (in milliseconds) for the first message retrieved.
     * @return An array of message objects with all the corresponding messages.
     * Null if no messages were present.
     */
    public static Message[] getMsg(String user, String pass, long timeFrom) {
        //Used to store the list of messages recieved from the server.
        MessageList msgList = new MessageList();
        String basicAuth = credentials(user, pass);

        try {
            URL url = new URL("http://" + ADDRESS + "/recieve");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // set the request method and properties.
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", basicAuth);

            //Save the time as a property.
            con.setRequestProperty("Time-From", String.valueOf(timeFrom));

            //Get the response code and check if it was successful.
            Integer response = con.getResponseCode();
            switch (response) {
                //When the server returns an OK code, read the data (Messages).
                case 200:
                    System.err.println("Client: Message Successfully Recieved.");

                    //Read the data from the server response.
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    //Create a stream for the data.
                    String output;
                    StringBuffer data = new StringBuffer();

                    //Read from the stream into the data.
                    while ((output = in.readLine()) != null) {
                        data.append(output);
                    }

                    System.out.println(data.toString());

                    //Parse the JSON into a list of messages.
                    msgList = new MessageList(data.toString());

                    //Clode the buffer.
                    in.close();
                    break;

                //When there was an error in authentication.
                case 403:
                    System.err.println("Client: Authentication error.");
                    System.err.println("        " + con.getResponseMessage());
                    break;

                //When anything else is sent, print the error.
                default:
                    System.err.println("Client: Error Sending Message.");
                    System.err.println("        " + con.getResponseMessage());
                    break;
            }

            con.disconnect();       //Disconnect from the server.
        } catch (Exception e) {
            e.printStackTrace();
        }

        //If no messages were queued, send null.
        if (msgList.size() == 0) {
            return null;
        }

        //Convert the message list into an array of messages and return it.
        return msgList.toArray();
    }


    /**
     * A function for recieving a file message from the server. It uses HTTP
     * calls to send the filename to the server. it then returns the status and
     * retrieves the message object for the file.
     * TODO: Add basic HTTP authentication support.
     *
     * @param user     The username for the sending user (Itself), to be used in Auth.
     * @param pass     The password for the sneding user (Itself), to be used in Auth.
     * @param fileName Name of the file (Same as the content of file's placeholder).
     * @return An array of message objects with all the corresponding messages.
     */
    public static Message getFile(String user, String pass, String fileName) {
        //A message which contains the file from the server.
        Message file = null;

        try {
            //Establish a HTTP connection to the server.
            URL url = new URL("http://" + ADDRESS + "/file");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            String basicAuth = credentials(user, pass);

            // set the request method and properties.
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", basicAuth);

            //Save the file name as a property.
            con.setRequestProperty("File-Name", fileName);

            //TODO: Add basic HTTP auth.

            //Get the response code and check if it was successful.
            Integer response = con.getResponseCode();
            switch (response) {
                //When the server returns an OK code, read the data (Messages).
                case 200:
                    System.err.println("Client: Message Successfully Recieved.");

                    //Read the data from the server response.
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    //Create a stream for the data.
                    String output;
                    StringBuffer data = new StringBuffer();

                    //Read from the stream into the data.
                    while ((output = in.readLine()) != null) {
                        data.append(output);
                    }

                    System.out.println(data.toString());

                    //Parse the JSON into the message (File).
                    file = new Message(data.toString());

                    //Clode the buffer.
                    in.close();
                    break;

                //When there was an error in authentication.
                case 403:
                    System.err.println("Client: Authentication error.");
                    System.err.println("        " + con.getResponseMessage());
                    break;

                //If the file does not exist.
                case 404:
                    System.err.println("Client: File does not exist.");
                    System.err.println("        " + con.getResponseMessage());
                    break;

                //When anything else is sent, print the error.
                default:
                    System.err.println("Client: Error Recieving File.");
                    System.err.println("        " + con.getResponseMessage());
                    break;
            }

            con.disconnect();       //Disconnect from the server.
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;                //Return the file message.
    }

    private static String credentials(String user, String pass) {
        String userCredentials = user + " " + pass;
        return "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
    }
}
