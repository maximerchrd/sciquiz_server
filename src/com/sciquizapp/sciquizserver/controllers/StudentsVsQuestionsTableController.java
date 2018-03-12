package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.SingleStudentAnswersLine;
import com.sciquizapp.sciquizserver.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by maximerichard on 12.03.18.
 */
public class StudentsVsQuestionsTableController implements Initializable {
    private ArrayList<String> questions;
    private ArrayList<Integer> questionsIDs;
    @FXML private TableView<SingleStudentAnswersLine> studentsQuestionsTable;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Student;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Status;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Evaluation;

    public void addQuestion(String question, Integer ID) {
        // Add extra columns if necessary:
        System.out.println("adding column");
        TableColumn column = new TableColumn(question);
        column.setPrefWidth(180);
        studentsQuestionsTable.getColumns().add(column);
        questions.add(question);
        questionsIDs.add(ID);
    }

    public void addUser(Student UserStudent, Boolean connection) {
        SingleStudentAnswersLine singleStudentAnswersLine = new SingleStudentAnswersLine(UserStudent.getName(),"connected","0");
        studentsQuestionsTable.getItems().add(singleStudentAnswersLine);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Student"));
        Status.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Status"));
        Evaluation.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Evaluation"));
        questions = new ArrayList<>();
        questionsIDs = new ArrayList<>();
    }
}
