package com.sciquizapp.sciquizserver;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import tools.Scalr;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class EditQuestion extends JPanel implements ActionListener {
    private JLabel question_label;
    private JTextArea question_text;
    private Vector<JLabel> labelVector;
    private Vector<JCheckBox> checkboxVector;
    private Vector<JTextArea> textfieldVector;
    final private Vector<JComboBox> subjectsVector;
    final private Vector<JComboBox> objectivesVector;
    private JFileChooser mFileChooser;
    private String mFilePath = "";
    private int new_correct_answer_index = 0;
    private int new_subject_index = 0;
    private int new_objective_index = 0;
    private int bottom_index = 7;
    final private int MAX_ANSWERS = 10;
    private JPanel panel;
    final JFrame new_question_frame;
    private int window_width;
    private int window_height;
    private GridBagLayout columnsLayout;
    private Object[] questiontypes;
    private JButton save_quest_button;
    private JButton add_image_button;
    JTextArea imagePathTextArea;
    private int questionTypeMember = -1;
    GridBagConstraints imagePathText_constraints;

    private QuestionMultipleChoice questionMultipleChoice = null;
    private QuestionShortAnswer questionShortAnswer = null;

    public EditQuestion(int globalId, int questionType, final JTree tree, DefaultMutableTreeNode treeNode) {
        questionTypeMember = questionType;
        new_question_frame = new JFrame(Language.translate(Language.ADDNEWQUESTION));
        window_width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8);
        window_height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7);
        panel = new JPanel();
        new_question_frame.add(panel);
        panel.setAutoscrolls(true);
        new_question_frame.pack();
        question_label = new JLabel("Question:");
        checkboxVector = new Vector<>();
        labelVector = new Vector<>();
        textfieldVector = new Vector<>();
        subjectsVector = new Vector<>();
        objectivesVector = new Vector<>();
        GridBagConstraints add_image_button_constraints = new GridBagConstraints();
        imagePathText_constraints = new GridBagConstraints();
        GridBagConstraints save_quest_button_constraints = new GridBagConstraints();
        add_image_button = new JButton("add a picture");
        save_quest_button = new JButton("save the question");
        imagePathTextArea = new JTextArea();

        try {
            if (questionType == 0) {
                questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(globalId);
                question_text = new JTextArea(questionMultipleChoice.getQUESTION());
                mFilePath = questionMultipleChoice.getIMAGE();
            } else if (questionType == 1) {
                questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(globalId);
                question_text = new JTextArea(questionShortAnswer.getQUESTION());
                mFilePath = questionShortAnswer.getIMAGE();
            } else {
                System.out.println("Problem editing question: questionType not recognized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        columnsLayout = new GridBagLayout();
        panel.setLayout(columnsLayout);


        GridBagConstraints question_label_constraints = new GridBagConstraints();
        question_label_constraints.gridwidth = 3;
        question_label_constraints.gridx = 0;
        question_label_constraints.gridy = 1;
        panel.add(question_label, question_label_constraints);

        GridBagConstraints question_text_constraints = new GridBagConstraints();
        question_text_constraints.fill = GridBagConstraints.HORIZONTAL;
        question_text_constraints.gridwidth = 3;
        question_text_constraints.gridx = 0;
        question_text_constraints.gridy = 2;
        panel.add(question_text, question_text_constraints);

        //implement a button to add an answer
        JButton add_correct_answer_button = new JButton("Add an answer");
        add_correct_answer_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                if (new_correct_answer_index < MAX_ANSWERS - 1) {
                    addAnswerOption(questionType, add_image_button_constraints, save_quest_button_constraints, "", false, 1);
                } else {
                    System.out.println("maximum answers number reached");
                }
            }
        });
        GridBagConstraints add_correct_answer_button_constraints = new GridBagConstraints();
        add_correct_answer_button_constraints.gridwidth = 3;
        add_correct_answer_button_constraints.gridx = 0;
        add_correct_answer_button_constraints.gridy = 3;
        panel.add(add_correct_answer_button, add_correct_answer_button_constraints);

        addSubjectUI();
        addObjectiveUI();


        //implement a button to add a picture
        add_image_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Set up the file chooser.
                if (mFileChooser == null) {
                    mFileChooser = new JFileChooser();

                    //Add a custom file filter and disable the default
                    //(Accept All) file filter.
                    //		        	mFileChooser.addChoosableFileFilter(new ImageFilter());
                    //		        	mFileChooser.setAcceptAllFileFilterUsed(false);

                }

                //Show it.
                int returnVal = mFileChooser.showDialog(EditQuestion.this,
                        "Attach");
                //Process the results.
                File source_file = new File("");
                String directory = "res/drawable/";

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    source_file = mFileChooser.getSelectedFile();
                    System.out.println("Attaching file: " + source_file.getName());
                } else {
                    System.out.println("Attachment cancelled by user.");
                }
                File dest_file = new File(directory + source_file.getName());
                try {
                    Files.copy(source_file.toPath(), dest_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                mFilePath = directory + source_file.getName();

                //Reset the file chooser for the next time it's shown.
                mFileChooser.setSelectedFile(null);

                imagePathTextArea.setText(mFilePath);
            }
        });
        add_image_button_constraints.gridwidth = 2;
        add_image_button_constraints.gridx = 0;
        add_image_button_constraints.gridy = bottom_index - 1;
        panel.add(add_image_button, add_image_button_constraints);

        imagePathTextArea.setText(mFilePath);
        imagePathText_constraints.gridx = 2;
        imagePathText_constraints.gridy = bottom_index - 1;
        panel.add(imagePathTextArea, imagePathText_constraints);

        //set the layout for save question button because we need it for adding the answer options
        save_quest_button_constraints.gridx = 0;
        save_quest_button_constraints.gridy = bottom_index;
        save_quest_button_constraints.gridwidth = 3;

        //add the answer options
        if (questionType == 0) {
            Vector<String> answers = questionMultipleChoice.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                if (i < questionMultipleChoice.getNB_CORRECT_ANS()) {
                    addAnswerOption(0, add_image_button_constraints, save_quest_button_constraints, answers.get(i), true, 1);
                } else {
                    addAnswerOption(0, add_image_button_constraints, save_quest_button_constraints, answers.get(i), false, 1);
                }
            }
        } else if (questionType == 1) {
            ArrayList<String> answers = questionShortAnswer.getANSWER();
            for (int i = 0; i < answers.size(); i++) {
                addAnswerOption(0, add_image_button_constraints, save_quest_button_constraints, answers.get(i), false, 1);
            }
        } else {
            System.out.println("Problem editing question: questionType not recognized");
        }

        //implement a button to save the question to the database
        save_quest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector<String> options_vector = new Vector<String>();
                for (int i = 0; i < 10; i++) options_vector.add(" ");
                for (int i = 0; i < 10 && i < textfieldVector.size() && !textfieldVector.elementAt(i).equals(" "); i++)
                    options_vector.set(i, textfieldVector.elementAt(i).getText());

                for (int i = 0; i < subjectsVector.size(); i++) {
                    try {
                        DbTableSubject.addSubject(subjectsVector.get(i).getSelectedItem().toString().replace("'", "''"));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                for (int i = 0; i < objectivesVector.size(); i++) {
                    try {
                        DbTableLearningObjectives.addObjective(objectivesVector.get(i).getSelectedItem().toString().replace("'", "''"), 1);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                //add question to database according to question type
                if (questionType == 1) {
                    QuestionShortAnswer edited_questshortanswer = new QuestionShortAnswer();
                    edited_questshortanswer.setQUESTION(question_text.getText().replace("'", "''"));
                    if (mFilePath.length() > 0) {
                        edited_questshortanswer.setIMAGE(mFilePath);
                    }
                    ArrayList<String> answerOptions = new ArrayList<String>();
                    for (int i = 0; i < textfieldVector.size(); i++) {
                        if (textfieldVector.get(i).toString().length() > 0) {
                            answerOptions.add(textfieldVector.get(i).getText().replace("'", "''"));
                        }
                    }
                    edited_questshortanswer.setANSWER(answerOptions);
                    String idGlobal = "-1";
                    try {
                        idGlobal = DbTableQuestionShortAnswer.addShortAnswerQuestion(edited_questshortanswer);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    edited_questshortanswer.setID(Integer.valueOf(idGlobal));

                    //resize image of question to fit icon size
                    ImageIcon icon = new ImageIcon(edited_questshortanswer.getIMAGE());
                    Image img = icon.getImage();
                    ImageIcon newIcon = null;
                    if (img.getWidth(null) > 0) {
                        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = bi.createGraphics();
                        g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                        BufferedImage scaledImage = Scalr.resize(bi, 40);
                        newIcon = new ImageIcon(scaledImage);
                    }

                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.nodeChanged(treeNode);
                    model.reload();

                    for (int i = 0; i < subjectsVector.size(); i++) {
                        try {
                            DbTableRelationQuestionSubject.addRelationQuestionSubject(edited_questshortanswer.getID(),
                                    subjectsVector.get(i).getSelectedItem().toString().replace("'", "''"));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    for (int i = 0; i < objectivesVector.size(); i++) {
                        try {
                            DbTableRelationQuestionObjective.addRelationQuestionObjective(edited_questshortanswer.getID(), objectivesVector.get(i).getSelectedItem().toString().replace("'", "''"));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if (questionType == 0) {
                    int number_correct_answers = 0;
                    String temp_option;
                    for (int i = 0; i < checkboxVector.size(); i++) {
                        if (checkboxVector.get(i).isSelected()) {
                            temp_option = options_vector.get(number_correct_answers);
                            options_vector.set(number_correct_answers, options_vector.get(i));
                            options_vector.set(i, temp_option);
                            number_correct_answers++;
                        }
                    }
                    QuestionMultipleChoice edited_questmultchoice = new QuestionMultipleChoice("1", question_text.getText().replace("'", "''"), options_vector.get(0).replace("'", "''"),
                            options_vector.get(1).replace("'", "''"), options_vector.get(2).replace("'", "''"), options_vector.get(3).replace("'", "''"), options_vector.get(4).replace("'", "''"),
                            options_vector.get(5).replace("'", "''"), options_vector.get(6).replace("'", "''"), options_vector.get(7).replace("'", "''"), options_vector.get(8).replace("'", "''"),
                            options_vector.get(9).replace("'", "''"), mFilePath.replace("'", "''"));
                    edited_questmultchoice.setNB_CORRECT_ANS(number_correct_answers);
                    edited_questmultchoice.setID(globalId);
                    try {
                        DbTableQuestionMultipleChoice.updateMultipleChoiceQuestion(edited_questmultchoice);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    //resize image of question to fit icon size
                    ImageIcon icon = new ImageIcon(edited_questmultchoice.getIMAGE());
                    Image img = icon.getImage();
                    ImageIcon newIcon = null;
                    if (img.getWidth(null) > 0) {
                        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = bi.createGraphics();
                        g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                        BufferedImage scaledImage = Scalr.resize(bi, 40);
                        newIcon = new ImageIcon(scaledImage);
                    }

                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    treeNode.setUserObject(edited_questmultchoice);
                    model.nodeChanged(treeNode);
                   // model.reload();

                    for (int i = 0; i < subjectsVector.size(); i++) {
                        try {
                            DbTableRelationQuestionSubject.addRelationQuestionSubject(edited_questmultchoice.getID(),
                                    subjectsVector.get(i).getSelectedItem().toString().replace("'", "''"));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    for (int i = 0; i < objectivesVector.size(); i++) {
                        try {
                            DbTableRelationQuestionObjective.addRelationQuestionObjective(edited_questmultchoice.getID(),
                                    objectivesVector.get(i).getSelectedItem().toString().replace("'", "''"));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                } else {
                    System.out.println("Problem saving question: question type not supported");
                }
//				DefaultListModel<String>  jlist_model = (DefaultListModel<String>) arg_dragFrom.getModel();
//				arg_dragFrom.setModel(jlist_model);
                new_question_frame.dispatchEvent(new WindowEvent(new_question_frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        panel.add(save_quest_button, save_quest_button_constraints);

        for (int i = 0; questionType == 1 && i < checkboxVector.size(); i++) {
            checkboxVector.get(i).setVisible(false);
        }
        for (int i = 0; questionType == 0 && i < checkboxVector.size(); i++) {
            checkboxVector.get(i).setVisible(true);
        }

        new_question_frame.setBounds(0, 0, window_width, window_height);
        new_question_frame.setVisible(true);
    }

    private void addAnswerOption(int questionType, GridBagConstraints add_image_button_constraints, GridBagConstraints save_quest_button_constraints,
                                 String answer, Boolean isCorrectAnswer, int starting_index) {
        JLabel new_answer_label = new JLabel("Answer " + (new_correct_answer_index + starting_index) + ":");
        JCheckBox new_checkbox = new JCheckBox();
        new_checkbox.setSelected(isCorrectAnswer);
        checkboxVector.add(new_checkbox);
        if (questionType == 1) {
            checkboxVector.get(checkboxVector.size() - 1).setSelected(isCorrectAnswer);
            checkboxVector.get(checkboxVector.size() - 1).setVisible(false);
        }
        JTextArea new_answer_text = new JTextArea(answer);
        JButton new_delete_answer_button = new JButton("x");
        new_delete_answer_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e2) {
                panel.remove(new_answer_label);
                panel.remove(new_checkbox);
                panel.remove(new_answer_text);
                panel.remove(new_delete_answer_button);
                panel.validate();
                panel.repaint();
            }
        });

        GridBagConstraints new_answer_label_constraints = new GridBagConstraints();
        new_answer_label_constraints.gridwidth = 3;
        new_answer_label_constraints.gridx = 0;
        new_answer_label_constraints.gridy = new_correct_answer_index * 2 + 6;
        panel.add(new_answer_label, new_answer_label_constraints);

        GridBagConstraints new_correct_checkbox_constraints = new GridBagConstraints();
        new_correct_checkbox_constraints.gridwidth = 1;
        new_correct_checkbox_constraints.gridx = 0;
        new_correct_checkbox_constraints.gridy = new_correct_answer_index * 2 + 7;
        panel.add(new_checkbox, new_correct_checkbox_constraints);

        GridBagConstraints new_answer_text_constraints = new GridBagConstraints();
        new_answer_text_constraints.fill = GridBagConstraints.HORIZONTAL;
        new_answer_text_constraints.gridwidth = 1;
        new_answer_text_constraints.gridx = 1;
        new_answer_text_constraints.gridy = new_correct_answer_index * 2 + 7;
        panel.add(new_answer_text, new_answer_text_constraints);

        GridBagConstraints new_delete_answer_button_constraints = new GridBagConstraints();
        new_delete_answer_button_constraints.gridwidth = 1;
        new_delete_answer_button_constraints.gridx = 2;
        new_delete_answer_button_constraints.gridy = new_correct_answer_index * 2 + 7;
        panel.add(new_delete_answer_button, new_delete_answer_button_constraints);

        labelVector.add(new_answer_label);
        textfieldVector.add(new_answer_text);
        new_question_frame.setSize(new_question_frame.getWidth(), new_question_frame.getHeight() + 30);
        new_correct_answer_index++;


        add_image_button_constraints.gridy += 2;
        imagePathText_constraints.gridy += 2;
        save_quest_button_constraints.gridy += 2;
        columnsLayout.setConstraints(add_image_button, add_image_button_constraints);
        columnsLayout.setConstraints(imagePathTextArea, imagePathText_constraints);
        columnsLayout.setConstraints(save_quest_button, save_quest_button_constraints);
        panel.revalidate();
        panel.repaint();
    }

    private void addObjectiveUI() {
        //implement a button to add an objective
        JButton add_objective_button = new JButton(Language.translate(Language.ADDOBJECTIVEBUTTON));
        add_objective_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                addObjectiveItem("	");
            }
        });
        GridBagConstraints add_objective_button_constraints = new GridBagConstraints();
        add_objective_button_constraints.gridwidth = 2;
        add_objective_button_constraints.gridx = 5;
        add_objective_button_constraints.gridy = 3;
        panel.add(add_objective_button, add_objective_button_constraints);
        if (questionTypeMember == 0) {
            for (int i = 0; i < questionMultipleChoice.getObjectives().size(); i++ ) {
                addObjectiveItem(questionMultipleChoice.getObjectives().get(i));
            }
        } else if (questionTypeMember == 1) {
            for (int i = 0; i < questionShortAnswer.getObjectives().size(); i++ ) {
                addObjectiveItem(questionShortAnswer.getObjectives().get(i));
            }
        }
    }

    private void addObjectiveItem(String objectiveText) {
        JComboBox new_objective_text = new JComboBox();
        Vector<String> allObjectives = DbTableLearningObjectives.getAllObjectives();
        Object[] elements = new Object[allObjectives.size()];
        for (int i = 0; i < allObjectives.size(); i++) {
            elements[i] = allObjectives.get(i);
        }
        AutoCompleteSupport.install(new_objective_text, GlazedLists.eventListOf(elements));
        new_objective_text.setPreferredSize(new Dimension(300,25));
        new_objective_text.setSelectedItem(objectiveText);
        objectivesVector.add(new_objective_text);
        JButton new_delete_objective_button = new JButton("x");
        new_delete_objective_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e2) {
                panel.remove(new_objective_text);
                panel.remove(new_delete_objective_button);
                panel.validate();
                panel.repaint();
                objectivesVector.remove(new_objective_text);
            }
        });

        GridBagConstraints new_objective_text_constraints = new GridBagConstraints();
        new_objective_text_constraints.fill = GridBagConstraints.HORIZONTAL;
        new_objective_text_constraints.gridwidth = 1;
        new_objective_text_constraints.gridx = 5;
        new_objective_text_constraints.gridy = new_objective_index + 4;
        panel.add(new_objective_text, new_objective_text_constraints);

        GridBagConstraints new_delete_objective_button_constraints = new GridBagConstraints();
        new_delete_objective_button_constraints.gridwidth = 1;
        new_delete_objective_button_constraints.gridx = 6;
        new_delete_objective_button_constraints.gridy = new_objective_index + 4;
        panel.add(new_delete_objective_button, new_delete_objective_button_constraints);
        panel.validate();
        panel.repaint();

        new_objective_index++;
    }

    private void addSubjectUI() {
        //implement a button to add a subject
        JButton add_subject_button = new JButton(Language.translate(Language.ADDSUBJECTBUTTON));
        add_subject_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                addSubjectItem("	");
            }
        });
        GridBagConstraints add_subject_button_constraints = new GridBagConstraints();
        add_subject_button_constraints.gridwidth = 2;
        add_subject_button_constraints.gridx = 3;
        add_subject_button_constraints.gridy = 3;
        panel.add(add_subject_button, add_subject_button_constraints);

        if (questionTypeMember == 0) {
            for (int i = 0; i < questionMultipleChoice.getSubjects().size(); i++ ) {
                addSubjectItem(questionMultipleChoice.getSubjects().get(i));
            }
        } else if (questionTypeMember == 1) {
            for (int i = 0; i < questionShortAnswer.getSubjects().size(); i++ ) {
                addSubjectItem(questionShortAnswer.getSubjects().get(i));
            }
        }
    }

    private void addSubjectItem(String subjectText) {
        JComboBox new_subject_text = new JComboBox();
        Vector<String> allSubjects = DbTableSubject.getAllSubjects();
        Object[] elements = new Object[allSubjects.size()];
        for (int i = 0; i < allSubjects.size(); i++) {
            elements[i] = allSubjects.get(i);
        }
        AutoCompleteSupport.install(new_subject_text, GlazedLists.eventListOf(elements));
        new_subject_text.setSelectedItem(subjectText);
        new_subject_text.setPreferredSize(new Dimension(200,25));
        subjectsVector.add(new_subject_text);
        JButton new_delete_subject_button = new JButton("x");
        new_delete_subject_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e2) {
                panel.remove(new_subject_text);
                panel.remove(new_delete_subject_button);
                panel.validate();
                panel.repaint();
                subjectsVector.remove(new_subject_text);
            }
        });

        GridBagConstraints new_subject_text_constraints = new GridBagConstraints();
        new_subject_text_constraints.fill = GridBagConstraints.HORIZONTAL;
        new_subject_text_constraints.gridwidth = 1;

        new_subject_text_constraints.gridx = 3;
        new_subject_text_constraints.gridy = new_subject_index + 4;
        panel.add(new_subject_text, new_subject_text_constraints);

        GridBagConstraints new_delete_subject_button_constraints = new GridBagConstraints();
        new_delete_subject_button_constraints.gridwidth = 1;
        new_delete_subject_button_constraints.gridx = 4;
        new_delete_subject_button_constraints.gridy = new_subject_index + 4;
        panel.add(new_delete_subject_button, new_delete_subject_button_constraints);
        panel.validate();
        panel.repaint();

        new_subject_index++;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }

}
