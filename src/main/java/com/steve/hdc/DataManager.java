package com.steve.hdc;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataManager{
    static{
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }
    public static boolean createFolder(String foldername){
        try{
            Process p = Runtime.getRuntime().exec("hdfs dfs –mkdir " + foldername);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            //read the output from the command
            String s = stdInput.readLine();
            while(s != null){
                System.out.println(s);
            }
            //read any errors from the attempted command
            String s1 = stdError.readLine();
            while(s1 != null){
                System.out.println(s1);
            }

        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
//TEMP
        return true;
    }

    public static boolean createFile(String local, String hdfs){
        try{
            Process p = Runtime.getRuntime().exec("hdfs dfs –put " + local + " " + hdfs);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // read the output from the command
            String s = stdInput.readLine();
            while (s != null) {
                System.out.println(s);
            }
            // read any errors from the attempted command
            String s1 = stdError.readLine();
            while (s1 != null) {
                System.out.println(s1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    //TEMP
        return true;
    }

    public static Message readFile(String filename, String local, String hdfs) throws IOException {
        try{
            Process p = Runtime.getRuntime().exec("hdfs dfs –copyToLocal " + hdfs + " " + local);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // read the output from the command
            String s = stdInput.readLine();
            while (s != null) {
                System.out.println(s);
            }
            // read any errors from the attempted command
            String s1 = stdError.readLine();
            while (s1 != null) {
                System.out.println(s1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Message(local, true);
    }

    public static List<String> fileList(String filename){
        List<String> arr = new ArrayList<String> ();
        try{
            Process p = Runtime.getRuntime().exec("hdfs dfs –ls " + filename);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            //read the output from the command
            // -rw-r--r--   3 pansyng_css534 supergroup        111 2019-12-07 18:46 /user/pansyng_css534/input/center.txt
            String s = stdInput.readLine();
            while(s != null){
                String[] pline = s.split(":");
                if(pline.length == 2){
                    String [] pline1 = pline[1].split(" ");
                    if(pline1.length == 2){
                        arr.add(pline1[1]);
                    }
                }

            }
            //read any errors from the attempted command
            String s1 = stdError.readLine();
            while(s1 != null){
                System.out.println(s);
            }

        } catch(IOException e){
            e.printStackTrace();
        }
        return arr;

    }
}
