package com.sciquizapp.sciquizserver.views;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.Test;
import com.sciquizapp.sciquizserver.database_management.DbTableIndividualQuestionForStudentResult;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionGeneric;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionMultipleChoice;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionShortAnswer;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML private TreeView<String> allQuestionsTree;

    //questions ready for activation (right panel)
    @FXML private ListView<String> readyQuestionsList;

    public void initialize(URL location, ResourceBundle resources) {
        //all questions tree (left panel)
        //retrieve data from db
        try {
            genericQuestionsList = DbTableQuestionGeneric.getAllGenericQuestions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //create root
        TreeItem<String> root = new TreeItem<>("All questions");
        root.setExpanded(true);
        allQuestionsTree.setShowRoot(false);
        populateTree(root);
        allQuestionsTree.setRoot(root);
    }

    private void populateTree(TreeItem<String> root) {
        //populate tree
        for (int i = 0; i < genericQuestionsList.size(); i++) {
                try {
                    QuestionMultipleChoice questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(genericQuestionsList.get(i).getGlobalID());
                    String question = questionMultipleChoice.getQUESTION();
                    Node questionImage = null;
                    if (question.length() < 1) {
                        QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(genericQuestionsList.get(i).getGlobalID());
                        question = questionShortAnswer.getQUESTION();
                        questionImage = new ImageView(new Image("file:" + questionShortAnswer.getIMAGE(), 20, 20, true, false));
                    } else {
                        if (questionMultipleChoice.getIMAGE().length() > 0) {
                            questionImage = new ImageView(new Image("file:" + questionMultipleChoice.getIMAGE(), 20, 20, true, false));
                        }
                    }
                    TreeItem<String> itemChild;
                    if (questionImage != null) {
                         itemChild = new TreeItem<>(question,questionImage);
                    } else {
                        itemChild = new TreeItem<>(question);
                    }
                    root.getChildren().add(itemChild);

                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    public void activateQuestionForStudents() {
        
    }

}