package com.sciquizapp.sciquizserver.questions;

/**
 * Created by maximerichard on 22.11.17.
 */
public class QuestionGeneric {
    private String typeOfList;
    private int indexInList;
    public QuestionGeneric(String typeoflist, int indexinlist) {
        typeOfList = typeoflist;
        indexInList = indexinlist;
    }
    public void setTypeOfList(String typeoflist) {
        typeOfList = typeoflist;
    }
    public void setIndexInList(int indexinlist) {
        indexInList = indexinlist;
    }
    public String getTypeOfList() {
        return typeOfList;
    }
    public int getIndexInList() {
        return indexInList;
    }
}
