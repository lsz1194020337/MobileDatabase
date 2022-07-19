package com.example.mobiledatabase.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MoveFile {
    public static boolean copyDir(String srcDir, String destDir){
        File sourceDir = new File(srcDir);
        if(!sourceDir.exists()){
            return false;
        }
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            if(!targetDir.exists()){
                targetDir.mkdirs();
            }
            for(int i= 0;i<fileList.length;i++){
                if(fileList[i].isDirectory()){
                    copyDir(fileList[i].getPath()+ "/",
                            destDir + fileList[i].getName() + "/");
                }else{
                    copyFile(fileList[i].getPath(), destDir +fileList[i].getName());
                }
            }
            return true;
        }else {
            copyFileToDir(srcDir,destDir);
            return true;
        }
    }
    public static boolean copyFile(String srcFile, String destFile){
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }
    public static boolean copyFileToDir(String srcFile, String destDir){
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir +"/" + new File(srcFile).getName();
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }
}
