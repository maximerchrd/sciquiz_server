package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.SingleStudentAnswersLine;
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
    final int MAX_NUMBER_COLUMNS = 300;
    private ArrayList<String> questions;
    private ArrayList<Integer> questionsIDs;
    @FXML private TableView<SingleStudentAnswersLine> studentsQuestionsTable;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Students;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Status;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Eval;

    public void addQuestion(String question, Integer ID) {
        // Add extra columns if necessary:
        System.out.println("adding column");
        studentsQuestionsTable.getColumns().get(5).setText("!!!" + question);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < MAX_NUMBER_COLUMNS; i++) {
            TableColumn<SingleStudentAnswersLine, String> column = new TableColumn("bla");
            studentsQuestionsTable.getColumns().add(column);
        }
        studentsQuestionsTable.getColumns().get(4).setText("ouaich enfin");
    }
}
