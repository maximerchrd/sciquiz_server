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

import com.sciquizapp.sciquizserver.database_management.DbTableQuestionMultipleChoice;
import com.sciquizapp.sciquizserver.database_management.DbTableQuestionShortAnswer;
import com.sciquizapp.sciquizserver.database_management.DbTableTests;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import tools.ListEntry;
import tools.ListEntryCellRenderer;
import tools.Scalr;

import com.sciquizapp.sciquizserver.database_management.DBManager;
import com.sciquizapp.sciquizserver.questions.Question;
import com.sciquizapp.sciquizserver.questions.QuestionGeneric;
import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class QuestionsBrowser extends JFrame {
    static public Vector<String> activeQuestionIDs = new Vector<>();
    private int splitpaneWidth = 500;
    private int splitpaneHeight = 200;
    public int question_index = 0;
    //DefaultListModel<String> from_questions = new DefaultListModel<String>();
    DefaultListModel from_questions = new DefaultListModel();
    DefaultListModel<String> from_IDs = new DefaultListModel<String>();
    DefaultListModel copy_question = new DefaultListModel<String>();
    ArrayList<String> copy_IDs = new ArrayList<>();
    private QuestionMultipleChoice questionMultChoiceSelectedNodeTreeFrom;
    private QuestionShortAnswer questionShortAnswerSelectedNodeTreeFrom;
    private Test testSelectedNodeTreeFrom;
    private DefaultMutableTreeNode selectedNodeTreeFrom;
    private JTree TreeFromQuestions;
    private DefaultMutableTreeNode topTreeNode = new DefaultMutableTreeNode("Questions");
    private List<DefaultMutableTreeNode> TreeNodeQuestions;
    private JList<ListEntry> copyFromList;
    final private JList<ListEntry> copyToList;
    public JPanel panel_for_from;
    public JPanel panel_for_copy;
    public JSplitPane splitPane;
    private List<DefaultMutableTreeNode> testsNodeList = new ArrayList<DefaultMutableTreeNode>();
    private List<Test> testsList = new ArrayList<Test>();
    private List<Question> questionList = new ArrayList<Question>();
    private List<QuestionMultipleChoice> multipleChoicesQuestList = new ArrayList<QuestionMultipleChoice>();
    private List<QuestionShortAnswer> shortAnswerQuestList = new ArrayList<QuestionShortAnswer>();
    private List<QuestionGeneric> genericQuestionList = new ArrayList<QuestionGeneric>();
    private List<QuestionGeneric> quiz = new ArrayList<QuestionGeneric>();
    private NetworkCommunication own_networkcommunication = null;

    public QuestionsBrowser(final JFrame parentFrame, final JPanel panel_questlist, final JPanel panel_disquest, final NetworkCommunication network_singleton) {
        super("QuestionsBrowser");

        String ip_address = "";
        try {
            ip_address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JLabel ipLabel = new JLabel("students should connect to the following address: " + ip_address);
        panel_questlist.add(ipLabel);

        own_networkcommunication = network_singleton;
        DBManager database = new DBManager();
        try {
            questionList = database.getAllQuestions();
            multipleChoicesQuestList = database.getAllMultipleChoiceQuestions();
            shortAnswerQuestList = DbTableQuestionShortAnswer.getAllShortAnswersQuestions();
            testsList = DbTableTests.getAllTests();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < testsList.size(); i++) {
            DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(testsList.get(i));
            topTreeNode.add(newTreeNode);
            testsNodeList.add(newTreeNode);
        }
        for (int i = 0; i < questionList.size(); i++) {
            ImageIcon icon = new ImageIcon(questionList.get(i).getIMAGE());
            Image img = icon.getImage();
            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
            BufferedImage scaledImage = Scalr.resize(bi, 40);
            ImageIcon newIcon = new ImageIcon(scaledImage);
            from_questions.addElement(new ListEntry(questionList.get(i).getQUESTION(), newIcon));
            from_IDs.addElement(String.valueOf(questionList.get(i).getID()));
            QuestionGeneric temp_generic_question = new QuestionGeneric("QUEST", i);
            genericQuestionList.add(temp_generic_question);
        }
        for (int i = 0; i < multipleChoicesQuestList.size(); i++) {
            ImageIcon newIcon = null;
            ImageIcon icon = new ImageIcon(multipleChoicesQuestList.get(i).getIMAGE());
            Image img = icon.getImage();
            if (img.getWidth(null) > 0) {
                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                BufferedImage scaledImage = Scalr.resize(bi, 40);
                newIcon = new ImageIcon(scaledImage);
            }
            from_questions.addElement(new ListEntry(multipleChoicesQuestList.get(i).getQUESTION(), newIcon));

            Boolean questionAdded = false;
            for (int j = 0; !questionAdded && j < testsList.size(); j++) {
                if (testsList.get(j).getIdsQuestions().contains(multipleChoicesQuestList.get(i).getID())) {
                    DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(multipleChoicesQuestList.get(i));
                    testsNodeList.get(j).add(newTreeNode);
                    questionAdded = true;
                }
            }
            if (!questionAdded) {
                DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(multipleChoicesQuestList.get(i));
                topTreeNode.add(newTreeNode);
            }
            from_IDs.addElement(String.valueOf(multipleChoicesQuestList.get(i).getID()));
            QuestionGeneric temp_generic_question = new QuestionGeneric("MULTQ", i);
            genericQuestionList.add(temp_generic_question);
        }
        for (int i = 0; i < shortAnswerQuestList.size(); i++) {
            ImageIcon newIcon = null;
            ImageIcon icon = new ImageIcon(shortAnswerQuestList.get(i).getIMAGE());
            Image img = icon.getImage();
            if (img.getWidth(null) > 0) {
                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                BufferedImage scaledImage = Scalr.resize(bi, 40);
                newIcon = new ImageIcon(scaledImage);
            }
            from_questions.addElement(new ListEntry(shortAnswerQuestList.get(i).getQUESTION(), newIcon));

            Boolean questionAdded = false;
            for (int j = 0; !questionAdded && j < testsList.size(); j++) {
                if (testsList.get(j).getIdsQuestions().contains(shortAnswerQuestList.get(i).getID())) {
                    DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(shortAnswerQuestList.get(i));
                    testsNodeList.get(j).add(newTreeNode);
                    questionAdded = true;
                }
            }
            if (!questionAdded) {
                DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(shortAnswerQuestList.get(i));
                topTreeNode.add(newTreeNode);
            }
            from_IDs.addElement(String.valueOf(shortAnswerQuestList.get(i).getID()));
            QuestionGeneric temp_generic_question = new QuestionGeneric("SHRTA", i);
            genericQuestionList.add(temp_generic_question);
        }

        panel_for_from = new JPanel();
        panel_for_from.setLayout(new BoxLayout(panel_for_from, BoxLayout.Y_AXIS));
        TreeFromQuestions = new JTree(topTreeNode);
        TreeFromQuestions.setToggleClickCount(1);
        TreeFromQuestions.setRootVisible(false);
        TreeFromQuestions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeFromQuestions.setDragEnabled(true);
        TreeFromQuestions.setDropMode(DropMode.ON);
        TreeFromQuestions.setTransferHandler(new TreeTransferHandler());

        TreeFromQuestions.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //Returns the last path element of the selection.
                //This method is useful only when the selection model allows a single selection.
                selectedNodeTreeFrom = (DefaultMutableTreeNode) TreeFromQuestions.getLastSelectedPathComponent();

                if (selectedNodeTreeFrom == null)
                    //Nothing is selected.
                    return;

                Object nodeInfo = selectedNodeTreeFrom.getUserObject();

                if (nodeInfo instanceof QuestionMultipleChoice && selectedNodeTreeFrom.isLeaf()) {
                    questionMultChoiceSelectedNodeTreeFrom = (QuestionMultipleChoice) nodeInfo;
                    questionShortAnswerSelectedNodeTreeFrom = null;
                    testSelectedNodeTreeFrom = null;
                } else if (nodeInfo instanceof QuestionShortAnswer && selectedNodeTreeFrom.isLeaf()) {
                    questionShortAnswerSelectedNodeTreeFrom = (QuestionShortAnswer) nodeInfo;
                    questionMultChoiceSelectedNodeTreeFrom = null;
                    testSelectedNodeTreeFrom = null;
                } else {
                    testSelectedNodeTreeFrom = (Test) nodeInfo;
                    questionMultChoiceSelectedNodeTreeFrom = null;
                    questionShortAnswerSelectedNodeTreeFrom = null;
                }
            }
        });
        TreeFromQuestions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (questionMultChoiceSelectedNodeTreeFrom != null) {
                        JList list = copyToList;
                        activateQuestionMultipleChoice(list, questionMultChoiceSelectedNodeTreeFrom);
                    } else if (questionShortAnswerSelectedNodeTreeFrom != null) {
                        JList list = copyToList;
                        activateQuestionShortAnswer(list, questionShortAnswerSelectedNodeTreeFrom);
                    }
                }
            }
        });
        TreeFromQuestions.setCellRenderer(new DefaultTreeCellRenderer() {
            private JLabel label;
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean isLeaf, int row, boolean focused) {
                label=(JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, hasFocus);
                Object o = ((DefaultMutableTreeNode) value).getUserObject();
                if (o instanceof QuestionMultipleChoice) {
                    QuestionMultipleChoice question = (QuestionMultipleChoice) o;
                    ImageIcon newIcon = null;
                    ImageIcon icon = new ImageIcon(question.getIMAGE());
                    Image img = icon.getImage();
                    if (img.getWidth(null) > 0) {
                        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = bi.createGraphics();
                        g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                        BufferedImage scaledImage = Scalr.resize(bi, 40);
                        newIcon = new ImageIcon(scaledImage);
                        label.setIcon(newIcon);
                    } else {
                        label.setIcon(null);
                    }
                    label.setText(question.getQUESTION());
                } else if (o instanceof QuestionShortAnswer) {
                    QuestionShortAnswer question = (QuestionShortAnswer) o;
                    ImageIcon newIcon = null;
                    ImageIcon icon = new ImageIcon(question.getIMAGE());
                    Image img = icon.getImage();
                    if (img.getWidth(null) > 0) {
                        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = bi.createGraphics();
                        g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                        BufferedImage scaledImage = Scalr.resize(bi, 40);
                        newIcon = new ImageIcon(scaledImage);
                        label.setIcon(newIcon);
                    } else {
                        label.setIcon(null);
                    }
                    label.setText(question.getQUESTION());
                } else if (o instanceof Test) {
                    label.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
                    label.setText("" + ((Test)((DefaultMutableTreeNode) value).getUserObject()).getTestName());
                } else {
                    System.out.println("problem rendering tree cell: object neither question multchoice, question short answer nor test");
                }
                return label;
            }
        });
        JScrollPane sp = new JScrollPane(TreeFromQuestions);
        sp.setAlignmentX(0f);
        panel_for_from.add(sp);

        //implement a button to add a new question to the database
        JButton new_quest_button = new JButton("create a question");
        new_quest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddNewQuestion new_quest = new AddNewQuestion(genericQuestionList, questionList, multipleChoicesQuestList, shortAnswerQuestList, from_questions, from_IDs, TreeFromQuestions);
            }
        });
        panel_for_from.add(new_quest_button);

        parentFrame.add(panel_for_from, BorderLayout.WEST);


        copyToList = new JList<ListEntry>(copy_question);
        copyToList.setTransferHandler(new ToTransferHandler(TransferHandler.COPY));
        copyToList.setDropMode(DropMode.INSERT);
        copyToList.setCellRenderer(new ListEntryCellRenderer());

        panel_for_copy = new JPanel();
        panel_for_copy.setLayout(new BoxLayout(panel_for_copy, BoxLayout.Y_AXIS));
        JScrollPane sp2 = new JScrollPane(copyToList);
        sp2.setAlignmentX(0f);
        panel_for_copy.add(sp2);
        panel_for_copy.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        panel_questlist.setLayout(new FlowLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, sp2);
        panel_questlist.add(splitPane);
        System.out.println("width: " + splitpaneWidth + "; height: " + splitpaneHeight);
        splitPane.setPreferredSize(new Dimension(splitpaneWidth,splitpaneHeight));
        splitPane.setDividerLocation(splitpaneWidth / 2);
        panel_questlist.add(panel_for_from);
        panel_questlist.add(panel_for_copy);

        //implement a button to remove a question from the panel for from
        JButton delete_question_from_button = new JButton("remove the selected question or test");
        delete_question_from_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedNodeTreeFrom != null) {
                    try {
                        if (selectedNodeTreeFrom.getUserObject() instanceof QuestionMultipleChoice) {
                            DbTableQuestionMultipleChoice.removeMultipleChoiceQuestionWithID(String.valueOf(questionMultChoiceSelectedNodeTreeFrom.getID()));
                        } else if (selectedNodeTreeFrom.getUserObject() instanceof Test) {
                            DbTableTests.removeTestWithID(String.valueOf(testSelectedNodeTreeFrom.getIdTest()));
                        } else {
                            System.out.println("problem deleting treenode; object neither question nor test");
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    DefaultTreeModel model = (DefaultTreeModel) TreeFromQuestions.getModel();
                    model.removeNodeFromParent(selectedNodeTreeFrom);
                    multipleChoicesQuestList.remove(questionMultChoiceSelectedNodeTreeFrom);
                }
            }
        });
        panel_for_from.add(delete_question_from_button);

        //implement a button to create a test/quiz in the panel for from
        JButton create_test_button = new JButton("create a test/quiz");
        create_test_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DbTableTests.addTest("new quiz/test");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Test newTest = DbTableTests.getLastTests();
                DefaultTreeModel model = (DefaultTreeModel) TreeFromQuestions.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                model.insertNodeInto(new DefaultMutableTreeNode(newTest), root, root.getChildCount());
            }
        });
        panel_for_from.add(create_test_button);

        //implement a button to activate a question or a test
        JButton activate_button = new JButton("activate the question or test/quiz");
        activate_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (testSelectedNodeTreeFrom != null) {
                    for (int i = 0; i < testSelectedNodeTreeFrom.getIdsQuestions().size(); i++) {
                        Boolean found = false;
                        int j = 0;
                        int questiontype = -1;
                        for (; !found && j < multipleChoicesQuestList.size(); j++) {
                            questiontype = -1;
                            if (multipleChoicesQuestList.get(j).getID() == testSelectedNodeTreeFrom.getIdsQuestions().get(i)) {
                                found = true;
                                questiontype = 0;
                            } else if (shortAnswerQuestList.get(j).getID() == testSelectedNodeTreeFrom.getIdsQuestions().get(i)) {
                                found = true;
                                questiontype = 1;
                            }
                        }
                        if (questiontype == 0) {
                            QuestionMultipleChoice questionToActivate = multipleChoicesQuestList.get(j - 1);
                            activateQuestionMultipleChoice(copyToList, questionToActivate);
                        } else if (questiontype == 1) {
                            QuestionShortAnswer questionToActivate = shortAnswerQuestList.get(j - 1);
                            activateQuestionShortAnswer(copyToList, questionToActivate);
                        }
                    }
                } else if (questionMultChoiceSelectedNodeTreeFrom != null){
                    activateQuestionMultipleChoice(copyToList, questionMultChoiceSelectedNodeTreeFrom);
                } else if (questionShortAnswerSelectedNodeTreeFrom != null) {
                    activateQuestionShortAnswer(copyToList, questionShortAnswerSelectedNodeTreeFrom);
                }
            }
        });
        panel_for_from.add(activate_button);

        //implement a button to send the questions from the panel for copy
        JButton send_questions_button = new JButton("Force sync questions with devices");
        send_questions_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    network_singleton.SendQuestionList(questionList, multipleChoicesQuestList, copy_IDs);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        panel_for_copy.add(send_questions_button);

        //implement a button to send the highlighted question
        JButton send_questID_button = new JButton("activate the question for students");
        send_questID_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id_to_send = Integer.valueOf(copy_IDs.get(copyToList.getSelectedIndex()));
                try {
                    System.out.println("sending question id");
                    network_singleton.SendQuestionID(id_to_send);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        panel_for_copy.add(send_questID_button);

        //implement a button to remove a question from the panel for copy
        JButton delete_question_button = new JButton("remove the selected question");
        delete_question_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = copyToList.getSelectedIndex();
                network_singleton.removeQuestion(index);
                copy_question.remove(index);
                copy_IDs.remove(index);
            }
        });
        panel_for_copy.add(delete_question_button);

        //((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        //getContentPane().setPreferredSize(new Dimension(320, 315));
    }

    class ToTransferHandler extends TransferHandler {
        int action;

        public ToTransferHandler(int action) {
            this.action = action;
        }

        public boolean canImport(TransferHandler.TransferSupport support) {         //useless code???
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

        public boolean importData(TransferHandler.TransferSupport support) {            //useless code???
            // if we can't handle the import, say so
            if (!canImport(support)) {
                return false;
            }

            // fetch the drop location
            //JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

            //int index = dl.getIndex();

            // fetch the data and bail if this fails
            String data;
            try {
                data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (java.io.IOException e) {
                return false;
            }

            JList list = (JList) support.getComponent();
            activateQuestionMultipleChoice(list, multipleChoicesQuestList.get(from_IDs.indexOf(copy_IDs.get(copy_IDs.size() - 1))));

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

    private void activateQuestionMultipleChoice(JList list, QuestionMultipleChoice questionMultipleChoice) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        int index = model.size();

        //resize image from db to icon size
        ImageIcon icon = new ImageIcon(questionMultipleChoice.getIMAGE());
        Image img = icon.getImage();
        ImageIcon newIcon = null;
        if (img.getWidth(null) > 0) {
            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
            BufferedImage scaledImage = Scalr.resize(bi, 40);
            newIcon = new ImageIcon(scaledImage);
        }
        ListEntry newListEntry = new ListEntry(questionMultipleChoice.getQUESTION(), newIcon);
        model.insertElementAt(newListEntry, index);
        own_networkcommunication.getClassroom().addQuestMultChoice(multipleChoicesQuestList.get(index));
        copy_IDs.add(String.valueOf(questionMultipleChoice.getID()));

        Rectangle rect = list.getCellBounds(index, index);
        list.scrollRectToVisible(rect);
        list.setSelectedIndex(index);
        list.requestFocusInWindow();

        try {
            own_networkcommunication.sendMultipleChoiceWithID(questionMultipleChoice.getID(), null);
            own_networkcommunication.addQuestion(questionMultipleChoice.getQUESTION());
        } catch (IOException e) {
            e.printStackTrace();
        }

        activeQuestionIDs.add(String.valueOf(questionMultipleChoice.getID()));
    }

    private void activateQuestionShortAnswer(JList list, QuestionShortAnswer questionShortAnswer) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        int index = model.size();

        //resize image from db to icon size
        ImageIcon icon = new ImageIcon(questionShortAnswer.getIMAGE());
        Image img = icon.getImage();
        ImageIcon newIcon = null;
        if (img.getWidth(null) > 0) {
            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
            BufferedImage scaledImage = Scalr.resize(bi, 40);
            newIcon = new ImageIcon(scaledImage);
        }
        ListEntry newListEntry = new ListEntry(questionShortAnswer.getQUESTION(), newIcon);
        model.insertElementAt(newListEntry, index);
        own_networkcommunication.getClassroom().addQuestShortAnswer(shortAnswerQuestList.get(index));
        copy_IDs.add(String.valueOf(questionShortAnswer.getID()));

        Rectangle rect = list.getCellBounds(index, index);
        list.scrollRectToVisible(rect);
        list.setSelectedIndex(index);
        list.requestFocusInWindow();

        try {
            own_networkcommunication.sendShortAnswerQuestionWithID(questionShortAnswer.getID(), null);
            own_networkcommunication.addQuestion(questionShortAnswer.getQUESTION());
        } catch (IOException e) {
            e.printStackTrace();
        }

        activeQuestionIDs.add(String.valueOf(questionShortAnswer.getID()));
    }
}