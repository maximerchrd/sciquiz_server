package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.database_management.DbTableClasses;
import com.sciquizapp.sciquizserver.database_management.DbTableRelationClassStudent;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by maximerichard on 22.01.18.
 */
public class DisplayTableUsersVsQuestions extends JPanel {
    public DisplayTableUsersVsQuestions(LayoutManager layout, boolean isDoubleBuffered, int screenWidth, int screenHeight, Table tableUserVsQuest) {
        super(layout, isDoubleBuffered);

        JComboBox chooseClass = null;
        JButton createNewClassButton = null;
        JButton saveClassButton = null;
        JButton addStudentToClassButton = null;
        JButton saveStudentToClassButton = null;
        JButton removeStudentFromClassButton = null;
        JButton editEvaluationButton = null;

        final DefaultComboBoxModel modelChooseClass = new DefaultComboBoxModel(DbTableClasses.getAllClasses());
        chooseClass = new JComboBox(modelChooseClass);
        GridBagConstraints chooseClassConstraints = new GridBagConstraints();
        chooseClassConstraints.gridx = 0;
        chooseClassConstraints.gridy = 0;
        this.add(chooseClass, chooseClassConstraints);

        Vector<Student> students = DbTableClasses.getStudentsInClass(modelChooseClass.getSelectedItem().toString());
        for (int i = 0; i < students.size(); i++) {
            tableUserVsQuest.addUser(students.get(i), false);
        }

        createNewClassButton = new JButton("create a new class");
        createNewClassButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                AddNewClass newClass = new AddNewClass(screenWidth, screenHeight, modelChooseClass);
            }
        });
        GridBagConstraints createNewClassButtonConstraints = new GridBagConstraints();
        createNewClassButtonConstraints.gridx = 1;
        createNewClassButtonConstraints.gridy = 0;
        this.add(createNewClassButton, createNewClassButtonConstraints);

        saveClassButton = new JButton("save students to class");
        saveClassButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                for (int i = 0; i < tableUserVsQuest.getTable().getRowCount(); i++) {
                    String studentName = tableUserVsQuest.getTable().getValueAt(0,i).toString();
                    String className = modelChooseClass.getSelectedItem().toString();
                    DbTableRelationClassStudent.addClassStudentRelation(className, studentName);
                }
            }
        });
        GridBagConstraints saveClassButtonConstraints = new GridBagConstraints();
        saveClassButtonConstraints.gridx = 2;
        saveClassButtonConstraints.gridy = 0;
        this.add(saveClassButton, saveClassButtonConstraints);

        removeStudentFromClassButton = new JButton("remove student from class");
        removeStudentFromClassButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                try {
                    String studentName = tableUserVsQuest.getTable().getValueAt(tableUserVsQuest.getTable().getSelectedRow(),0).toString();
                    String className = modelChooseClass.getSelectedItem().toString();
                    DbTableRelationClassStudent.removeStudentFromClass(studentName, className);
                            tableUserVsQuest.removeStudent(studentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        GridBagConstraints removeStudentFromClassButtonConstraints = new GridBagConstraints();
        removeStudentFromClassButtonConstraints.gridx = 0;
        removeStudentFromClassButtonConstraints.gridy = 1;
        this.add(removeStudentFromClassButton, removeStudentFromClassButtonConstraints);

        editEvaluationButton = new JButton("edit evaluation");
        editEvaluationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                try {
                    Student student = tableUserVsQuest.getStudentWithRow(tableUserVsQuest.getTable().getSelectedRow());
                    Integer globalID = Integer.parseInt(QuestionsBrowser.IDsFromBroadcastedQuestions.get(tableUserVsQuest.getTable().getSelectedColumn() - 3));
                    ChangeEvaluationOfQuestion changeEvaluationOfQuestion = new ChangeEvaluationOfQuestion(globalID, student.getStudentID());
                    System.out.println("student: " + student.getName() + " row: " + tableUserVsQuest.getTable().getSelectedRow());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        GridBagConstraints editEvaluationButtonConstraints = new GridBagConstraints();
        editEvaluationButtonConstraints.gridx = 1;
        editEvaluationButtonConstraints.gridy = 1;
        this.add(editEvaluationButton, editEvaluationButtonConstraints);

        tableUserVsQuest.setPreferredSize(new Dimension((int)(screenWidth*0.55),(int)(screenHeight*0.3)));
        GridBagConstraints tableUserVsQuestConstraints = new GridBagConstraints();
        tableUserVsQuestConstraints.gridx = 0;
        tableUserVsQuestConstraints.gridy = 2;
        tableUserVsQuestConstraints.gridwidth = 3;
        this.add(tableUserVsQuest, tableUserVsQuestConstraints);
    }
}
