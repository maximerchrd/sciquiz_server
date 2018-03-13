package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.NetworkCommunication;
import com.sciquizapp.sciquizserver.Test;
import com.sciquizapp.sciquizserver.database_management.*;
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
import org.apache.commons.io.FileUtils;
import sun.nio.ch.Net;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.lang.*;

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

    public void removeQuestion() {
        int index = readyQuestionsList.getSelectionModel().getSelectedIndex();
        NetworkCommunication.networkCommunicationSingleton.removeQuestion(index);
        IDsFromBroadcastedQuestions.remove(index);
        readyQuestionsList.getItems().remove(index);
    }

    public void importQuestions() {
        List<String> input = readFile("questions/questions.csv");
        input.remove(0);
        for (int i = 0; i < input.size(); i++) {
            String[] question = input.get(i).split(";");

            //insert subjects
            String[] subjects = question[5].split("///");
            for (int j = 0; j < subjects.length; j++) {
                try {
                    DbTableSubject.addSubject(subjects[j].replace("'","''"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            //insert objectives
            String[] objectives = question[6].split("///");
            for (int j = 0; j < objectives.length; j++) {
                try {
                    DbTableLearningObjectives.addObjective(objectives[j].replace("'","''"),1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (question[0].contentEquals("0")) {
                insertQuestionMultipleChoice(question);
            } else {
                insertQuestionShortAnswer(question);
            }
        }
    }

    public void exportQuestions() {
        ArrayList<QuestionGeneric> questionGenericArrayList = new ArrayList<>();
        try {
            questionGenericArrayList = DbTableQuestionGeneric.getAllGenericQuestions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("questions/questions.csv", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println("Questions Type (0 = question multiple choice, 1 = question short answer);Question text;Right Answers;Other Options;Picture;Subjects;Objectives");
        for (int i = 0; i < questionGenericArrayList.size(); i++) {
            if (questionGenericArrayList.get(i).getIntTypeOfQuestion() == 1) {
                String question = "1;";
                QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(questionGenericArrayList.get(i).getGlobalID());

                //copy image file to correct directory
                if (questionShortAnswer.getIMAGE().length() > 0 && !questionShortAnswer.getIMAGE().contentEquals("none")) {
                    File source = new File(questionShortAnswer.getIMAGE());
                    File dest = new File("questions/" + questionShortAnswer.getIMAGE());
                    try {
                        FileUtils.copyFile(source, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                question += questionShortAnswer.getQUESTION();
                question += ";";
                ArrayList<String> answers = questionShortAnswer.getANSWER();
                for (int j = 0; j < answers.size(); j++) {
                    question += answers.get(j) + "///";
                }
                question += ";;";       //because short answer questions don't have "other options" -> double ;;
                question += questionShortAnswer.getIMAGE();
                question += ";";
                Vector<String> subjects = questionShortAnswer.getSubjects();
                for (int j = 0; j < subjects.size(); j++) {
                    question += subjects.get(j) + "///";
                }
                question += ";";
                Vector<String> objectives = questionShortAnswer.getObjectives();
                for (int j = 0; j < objectives.size(); j++) {
                    question += objectives.get(j) + "///";
                }
                question += ";";
                writer.println(question);
            } else if (questionGenericArrayList.get(i).getIntTypeOfQuestion() == 0) {
                String question = "0;";
                QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
                try {
                    questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(questionGenericArrayList.get(i).getGlobalID());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //copy image file to correct directory
                if (questionMultipleChoice.getIMAGE().length() > 0 && !questionMultipleChoice.getIMAGE().contentEquals("none")) {
                    File source = new File(questionMultipleChoice.getIMAGE());
                    File dest = new File("questions/" + questionMultipleChoice.getIMAGE());
                    try {
                        FileUtils.copyFile(source, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                question += questionMultipleChoice.getQUESTION();
                question += ";";
                Vector<String> answers = questionMultipleChoice.getCorrectAnswers();
                for (int j = 0; j < answers.size(); j++) {
                    question += answers.get(j) + "///";
                }
                question += ";";
                Vector<String> incorrectOptions = questionMultipleChoice.getIncorrectAnswers();
                for (int j = 0; j < incorrectOptions.size(); j++) {
                    question += incorrectOptions.get(j) + "///";
                }
                question += ";";
                question += questionMultipleChoice.getIMAGE();
                question += ";";
                Vector<String> subjects = questionMultipleChoice.getSubjects();
                for (int j = 0; j < subjects.size(); j++) {
                    question += subjects.get(j) + "///";
                }
                question += ";";
                Vector<String> objectives = questionMultipleChoice.getObjectives();
                for (int j = 0; j < objectives.size(); j++) {
                    question += objectives.get(j) + "///";
                }
                question += ";";
                writer.println(question);
            }

        }
        writer.close();
    }

    //OTHER METHODS
    private void insertQuestionMultipleChoice(String[] question) {
        Vector<String> options_vector = new Vector<String>();
        for (int i = 0; i < 10; i++) options_vector.add(" ");
        String[] rightAnswers = question[2].split("///");
        String[] otherOptions = question[3].split("///");
        String[] allOptions = concatenate(rightAnswers, otherOptions);
        for (int i = 0; i < 10 && i < allOptions.length && !allOptions[i].contentEquals(" "); i++) {
            options_vector.set(i, allOptions[i]);
        }
        int number_correct_answers = rightAnswers.length;
        QuestionMultipleChoice new_questmultchoice = new QuestionMultipleChoice("1", question[1].replace("'", "''"), options_vector.get(0).replace("'", "''"),
                options_vector.get(1).replace("'", "''"), options_vector.get(2).replace("'", "''"), options_vector.get(3).replace("'", "''"), options_vector.get(4).replace("'", "''"),
                options_vector.get(5).replace("'", "''"), options_vector.get(6).replace("'", "''"), options_vector.get(7).replace("'", "''"), options_vector.get(8).replace("'", "''"),
                options_vector.get(9).replace("'", "''"), question[4].replace("'", "''"));
        new_questmultchoice.setNB_CORRECT_ANS(number_correct_answers);

        //copy image file to correct directory
        if (new_questmultchoice.getIMAGE().length() > 0 && !new_questmultchoice.getIMAGE().contains("none")) {
            File source = new File("questions/" + new_questmultchoice.getIMAGE());
            File dest = new File(new_questmultchoice.getIMAGE());
            try {
                FileUtils.copyFile(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new_questmultchoice.setIMAGE("");
        }

        try {
            DbTableQuestionMultipleChoice.addMultipleChoiceQuestion(new_questmultchoice);
            new_questmultchoice.setID(DbTableQuestionMultipleChoice.getLastIDGlobal());

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //insert question in tree view
        QuestionGeneric questionGeneric = new QuestionGeneric(new_questmultchoice.getID(), 0);
        questionGeneric.setQuestion(new_questmultchoice.getQUESTION());
        questionGeneric.setImagePath(new_questmultchoice.getIMAGE());
        questionGeneric.setTypeOfQuestion("0");
        genericQuestionsList.add(questionGeneric);
        Node questionImage = null;
        questionImage = new ImageView(new Image("file:" + new_questmultchoice.getIMAGE(), 20, 20, true, false));
        TreeItem<QuestionGeneric> itemChild;
        if (new_questmultchoice.getIMAGE().length() < 1) {
            itemChild = new TreeItem<>(questionGeneric);
        } else {
            itemChild = new TreeItem<>(questionGeneric, questionImage);
        }
        allQuestionsTree.getRoot().getChildren().add(itemChild);

        //adding subjects relations
        String[] subjects = question[5].split("///");
        for (int i = 0; i < subjects.length; i++) {
            try {
                DbTableRelationQuestionSubject.addRelationQuestionSubject(subjects[i].replace("'", "''"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //adding objectives relations
        String[] objectives = question[6].split("///");
        for (int i = 0; i < objectives.length; i++) {
            try {
                DbTableRelationQuestionObjective.addRelationQuestionObjective(objectives[i].replace("'", "''"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void insertQuestionShortAnswer(String[] question) {
        QuestionShortAnswer new_questshortanswer = new QuestionShortAnswer();
        new_questshortanswer.setQUESTION(question[1].replace("'", "''"));
        if (question[4].length() > 0) {
            new_questshortanswer.setIMAGE(question[4]);
        }
        ArrayList<String> answerOptions = new ArrayList<String>();
        String[] rightAnswers = question[2].split("///");
        for (int i = 0; i < rightAnswers.length; i++) {
            String answerOption = rightAnswers[i];
            if (answerOption.length() > 0) {
                answerOptions.add(answerOption.replace("'", "''"));
            }
        }
        new_questshortanswer.setANSWER(answerOptions);

        //copy image file to correct directory
        if (new_questshortanswer.getIMAGE().length() > 0 && !new_questshortanswer.getIMAGE().contains("none")) {
            File source = new File("questions/" + new_questshortanswer.getIMAGE());
            File dest = new File(new_questshortanswer.getIMAGE());
            try {
                FileUtils.copyFile(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new_questshortanswer.setIMAGE("");
        }

        String idGlobal = "-1";
        try {
            idGlobal = DbTableQuestionShortAnswer.addShortAnswerQuestion(new_questshortanswer);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        new_questshortanswer.setID(Integer.valueOf(idGlobal));


        //put the question in the treeView
        QuestionGeneric questionGeneric = new QuestionGeneric(new_questshortanswer.getID(), 1);
        questionGeneric.setQuestion(new_questshortanswer.getQUESTION());
        questionGeneric.setImagePath(new_questshortanswer.getIMAGE());
        questionGeneric.setTypeOfQuestion("1");
        genericQuestionsList.add(questionGeneric);
        Node questionImage = null;
        questionImage = new ImageView(new Image("file:" + new_questshortanswer.getIMAGE(), 20, 20, true, false));
        TreeItem<QuestionGeneric> itemChild;
        if (new_questshortanswer.getIMAGE().length() < 1) {
            itemChild = new TreeItem<>(questionGeneric);
        } else {
            itemChild = new TreeItem<>(questionGeneric, questionImage);
        }
        allQuestionsTree.getRoot().getChildren().add(itemChild);

        //adding subjects relations
        String[] subjects = question[5].split("///");
        for (int i = 0; i < subjects.length; i++) {
            try {
                DbTableRelationQuestionSubject.addRelationQuestionSubject(subjects[i].replace("'", "''"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //adding objectives relations
        String[] objectives = question[6].split("///");
        for (int i = 0; i < objectives.length; i++) {
            try {
                DbTableRelationQuestionObjective.addRelationQuestionObjective(objectives[i].replace("'", "''"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private List<String> readFile(String filename) {
        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private void broadcastQuestionMultipleChoice(QuestionMultipleChoice questionMultipleChoice) {
        NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestMultChoice(questionMultipleChoice);
        try {
            NetworkCommunication.networkCommunicationSingleton.sendMultipleChoiceWithID(questionMultipleChoice.getID(), null);
            NetworkCommunication.networkCommunicationSingleton.addQuestion(questionMultipleChoice.getQUESTION(), questionMultipleChoice.getID());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcastQuestionShortAnswer(QuestionShortAnswer questionShortAnswer) {
        NetworkCommunication.networkCommunicationSingleton.getClassroom().addQuestShortAnswer(questionShortAnswer);
        try {
            NetworkCommunication.networkCommunicationSingleton.sendShortAnswerQuestionWithID(questionShortAnswer.getID(), null);
            NetworkCommunication.networkCommunicationSingleton.addQuestion(questionShortAnswer.getQUESTION(), questionShortAnswer.getID());
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