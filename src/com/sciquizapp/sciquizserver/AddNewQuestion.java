package com.sciquizapp.sciquizserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class AddNewQuestion {
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

	public AddNewQuestion() {
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

		final DBManager new_db_man = new DBManager();
		//implement a button to add a new question to the database
		JButton save_quest_button = new JButton("ajouter une question");
		save_quest_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Question new_quest = new Question("chimie", "1", question_text.getText(), answer1_text.getText(), answer2_text.getText(), answer3_text.getText(), answer4_text.getText(),answer1_text.getText(),"");
				try {
					new_db_man.addQuestion(new_quest);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				new_question_frame.dispatchEvent(new WindowEvent(new_question_frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		box.add(save_quest_button);

		new_question_frame.setBounds(0, 0, 500, 500);
		new_question_frame.setVisible(true);
	}

}
