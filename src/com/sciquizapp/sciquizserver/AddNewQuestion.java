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

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AddNewQuestion extends JPanel implements ActionListener{
	JLabel question_label;
	JLabel answer1_label;
	JLabel answer2_label;
	JLabel answer3_label;
	JLabel answer4_label;
	JTextField question_text;
	JTextField answer1_text;
	JTextField answer2_text;
	JTextField answer3_text;
	JTextField answer4_text;
	ImageIcon icon;
	JLabel thumb;
	private JFileChooser mFileChooser;
	private String mFilePath = "";

	public AddNewQuestion(final List<Question> arg_questionList, final DefaultListModel<String> arg_from_questions, 
			final DefaultListModel<String> arg_from_IDs) {
		final JFrame new_question_frame = new JFrame("Ajouter une nouvelle question");
		Box box = Box.createVerticalBox();
		new_question_frame.add( box );
		question_label = new JLabel("Question:");
		question_text = new JTextField("");
		answer1_label = new JLabel("Réponse 1:");
		answer1_text = new JTextField("");
		answer2_label = new JLabel("Réponse 2:");
		answer2_text = new JTextField("");
		answer3_label = new JLabel("Réponse 3:");
		answer3_text = new JTextField("");
		answer4_label = new JLabel("Réponse 4:");
		answer4_text = new JTextField("");

		//Image image=GenerateImage.toImage(true);  //this generates an image file
		//icon = new ImageIcon(image); 
		thumb = new JLabel();
		//thumb.setIcon(icon);
		box.add(question_label);
		box.add(question_text);
		box.add(answer1_label);
		box.add(answer1_text);
		box.add(answer2_label);
		box.add(answer2_text);
		box.add(answer3_label);
		box.add(answer3_text);
		box.add(answer4_label);
		box.add(answer4_text);

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
		
		JButton save_quest_button = new JButton("ajouter une question");
		save_quest_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Question new_quest = new Question("chimie", "1", question_text.getText(), answer1_text.getText(), 
						answer2_text.getText(), answer3_text.getText(), answer4_text.getText(),answer1_text.getText(),mFilePath);
				try {
					new_db_man.addQuestion(new_quest);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				arg_questionList.add(new_quest);
				arg_from_questions.addElement(new_quest.getQUESTION());
				arg_from_IDs.addElement(String.valueOf(arg_questionList.get(arg_questionList.size() - 1).getID()));
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
