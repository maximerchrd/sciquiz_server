package com.sciquizapp.sciquizserver.views;

import com.sciquizapp.sciquizserver.NetworkCommunication;
import com.sciquizapp.sciquizserver.Test;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionGeneric;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionMultipleChoice;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionShortAnswer;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class QuestionSendingController implements Initializable {
    //all questions tree (left panel)
    private QuestionMultipleChoice questionMultChoiceSelectedNodeTreeFrom;
    private QuestionShortAnswer questionShortAnswerSelectedNodeTreeFrom;
    private Test testSelectedNodeTreeFrom;
    private List<Test> testsList = new ArrayList<Test>();
    private List<QuestionGeneric> genericQuestionsList = new ArrayList<QuestionGeneric>();

    @FXML private TreeView<QuestionGeneric> allQuestionsTree;

    //questions ready for activation (right panel)
    static public Vector<String> IDsFromBroadcastedQuestions = new Vector<>();
    @FXML private ListView<QuestionGeneric> readyQuestionsList;

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
        readyQuestionsList.getItems().add(questionGeneric);
        if (questionGeneric.getTypeOfQuestion().contentEquals("0")) {
            try {
                broadcastQuestionMultipleChoice(DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(globalID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            broadcastQuestionShortAnswer(DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(globalID));
        }
    }

    public void activateQuestionForStudents() {
        try {
            NetworkCommunication.networkCommunicationSingleton.SendQuestionID(readyQuestionsList.getSelectionModel().getSelectedItem().getGlobalID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //OTHER METHODS
    private void broadcastQuestionMultipleChoice(QuestionMultipleChoice questionMultipleChoice) {
        if (!IDsFromBroadcastedQuestions.contains(String.valueOf(questionMultipleChoice.getID()))) {
            NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestMultChoice(questionMultipleChoice);
            IDsFromBroadcastedQuestions.add(String.valueOf(questionMultipleChoice.getID()));
            try {
                NetworkCommunication.networkCommunicationSingleton.sendMultipleChoiceWithID(questionMultipleChoice.getID(), null);
                NetworkCommunication.networkCommunicationSingleton.addQuestion(questionMultipleChoice.getQUESTION());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //JOptionPane.showMessageDialog(null, "Unfortunately, you cannot use a question twice in the same set", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void broadcastQuestionShortAnswer(QuestionShortAnswer questionShortAnswer) {
        if (!IDsFromBroadcastedQuestions.contains(String.valueOf(questionShortAnswer.getID()))) {
            NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestShortAnswer(questionShortAnswer);
            IDsFromBroadcastedQuestions.add(String.valueOf(questionShortAnswer.getID()));
            try {
                NetworkCommunication.networkCommunicationSingleton.sendShortAnswerQuestionWithID(questionShortAnswer.getID(), null);
                NetworkCommunication.networkCommunicationSingleton.addQuestion(questionShortAnswer.getQUESTION());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //JOptionPane.showMessageDialog(null, "Unfortunately, you cannot use a question twice in the same set", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}