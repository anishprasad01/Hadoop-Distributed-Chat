package com.steve.hdc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataManager{

    public static final String ROOT_NAME = "HDFS_DATA";

    //A function to execute a given set of arguments.
    private static boolean exec(String args) {
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
    }

    //Create a root directory. TODO: Initialize it if it exists.
    public static void init() {
        exec("mkdir " + ROOT_NAME);
    }

    //Create a folder in the root directory by the given name.
    public static boolean createFolder(String foldername){
        return exec("mkdir " + ROOT_NAME + "/" + foldername);
    }


    //Copy a file from the local directory to the remote..
    public static boolean pushFile(String local, String hdfs){
        return exec("cp " + local + " " + ROOT_NAME + "/" + hdfs);
    }

    //Pushes a file and also removes the local copy.
    public static boolean pushFile(String local, String hdfs, boolean rm){
        //Remove the file if the move was successful.
        if(pushFile(local, hdfs)) {
            rmLocalFile(local);
            return true;
        }

        //If we get here it was unsuccesful.
        return false;
    }

    //Copy a file from the remote directory into the local.
    public static boolean pullFile(String hdfs, String local){
        return exec("cp  " + ROOT_NAME + "/" + hdfs + " " + local);
    }


    //Read a file from the Root name directory and send it back as a message.
    public static Message readFile(String local, String hdfs){
        exec("cp " + ROOT_NAME + "/" +  hdfs + " " + local);
        Message msg = new Message(local, true);
        rmLocalFile(local);
        return msg;
    }

    //Remove a file from the local directory.
    public static void rmLocalFile(String filename) {
        //Remove the local copy of the message.
        exec("rm " + filename);
    }

    //Return an arraylist of files.
    public static ArrayList<String> fileList(String filename){
        ArrayList<String> arr = new ArrayList<String> ();

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

        return arr;
    }
}
