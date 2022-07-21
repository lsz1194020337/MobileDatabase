package com.example.mobiledatabase.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MoveFile {
    public static void moveFile(@NonNull File old, @NonNull File move) {
        if (old.exists()) {
            FileChannel outF;
            try {
                outF = new FileOutputStream(move).getChannel();
                new FileInputStream(old).getChannel().transferTo(0, old.length(), outF);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
