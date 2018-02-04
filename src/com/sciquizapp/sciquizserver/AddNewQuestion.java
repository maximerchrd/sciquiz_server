package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import tools.ListEntry;
import tools.Scalr;

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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class AddNewQuestion extends JPanel implements ActionListener{
	JLabel question_label;
	JLabel answer1_label;
	JTextArea question_text;
	JCheckBox answer1_checkbox;
	JTextArea answer1_text;
	JButton answer1_delete_button;
	JComboBox questiontype_list;
	Vector<JLabel> labelVector;
	Vector<JCheckBox> checkboxVector;
	Vector<JTextArea> textfieldVector;
	final Vector<JTextArea> subjectsVector;
	final Vector<JTextArea> objectivesVector;
	private JFileChooser mFileChooser;
	private String mFilePath = "";
	int new_correct_answer_index = 0;
	int new_subject_index = 0;
	int new_objective_index = 0;
	int bottom_index = 7;
	final int MAX_ANSWERS = 10;
	JPanel panel;
	final JFrame new_question_frame;
	int window_width;
	int window_height;
	GridBagLayout columnsLayout;
	Object[] questiontypes;
	JButton save_quest_button;
	JButton add_image_button;


	public AddNewQuestion(final List<QuestionGeneric> arg_genericQuestionList, final JTree tree) {
		new_question_frame = new JFrame(Language.translate(Language.ADDNEWQUESTION));
		window_width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8);
		window_height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7);
		panel = new JPanel();
		new_question_frame.add( panel );
		panel.setAutoscrolls(true);
		new_question_frame.pack();
		questiontypes = new Object[]{"multiple choice question","short answer question"};
		questiontype_list = new JComboBox(questiontypes);
		question_label = new JLabel("Question:");
		question_text = new JTextArea("	");
		answer1_checkbox = new JCheckBox();
		answer1_label = new JLabel("Answer 1:");
		answer1_text = new JTextArea("	");
		checkboxVector = new Vector<>();
		labelVector = new Vector<>();
		textfieldVector = new Vector<>();
		subjectsVector = new Vector<>();
		objectivesVector = new Vector<>();
		GridBagConstraints add_image_button_constraints = new GridBagConstraints();
		GridBagConstraints save_quest_button_constraints = new GridBagConstraints();
		add_image_button = new JButton("add a picture");
		save_quest_button = new JButton("save the question");


		columnsLayout = new GridBagLayout();
		panel.setLayout(columnsLayout);


		GridBagConstraints questiontype_list_constraints = new GridBagConstraints();
		questiontype_list_constraints.gridwidth = 3;
		questiontype_list_constraints.gridx = 0;
		questiontype_list_constraints.gridy = 0;
		panel.add(questiontype_list,questiontype_list_constraints);

		GridBagConstraints question_label_constraints = new GridBagConstraints();
		question_label_constraints.gridwidth = 3;
		question_label_constraints.gridx = 0;
		question_label_constraints.gridy = 1;
		panel.add(question_label,question_label_constraints);

		GridBagConstraints question_text_constraints = new GridBagConstraints();
		question_text_constraints.fill = GridBagConstraints.HORIZONTAL;
		question_text_constraints.gridwidth = 3;
		question_text_constraints.gridx = 0;
		question_text_constraints.gridy = 2;
		panel.add(question_text,question_text_constraints);

		GridBagConstraints answer1_label_constraints = new GridBagConstraints();
		answer1_label_constraints.gridwidth = 3;
		answer1_label_constraints.gridx = 0;
		answer1_label_constraints.gridy = 4;
		panel.add(answer1_label,answer1_label_constraints);

		GridBagConstraints answer1_checkbox_constraints = new GridBagConstraints();
		answer1_checkbox_constraints.fill = GridBagConstraints.HORIZONTAL;
		answer1_checkbox_constraints.gridwidth = 1;
		answer1_checkbox_constraints.gridx = 0;
		answer1_checkbox_constraints.gridy = 5;
		panel.add(answer1_checkbox,answer1_checkbox_constraints);
		checkboxVector.add(answer1_checkbox);

		GridBagConstraints answer1_text_constraints = new GridBagConstraints();
		answer1_text_constraints.fill = GridBagConstraints.HORIZONTAL;
		answer1_text_constraints.gridwidth = 1;
		answer1_text_constraints.gridx = 1;
		answer1_text_constraints.gridy = 5;
		panel.add(answer1_text,answer1_text_constraints);
		textfieldVector.add(answer1_text);

		JButton answer1_delete_button = new JButton("x");
		answer1_delete_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(answer1_checkbox);
				panel.remove(answer1_label);
				panel.remove(answer1_text);
				panel.remove(answer1_delete_button);
				panel.validate();
				panel.repaint();
			}
		});
		GridBagConstraints answer1_delete_button_constraints = new GridBagConstraints();
		answer1_delete_button_constraints.gridwidth = 1;
		answer1_delete_button_constraints.gridx = 2;
		answer1_delete_button_constraints.gridy = 5;
		panel.add(answer1_delete_button,answer1_delete_button_constraints);

		//implement a button to add a correct answer
		JButton add_correct_answer_button = new JButton("Add an answer");
		add_correct_answer_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1) {
				if (new_correct_answer_index < MAX_ANSWERS - 1) {
					JLabel new_answer_label = new JLabel("Answer " + (new_correct_answer_index + 2) + ":");
					JCheckBox new_checkbox = new JCheckBox();
					checkboxVector.add(new_checkbox);
					JTextArea new_answer_text = new JTextArea("	");
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
					save_quest_button_constraints.gridy += 2;
					columnsLayout.setConstraints(add_image_button, add_image_button_constraints);
					columnsLayout.setConstraints(save_quest_button, save_quest_button_constraints);
					panel.revalidate();
					panel.repaint();
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
		add_image_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//Set up the file chooser.
				if (mFileChooser == null) {
					mFileChooser = new JFileChooser();

					//Add a custom file filter and disable the default
					//(Accept All) file filter.
					//		        	mFileChooser.addChoosableFileFilter(new ImageFilter());
					//		        	mFileChooser.setAcceptAllFileFilterUsed(false);

				}

				//Show it.
				int returnVal = mFileChooser.showDialog(AddNewQuestion.this,
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
			}
		});
		add_image_button_constraints.gridwidth = 3;
		add_image_button_constraints.gridx = 0;
		add_image_button_constraints.gridy = bottom_index - 1;
		panel.add(add_image_button, add_image_button_constraints);

		//implement a button to add a new question to the database
		final DBManager new_db_man = new DBManager();
		

		save_quest_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Vector<String> options_vector = new Vector<String>();
				for (int i = 0; i < 10; i++) options_vector.add(" ");
				for (int i = 0; i < 10 && i < textfieldVector.size() && !textfieldVector.elementAt(i).equals(" "); i++) options_vector.set(i,textfieldVector.elementAt(i).getText());

				for (int i = 0; i < subjectsVector.size(); i++) {
					try {
						DbTableSubject.addSubject(subjectsVector.get(i).getText().replace("'","''"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				for (int i = 0; i < objectivesVector.size(); i++) {
					try {
						DbTableLearningObjectives.addObjective(objectivesVector.get(i).getText().replace("'","''"),1);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				//add question to database according to question type
				if (questiontype_list.getSelectedItem().toString().equals("short answer question")) {
					QuestionShortAnswer new_questshortanswer = new QuestionShortAnswer();
					new_questshortanswer.setQUESTION(question_text.getText().replace("'","''"));
					if (mFilePath.length() > 0) {
						new_questshortanswer.setIMAGE(mFilePath);
					}
					ArrayList<String> answerOptions = new ArrayList<String>();
					for (int i = 0; i < textfieldVector.size(); i++) {
						if (textfieldVector.get(i).toString().length() > 0) {
							answerOptions.add(textfieldVector.get(i).getText().replace("'","''"));
						}
					}
					new_questshortanswer.setANSWER(answerOptions);
					String idGlobal = "-1";
					try {
						idGlobal = DbTableQuestionShortAnswer.addShortAnswerQuestion(new_questshortanswer);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					new_questshortanswer.setID(Integer.valueOf(idGlobal));
					arg_genericQuestionList.add(new QuestionGeneric(new_questshortanswer.getID(), 1));

					//resize image of question to fit icon size
					ImageIcon icon = new ImageIcon(new_questshortanswer.getIMAGE());
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
					DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
					model.insertNodeInto(new DefaultMutableTreeNode(new_questshortanswer), root, root.getChildCount());
					model.reload();

					for (int i = 0; i < subjectsVector.size(); i++) {
						try {
							DbTableRelationQuestionSubject.addRelationQuestionSubject(subjectsVector.get(i).getText().replace("'","''"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					for (int i = 0; i < objectivesVector.size(); i++) {
						try {
							DbTableRelationQuestionObjective.addRelationQuestionObjective(objectivesVector.get(i).getText().replace("'","''"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} else if (questiontype_list.getSelectedItem().toString().equals("multiple choice question")) {
					int number_correct_answers = 0;
					String temp_option;
					for (int i = 0; i < checkboxVector.size(); i++) {
						if (checkboxVector.get(i).isSelected()) {
							temp_option = options_vector.get(number_correct_answers);
							options_vector.set(number_correct_answers,options_vector.get(i));
							options_vector.set(i,temp_option);
							number_correct_answers++;
						}
					}
					QuestionMultipleChoice new_questmultchoice = new QuestionMultipleChoice("1", question_text.getText().replace("'","''"), options_vector.get(0).replace("'","''"),
							options_vector.get(1).replace("'","''"), options_vector.get(2).replace("'","''"), options_vector.get(3).replace("'","''"), options_vector.get(4).replace("'","''"),
							options_vector.get(5).replace("'","''"), options_vector.get(6).replace("'","''"), options_vector.get(7).replace("'","''"), options_vector.get(8).replace("'","''"),
							options_vector.get(9).replace("'","''"), mFilePath.replace("'","''"));
					new_questmultchoice.setNB_CORRECT_ANS(number_correct_answers);
					try {
						DbTableQuestionMultipleChoice.addMultipleChoiceQuestion(new_questmultchoice);
						new_questmultchoice.setID(DbTableQuestionMultipleChoice.getLastIDGlobal());

					} catch (Exception e1) {
						e1.printStackTrace();
					}
					arg_genericQuestionList.add(new QuestionGeneric(new_questmultchoice.getID(),0));

					//resize image of question to fit icon size
					ImageIcon icon = new ImageIcon(new_questmultchoice.getIMAGE());
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
					DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
					model.insertNodeInto(new DefaultMutableTreeNode(new_questmultchoice), root, root.getChildCount());
					model.reload();

					for (int i = 0; i < subjectsVector.size(); i++) {
						try {
							DbTableRelationQuestionSubject.addRelationQuestionSubject(subjectsVector.get(i).getText().replace("'","''"));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					for (int i = 0; i < objectivesVector.size(); i++) {
						try {
							DbTableRelationQuestionObjective.addRelationQuestionObjective(objectivesVector.get(i).getText().replace("'","''"));
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
		save_quest_button_constraints.gridy = bottom_index;
		save_quest_button_constraints.gridwidth = 3;
		panel.add(save_quest_button, save_quest_button_constraints);

		new_question_frame.setBounds(0, 0, window_width, window_height);
		new_question_frame.setVisible(true);
	}

	private void addObjectiveUI() {
		//implement a button to add an objective
		JButton add_objective_button = new JButton(Language.translate(Language.ADDOBJECTIVEBUTTON));
		add_objective_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				JTextArea new_objective_text = new JTextArea("	");
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
				panel.add(new_objective_text,new_objective_text_constraints);

				GridBagConstraints new_delete_objective_button_constraints = new GridBagConstraints();
				new_delete_objective_button_constraints.gridwidth = 1;
				new_delete_objective_button_constraints.gridx = 6;
				new_delete_objective_button_constraints.gridy = new_objective_index + 4;
				panel.add(new_delete_objective_button,new_delete_objective_button_constraints);
				panel.validate();
				panel.repaint();

				new_objective_index++;
			}
		});
		GridBagConstraints add_objective_button_constraints = new GridBagConstraints();
		add_objective_button_constraints.gridwidth = 2;
		add_objective_button_constraints.gridx = 5;
		add_objective_button_constraints.gridy = 3;
		panel.add(add_objective_button, add_objective_button_constraints);
	}

	private void addSubjectUI() {
		//implement a button to add a subject
		JButton add_subject_button = new JButton(Language.translate(Language.ADDSUBJECTBUTTON));
		add_subject_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				JTextArea new_subject_text = new JTextArea("	");
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
				panel.add(new_subject_text,new_subject_text_constraints);

				GridBagConstraints new_delete_subject_button_constraints = new GridBagConstraints();
				new_delete_subject_button_constraints.gridwidth = 1;
				new_delete_subject_button_constraints.gridx = 4;
				new_delete_subject_button_constraints.gridy = new_subject_index + 4;
				panel.add(new_delete_subject_button,new_delete_subject_button_constraints);
				panel.validate();
				panel.repaint();

				new_subject_index++;
			}
		});
		GridBagConstraints add_subject_button_constraints = new GridBagConstraints();
		add_subject_button_constraints.gridwidth = 2;
		add_subject_button_constraints.gridx = 3;
		add_subject_button_constraints.gridy = 3;
		panel.add(add_subject_button, add_subject_button_constraints);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
