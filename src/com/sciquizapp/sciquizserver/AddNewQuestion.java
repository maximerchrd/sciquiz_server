package com.sciquizapp.sciquizserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.*;


public class AddNewQuestion extends JPanel implements ActionListener{
	JLabel question_label;
	JLabel answer1_label;
	JTextField question_text;
	JTextField answer1_text;
	JComboBox questiontype_list;
	Vector<JLabel> labelVector;
	Vector<JTextField> textfieldVector;
	ImageIcon icon;
	JLabel thumb;
	private JFileChooser mFileChooser;
	private String mFilePath = "";
	int new_option_index = 2;

	public AddNewQuestion(final List<Question> arg_questionList, final List<QuestionMultipleChoice> arg_multChoiceQuestionList,final DefaultListModel<String> arg_from_questions,
			final DefaultListModel<String> arg_from_IDs) {
		final JFrame new_question_frame = new JFrame("Ajouter une nouvelle question");
		Box box = Box.createVerticalBox();
		new_question_frame.add( box );
		Object[] questiontypes = new Object[]{"default","question ? choix multiples","question ? r?ponse br?ve"};
		questiontype_list = new JComboBox(questiontypes);
		question_label = new JLabel("Question:");
		question_text = new JTextField("");
		answer1_label = new JLabel("R?ponse 1:");
		answer1_text = new JTextField("");
		labelVector = new Vector<>();
		textfieldVector = new Vector<>();

		//Image image=GenerateImage.toImage(true);  //this generates an image file
		//icon = new ImageIcon(image); 
		thumb = new JLabel();
		//thumb.setIcon(icon);
		box.add(questiontype_list);
		box.add(question_label);
		box.add(question_text);
		box.add(answer1_label);
		box.add(answer1_text);

		//implement a button to add a choice
		JButton add_choice_button = new JButton("ajouter une option de r?ponse");
		add_choice_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JLabel new_answer_label = new JLabel("R?ponse " + new_option_index + ":");
				JTextField new_answer_text = new JTextField("");
				box.add(new_answer_label,(new_option_index - 2)* 2 + 5);
				box.add(new_answer_text,(new_option_index - 2) * 2 + 6);
				labelVector.add(new_answer_label);
				textfieldVector.add(new_answer_text);
				new_question_frame.setSize(new_question_frame.getWidth(), new_question_frame.getHeight()+30);
				new_option_index++;
			}
		});
		box.add(add_choice_button);

		//implement an action listener for the JComboBox to chose the question type
		questiontype_list.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (questiontype_list.getSelectedItem().toString().equals("question ? r?ponse br?ve")) {
					add_choice_button.setText("ajouter une r?ponse alternative");
				} else if (questiontype_list.getSelectedItem().toString().equals("question ? choix multiples")) {
					add_choice_button.setText("ajouter une option de r?ponse");
				}
			}
		});
		//implement a button to add a picture
		JButton add_image_button = new JButton("ajouter une image");
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
		box.add(add_image_button);

		//implement a button to add a new question to the database
		final DBManager new_db_man = new DBManager();
		
		JButton save_quest_button = new JButton("ajouter la question");
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
		box.add(save_quest_button);

		new_question_frame.setBounds(0, 0, 500, 500);
		new_question_frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
