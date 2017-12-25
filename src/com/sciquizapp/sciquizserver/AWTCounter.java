package com.sciquizapp.sciquizserver;
import com.sciquizapp.sciquizserver.database_management.DBManager;
import com.sciquizapp.sciquizserver.questions.Question;

import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

// An AWT program inherits from the top-level container java.awt.Frame
public class AWTCounter extends Frame implements ActionListener {
	private Label lblCount;    // Declare component Label
	private TextField tfCount; // Declare component TextField
	private TextField tfAnswer;
	public Button btnSetQuestNumber;   // Declare component Button
	private int questionNumber = 0;     // Counter's value
	private Question current_question = null; //active question (highlighted when button pressed)
	private Table UVsQ;
	private ChooseDropActionDemo drop_action_member = null;
	private List<Question> questionList = new ArrayList<Question>();
	/** Constructor to setup GUI components and event handling */
	public AWTCounter (Table UsersVsQuestions, JFrame parentFrame, ChooseDropActionDemo dropaction, JPanel panel_counter) {
		UVsQ = UsersVsQuestions;
		drop_action_member = dropaction;
		setLayout(new FlowLayout());
		// "super" Frame sets its layout to FlowLayout, which arranges the components
		//  from left-to-right, and flow to next row from top-to-bottom.
		
		//put the questions in a list
		DBManager database = new DBManager();
        try {
			questionList = database.getAllQuestions();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblCount = new Label("Question Number");  // construct Label
		panel_counter.add(lblCount);                    // "super" Frame adds Label

		tfCount = new TextField("0", 10); // construct TextField
		tfCount.setBounds(200, 50, 30, 10);
		//tfCount.setEditable(false);       // set to read-only
		panel_counter.add(tfCount);                     // "super" Frame adds tfCount
		
		btnSetQuestNumber = new Button("Set Question Number");   // construct Button
		btnSetQuestNumber.setBounds(300, 50, 30, 10);
		panel_counter.add(btnSetQuestNumber);                    // "super" Frame adds Button

		btnSetQuestNumber.addActionListener(this);
		// Clicking Button source fires ActionEvent
		// btnCount registers this instance as ActionEvent listener

		//setTitle("Sciquiz Server");  // "super" Frame sets title
		parentFrame.setSize(500, 500);        // "super" Frame sets initial window size
		//parentFrame.setVisible(true);         // "super" Frame shows
		tfAnswer = new TextField("", 30); // construct TextField
		tfAnswer.setEditable(false);       // set to read-only
		add(tfAnswer);                     // "super" Frame adds tfCount

	}

	/** ActionEvent handler - Called back upon button-click. */
	@Override
	public void actionPerformed(ActionEvent evt) {
		//++questionNumber; // increase the counter value
		// Display the counter value on the TextField tfCount
		questionNumber = Integer.parseInt(tfCount.getText());
		try {
			NetworkCommunication new_send_question = new NetworkCommunication();
			new_send_question.SendQuestion(questionList.get(drop_action_member.question_index), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UVsQ.addQuestion(String.valueOf(questionNumber));
		//tfCount.setText(questionNumber + ""); // convert int to String
	}

	public void editTextField(String TextToPrint) {
		//++questionNumber; // increase the counter value
		// Display the counter value on the TextField tfCount
		tfAnswer.setText(TextToPrint);
		//tfCount.setText(questionNumber + ""); // convert int to String
	}

	public int getQuestionNumber() {
		return questionNumber;
	}
	public Question getQuestion() {
		return current_question;
	}
}