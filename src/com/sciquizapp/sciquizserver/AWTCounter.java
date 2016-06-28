package com.sciquizapp.sciquizserver;
import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

import javax.swing.JFrame;

// An AWT program inherits from the top-level container java.awt.Frame
public class AWTCounter extends Frame implements ActionListener {
	private Label lblCount;    // Declare component Label
	private TextField tfCount; // Declare component TextField
	private TextField tfAnswer;
	private Button btnSetQuestNumber;   // Declare component Button
	private int questionNumber = 0;     // Counter's value
	private Table UVsQ;
	/** Constructor to setup GUI components and event handling */
	public AWTCounter (Table UsersVsQuestions, JFrame parentFrame) {
		UVsQ = UsersVsQuestions;
		
		setLayout(new FlowLayout());
		// "super" Frame sets its layout to FlowLayout, which arranges the components
		//  from left-to-right, and flow to next row from top-to-bottom.

		lblCount = new Label("Question Number");  // construct Label
		parentFrame.add(lblCount);                    // "super" Frame adds Label

		tfCount = new TextField("0", 10); // construct TextField
		tfCount.setBounds(200, 50, 30, 10);
		//tfCount.setEditable(false);       // set to read-only
		parentFrame.add(tfCount);                     // "super" Frame adds tfCount

		btnSetQuestNumber = new Button("Set Question Number");   // construct Button
		btnSetQuestNumber.setBounds(300, 50, 30, 10);
		parentFrame.add(btnSetQuestNumber);                    // "super" Frame adds Button

		btnSetQuestNumber.addActionListener(this);
		// Clicking Button source fires ActionEvent
		// btnCount registers this instance as ActionEvent listener

		//setTitle("Sciquiz Server");  // "super" Frame sets title
		parentFrame.setSize(500, 500);        // "super" Frame sets initial window size

		// System.out.println(this);
		// System.out.println(lblCount);
		// System.out.println(tfCount);
		// System.out.println(btnCount);

		parentFrame.setVisible(true);         // "super" Frame shows

		// System.out.println(this);
		// System.out.println(lblCount);
		// System.out.println(tfCount);
		// System.out.println(btnCount);

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
}