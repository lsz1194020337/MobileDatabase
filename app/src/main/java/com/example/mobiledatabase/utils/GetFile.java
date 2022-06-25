package com.example.mobiledatabase.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetFile {
    public List<String> GetDBFileName(String fileDir) {
        List<String> pathList = new ArrayList<>();
        File file = new File(fileDir);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // identify if is the file directory
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // end with .DB
                if (filename.trim().toLowerCase().endsWith(".DB")) {
                    pathList.add(filename);
                } else if (filename.trim().toUpperCase().endsWith(".DB")) {
                    pathList.add(filename);
                }
            }
        }
        return pathList;
    }
}
