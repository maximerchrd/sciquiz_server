package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.questions.Question;

import java.util.Vector;

/**
 * Created by maximerichard on 21/04/17.
 */
public class Quiz {
    private Vector<Question> mQuestionVector;
    private int mNumberOfQuestions;

    public Quiz () {
        mQuestionVector = new Vector<>();
    }
    public Quiz (Vector<Question> arg_questionVector) {
        mQuestionVector = arg_questionVector;
    }

    public Vector<Question> getQuestionVector() {
        return mQuestionVector;
    }

    public int getmNumberOfQuestions() {
        return mNumberOfQuestions;
    }

    public void addQuestion (Question arg_question) {
        mQuestionVector.add(arg_question);
        mNumberOfQuestions = mQuestionVector.size();
    }
}
