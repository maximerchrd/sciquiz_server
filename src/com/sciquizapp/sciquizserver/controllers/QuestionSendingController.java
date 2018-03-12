package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.NetworkCommunication;
import com.sciquizapp.sciquizserver.Test;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionGeneric;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionMultipleChoice;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionShortAnswer;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import sun.nio.ch.Net;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Created by maximerichard on 01.03.18.
 */
public class QuestionSendingController extends Window implements Initializable {
    //all questions tree (left panel)
    private QuestionMultipleChoice questionMultChoiceSelectedNodeTreeFrom;
    private QuestionShortAnswer questionShortAnswerSelectedNodeTreeFrom;
    private Test testSelectedNodeTreeFrom;
    private List<Test> testsList = new ArrayList<Test>();
    private List<QuestionGeneric> genericQuestionsList = new ArrayList<QuestionGeneric>();

    @FXML
    private TreeView<QuestionGeneric> allQuestionsTree;

    //questions ready for activation (right panel)
    static public Vector<String> IDsFromBroadcastedQuestions = new Vector<>();
    @FXML
    private ListView<QuestionGeneric> readyQuestionsList;

    public void initialize(URL location, ResourceBundle resources) {
        //all questions tree (left panel)
        //retrieve data from db
        try {
            genericQuestionsList = DbTableQuestionGeneric.getAllGenericQuestions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //create root
        TreeItem<QuestionGeneric> root = new TreeItem<>(new QuestionGeneric());
        root.setExpanded(true);
        allQuestionsTree.setShowRoot(false);
        populateTree(root);
        allQuestionsTree.setRoot(root);

        //question ready (right panel)
        readyQuestionsList.setCellFactory(param -> new ListCell<QuestionGeneric>() {
            private ImageView imageView = new ImageView();

            public void updateItem(QuestionGeneric questionGeneric, boolean empty) {
                super.updateItem(questionGeneric, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image("file:" + questionGeneric.getImagePath(), 40, 40, true, false));
                    setText(questionGeneric.getQuestion());
                    setGraphic(imageView);
                }
            }
        });
    }


    private void populateTree(TreeItem<QuestionGeneric> root) {
        //populate tree
        for (int i = 0; i < genericQuestionsList.size(); i++) {
            try {
                QuestionMultipleChoice questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(genericQuestionsList.get(i).getGlobalID());
                Node questionImage = null;
                if (questionMultipleChoice.getQUESTION().length() < 1) {
                    QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(genericQuestionsList.get(i).getGlobalID());
                    genericQuestionsList.get(i).setQuestion(questionShortAnswer.getQUESTION());
                    genericQuestionsList.get(i).setImagePath(questionShortAnswer.getIMAGE());
                    genericQuestionsList.get(i).setTypeOfQuestion("1");
                    questionImage = new ImageView(new Image("file:" + questionShortAnswer.getIMAGE(), 20, 20, true, false));
                } else {
                    genericQuestionsList.get(i).setQuestion(questionMultipleChoice.getQUESTION());
                    if (questionMultipleChoice.getIMAGE().length() > 0) {
                        genericQuestionsList.get(i).setImagePath(questionMultipleChoice.getIMAGE());
                        genericQuestionsList.get(i).setTypeOfQuestion("0");
                        questionImage = new ImageView(new Image("file:" + questionMultipleChoice.getIMAGE(), 20, 20, true, false));
                    }
                }
                TreeItem<QuestionGeneric> itemChild;
                if (questionImage != null) {
                    itemChild = new TreeItem<>(genericQuestionsList.get(i), questionImage);
                } else {
                    itemChild = new TreeItem<>(genericQuestionsList.get(i));
                }
                root.getChildren().add(itemChild);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //BUTTONS
    public void broadcastQuestionForStudents() {
        QuestionGeneric questionGeneric = allQuestionsTree.getSelectionModel().getSelectedItem().getValue();
        int globalID = questionGeneric.getGlobalID();
        if (!IDsFromBroadcastedQuestions.contains(String.valueOf(globalID))) {
            readyQuestionsList.getItems().add(questionGeneric);
            IDsFromBroadcastedQuestions.add(String.valueOf(globalID));
            if (questionGeneric.getTypeOfQuestion().contentEquals("0")) {
                try {
                    broadcastQuestionMultipleChoice(DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(globalID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                broadcastQuestionShortAnswer(DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(globalID));
            }
        } else {
            popUpIfQuestionCollision();
        }
    }

    public void activateQuestionForStudents() {
        try {
            NetworkCommunication.networkCommunicationSingleton.SendQuestionID(readyQuestionsList.getSelectionModel().getSelectedItem().getGlobalID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createQuestion() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/CreateQuestion.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CreateQuestionController controller = fxmlLoader.<CreateQuestionController>getController();
        controller.initVariables(genericQuestionsList, allQuestionsTree);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Create a New Question");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    //OTHER METHODS
    private void broadcastQuestionMultipleChoice(QuestionMultipleChoice questionMultipleChoice) {
        NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestMultChoice(questionMultipleChoice);
        try {
            NetworkCommunication.networkCommunicationSingleton.sendMultipleChoiceWithID(questionMultipleChoice.getID(), null);
            NetworkCommunication.networkCommunicationSingleton.addQuestion(questionMultipleChoice.getQUESTION());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcastQuestionShortAnswer(QuestionShortAnswer questionShortAnswer) {
        NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestShortAnswer(questionShortAnswer);
        try {
            NetworkCommunication.networkCommunicationSingleton.sendShortAnswerQuestionWithID(questionShortAnswer.getID(), null);
            NetworkCommunication.networkCommunicationSingleton.addQuestion(questionShortAnswer.getQUESTION());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void popUpIfQuestionCollision() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(this);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("Unfortunately, you cannot use a question twice in the same set"));
        Scene dialogScene = new Scene(dialogVbox, 400, 40);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}