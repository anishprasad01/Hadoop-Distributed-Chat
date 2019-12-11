package com.steve.hdc;

import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Object;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.io.IOUtils;
<<<<<<< Updated upstream
=======
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
>>>>>>> Stashed changes
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager{
<<<<<<< Updated upstream
    static{ 
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }
    public void createFolder(String foldername){
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
        }

    }
    public void createFile(String local, String hdfs){
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
        }

    }

    public InputStream readFile(String filename) {
        InputStream in = null;
        String str1 = "hdfs://localhost:8080";
        in = new URL(str1).openStream();
        IOUtils.copyBytes(in, System.out, 1024, false);
        return in;
    }

    public List<String> fileList(String filename){
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
=======
    
    public static boolean createFolder(String foldername, Configuration conf) throws IOException{
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(foldername);
        boolean isSuccess = fs.mkdirs(path);
        fs.close();
        return isSuccess;
    }
    public static FSDataOutputStream createFile(String file, Configuration conf) throws IOException{
        FileSystem fs  = FileSystem.get(conf);
        Path path = new Path(file);
        if(!fs.exists(path)){
            System.err.println("File " + file + "already exists");
        }
        FSDataOutputStream out = fs.create(path);

        return out;
    }

    public static FSDataInputStream readFile(String file, Configuration conf) throws IOException{
        FileSystem fs  = FileSystem.get(conf);
        Path path = new Path(file);
        FSDataInputStream in = fs.open(path);
        return in;
    }


>>>>>>> Stashed changes

    public static List<Path> fileList(Path directory) throws IOException{
        List<Path> arr = new ArrayList<String> ();
        FileSystem fs = FileSystem.get(conf);
        RemoteIterator<LocatedFileStatus> i = fs.listFiles(path, true);
        while(i.hasNext()){
            LocatedFileStatus fileStatus = i.next();
            Path p = fileStatus.getPath();
            arr.add(p)
        }
        return arr;


    }
}