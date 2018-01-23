package com.sciquizapp.sciquizserver;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        /*
        String[] chart_entries_options = {"New class"};
        chooseClass = new JComboBox(chart_entries_options);
        GridBagConstraints chooseClassConstraints = new GridBagConstraints();
        chooseClassConstraints.gridx = 0;
        chooseClassConstraints.gridy = 0;
        this.add(chooseClass, chooseClassConstraints);

        createNewClassButton = new JButton("create a new class");
        createNewClassButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                AddNewClass newClass = new AddNewClass(screenWidth, screenHeight);
            }
        });
        GridBagConstraints createNewClassButtonConstraints = new GridBagConstraints();
        createNewClassButtonConstraints.gridx = 1;
        createNewClassButtonConstraints.gridy = 0;
        this.add(createNewClassButton, createNewClassButtonConstraints);

        saveClassButton = new JButton("save students to class");
        GridBagConstraints saveClassButtonConstraints = new GridBagConstraints();
        saveClassButtonConstraints.gridx = 2;
        saveClassButtonConstraints.gridy = 0;
        this.add(saveClassButton, saveClassButtonConstraints);

        addStudentToClassButton = new JButton("add selected student to class");
        GridBagConstraints addStudentToClassButtonConstraints = new GridBagConstraints();
        addStudentToClassButtonConstraints.gridx = 0;
        addStudentToClassButtonConstraints.gridy = 1;
        this.add(addStudentToClassButton, addStudentToClassButtonConstraints);

        saveStudentToClassButton = new JButton("add selected student to class");
        GridBagConstraints saveStudentToClassButtonConstraints = new GridBagConstraints();
        saveStudentToClassButtonConstraints.gridx = 1;
        saveStudentToClassButtonConstraints.gridy = 1;
        this.add(saveStudentToClassButton, saveStudentToClassButtonConstraints);

        removeStudentFromClassButton = new JButton("remove student from class");
        GridBagConstraints removeStudentFromClassButtonConstraints = new GridBagConstraints();
        removeStudentFromClassButtonConstraints.gridx = 2;
        removeStudentFromClassButtonConstraints.gridy = 1;
        this.add(removeStudentFromClassButton, removeStudentFromClassButtonConstraints);*/

        tableUserVsQuest.setPreferredSize(new Dimension((int)(screenWidth*0.55),(int)(screenHeight*0.3)));
        GridBagConstraints tableUserVsQuestConstraints = new GridBagConstraints();
        tableUserVsQuestConstraints.gridx = 0;
        tableUserVsQuestConstraints.gridy = 2;
        tableUserVsQuestConstraints.gridwidth = 3;
        this.add(tableUserVsQuest, tableUserVsQuestConstraints);
    }
}
