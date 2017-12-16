package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.database_management.DBManager;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Vector;

import javax.swing.*;


public class AddNewQuestion extends JPanel implements ActionListener{
	JLabel question_label;
	JLabel answer1_label;
	JTextArea question_text;
	JCheckBox answer1_checkbox;
	JTextArea answer1_text;
	JButton answer1_delete_button;
	JComboBox questiontype_list;
	Vector<JLabel> labelVector;
	Vector<JTextArea> textfieldVector;
	private JFileChooser mFileChooser;
	private String mFilePath = "";
	int new_correct_answer_index = 0;
	int new_incorrect_option_index = 0;
	int bottom_index = 7;
	final int MAX_ANSWERS = 10;

	public AddNewQuestion(final List<QuestionGeneric> arg_genericQuestionList, final List<Question> arg_questionList, final List<QuestionMultipleChoice> arg_multChoiceQuestionList, final DefaultListModel<String> arg_from_questions,
						  final DefaultListModel<String> arg_from_IDs) {
		final JFrame new_question_frame = new JFrame("Ajouter une nouvelle question");
		int window_width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.4);
		int window_height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7);
		JPanel panel = new JPanel();
		new_question_frame.add( panel );
		panel.setAutoscrolls(true);
		new_question_frame.pack();
		Object[] questiontypes = new Object[]{"default","question ? choix multiples","question ? r?ponse br?ve"};
		questiontype_list = new JComboBox(questiontypes);
		question_label = new JLabel("Question:");
		question_text = new JTextArea("");
		answer1_checkbox = new JCheckBox();
		answer1_label = new JLabel("R?ponse 1:");
		answer1_text = new JTextArea("");
		labelVector = new Vector<>();
		textfieldVector = new Vector<>();
		GridBagConstraints add_image_button_constraints = new GridBagConstraints();
		GridBagConstraints save_quest_button_constraints = new GridBagConstraints();
		JButton add_image_button = new JButton("ajouter une image");
		JButton save_quest_button = new JButton("ajouter la question");


		GridBagLayout twoColLayout = new GridBagLayout();
		panel.setLayout(twoColLayout);


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

		GridBagConstraints answer1_text_constraints = new GridBagConstraints();
		answer1_text_constraints.fill = GridBagConstraints.HORIZONTAL;
		answer1_text_constraints.gridwidth = 1;
		answer1_text_constraints.ipadx = (int) (window_width * 0.5);
		answer1_text_constraints.ipady = (int) (window_height * 0.7);
		answer1_text_constraints.gridx = 1;
		answer1_text_constraints.gridy = 5;
		panel.add(answer1_text,answer1_text_constraints);

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
		JButton add_correct_answer_button = new JButton("Ajouter une r?ponse");
		add_correct_answer_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e1)
			{
				JLabel new_answer_label = new JLabel("R?ponse " + (new_correct_answer_index + 2) + ":");
				JCheckBox new_correct_checkbox = new JCheckBox();
				JTextArea new_answer_text = new JTextArea("");
				JButton new_delete_answer_button = new JButton("x");
				new_delete_answer_button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e2) {
						panel.remove(new_answer_label);
						panel.remove(new_correct_checkbox);
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
				panel.add(new_answer_label,new_answer_label_constraints);

				GridBagConstraints new_correct_checkbox_constraints = new GridBagConstraints();
				new_correct_checkbox_constraints.gridwidth = 1;
				new_correct_checkbox_constraints.gridx = 0;
				new_correct_checkbox_constraints.gridy = new_correct_answer_index * 2 + 7;
				panel.add(new_correct_checkbox,new_correct_checkbox_constraints);

				GridBagConstraints new_answer_text_constraints = new GridBagConstraints();
				new_answer_text_constraints.fill = GridBagConstraints.HORIZONTAL;
				new_answer_text_constraints.gridwidth = 1;
				new_answer_text_constraints.ipadx = (int) (window_width * 0.5);
				new_answer_text_constraints.ipady = (int) (window_height * 0.07);
				new_answer_text_constraints.gridx = 1;
				new_answer_text_constraints.gridy = new_correct_answer_index * 2 + 7;
				panel.add(new_answer_text,new_answer_text_constraints);

				GridBagConstraints new_delete_answer_button_constraints = new GridBagConstraints();
				new_delete_answer_button_constraints.gridwidth = 1;
				new_delete_answer_button_constraints.gridx = 2;
				new_delete_answer_button_constraints.gridy = new_correct_answer_index * 2 + 7;
				panel.add(new_delete_answer_button,new_delete_answer_button_constraints);

				labelVector.add(new_answer_label);
				textfieldVector.add(new_answer_text);
				new_question_frame.setSize(new_question_frame.getWidth(), new_question_frame.getHeight()+30);
				new_correct_answer_index++;

				if (new_correct_answer_index >= new_incorrect_option_index) {
					add_image_button_constraints.gridy += 2;
					save_quest_button_constraints.gridy += 2;
					twoColLayout.setConstraints(add_image_button, add_image_button_constraints);
					twoColLayout.setConstraints(save_quest_button, save_quest_button_constraints);
					panel.revalidate();
					panel.repaint();
				}
			}
		});
		GridBagConstraints add_correct_answer_button_constraints = new GridBagConstraints();
		add_correct_answer_button_constraints.gridwidth = 3;
		add_correct_answer_button_constraints.gridx = 0;
		add_correct_answer_button_constraints.gridy = 3;
		panel.add(add_correct_answer_button, add_correct_answer_button_constraints);

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
				for (int i = 0; i < 9; i++) options_vector.add(" ");
				for (int i = 0; i < 9 && i < textfieldVector.size() && !textfieldVector.elementAt(i).equals(" "); i++) options_vector.set(i,textfieldVector.elementAt(i).getText());


				//add question to database according to question type
				if (questiontype_list.getSelectedItem().toString().equals("question ? r?ponse br?ve")) {
					QuestionShortAnswer new_questshortanswer = new QuestionShortAnswer("chimie", "1", question_text.getText(), answer1_text.getText(), mFilePath);

				} else if (questiontype_list.getSelectedItem().toString().equals("question ? choix multiples")) {
					QuestionMultipleChoice new_questmultchoice = new QuestionMultipleChoice("chimie", "1", question_text.getText(), answer1_text.getText(),
							options_vector.elementAt(0), options_vector.elementAt(1), options_vector.elementAt(2), options_vector.elementAt(3),
							options_vector.elementAt(4), options_vector.elementAt(5), options_vector.elementAt(6), options_vector.elementAt(7),
							options_vector.elementAt(8), mFilePath);
					try {
						new_db_man.addMultipleChoiceQuestion(new_questmultchoice);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					arg_multChoiceQuestionList.add(new_questmultchoice);
					arg_genericQuestionList.add(new QuestionGeneric("MULTQ",arg_multChoiceQuestionList.size()-1));
					arg_from_questions.addElement(new_questmultchoice.getQUESTION());
					arg_from_IDs.addElement(String.valueOf(arg_multChoiceQuestionList.get(arg_multChoiceQuestionList.size() - 1).getID()));

				} else {
					Question new_quest = new Question("chimie", "1", question_text.getText(), answer1_text.getText(),
							options_vector.elementAt(0), options_vector.elementAt(1), options_vector.elementAt(2),answer1_text.getText(),mFilePath);
					try {
						new_db_man.addQuestion(new_quest);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					arg_questionList.add(new_quest);
					arg_from_questions.addElement(new_quest.getQUESTION());
					arg_from_IDs.addElement(String.valueOf(arg_questionList.get(arg_questionList.size() - 1).getID()));
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
