package com.steve.hdc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataManager{

    /** The root folder name for the stored data. */
    public static final String ROOT_NAME = "HDFS_DATA";

    /** Checks if the system is running locally or remotely. */
    public static boolean isLocal = true;

    //A function to execute a given set of arguments.
    private static boolean exec(String args) {
        //If we're running locally.
        if(DataManager.isLocal) {
            try{
                //Execute the commands.
                Process p = Runtime.getRuntime().exec(args);

                //Wait for the process to finish.
                p.waitFor();
            } catch(Exception e){
                e.printStackTrace();
                return false;
            }

            return true;

        //If we're in remote mode.
        } else {

        }

        //If we get here, an error occured.
        return false;
    }

    //Create a root directory. TODO: Initialize it if it exists.
    public static void init(boolean local) {
        DataManager.isLocal = local ;

        System.err.println("DataManager: Initialized with " + (local ? "local" : "remote") + " storage.");

        //If we're running locally.
        if(DataManager.isLocal) {
            exec("mkdir " + ROOT_NAME);

        //If we're in remote mode.
        } else {

        }
    }

    //Create a folder in the root directory by the given name.
    public static boolean createFolder(String foldername){
        //If we're running locally.
        if(DataManager.isLocal) {
            return exec("mkdir " + ROOT_NAME + "/" + foldername);

        //If we're in remote mode.
        } else {

        }

        //If we get here it was unsuccesful.
        return false;
    }


    //Copy a file from the local directory to the remote..
    public static boolean pushFile(String local, String hdfs){
        //If we're running locally.
        if(DataManager.isLocal) {
            return exec("cp " + local + " " + ROOT_NAME + "/" + hdfs);

        //If we're in remote mode.
        } else {

        }

        //If we get here it was unsuccesful.
        return false;
    }

    //Pushes a file and also removes the local copy.
    public static boolean pushFile(String local, String hdfs, boolean rm){
        //If we're running locally.
        if(DataManager.isLocal) {
            //Remove the file if the move was successful.
            if(pushFile(local, hdfs)) {
                rmLocalFile(local);
                return true;
            }

        //If we're in remote mode.
        } else {

        }

        //If we get here it was unsuccesful.
        return false;
    }

    //Copy a file from the remote directory into the local.
    public static boolean pullFile(String hdfs, String local){
        //If we're running locally.
        if(DataManager.isLocal) {
            return exec("cp  " + ROOT_NAME + "/" + hdfs + " " + local);

        //If we're in remote mode.
        } else {

        }

        //If we get here it was unsuccesful.
        return false;
    }


    //Read a file from the Root name directory and send it back as a message.
    public static Message readFile(String local, String hdfs){
        Message msg = null;

        //If we're running locally.
        if(DataManager.isLocal) {
            exec("cp " + ROOT_NAME + "/" +  hdfs + " " + local);
            msg = new Message(local, true);
            rmLocalFile(local);

        //If we're in remote mode.
        } else {

        }

        return msg;
    }

    //Remove a file from the local directory.
    public static void rmLocalFile(String filename) {
        //If we're running locally.
        if(DataManager.isLocal) {
            //Remove the local copy of the message.
            exec("rm " + filename);

        //If we're in remote mode.
        } else {

        }

    }

    //Return an arraylist of files.
    public static ArrayList<String> fileList(String filename){
        //To hold the filenames.
        ArrayList<String> arr = new ArrayList<String> ();

        //If we're running locally.
        if(DataManager.isLocal) {
            try{
                //List all the files, while changing their names
                Process p = Runtime.getRuntime().exec("ls " + ROOT_NAME + "/" + filename + "/ | tr \"\t\" \"\n\"");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                //Read every line of the std output and save it to array.
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    arr.add(s);
                }

                //Remove the head of array since it's the command executed.
                arr.remove(0);

                //Wait till the execution is complete.
                p.waitFor();

            } catch(Exception e){
                e.printStackTrace();
            }

        //If we're in remote mode.
        } else {

        }

        return arr;
    }
}
