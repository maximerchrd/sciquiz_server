package com.sciquizapp.sciquizserver;

import java.util.ArrayList;

/**
 * Created by maximerichard on 15.01.18.
 */
public class Test {
    private String testName;
    private int idTest;
    private ArrayList<Integer> idsQuestions;

    public Test() {
        this.testName = "";
        this.idTest = -1;
        this.idsQuestions = new ArrayList<Integer>();
    }

    public String getTestName() {
        return testName;
    }

    public int getIdTest() {
        return idTest;
    }

    public ArrayList<Integer> getIdsQuestions() {
        return idsQuestions;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setIdTest(int idTest) {
        this.idTest = idTest;
    }

    public void setIdsQuestions(ArrayList<Integer> idsQuestions) {
        this.idsQuestions = idsQuestions;
    }
}
