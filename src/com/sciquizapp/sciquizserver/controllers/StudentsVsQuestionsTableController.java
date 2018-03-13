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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;

import java.net.URL;
import java.text.DecimalFormat;
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
        column.setCellFactory(new Callback<TableColumn<SingleStudentAnswersLine, String>, TableCell<SingleStudentAnswersLine, String>>() {
            @Override
            public TableCell<SingleStudentAnswersLine, String> call(TableColumn<SingleStudentAnswersLine, String> param) {
                return new TableCell<SingleStudentAnswersLine, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.RED);
                            // Get fancy and change color based on data
                            if(item.contains("#/#")) {
                                this.setTextFill(Color.GREEN);
                            }
                            setText(item.replace("#/#",""));

                        }
                    }

                };
            }
        });
    }

    public void removeQuestion(int index) {
        for (int i = 0; i < studentsQuestionsTable.getItems().size(); i++) {
            studentsQuestionsTable.getItems().get(i).getAnswers().remove(index);
        }
        studentsQuestionsTable.getColumns().remove(index + 3);
        questions.remove(index);
        questionsIDs.remove(index);

        for (int i = 3 + index; i < studentsQuestionsTable.getColumns().size(); i++) {
            TableColumn column = studentsQuestionsTable.getColumns().get(i);
            final int questionIndex = i - 3;
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SingleStudentAnswersLine, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<SingleStudentAnswersLine, String> p) {
                    // p.getValue() returns the Person instance for a particular TableView row
                    return p.getValue().getAnswers().get(questionIndex);
                }
            });
        }
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

        //set answer
        answer = answer.replace("|||",";");
        if (evaluation == 100) {
            answer += "#/#";
        }
        Integer indexColumn = questionsIDs.indexOf(questionId);
        Integer indexRow = students.indexOf(student.getName());
        SingleStudentAnswersLine singleStudentAnswersLine = studentsQuestionsTable.getItems().get(indexRow);
        if (indexColumn >= 0 && indexRow >= 0) {
            singleStudentAnswersLine.setAnswer(answer,indexColumn);
        }

        //update evaluation
        int numberAnswers = 0;
        for (int i = 0; i < singleStudentAnswersLine.getAnswers().size(); i++) {
            String answerInCell = singleStudentAnswersLine.getAnswers().get(i).getValue();
            if (answerInCell.length() > 0) numberAnswers++;
        }
        Double meanEvaluation = Double.parseDouble(singleStudentAnswersLine.getEvaluation());
        meanEvaluation = ((meanEvaluation * (numberAnswers -1)) + evaluation) / numberAnswers;
        DecimalFormat df = new DecimalFormat("#.#");
        singleStudentAnswersLine.setEvaluation(String.valueOf(df.format(meanEvaluation)));
        studentsQuestionsTable.getItems().set(indexRow,singleStudentAnswersLine);
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
