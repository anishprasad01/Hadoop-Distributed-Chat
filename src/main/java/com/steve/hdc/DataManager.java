package com.steve.hdc;

import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Object;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.*;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataManager{
    public FileSystem configureFileSystem(String coreSitePath, String hdfsSitePath){
        FileSystem fs = null;
        try{
            Configuration conf = new Configuration();
            conf.setBoolean("dfs.support.append", true);
            Path coreSite = new Path(coreSitePath);
            Path hdfsSite = new Path(hdfsSitePath);
            conf.addResource(coreSite);
            conf.addResource(hdfsSite);
            fs = FileSystem.get(conf);
        } catch(IOException e){
            System.err.println("Error ocurred while configuring FileSystem");
        }
        return fs;
    }
    public static boolean createFolder(FileSystem fs, String foldername) throws IOException{
        Path path = new Path("HDFS_DATA/" + foldername);
        boolean isSuccess = fs.mkdirs(path);
        fs.close();
        return isSuccess;
    }
    public static FSDataOutputStream createFile(FileSystem fs, String path){
        Path path1 = new Path(path);
        FSDataOutputStream out = fs.create(path);
        return out;
    }
    public static boolean moveToHdfs(FileSystem fs, String local, String hdfs) throws IOException{
        boolean isSuccess;
        Path localpath = new Path(local);
        Path hdfspath = new Path("HDFS_DATA/" + hdfs);
        if(fs.exists(hdfspath)){
            System.err.println("File " + file + "already exists");
            isSuccess = false;
        }
        try{
            fs.moveFromLocalFile(localpath, hdfspath);
            isSuccess = true;
        }
        finally{
            fs.close();
        }
        

        return isSuccess;
    }

    public static Message copyTolocal(FileSystem fs, String local, String hdfs) throws IOException{
        Path localpath = new Path(local);
        Path hdfspath =new Path("HDFS_DATA/" + hdfs);
        if(fs.exists(hdfspath)){
            System.err.println("File " + file + "already exists");
            isSuccess = false;
        }
        try{
            fs.copyToLocalFile(localpath, hdfspath);
            isSuccess = true;
        }
        finally{
            fs.close();
        }
        return Message(local, true);
    }

    public static FSDataInputStream readFile(FileSystem fs, String path) throws IOException {
        Path path1 = new Path(path);
        FSDataInputStream in = fs.open(path1);
        return in;
    }



    public static List<String> fileList(FileSystem fs, String dstr) throws IOException{
        Path directory = new Path(dstr);
        List<Path> arr = new ArrayList<String> ();
        RemoteIterator<LocatedFileStatus> i = fs.listFiles(directory, true);
        while(i.hasNext()){
            LocatedFileStatus fileStatus = i.next();
            Path p = fileStatus.getPath().toString();
            arr.add(p);
        }
        return arr;


    }
    public static void close(FileSystem fs){
        try{
            fs.close();
        }
        catch(IOException e){
            System.err.println("error on closing file system");
        }
    }
    
}
