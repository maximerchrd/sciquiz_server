/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sciquizapp.sciquizserver;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.sciquizapp.sciquizserver.database_management.DBManager;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseDropActionDemo extends JFrame {
	public int question_index = 0;
	DefaultListModel<String> from_questions = new DefaultListModel<String>();
	DefaultListModel<String> from_IDs = new DefaultListModel<String>();
	DefaultListModel<String> copy_question = new DefaultListModel<String>();
	DefaultListModel<String> copy_IDs = new DefaultListModel<String>();
	JList<String> dragFrom;
	public JPanel panel_for_from;
	public JPanel panel_for_copy;
	private List<Question> questionList = new ArrayList<Question>();
	private List<QuestionMultipleChoice> multipleChoicesQuestList = new ArrayList<QuestionMultipleChoice>();
	private List<QuestionGeneric> genericQuestionList = new ArrayList<QuestionGeneric>();
	private List<QuestionGeneric> quiz = new ArrayList<QuestionGeneric>();
	private NetworkCommunication own_networkcommunication = null;

	public ChooseDropActionDemo(final JFrame parentFrame, final JPanel panel_questlist, final JPanel panel_disquest, final NetworkCommunication network_singleton) {
		super("ChooseDropActionDemo");

		own_networkcommunication = network_singleton;
		DBManager database = new DBManager();
		try {
			questionList = database.getAllQuestions();
			multipleChoicesQuestList = database.getAllMultipleChoiceQuestions();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < questionList.size(); i++) {
			from_questions.addElement(questionList.get(i).getQUESTION());
			from_IDs.addElement(String.valueOf(questionList.get(i).getID()));
			QuestionGeneric temp_generic_question = new QuestionGeneric("QUEST",i);
			genericQuestionList.add(temp_generic_question);
		}
		for (int i = 0; i < multipleChoicesQuestList.size(); i++) {
			from_questions.addElement(multipleChoicesQuestList.get(i).getQUESTION());
			from_IDs.addElement(String.valueOf(multipleChoicesQuestList.get(i).getID()));
			QuestionGeneric temp_generic_question = new QuestionGeneric("MULTQ",i);
			genericQuestionList.add(temp_generic_question);
		}

		panel_for_from = new JPanel();
		panel_for_from.setLayout(new BoxLayout(panel_for_from, BoxLayout.Y_AXIS));
		dragFrom = new JList<String>(from_questions);
		dragFrom.setTransferHandler(new FromTransferHandler());
		dragFrom.setPrototypeCellValue("List Item WWWWWW");
		dragFrom.setDragEnabled(true);
		dragFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JLabel label = new JLabel("Drag from here:");
		label.setAlignmentX(0f);
		panel_for_from.add(label);
		JScrollPane sp = new JScrollPane(dragFrom);
		sp.setAlignmentX(0f);
		panel_for_from.add(sp);

		//implement a button to add a new question to the database
		JButton new_quest_button = new JButton("ajouter une question");
		new_quest_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AddNewQuestion new_quest = new AddNewQuestion(questionList, multipleChoicesQuestList, from_questions, from_IDs);
			}
		});
		panel_for_from.add(new_quest_button);

		parentFrame.add(panel_for_from, BorderLayout.WEST);

		/*JList moveTo = new JList(move);
        moveTo.setTransferHandler(new ToTransferHandler(TransferHandler.MOVE));
        moveTo.setDropMode(DropMode.INSERT);*/

		final JList<String> copyTo = new JList<String>(copy_question);
		copyTo.setTransferHandler(new ToTransferHandler(TransferHandler.COPY));
		copyTo.setDropMode(DropMode.INSERT);

		//listener for when highlighted line changed
		final DisplayQuestion dis_question = new DisplayQuestion(panel_disquest);
		copyTo.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					//display question
					int indexOfQuestion = copyTo.getSelectedIndex();
					question_index = indexOfQuestion;
					Question questionToDisplay;
					questionToDisplay = questionList.get(indexOfQuestion);					//once db updated, fix this to display question according to index
					dis_question.ShowQuestion(questionToDisplay, parentFrame, panel_disquest);
					dis_question.repaint();
				}
			}
		}); 

		panel_for_copy = new JPanel();
		panel_for_copy.setLayout(new BoxLayout(panel_for_copy, BoxLayout.Y_AXIS));
		/*label = new JLabel("Drop to MOVE to here:");
        label.setAlignmentX(0f);
        panel_for_copy.add(label);
        sp = new JScrollPane(moveTo);
        sp.setAlignmentX(0f);
        panel_for_copy.add(sp);*/
		label = new JLabel("Drop to COPY to here:");
		label.setAlignmentX(0f);
		panel_for_copy.add(label);
		sp = new JScrollPane(copyTo);
		sp.setAlignmentX(0f);
		panel_for_copy.add(sp);
		panel_for_copy.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		panel_questlist.setLayout(new FlowLayout());
		panel_questlist.add(panel_for_from);
		panel_questlist.add(panel_for_copy);

		//implement a button to send the highlighted question
		JButton send_quest_button = new JButton("activer la question");
		send_quest_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Question question_to_send;
				System.out.println("copyTo.getSelectedIndex() =  " + copyTo.getSelectedIndex());
				question_to_send = questionList.get(copyTo.getSelectedIndex());
				try {
					network_singleton.SendQuestion(question_to_send, false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_for_from.add(send_quest_button);

        //implement a button to send the questions from the panel for copy
        JButton send_questions_button = new JButton("envoyer les questions");
        send_questions_button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try {
					network_singleton.SendQuestionList(questionList, multipleChoicesQuestList);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        panel_for_copy.add(send_questions_button);

		//implement a button to send the highlighted question
		JButton send_questID_button = new JButton("activer la question avec ID");
		send_questID_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Question question_to_send;
				question_to_send = questionList.get(copyTo.getSelectedIndex());
				try {
					System.out.println("sending question id");
					network_singleton.SendQuestionID(question_to_send.getID());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_for_copy.add(send_questID_button);

		//parentFrame.add(panel_for_copy, BorderLayout.CENTER);

		((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		getContentPane().setPreferredSize(new Dimension(320, 315));
	}

	class FromTransferHandler extends TransferHandler {
		public int getSourceActions(JComponent comp) {
			return COPY_OR_MOVE;
		}

		private int index = 0;

		public Transferable createTransferable(JComponent comp) {
			index = dragFrom.getSelectedIndex();
			if (index < 0 || index >= from_questions.getSize()) {
				return null;
			}

			return new StringSelection((String)dragFrom.getSelectedValue());
		}

		public void exportDone(JComponent comp, Transferable trans, int action) {
			quiz.add(genericQuestionList.get(index));
			if (action != MOVE) {
				return;
			}
			from_questions.removeElementAt(index);
		}
	}

	class ToTransferHandler extends TransferHandler {
		int action;

		public ToTransferHandler(int action) {
			this.action = action;
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			// for the demo, we'll only support drops (not clipboard paste)
			if (!support.isDrop()) {
				return false;
			}

			// we only import Strings
			if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}

			boolean actionSupported = (action & support.getSourceDropActions()) == action;
			if (actionSupported) {
				support.setDropAction(action);
				return true;
			}

			return false;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			// if we can't handle the import, say so
			if (!canImport(support)) {
				return false;
			}

			// fetch the drop location
			JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();

			int index = dl.getIndex();

			// fetch the data and bail if this fails
			String data;
			try {
				data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (java.io.IOException e) {
				return false;
			}

			JList list = (JList)support.getComponent();
			DefaultListModel model = (DefaultListModel)list.getModel();
			model.insertElementAt(data, index);
			own_networkcommunication.getClassroom().addQuestMultChoice(multipleChoicesQuestList.get(index));

			Rectangle rect = list.getCellBounds(index, index);
			list.scrollRectToVisible(rect);
			list.setSelectedIndex(index);
			list.requestFocusInWindow();

			return true;
		}
		public Question getSelectedQuestion() {
			JList copyTo = new JList(copy_question);
			int indexOfQuestion = copyTo.getSelectedIndex();
			Question questionToReturn;
			questionToReturn = questionList.get(indexOfQuestion);
			return questionToReturn;           
		}
	} 
}