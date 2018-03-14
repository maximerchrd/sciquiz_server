package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Created by maximerichard on 11.03.18.
 */
public class EditQuestionController implements Initializable {
    private ArrayList<HBox> hBoxArrayList;
    private ArrayList<CheckBox> checkBoxArrayList;
    private ArrayList<TextField> textFieldArrayList;
    private ArrayList<ComboBox> subjectsComboBoxArrayList;
    private ArrayList<ComboBox> objectivesComboBoxArrayList;
    private List<QuestionGeneric> genericQuestionsList;
    private TreeView<QuestionGeneric> allQuestionsTree;
    private QuestionGeneric questionGeneric;
    private TreeItem treeItem;

    @FXML private VBox vBox;
    @FXML private VBox vBoxSubjects;
    @FXML private VBox vBoxObjectives;
    @FXML private ComboBox typeOfQuestion;
    @FXML private TextArea questionText;
    @FXML private TextField imagePath;

    public void initVariables(List<QuestionGeneric> argGenericQuestionsList, TreeView<QuestionGeneric> argAllQuestionsTree,
                              QuestionGeneric questionGeneric, TreeItem treeItem) {
        genericQuestionsList = argGenericQuestionsList;
        allQuestionsTree = argAllQuestionsTree;
        this.questionGeneric = questionGeneric;
        this.treeItem = treeItem;
        fillFields();
    }

    public void addAnswerOption() {
        addAnswerOption("", false);
    }
    public void addAnswerOption(String option, Boolean isChecked) {
        HBox hBox = new HBox();
        TextField textField = new TextField(option);

        CheckBox checkBox = new CheckBox();
        if (typeOfQuestion.getSelectionModel().getSelectedItem().toString().contentEquals("Question with Short Answer")) {
            checkBox.setVisible(false);
        }
        checkBox.setSelected(isChecked);
        hBox.getChildren().add(checkBox);
        hBox.getChildren().add(textField);
        vBox.getChildren().add(vBox.getChildren().size() - 2, hBox);
        hBoxArrayList.add(hBox);
    }

    public void addSubject() {
        addSubject("");
    }
    public void addSubject(String subject) {
        Vector<String> subjectsVector = DbTableSubject.getAllSubjects();
        String[] subjects = subjectsVector.toArray(new String[subjectsVector.size()]);;
        ObservableList<String> options =
                FXCollections.observableArrayList(subjects);
        ComboBox comboBox = new ComboBox(options);
        comboBox.setEditable(true);
        comboBox.setValue(subject);
        subjectsComboBoxArrayList.add(comboBox);
        vBoxSubjects.getChildren().add(comboBox);
        TextFields.bindAutoCompletion(comboBox.getEditor(), comboBox.getItems());
    }

    public void addObjective() {
        addObjective("");
    }
    public void addObjective(String objective) {
        Vector<String> objectivessVector = DbTableLearningObjectives.getAllObjectives();
        String[] objectives = objectivessVector.toArray(new String[objectivessVector.size()]);;
        ObservableList<String> options =
                FXCollections.observableArrayList(objectives);
        ComboBox comboBox = new ComboBox(options);
        comboBox.setEditable(true);
        comboBox.setValue(objective);
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

    public void addPicture(String path) {
        imagePath.setText(path);
    }
    public void addPicture() {
        File theDir = new File("pictures");
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created");
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image file");
        Stage stage = (Stage) vBox.getScene().getWindow();
        File source_file = fileChooser.showOpenDialog(stage);
        String directory = "pictures/";
        File dest_file = new File(directory + source_file.getName());
        try {
            Files.copy(source_file.toPath(), dest_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        imagePath.setText(dest_file.getPath());
        imagePath.setEditable(false);
    }

    public void saveQuestion() {
        for (int i = 0; i < subjectsComboBoxArrayList.size(); i++) {
            try {
                DbTableSubject.addSubject(subjectsComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        for (int i = 0; i < objectivesComboBoxArrayList.size(); i++) {
            try {
                DbTableLearningObjectives.addObjective(objectivesComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"),1);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //add question to database according to question type
        if (typeOfQuestion.getSelectionModel().getSelectedItem().toString().equals("Question with Short Answer")) {
            QuestionShortAnswer new_questshortanswer = new QuestionShortAnswer();
            new_questshortanswer.setQUESTION(questionText.getText().replace("'","''"));
            if (imagePath.getText().length() > 0) {
                new_questshortanswer.setIMAGE(imagePath.getText());
            }
            ArrayList<String> answerOptions = new ArrayList<String>();
            for (int i = 0; i < hBoxArrayList.size(); i++) {
                TextField textField = (TextField)  hBoxArrayList.get(i).getChildren().get(1);
                String answerOption = textField.getText();
                if (answerOption.length() > 0) {
                    answerOptions.add(answerOption.replace("'","''"));
                }
            }
            new_questshortanswer.setANSWER(answerOptions);
            new_questshortanswer.setID(questionGeneric.getGlobalID());
            try {
                DbTableQuestionShortAnswer.updateShortAnswerQuestion(new_questshortanswer);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //update question in treeView
            questionGeneric.setTypeOfQuestion("1");
            questionGeneric.setIntTypeOfQuestion(1);
            questionGeneric.setQuestion(new_questshortanswer.getQUESTION());
            questionGeneric.setImagePath(new_questshortanswer.getIMAGE());
            Node questionImage = null;
            questionImage = new ImageView(new Image("file:" + new_questshortanswer.getIMAGE(), 20, 20, true, false));
            if (new_questshortanswer.getIMAGE().length() < 1) {
                treeItem.setValue(questionGeneric);
            } else {
                treeItem.setValue(questionGeneric);
                treeItem.setGraphic(questionImage);
            }

            for (int i = 0; i < subjectsComboBoxArrayList.size(); i++) {
                try {
                    DbTableRelationQuestionSubject.addRelationQuestionSubject(questionGeneric.getGlobalID(),subjectsComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            for (int i = 0; i < objectivesComboBoxArrayList.size(); i++) {
                try {
                    DbTableRelationQuestionObjective.addRelationQuestionObjective(questionGeneric.getGlobalID(),objectivesComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else if (typeOfQuestion.getSelectionModel().getSelectedItem().toString().equals("Question Multiple Choice")) {
            Vector<String> options_vector = new Vector<String>();
            for (int i = 0; i < 10; i++) options_vector.add(" ");
            for (int i = 0; i < 10 && i < hBoxArrayList.size() && !((TextField) hBoxArrayList.get(i).getChildren().get(1)).getText().contentEquals(" "); i++) {
                options_vector.set(i,((TextField) hBoxArrayList.get(i).getChildren().get(1)).getText());
            }
            int number_correct_answers = 0;
            String temp_option;
            for (int i = 0; i < hBoxArrayList.size(); i++) {
                CheckBox checkBox = (CheckBox) hBoxArrayList.get(i).getChildren().get(0);
                if (checkBox.isSelected()) {
                    temp_option = options_vector.get(number_correct_answers);
                    options_vector.set(number_correct_answers,options_vector.get(i));
                    options_vector.set(i,temp_option);
                    number_correct_answers++;
                }
            }
            QuestionMultipleChoice new_questmultchoice = new QuestionMultipleChoice("1", questionText.getText().replace("'","''"), options_vector.get(0).replace("'","''"),
                    options_vector.get(1).replace("'","''"), options_vector.get(2).replace("'","''"), options_vector.get(3).replace("'","''"), options_vector.get(4).replace("'","''"),
                    options_vector.get(5).replace("'","''"), options_vector.get(6).replace("'","''"), options_vector.get(7).replace("'","''"), options_vector.get(8).replace("'","''"),
                    options_vector.get(9).replace("'","''"), imagePath.getText().replace("'","''"));
            new_questmultchoice.setNB_CORRECT_ANS(number_correct_answers);
            new_questmultchoice.setID(questionGeneric.getGlobalID());
            try {
                DbTableQuestionMultipleChoice.updateMultipleChoiceQuestion(new_questmultchoice);

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //update question in treeView
            questionGeneric.setTypeOfQuestion("0");
            questionGeneric.setIntTypeOfQuestion(0);
            questionGeneric.setQuestion(new_questmultchoice.getQUESTION());
            questionGeneric.setImagePath(new_questmultchoice.getIMAGE());
            Node questionImage = null;
            questionImage = new ImageView(new Image("file:" + new_questmultchoice.getIMAGE(), 20, 20, true, false));
            if (new_questmultchoice.getIMAGE().length() < 1) {
                treeItem.setValue(questionGeneric);
            } else {
                treeItem.setValue(questionGeneric);
                treeItem.setGraphic(questionImage);
            }

            for (int i = 0; i < subjectsComboBoxArrayList.size(); i++) {
                try {
                    DbTableRelationQuestionSubject.addRelationQuestionSubject(questionGeneric.getGlobalID(),subjectsComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            for (int i = 0; i < objectivesComboBoxArrayList.size(); i++) {
                try {
                    DbTableRelationQuestionObjective.addRelationQuestionObjective(questionGeneric.getGlobalID(),objectivesComboBoxArrayList.get(i).getSelectionModel().getSelectedItem().toString().replace("'","''"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            System.out.println("Problem saving question: question type not supported");
        }
        Stage stage = (Stage) vBox.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    private void fillFields() {
        //fill the corresponding fields
        if (questionGeneric.getIntTypeOfQuestion() == 0) {
            typeOfQuestion.getSelectionModel().select(0);
            QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
            try {
                questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(questionGeneric.getGlobalID());
            } catch (Exception e) {
                e.printStackTrace();
            }
            questionText.setText(questionMultipleChoice.getQUESTION());
            for (int i = 0; i < questionMultipleChoice.getAnswers().size(); i++) {
                if (i < questionMultipleChoice.getNB_CORRECT_ANS()) {
                    addAnswerOption(questionMultipleChoice.getAnswers().get(i), true);
                } else {
                    addAnswerOption(questionMultipleChoice.getAnswers().get(i), false);
                }
            }
            addPicture(questionMultipleChoice.getIMAGE());
            for (int i = 0; i < questionMultipleChoice.getSubjects().size(); i++) {
                addSubject(questionMultipleChoice.getSubjects().get(i));
            }
            for (int i = 0; i < questionMultipleChoice.getObjectives().size(); i++) {
                addObjective(questionMultipleChoice.getObjectives().get(i));
            }
        } else {
            typeOfQuestion.getSelectionModel().select(1);
            QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(questionGeneric.getGlobalID());
            questionText.setText(questionShortAnswer.getQUESTION());
            for (int i = 0; i < questionShortAnswer.getANSWER().size(); i++) {
                addAnswerOption(questionShortAnswer.getANSWER().get(i),false);
            }
            addPicture(questionShortAnswer.getIMAGE());
            for (int i = 0; i < questionShortAnswer.getSubjects().size(); i++) {
                addSubject(questionShortAnswer.getSubjects().get(i));
            }
            for (int i = 0; i < questionShortAnswer.getObjectives().size(); i++) {
                addObjective(questionShortAnswer.getObjectives().get(i));
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
        typeOfQuestion.getSelectionModel().selectFirst();
    }
}
