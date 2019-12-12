package com.steve.hdc;

import java.lang.*;
import java.util.*;
import java.io.*;                           //For de/serialization.
import java.nio.charset.Charset;            //For supprting UniCode.
import java.nio.charset.StandardCharsets;   //For UTF-8
import java.nio.file.*;                     //For supprting Files, and Path.
import org.json.JSONObject ;                //For JSON parsing.


/**
 *  A class which represents a HDC message which is passed between the chat
 *  clients and the server. It provides serialization, and deserialization
 *  to/from the JSON format.
 *
 *  @author Ardalan Ahanchi
 *  @version 1.0
 */
public class Message implements Serializable {
    // Predefined types supported in the clinet and the server *****************
    // Any other type would be a file type (Actual file content).
    // The content will differ based on the message type.

    /** Message type which is a string representing a message. */
    private static final String TYPE_MSG = "Message" ;

    /** FileName type represents a file placeholder (File's name as content) */
    private static final String TYPE_PLACEHOLDER = "Placeholder" ;

    // Message variables *******************************************************
    String sender ;         /**< Name of the sender of message. */
    String reciever ;       /**< Name of the reciever of message. */
    private long time ;             /**< Epoch time of the message creation. */
    private String type ;           /**< type of the message for context. */
    private byte[] content ;        /**< Msg content which depends on type. */

    // Constructors ************************************************************


    /**
     *  A constructor which creates a text message type. It is used for exchaning
     *  regular string messages (No data / files).
     *
     *  @param senderName Username of the sending party.
     *  @param recieverName Username of the reciever party.
     *  @param msgText A string which represents the message being sent.
     */
    public Message(String senderName, String recieverName, String msgText) {
        //Set the sender, and reciever names.
        this.sender = senderName ;
        this.reciever = recieverName ;

        //Calculate and set epoch time (Current time in milliseconds / 1000).
        this.time = System.currentTimeMillis() / 1000;

        //Set the message type, and content (Assume it's UniCode).
        this.type = TYPE_MSG ;
        this.content = msgText.getBytes(Charset.forName("UTF-8"));
    }


    /**
     *  A constructor which creates a data message type. This represents a file
     *  which can be sent as a message. Please make sure fileName exists in the
     *  correct directory before calling this constructor.
     *
     *  @param senderName Username of the sending party.
     *  @param recieverName Username of the reciever party.
     *  @param fileName A string which represents the name of the file being sent.
     *  @param isData A dummy boolean to mark this message as a data type.
     */
    public Message(String senderName, String recieverName, String fileName, boolean isData) {
        //Set the sender, and reciever names.
        this.sender = senderName ;
        this.reciever = recieverName ;

        //Calculate and set epoch time (Current time in milliseconds / 1000).
        this.time = System.currentTimeMillis() / 1000;

        //Set the file type as the file name + it's time stamp.
        //The timestamp is used to avoid duplication issues.
        this.type = String.valueOf(this.time) + "_" + fileName.toLowerCase();

        //Read the content from the disk and save it into the content.
        try {
            this.content = Files.readAllBytes(Paths.get(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  A constructor which reconstructs the message based on the serialized
     *  message which was previously saved to the Disk (By toDisk() method).
     *
     *  @param serializedFileName Name of the msg file which was serialized.
     *  @param isSerialized A dummy boolean to differentiate constructors.
     */
    public Message(String serializedFileName, boolean isSerialized) {
        try {
            //Open a stream for the serialized file.
            FileInputStream fis = new FileInputStream(serializedFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            //Read the message into the memory.
            Message newMsg = (Message) ois.readObject();

            //Close the streams.
            ois.close();
            fis.close();

            //Create a shallow copy of the object into the current object.
            this.sender = newMsg.sender ;
            this.reciever = newMsg.reciever ;
            this.time = newMsg.time ;
            this.type = newMsg.type ;
            this.content = newMsg.content ;
        } catch (Exception e) {
            //If we get here an error occured when reading serialized file.
            e.printStackTrace();
        }
    }


    /**
     *  A constructor which reconstructs the message based on the serialized
     *  JSON message which is passed to it.
     *
     *  @param jsonText Mesasge text which is used for parsing.
     */
    public Message(String jsonText) {
        //Create a JSON object from the text.
        JSONObject obj = new JSONObject(jsonText);

        //Get the values from the JSON object and initialize.
        this.sender = (String) obj.get("sender");
        this.reciever = (String) obj.get("reciever");
        this.time = Long.parseLong((String) obj.get("time"));
        this.type = (String) obj.get("type");

        //Decode the string content into an array of bytes.
        try {
            this.content = Base64.getDecoder().decode((String) obj.get("content"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  A constructor which creates a placeholder message for the passed data
     *  message. It is used in the server to create placeolders to pass to clients.
     *
     *  @param dataMsg The Messgage object which includes the actual data.
     */
    public Message(Message dataMsg) {
        //Same sender, reciever, and time as the data file.
        this.sender = dataMsg.sender ;
        this.reciever = dataMsg.reciever ;
        this.time = dataMsg.time ;

        //Set the type as the placeholder type.
        this.type = TYPE_PLACEHOLDER ;

        //The message content is the File's name (+Timestamp).
        this.content = dataMsg.type.getBytes(Charset.forName("UTF-8"));
    }

    // Helper Methods **********************************************************


    /**
     *  A method which is used in debugging, it prints the msg data and content
     *  to the std err in a nice format.
     */
    public void dump() {
        //Print the message data (General information).
        System.err.println("HDC: Dumping Message Metadata.");
        System.err.println("     Sender : " + this.sender);
        System.err.println("     Reciever : " + this.reciever);
        System.err.println("     Time : " + this.time);
        System.err.println("     Type : " + this.type);

        //Print the message content.
        System.err.println("HDC: Dumping Message Content.\n");
        System.err.println(this.content);
    }


    /**
     *  A method which writes (serializes) the contents of the messgae to
     *  the disk. It determines the fileName based on the contents of the message.
     *  The message can be reconstructed with one of the constructors.
     *  Specifically, public Message(String serializedFileName).
     *
     *  @return The file name in which the message was written to.
     */
    public String toDisk() {
        //The File name which is returned to the calling entity.
        String outputFile = "" ;

        //Check if it's a regular msg, or file placeholder.
        if(this.type.equals(TYPE_MSG) || this.type.equals(TYPE_PLACEHOLDER)) {
            outputFile = String.valueOf(time) + "_" + this.sender + "_" + this.reciever ;

            try {
                //Create streams for writing to output.
                FileOutputStream fos = new FileOutputStream(outputFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                //Write the current object to the file.
                oos.writeObject(this);

                //Close the streams.
                oos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {    //If it gets here it's a data file.

            try {
                //First write the actual file data to the disk.
                outputFile = this.type ;
                Files.write(Paths.get(outputFile), this.content);
            } catch (Exception e) {
                //If we get here there was an error writing the file.
                e.printStackTrace();
            }

            //Then create a placeholder file using this datafile.
            Message placeholder = new Message(this);

            //Write the placeholder to the disk as well.
            placeholder.toDisk();
        }

        //Return the name of the created file.
        return outputFile;
    }


    /**
     *  A function which translates Message objects into JSON strings.
     *
     *  @return A string in the format of JSON which represents message.
     */
    public String toJSON() {
        //Used to put the values in JSON format.
        JSONObject obj = new JSONObject();

        //Put all the class values to JSON.
        obj.put("sender", this.sender);
        obj.put("reciever", this.reciever);
        obj.put("time", String.valueOf(this.time));
        obj.put("type", this.type);

        //Encode the content and put it in the JSON object.
        try {
            obj.put("content", Base64.getEncoder().encodeToString(this.content));
        } catch(Exception e) {
            e.printStackTrace();
        }

        //Return the string back.
        return obj.toString();
    }


    /**
     *  A method which checks if two messages are equal to each other. It does
     *  not check the message content, but the size of the content, and the
     *  time stamp, along with the sender, and reciever.
     *
     *  @param other The Message object which we're comparing it to.
     *  @return True if the messages are the same, false otherwise.
     */
    public boolean equals(Message other) {
        return this.time == other.time && this.sender.equals(other.sender) &&
                this.reciever.equals(other.reciever) &&
                this.content.length == other.content.length ;
    }


    // Getters *****************************************************************


    /**
     *  A getter for the message sender's name.
     *
     *  @return A string which represents the sender's name.
     */
    public String getSender() {
        return this.sender;
    }


    /**
     *  A getter for the message reciever's name.
     *
     *  @return A string which represents the reciever's name.
     */
    public String getReciever() {
        return this.reciever;
    }


    /**
     *  A getter for the time when the message was created.
     *
     *  @return A long value which is the Epoch time of the message creation.
     */
    public long getTime() {
        return this.time;
    }


    /**
     *  A getter for the message's type. The type would provide context for
     *  the message content. Predefined types are explained at the beginning of
     *  this file.
     *
     *  @return A string which represents tbe message's type.
     */
    public String getType() {
        return this.type;
    }


    /**
     *  A getter which returns the message's content (Returned by reference).
     *
     *  @return An array of bytes which can be interpreted based on msg type.
     */
    public byte[] getContent() {
        return this.content;
    }


    /**
     *  A getter which returns the message's text content as a string.
     *
     *  @return A string which represents the message text.
     */
    public String getMessage() {
        return new String(this.content, Charset.forName("UTF-8"));
    }


}
