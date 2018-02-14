package com.sciquizapp.sciquizserver;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import com.sciquizapp.sciquizserver.database_management.DbTableRelationSubjectSubject;
import com.sciquizapp.sciquizserver.database_management.DbTableSubject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;


public class AddNewSubject extends JPanel implements ActionListener{
	private final Vector<JComboBox> parentSubjectsVector;
	private final Vector<JComboBox> childSubjectsVector;
	private int new_subject_index = 0;
	private int new_objective_index = 0;
	private int bottom_index = 7;
	private JPanel panel;
	private final JFrame new_subject_frame;
	private int window_width;
	private int window_height;
	private GridBagLayout columnsLayout;
	private JLabel newSubjectLabel;
	private JTextArea newSubjectTextArea;
	private JButton saveSubjectButton;
	private QuestionsBrowser mQuestionsBrowser;


	public AddNewSubject(QuestionsBrowser questionsBrowser) {
		mQuestionsBrowser = questionsBrowser;
		new_subject_frame = new JFrame("Create a new subject");
		window_width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.5);
		window_height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.35);
		panel = new JPanel();
		new_subject_frame.add( panel );
		panel.setAutoscrolls(true);
		new_subject_frame.pack();
		parentSubjectsVector = new Vector<>();
		childSubjectsVector = new Vector<>();

		newSubjectLabel = new JLabel("New Subject:");
		newSubjectTextArea = new JTextArea("		");
		saveSubjectButton = new JButton("save the subject");
		saveSubjectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				saveSubject();
			}
		});


		GridBagConstraints newSubjectLabelConstraints = new GridBagConstraints();
		newSubjectLabelConstraints.gridx = 0;
		newSubjectLabelConstraints.gridy = 0;
		panel.add(newSubjectLabel,newSubjectLabelConstraints);

		GridBagConstraints newSubjectTextAreaConstraints = new GridBagConstraints();
		newSubjectTextAreaConstraints.gridwidth = 3;
		newSubjectTextAreaConstraints.gridx = 1;
		newSubjectTextAreaConstraints.gridy = 0;
		panel.add(newSubjectTextArea);

		GridBagConstraints saveSubjectButtonConstraints = new GridBagConstraints();
		saveSubjectButtonConstraints.gridx = 4;
		saveSubjectButtonConstraints.gridy = 0;
		panel.add(saveSubjectButton,saveSubjectButtonConstraints);


		columnsLayout = new GridBagLayout();
		panel.setLayout(columnsLayout);

		addParentSubjectUI();
		addChildSubjectUI();


		new_subject_frame.setBounds(0, 0, window_width, window_height);
		new_subject_frame.setVisible(true);
	}

	private void addChildSubjectUI() {
		//implement a button to add an objective
		JButton addChildSubjectButton = new JButton("Add a child subject");
		addChildSubjectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				JComboBox newChildSubjectText = new JComboBox();
				Vector<String> allSubjects = DbTableSubject.getAllSubjects();
				Object[] elements = new Object[allSubjects.size()];
				for (int i = 0; i < allSubjects.size(); i++) {
					elements[i] = allSubjects.get(i);
				}
				AutoCompleteSupport.install(newChildSubjectText, GlazedLists.eventListOf(elements));
				newChildSubjectText.setPreferredSize(new Dimension(200,25));
				childSubjectsVector.add(newChildSubjectText);
				JButton removeChildSubjectButton = new JButton("x");
				removeChildSubjectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e2) {
						panel.remove(newChildSubjectText);
						panel.remove(removeChildSubjectButton);
						panel.validate();
						panel.repaint();
						childSubjectsVector.remove(newChildSubjectText);
					}
				});

				GridBagConstraints new_objective_text_constraints = new GridBagConstraints();
				new_objective_text_constraints.fill = GridBagConstraints.HORIZONTAL;
				new_objective_text_constraints.gridwidth = 1;
				new_objective_text_constraints.gridx = 2;
				new_objective_text_constraints.gridy = new_objective_index + 4;
				panel.add(newChildSubjectText,new_objective_text_constraints);

				GridBagConstraints new_delete_objective_button_constraints = new GridBagConstraints();
				new_delete_objective_button_constraints.gridwidth = 1;
				new_delete_objective_button_constraints.gridx = 3;
				new_delete_objective_button_constraints.gridy = new_objective_index + 4;
				panel.add(removeChildSubjectButton,new_delete_objective_button_constraints);
				panel.validate();
				panel.repaint();

				new_objective_index++;
			}
		});
		GridBagConstraints addChildSubjectButtonConstraints = new GridBagConstraints();
		addChildSubjectButtonConstraints.gridwidth = 2;
		addChildSubjectButtonConstraints.gridx = 2;
		addChildSubjectButtonConstraints.gridy = 3;
		panel.add(addChildSubjectButton, addChildSubjectButtonConstraints);
	}

	private void addParentSubjectUI() {
		//implement a button to add a subject
		JButton addParentSubjectButton = new JButton("Add a parent subject");
		addParentSubjectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				JComboBox newParentSubjectText = new JComboBox();
				Vector<String> allSubjects = DbTableSubject.getAllSubjects();
				Object[] elements = new Object[allSubjects.size()];
				for (int i = 0; i < allSubjects.size(); i++) {
					elements[i] = allSubjects.get(i);
				}
				AutoCompleteSupport.install(newParentSubjectText, GlazedLists.eventListOf(elements));
				newParentSubjectText.setPreferredSize(new Dimension(200,25));
				parentSubjectsVector.add(newParentSubjectText);
				JButton removeParentSubjectButton = new JButton("x");
				removeParentSubjectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e2) {
						panel.remove(newParentSubjectText);
						panel.remove(removeParentSubjectButton);
						panel.validate();
						panel.repaint();
						parentSubjectsVector.remove(newParentSubjectText);
					}
				});

				GridBagConstraints new_subject_text_constraints = new GridBagConstraints();
				new_subject_text_constraints.fill = GridBagConstraints.HORIZONTAL;
				new_subject_text_constraints.gridwidth = 1;

				new_subject_text_constraints.gridx = 0;
				new_subject_text_constraints.gridy = new_subject_index + 4;
				panel.add(newParentSubjectText,new_subject_text_constraints);

				GridBagConstraints new_delete_subject_button_constraints = new GridBagConstraints();
				new_delete_subject_button_constraints.gridwidth = 1;
				new_delete_subject_button_constraints.gridx = 1;
				new_delete_subject_button_constraints.gridy = new_subject_index + 4;
				panel.add(removeParentSubjectButton,new_delete_subject_button_constraints);
				panel.validate();
				panel.repaint();

				new_subject_index++;
			}
		});
		GridBagConstraints addParentSubjectButtonConstraints = new GridBagConstraints();
		addParentSubjectButtonConstraints.gridwidth = 2;
		addParentSubjectButtonConstraints.gridx = 0;
		addParentSubjectButtonConstraints.gridy = 3;
		panel.add(addParentSubjectButton, addParentSubjectButtonConstraints);
	}

	private void saveSubject() {
		DbTableSubject.addSubject(newSubjectTextArea.getText());
		DbTableRelationSubjectSubject.removeRelationsForSubject(newSubjectTextArea.getText());
		for (int i = 0; i < parentSubjectsVector.size(); i++) {
			DbTableRelationSubjectSubject.addRelationSubjectSubject(parentSubjectsVector.get(i).getSelectedItem().toString().replace("'", "''"),newSubjectTextArea.getText().replace("'", "''"));
		}
		for (int i = 0; i < childSubjectsVector.size(); i++) {
			DbTableRelationSubjectSubject.addRelationSubjectSubject(newSubjectTextArea.getText().replace("'", "''"),childSubjectsVector.get(i).getSelectedItem().toString().replace("'", "''"));
		}

		mQuestionsBrowser.buildSubjectTree();
		new_subject_frame.dispatchEvent(new WindowEvent(new_subject_frame, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
