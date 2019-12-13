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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataManager{
    public static final String DEFAULT_FILE_SYSTEM = "fs.defaultFS";
    public static final String URI_FILE_SYSTEM = "hdsf://master: 8088"

    public static boolean createFolder(String foldername) throws IOException{
        Configuration conf = new Configuration();
        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
        FileSystem fs = FileSystem.get((Configuration)conf);
        Path path = new Path(foldername);
        boolean isSuccess = fs.mkdirs(path);
        fs.close();
        return isSuccess;
    }
//     public static FSDataOutputStream createFile(String file, Configuration conf) throws IOException{
//         FileSystem fs  = FileSystem.get(conf);
//         Path path = new Path(file);
//         if(!fs.exists(path)){
//             System.err.println("File " + file + "already exists");
//         }
//         FSDataOutputStream out = fs.create(path);

//         return out;
//     }

//     public static FSDataInputStream readFile(String local, String hdfs, Configuration conf) throws IOException{
//         FileSystem fs  = FileSystem.get(conf);
//         Path path = new Path(file);
//         FSDataInputStream in = fs.copyToLocalFile(false, hdfs, local, true);
//         return in;
//     }




//     public static List<Path> fileList(Path directory) throws IOException{
//         List<Path> arr = new ArrayList<String> ();
//         FileSystem fs = FileSystem.get(conf);
//         RemoteIterator<LocatedFileStatus> i = fs.listFiles(path, true);
//         while(i.hasNext()){
//             LocatedFileStatus fileStatus = i.next();
//             Path p = fileStatus.getPath();
//             arr.add(p)
//         }
//         return arr;


//     }
//     public static void
// }
// 