package com.sciquizapp.sciquizserver.questions;

/**
 * Created by maximerichard on 22.11.17.
 */
public class QuestionGeneric {
    private String typeOfQuestion;
    private int indexInList;
    private int globalID;
    public QuestionGeneric(String typeoflist, int indexinlist) {
        typeOfQuestion = typeoflist;
        indexInList = indexinlist;
    }
    public int getGlobalID() {
        return globalID;
    }
    public void setGlobalID(int globalID) {
        this.globalID = globalID;
    }
    public void setTypeOfList(String typeofquestion) {
        typeOfQuestion = typeofquestion;
    }
    public void setIndexInList(int indexinlist) {
        indexInList = indexinlist;
    }
    public String getTypeOfList() {
        return typeOfQuestion;
    }
    public int getIndexInList() {
        return indexInList;
    }
}
