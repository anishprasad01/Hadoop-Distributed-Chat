package com.steve.hdc;

import java.util.ArrayList;                 //For ArrayLists.
import org.json.JSONObject ;                //For JSON parsing.


/**
 *  A class which represents a list of HDC messages, it is used to parse
 *  to/from JSON. This allows multiple messages to be sent at the same time.
 *
 *  @author Ardalan Ahanchi
 *  @version 1.0
 */
public class MessageList {
    /** A list which holds the JSON representation of messages. */
    private ArrayList<String> list;


    /**
     *  Default constructor which intializes the list, messages can be added by
     *  the add(Message) method on this object.
     */
    public MessageList() {
        list = new ArrayList<String>();
    }


    /**
     *  A constructor which recreates the message list from the JSON created by
     *  MessageList's toJSON method. This is used to translate the recieved JSON
     *  into this object. The accepted format is the number of messages as a JSON
     *  parameter, and then the messages mapped to keys indexed at 0.
     *
     *  @param jsonText A JSON representation for the list of messages.
     */
    public MessageList(String jsonText) {
        //Initialize the list.
        list = new ArrayList<String>();

        //Create a JSON object from the text.
        JSONObject obj = new JSONObject(jsonText);

        //Get the number of messages.
        int numOfMessages = (int) obj.get("number_of_msgs");

        //Go through the JSON, and get messages in each index and put it in list.
        for(int i = 0; i < numOfMessages; i++) {
            list.add((String) obj.get(Integer.toString(i)));
        }
    }


    /**
     *  A method which adds the message to the message list object. It is used
     *  by the server to store the messages.
     *
     *  @param msg The message object which we'll add to this list.
     */
    public void add(Message msg) {
        //Convert the message to JSON, and add it to the list.
        list.add(msg.toJSON());
    }


    /**
     *  A method which translates this MessageList object into a JSON string.
     *  This is used by the server to transmit a list of messages to the client.
     *
     *  @return The string representation of this message list.
     */
    public String toJSON() {
        //Used to put the values in JSON format.
        JSONObject obj = new JSONObject();

        //Put the number of messages in the JSON.
        obj.put("number_of_msgs", list.size());

        //Go through the list and add the JSONs for each message into an index.
        for(int i = 0; i < list.size(); i++) {
            obj.put(Integer.toString(i), list.get(0));
        }

        //Convert to a string and send it back.
        return obj.toString();
    }


    /**
     *  A method which returns an array of message objects from this message list
     *  object. It is used by the client to convert this object into easily accessible
     *  data (Actual messages).
     *
     *  @return An array of Messages initialized with the data.
     */
    public Message[] toArray() {
        Message[] msgArr = new Message[list.size()];
        for(int i = 0; i < list.size(); i++) {
            msgArr[i] = new Message(list.get(i));
        }
        return msgArr;
    }

    /**
     *  A method which retrieves the number of messages in the list.
     *
     *  @return The number of messages in the list.
     */
    public int size() {
        return list.size();
    }
}
