package com.sciquizapp.sciquizserver;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

/**
 * Created by maximerichard on 12.03.18.
 */
public class SingleStudentAnswersLine {
    private final SimpleStringProperty Student = new SimpleStringProperty("");
    private final SimpleStringProperty Status = new SimpleStringProperty("");
    private final SimpleStringProperty Evaluation = new SimpleStringProperty("");
    private final ArrayList<SimpleStringProperty> Answers = new ArrayList<>();

    private final SimpleStringProperty Evluation = new SimpleStringProperty("");

    public String getEvluation() {
        return Evluation.get();
    }

    public SimpleStringProperty evluationProperty() {
        return Evluation;
    }

    public void setEvluation(String evluation) {
        this.Evluation.set(evluation);
    }

    public SingleStudentAnswersLine(String student, String status, String evaluation) {
        setStudent(student);
        setStatus(status);
        setEvaluation(evaluation);
    }

    public String getStudent() {
        return Student.get();
    }

    public SimpleStringProperty studentProperty() {
        return Student;
    }

    public void setStudent(String student) {
        this.Student.set(student);
    }

    public String getStatus() {
        return Status.get();
    }

    public SimpleStringProperty statusProperty() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status.set(status);
    }

    public String getEvaluation() {
        return Evaluation.get();
    }

    public SimpleStringProperty evaluationProperty() {
        return Evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.Evaluation.set(evaluation);
    }

    public ArrayList<SimpleStringProperty> getAnswers() {
        return Answers;
    }
}
