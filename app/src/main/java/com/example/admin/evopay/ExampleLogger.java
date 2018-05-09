package com.example.admin.evopay;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class ExampleLogger {

    String logfilepath;
    String log_datetime;
    FileWriter writer;

    private final String tag;


    public ExampleLogger(String tag) {

        this.tag = tag;

    }

    public void log(String message) {
        try {
            logfilepath = "EvoPayment";

            Date d = new Date();

            log_datetime = d.toLocaleString();

            File root = new File(Environment.getExternalStorageDirectory(), "Evo");

            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }
            File filepath = new File(root, logfilepath + ".txt"); // file path to save

            if (!filepath.exists()) {
                System.out.println("We had to make a new file.");
                filepath.createNewFile();
            }

            writer = new FileWriter(filepath, true);
            // writer = new BufferedWriter(writer1);

        }
        catch (IOException e) {
            e.printStackTrace();

        }
        try {
            writer.append(log_datetime + " " + message + " " + " \n");
            writer.flush();
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        // use the tag field, add a timestamp, etc. to whatever logging mechanism you want to use
    }

    public void log(String message, Object... args) {
        log(String.format(message, args));
    }

    public void log(Throwable t, String message) {
        log(String.format("Exception: %s Message: %s", t, message));
    }

    public void log(Throwable t, String message, Object... args) {
        log(t, String.format(message, args));
    }
}
