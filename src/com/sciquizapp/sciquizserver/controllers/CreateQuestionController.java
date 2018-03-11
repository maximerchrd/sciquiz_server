package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.database_management.DbTableLearningObjectives;
import com.sciquizapp.sciquizserver.database_management.DbTableSubject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Created by maximerichard on 11.03.18.
 */
public class CreateQuestionController implements Initializable {
    private ArrayList<HBox> hBoxArrayList;
    private ArrayList<CheckBox> checkBoxArrayList;
    private ArrayList<TextField> textFieldArrayList;
    private ArrayList<ComboBox> subjectsComboBoxArrayList;
    private ArrayList<ComboBox> objectivesComboBoxArrayList;


    @FXML private VBox vBox;
    @FXML private VBox vBoxSubjects;
    @FXML private VBox vBoxObjectives;
    @FXML private ComboBox typeOfQuestion;
    @FXML private HBox firstAnswer;


    public void addAnswerOption() {
        HBox hBox = new HBox();
        TextField textField = new TextField("blabla");

        CheckBox checkBox = new CheckBox();
        hBox.getChildren().add(checkBox);
        hBox.getChildren().add(textField);
        vBox.getChildren().add(vBox.getChildren().size() - 1, hBox);
        hBoxArrayList.add(hBox);
    }

    public void addSubject() {
        Vector<String> subjectsVector = DbTableSubject.getAllSubjects();
        String[] subjects = subjectsVector.toArray(new String[subjectsVector.size()]);;
        ObservableList<String> options =
                FXCollections.observableArrayList(subjects);
        ComboBox comboBox = new ComboBox(options);
        comboBox.setEditable(true);
        subjectsComboBoxArrayList.add(comboBox);
        vBoxSubjects.getChildren().add(comboBox);
        TextFields.bindAutoCompletion(comboBox.getEditor(), comboBox.getItems());
    }

    public void addObjective() {
        Vector<String> objectivessVector = DbTableLearningObjectives.getAllObjectives();
        String[] objectives = objectivessVector.toArray(new String[objectivessVector.size()]);;
        ObservableList<String> options =
                FXCollections.observableArrayList(objectives);
        ComboBox comboBox = new ComboBox(options);
        comboBox.setEditable(true);
        objectivesComboBoxArrayList.add(comboBox);
        vBoxObjectives.getChildren().add(comboBox);
        TextFields.bindAutoCompletion(comboBox.getEditor(), comboBox.getItems());
    }

    public void comboAction() {
        if (typeOfQuestion.getSelectionModel().getSelectedItem().toString().contentEquals("Question with Short Answer")) {
            for (int i = 0; i < hBoxArrayList.size(); i++) {
                hBoxArrayList.get(i).getChildren().get(0).setVisible(false);
            }
        } else {
            for (int i = 0; i < hBoxArrayList.size(); i++) {
                hBoxArrayList.get(i).getChildren().get(0).setVisible(true);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subjectsComboBoxArrayList = new ArrayList<>();
        objectivesComboBoxArrayList = new ArrayList<>();
        hBoxArrayList = new ArrayList<>();
        ObservableList<String> options =
                FXCollections.observableArrayList("Question Multiple Choice", "Question with Short Answer");
        typeOfQuestion.setItems(options);

        hBoxArrayList.add(firstAnswer);
    }
}
