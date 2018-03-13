package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.SingleStudentAnswersLine;
import com.sciquizapp.sciquizserver.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by maximerichard on 12.03.18.
 */
public class StudentsVsQuestionsTableController extends Window implements Initializable {
    private ArrayList<String> questions;
    private ArrayList<Integer> questionsIDs;
    private ArrayList<String> students;
    @FXML private TableView<SingleStudentAnswersLine> studentsQuestionsTable;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Student;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Status;
    @FXML private TableColumn<SingleStudentAnswersLine, String> Evaluation;

    public void addQuestion(String question, Integer ID) {
        // Add extra columns if necessary:
        System.out.println("adding column");
        TableColumn column = new TableColumn(question);
        //column.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Question" + String.valueOf(studentsQuestionsTable.getColumns().size() - 3)));
        column.setPrefWidth(180);
        studentsQuestionsTable.getColumns().add(column);
        questions.add(question);
        questionsIDs.add(ID);
        for (int i = 0; i < studentsQuestionsTable.getItems().size(); i++) {
            studentsQuestionsTable.getItems().get(i).addAnswer();
        }
        final int questionIndex = questions.size() - 1;
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SingleStudentAnswersLine, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SingleStudentAnswersLine, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().getAnswers().get(questionIndex);
            }
        });
    }

    public void addUser(Student UserStudent, Boolean connection) {
        SingleStudentAnswersLine singleStudentAnswersLine = new SingleStudentAnswersLine(UserStudent.getName(),"connected","0");
        for (int i = 0; i < questions.size(); i++) {
            singleStudentAnswersLine.addAnswer();
        }
        studentsQuestionsTable.getItems().add(singleStudentAnswersLine);
        students.add(UserStudent.getName());
    }

    public void addAnswerForUser(Student student, String answer, String question, double evaluation, Integer questionId) {
        if (!questions.contains(question)) {
            popUpIfQuestionNotCorresponding();
        }
        Integer indexColumn = questionsIDs.indexOf(questionId);
        Integer indexRow = students.indexOf(student.getName());
        if (indexColumn >= 0 && indexRow >= 0) {
            SingleStudentAnswersLine singleStudentAnswersLine = studentsQuestionsTable.getItems().get(indexRow);
            singleStudentAnswersLine.setAnswer(answer,indexColumn);
            //Set the i-th item
            studentsQuestionsTable.getItems().set(indexRow,singleStudentAnswersLine);
        }
    }

    private void popUpIfQuestionNotCorresponding() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(this);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("Question stored in the database doesn't correspond to the question answered by a student"));
        Scene dialogScene = new Scene(dialogVbox, 400, 40);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Student"));
        Status.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Status"));
        Evaluation.setCellValueFactory(new PropertyValueFactory<SingleStudentAnswersLine, String>("Evaluation"));
        questions = new ArrayList<>();
        questionsIDs = new ArrayList<>();
        students = new ArrayList<>();
    }
}
