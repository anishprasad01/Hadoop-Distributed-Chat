package com.steve.hdc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class DataManager {
    public static final String DEFAULT_FILE_SYSTEM = "fs.defaultFS";
    public static final String URI_FILE_SYSTEM = "hdsf://master: 8088"

    public static boolean createFolder(String foldername) throws IOException {
        Configuration conf = new Configuration();
        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
        FileSystem fs = FileSystem.get((Configuration) conf);
        Path path = new Path("HDFS_DATA/" + foldername);
        boolean isSuccess = fs.mkdirs(path);
        fs.close();
        return isSuccess;
    }
//    public static FSDataOutputStream createFile(String path){
//        Configuration conf = new Configuration();
//        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
//        FileSystem fs  = FileSystem.get(conf);
//        Path path1 = new Path(path);
//        FSDataOutputStream out = fs.create(path);
//        return out;
//    }
//    public static boolean moveToHdfs(String local, String hdfs) throws IOException{
//        boolean isSuccess;
//        Configuration conf = new Configuration();
//        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
//        FileSystem fs  = FileSystem.get(conf);
//        Path localpath = new Path(local);
//        Path hdfspath = new Path("HDFS_DATA/" + hdfs);
//        if(fs.exists(hdfspath)){
//            System.err.println("File " + file + "already exists");
//            isSuccess = false;
//        }
//        try{
//            fs.moveFromLocalFile(localpath, hdfspath);
//            isSuccess = true;
//        }
//        finally{
//            fs.close();
//        }
//
//
//        return isSuccess;
//    }
//
//    public static Message copyTolocal(String local, String hdfs) throws IOException{
//        Configuration conf = new Configuration();
//        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
//        FileSystem fs  = FileSystem.get(conf);
//        Path localpath = new Path(local);
//        Path hdfspath =new Path("HDFS_DATA/" + hdfs);
//        if(fs.exists(hdfspath)){
//            System.err.println("File " + file + "already exists");
//            isSuccess = false;
//        }
//        try{
//            fs.copyToLocalFile(localpath, hdfspath);
//            isSuccess = true;
//        }
//        finally{
//            fs.close();
//        }
//        return Message(local, true);
//    }
//
//    public static FSDataInputStream readFile(String path) throws IOException {
//        Configuration conf = new Configuration();
//        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
//        FileSystem fs = FileSystem.get(conf);
//        Path path1 = new Path(path);
//        FSDataInputStream in = fs.open(path1);
//        return in;
//    }
//
//
//
//    public static List<Path> fileList(Path directory) throws IOException{
//        Configuration conf = new Configuration();
//        conf.set(DEFAULT_FILE_SYSTEM, URI_FILE_SYSTEM);
//        List<Path> arr = new ArrayList<String> ();
//        FileSystem fs = FileSystem.get(conf);
//        RemoteIterator<LocatedFileStatus> i = fs.listFiles(path, true);
//        while(i.hasNext()){
//            LocatedFileStatus fileStatus = i.next();
//            Path p = fileStatus.getPath();
//            arr.add(p)
//        }
//        return arr;
//
//
//    }

}
