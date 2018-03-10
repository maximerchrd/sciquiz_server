package com.sciquizapp.sciquizserver.views;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.database_management.DbTableIndividualQuestionForStudentResult;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by maximerichard on 01.03.18.
 */
public class ResultsTableController implements Initializable {
    @FXML private TableView<SingleResultForTable> resultsTable;
    @FXML private TableColumn<SingleResultForTable, String> Name;
    @FXML private TableColumn<SingleResultForTable, String> Date;
    @FXML private TableColumn<SingleResultForTable, String> Question;
    @FXML private TableColumn<SingleResultForTable, String> Evaluation;
    @FXML private TableColumn<SingleResultForTable, String> StudentsAnswer;
    @FXML private TableColumn<SingleResultForTable, String> CorrectAnswer;
    @FXML private TableColumn<SingleResultForTable, String> IncorrectAnswer;
    @FXML private TableColumn<SingleResultForTable, String> Subject;
    @FXML private TableColumn<SingleResultForTable, String> Objectives;


    public void initialize(URL location, ResourceBundle resources) {
        Name.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("name"));
        Date.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("date"));
        Question.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("question"));
        Evaluation.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("evaluation"));
        StudentsAnswer.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("studentsAnswer"));
        CorrectAnswer.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("correctAnswer"));
        IncorrectAnswer.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("incorrectAnswer"));
        Subject.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("subjects"));
        Objectives.setCellValueFactory(new PropertyValueFactory<SingleResultForTable, String>("objectives"));

        addResults();
    }

    void addResults() {
        ObservableList<SingleResultForTable> data = resultsTable.getItems();
        ArrayList<SingleResultForTable> resultsList = DbTableIndividualQuestionForStudentResult.getAllSingleResults();
        for (int i = 0; i < resultsList.size(); i++) {
            data.add(resultsList.get(i));
        }
    }
}