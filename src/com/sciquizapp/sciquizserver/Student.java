package com.sciquizapp.sciquizserver;

/**
 * Created by maximerichard on 14/02/17.
 */
public class Student {
    private String mAddress = "no address";
    private String mName = "no name";
    private double overallPercentage = -1;

    //constructors
    public Student() {

    }
    public Student(String arg_address, String arg_name) {
        mAddress = arg_address;
        mName = arg_name;
    }
    //setters
    public void setAddress(String arg_address) {
        mAddress = arg_address;
    }
    public void setName(String arg_name) {
        mName = arg_name;
    }
    //getters
    public String getAddress() {
        return mAddress;
    }
    public  String getName() {
        return  mName;
    }
}
