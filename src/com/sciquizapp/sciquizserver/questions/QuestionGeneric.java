package com.sciquizapp.sciquizserver.questions;

/**
 * Created by maximerichard on 22.11.17.
 */
public class QuestionGeneric {
    private String typeOfQuestion;
    private String question;
    private String imagePath;
    private int intTypeOfQuestion;
    private int indexInList;
    private int globalID;
    public QuestionGeneric () {
        typeOfQuestion = "undefined";
        indexInList = -1;
        globalID = -1;
        intTypeOfQuestion = -1;
        question = "question not initialized";
        imagePath = "";
    }
    public QuestionGeneric(String typeoflist, int indexinlist) {
        typeOfQuestion = typeoflist;
        indexInList = indexinlist;
    }
    public QuestionGeneric(int GlobalID, int typeofQuest) {
        intTypeOfQuestion = typeofQuest;
        globalID = GlobalID;
    }
    public int getGlobalID() {
        return globalID;
    }
    public void setGlobalID(int globalID) {
        this.globalID = globalID;
    }
    public void setTypeOfQuestion(String typeofquestion) {
        typeOfQuestion = typeofquestion;
    }
    public int getIntTypeOfQuestion() {
        return intTypeOfQuestion;
    }
    public String getQuestion() {
        return question;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setIndexInList(int indexinlist) {
        indexInList = indexinlist;
    }
    public String getTypeOfQuestion() {
        return typeOfQuestion;
    }
    public int getIndexInList() {
        return indexInList;
    }
    public void setIntTypeOfQuestion(int intTypeOfQuestion) {
        this.intTypeOfQuestion = intTypeOfQuestion;
    }
    @Override
    public String toString()  {
        return this.question;
    }
}
