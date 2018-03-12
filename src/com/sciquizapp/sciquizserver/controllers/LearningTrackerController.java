package com.sciquizapp.sciquizserver.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by maximerichard on 12.03.18.
 */
public class LearningTrackerController implements Initializable {
    @FXML private AnchorPane ClassroomActivityTab;
    @FXML private ClassroomActivityTabController ClassroomActivityTabController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void addQuestion(String question, Integer id) {
        ClassroomActivityTabController.addQuestion(question,id);
    }
}
