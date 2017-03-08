package com.sciquizapp.sciquizserver;

import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by maximerichard on 14/02/17.
 */
public class Student {
    private String mAddress = "no address";
    private String mName = "no name";
    private StreamConnection mConnection = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private double overallPercentage = -1;

    //constructors
    public Student() {

    }
    public Student(String arg_address, String arg_name) {
        mAddress = arg_address;
        mName = arg_name;
    }
    public Student(String arg_address, String arg_name, StreamConnection arg_connection) {
        mAddress = arg_address;
        mName = arg_name;
        mConnection = arg_connection;
    }
    //setters
    public void setAddress(String arg_address) {
        mAddress = arg_address;
    }
    public void setName(String arg_name) {
        mName = arg_name;
    }
    public void setConnection(StreamConnection arg_connection) {
        mConnection = arg_connection;
    }
    //getters
    public String getAddress() {
        return mAddress;
    }
    public  String getName() {
        return  mName;
    }
    public StreamConnection getConnection() {
        return mConnection;
    }
    public OutputStream getOutputStream() {
        return mOutputStream;
    }
    public InputStream getInputStream() {
        return mInputStream;
    }

    /**
     * method that opens the streams if the connection is opened
     * @return false if the connection is null
     * @throws IOException
     */
    public Boolean OpenStreams() throws IOException {
        if (mConnection != null) {
            mOutputStream = mConnection.openOutputStream();
            mInputStream = mConnection.openInputStream();
            return true;
        } else {
            return false;
        }
    }
}
