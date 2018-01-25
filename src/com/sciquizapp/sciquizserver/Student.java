package com.sciquizapp.sciquizserver;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Created by maximerichard on 14/02/17.
 */
public class Student {
    private RemoteDevice mRemoteDevice = null;
    private String mAddress = "no address";
    private InetAddress mInetAddress = null;
    private String mName = "no name";
    private StreamConnection mConnection = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private double overallPercentage = -1;
    private Boolean mConnectedByBT = false;
    private Integer numberOfAnswers;


    //constructors
    public Student() {
        numberOfAnswers = 0;
    }
    public Student(String arg_address, String arg_name) {
        mAddress = arg_address;
        mName = arg_name;
        numberOfAnswers = 0;
    }
    public Student(String arg_address, String arg_name, StreamConnection arg_connection, Boolean connectedByBT) {
        mAddress = arg_address;
        mName = arg_name;
        mConnection = arg_connection;
        mConnectedByBT = connectedByBT;
        numberOfAnswers = 0;
    }
    public void increaseNumberOfAnswers () {
        numberOfAnswers++;
    }
    //setters
    public void setRemoteDevice(RemoteDevice arg_remoteDevice) {
        mRemoteDevice = arg_remoteDevice;
    }
    public void setInetAddress(InetAddress arg_inetaddress) {
        mInetAddress = arg_inetaddress;
    }
    public void setAddress(String arg_address) {
        mAddress = arg_address;
    }
    public void setName(String arg_name) {
        mName = arg_name;
    }
    public void setConnection(StreamConnection arg_connection) {
        mConnection = arg_connection;
    }
    public void setmConnectedByBT(Boolean connectedByBT) {
        mConnectedByBT = connectedByBT;
    }
    public void setOutputStream(OutputStream arg_outputstream) {
        mOutputStream = arg_outputstream;
    }
    public void setInputStream(InputStream arg_inputstream) {
        mInputStream = arg_inputstream;
    }
    //getters
    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }
    public RemoteDevice getRemoteDevice() {
        return mRemoteDevice;
    }
    public InetAddress getInetAddress() {
        return mInetAddress;
    }
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
    public Boolean getmConnectedByBT() {
        return mConnectedByBT;
    }

    /**
     * method that opens the streams if the connection is opened
     * @return false if the connection is null
     * @throws IOException
     */
    public Boolean openStreams() throws IOException {
        if (mConnection != null && mOutputStream == null) {
            mOutputStream = mConnection.openOutputStream();
            mInputStream = mConnection.openInputStream();
            return true;
        } else {
            return false;
        }
    }

    public Boolean openInputStream() throws IOException {
        if (mConnection != null && mInputStream == null) {
            mInputStream = mConnection.openInputStream();
            return true;
        } else {
            return false;
        }
    }

    public Boolean openOutputStream() throws IOException {
        if (mConnection != null && mOutputStream == null) {
            mOutputStream = mConnection.openOutputStream();
            return true;
        } else {
            return false;
        }
    }
}
