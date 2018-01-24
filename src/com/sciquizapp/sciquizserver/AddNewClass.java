package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import tools.ListEntry;
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
import java.util.List;
import java.util.Vector;


public class AddNewClass extends JPanel implements ActionListener{
	JFrame new_class_frame;
	JPanel createClassPanel;
	JLabel classNameLabel;
	JTextArea classNameText;
	JLabel classLevelLabel;
	JTextArea classLevelText;
	JLabel classYearLabel;
	JTextArea classYearText;
	JButton createClassButton;


	public AddNewClass(int screenWidth, int screenHeight, DefaultComboBoxModel chooseClasse) {
		new_class_frame = new JFrame("Create a new class");
		createClassPanel = new JPanel(new GridBagLayout());

		GridBagConstraints classNameLabelConstraints = new GridBagConstraints();
		classNameLabelConstraints.gridx = 0;
		classNameLabelConstraints.gridy = 0;
		classNameLabel = new JLabel("Name of the class: \n");
		createClassPanel.add(classNameLabel, classNameLabelConstraints);

		GridBagConstraints classNameTextConstraints = new GridBagConstraints();
		classNameTextConstraints.gridx = 1;
		classNameTextConstraints.gridy = 0;
		classNameText = new JTextArea();
		classNameText.setPreferredSize(new Dimension(200,15));
		createClassPanel.add(classNameText, classNameTextConstraints);

		GridBagConstraints classLevelLabelConstraints = new GridBagConstraints();
		classLevelLabelConstraints.gridx = 0;
		classLevelLabelConstraints.gridy = 1;
		classLevelLabel = new JLabel("Level of the class: \n");
		createClassPanel.add(classLevelLabel, classLevelLabelConstraints);

		GridBagConstraints classLevelTextConstraints = new GridBagConstraints();
		classLevelTextConstraints.gridx = 1;
		classLevelTextConstraints.gridy = 1;
		classLevelText = new JTextArea();
		classLevelText.setPreferredSize(new Dimension(200,15));
		createClassPanel.add(classLevelText, classLevelTextConstraints);

		GridBagConstraints classYearLabelConstraints = new GridBagConstraints();
		classYearLabelConstraints.gridx = 0;
		classYearLabelConstraints.gridy = 2;
		classYearLabel = new JLabel("Year of the class: \n");
		createClassPanel.add(classYearLabel, classYearLabelConstraints);

		GridBagConstraints classYearTextConstraints = new GridBagConstraints();
		classYearTextConstraints.gridx = 1;
		classYearTextConstraints.gridy = 2;
		classYearText = new JTextArea();
		classYearText.setPreferredSize(new Dimension(200,15));
		createClassPanel.add(classYearText, classYearTextConstraints);

		GridBagConstraints createClassButtonConstraints = new GridBagConstraints();
		createClassButtonConstraints.gridx = 0;
		createClassButtonConstraints.gridy = 3;
		createClassButtonConstraints.gridwidth = 2;
		createClassButton = new JButton("create the class");
		createClassButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e1) {
				DbTableClasses.addClass(classNameText.getText(), classLevelText.getText(), classYearText.getText());
				chooseClasse.addElement(classNameText.getText());
				new_class_frame.dispatchEvent(new WindowEvent(new_class_frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		createClassPanel.add(createClassButton, createClassButtonConstraints);


		new_class_frame.add(createClassPanel);
		new_class_frame.setBounds((int)(screenWidth * 0.5), 0, (int)(screenWidth* 0.25), (int)(screenHeight * 0.15));
		new_class_frame.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
