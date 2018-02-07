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

import com.sciquizapp.sciquizserver.database_management.*;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;
import tools.ListEntry;
import tools.ListEntryCellRenderer;
import tools.Scalr;

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
    private int splitpaneWidth = 500;
    private int splitpaneHeight = 200;
    public JSplitPane splitPane;
    private NetworkCommunication own_networkcommunication = null;

    //members for left questions list (JTree)
    DefaultListModel leftQuestionListModel = new DefaultListModel();
    private QuestionMultipleChoice questionMultChoiceSelectedNodeTreeFrom;
    private QuestionShortAnswer questionShortAnswerSelectedNodeTreeFrom;
    private Test testSelectedNodeTreeFrom;
    private DefaultMutableTreeNode selectedNodeTreeFrom;
    private JTree TreeFromQuestions;
    private DefaultMutableTreeNode topTreeNode = new DefaultMutableTreeNode("Questions");
    public JPanel panel_for_from;
    private List<DefaultMutableTreeNode> testsNodeList = new ArrayList<DefaultMutableTreeNode>();
    private List<Test> testsList = new ArrayList<Test>();
    private List<QuestionGeneric> leftGenericQuestionList = new ArrayList<QuestionGeneric>();

    //members for right questions list
    static public Vector<String> IDsFromBroadcastedQuestions = new Vector<>();
    DefaultListModel rightQuestionsListModel = new DefaultListModel<String>();
    final private JList<ListEntry> rightJlist;
    public JPanel panel_for_copy;

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
        try {
            leftGenericQuestionList = DbTableQuestionGeneric.getAllGenericQuestions();
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
        for (int i = 0; i < leftGenericQuestionList.size(); i++) {
            QuestionMultipleChoice questionMultipleChoice = null;
            QuestionShortAnswer questionShortAnswer = null;
            if (leftGenericQuestionList.get(i).getIntTypeOfQuestion() == 0) {
                try {
                    questionMultipleChoice = DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(leftGenericQuestionList.get(i).getGlobalID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (leftGenericQuestionList.get(i).getIntTypeOfQuestion() == 1) {
                try {
                    questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(leftGenericQuestionList.get(i).getGlobalID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Problem reading generic question list: question type not supported");
            }

            String imagePath = questionMultipleChoice == null ? questionShortAnswer.getIMAGE() : questionMultipleChoice.getIMAGE();
            String questionText = questionMultipleChoice == null ? questionShortAnswer.getQUESTION() : questionMultipleChoice.getQUESTION();
            Integer globalID = questionMultipleChoice == null ? questionShortAnswer.getID() : questionMultipleChoice.getID();

            ImageIcon newIcon = null;
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();
            if (img.getWidth(null) > 0) {
                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                BufferedImage scaledImage = Scalr.resize(bi, 40);
                newIcon = new ImageIcon(scaledImage);
            }
            leftQuestionListModel.addElement(new ListEntry(questionText, newIcon));

            Boolean questionAdded = false;
            for (int j = 0; !questionAdded && j < testsList.size(); j++) {
                if (testsList.get(j).getIdsQuestions().contains(globalID)) {
                    DefaultMutableTreeNode newTreeNode = questionMultipleChoice == null ?
                            new DefaultMutableTreeNode(questionShortAnswer) : new DefaultMutableTreeNode(questionMultipleChoice);
                    testsNodeList.get(j).add(newTreeNode);
                    questionAdded = true;
                }
            }
            if (!questionAdded) {
                DefaultMutableTreeNode newTreeNode = questionMultipleChoice == null ?
                        new DefaultMutableTreeNode(questionShortAnswer) : new DefaultMutableTreeNode(questionMultipleChoice);
                topTreeNode.add(newTreeNode);
            }
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
                        JList list = rightJlist;
                        broadcastQuestionMultipleChoice(list, questionMultChoiceSelectedNodeTreeFrom);
                    } else if (questionShortAnswerSelectedNodeTreeFrom != null) {
                        JList list = rightJlist;
                        broadcastQuestionShortAnswer(list, questionShortAnswerSelectedNodeTreeFrom);
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
                AddNewQuestion new_quest = new AddNewQuestion(leftGenericQuestionList, TreeFromQuestions);
            }
        });
        panel_for_from.add(new_quest_button);

        //implement a button to edit a question
        JButton edit_quest_button = new JButton("edit the selected question");
        edit_quest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedNodeTreeFrom != null) {
                    try {
                        if (selectedNodeTreeFrom.getUserObject() instanceof QuestionMultipleChoice) {
                            EditQuestion editQuestion = new EditQuestion(questionMultChoiceSelectedNodeTreeFrom.getID(), 0, TreeFromQuestions);
                        } else if (selectedNodeTreeFrom.getUserObject() instanceof QuestionShortAnswer) {
                            EditQuestion editQuestion = new EditQuestion(questionShortAnswerSelectedNodeTreeFrom.getID(), 1, TreeFromQuestions);
                        } else {
                            System.out.println("problem editing question; object not a question");
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        panel_for_from.add(edit_quest_button);

        parentFrame.add(panel_for_from, BorderLayout.WEST);


        rightJlist = new JList<ListEntry>(rightQuestionsListModel);
        //rightJlist.setTransferHandler(new ToTransferHandler(TransferHandler.COPY));
        rightJlist.setDropMode(DropMode.INSERT);
        rightJlist.setCellRenderer(new ListEntryCellRenderer());

        panel_for_copy = new JPanel();
        panel_for_copy.setLayout(new BoxLayout(panel_for_copy, BoxLayout.Y_AXIS));
        JScrollPane sp2 = new JScrollPane(rightJlist);
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
                    //leftGenericQuestionList.remove(selectedNodeTreeFrom);
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
                Test newTest = new Test();
                newTest = DbTableTests.getLastTests();
                DefaultTreeModel model = (DefaultTreeModel) TreeFromQuestions.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                model.insertNodeInto(new DefaultMutableTreeNode(newTest), root, root.getChildCount());
            }
        });
        panel_for_from.add(create_test_button);

        //implement a button to activate a question or a test
        JButton activate_button = new JButton("broadcast the question or test/quiz");
        activate_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (testSelectedNodeTreeFrom != null) {
                    for (int i = 0; i < testSelectedNodeTreeFrom.getGenericQuestions().size(); i++) {
                        if (testSelectedNodeTreeFrom.getGenericQuestions().get(i).getIntTypeOfQuestion() == 0) {
                            try {
                                broadcastQuestionMultipleChoice(rightJlist, DbTableQuestionMultipleChoice.getMultipleChoiceQuestionWithID(testSelectedNodeTreeFrom.getGenericQuestions().get(i).getGlobalID()));
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else if (testSelectedNodeTreeFrom.getGenericQuestions().get(i).getIntTypeOfQuestion() == 1) {
                            broadcastQuestionShortAnswer(rightJlist, DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(testSelectedNodeTreeFrom.getGenericQuestions().get(i).getGlobalID()));
                        }
                    }
                } else if (questionMultChoiceSelectedNodeTreeFrom != null){
                    broadcastQuestionMultipleChoice(rightJlist, questionMultChoiceSelectedNodeTreeFrom);
                } else if (questionShortAnswerSelectedNodeTreeFrom != null) {
                    broadcastQuestionShortAnswer(rightJlist, questionShortAnswerSelectedNodeTreeFrom);
                }
            }
        });
        panel_for_from.add(activate_button);

        //implement a button to send the questions from the panel for copy
        JButton send_questions_button = new JButton("Force sync questions with devices");
        send_questions_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*try {
                    network_singleton.SendQuestionList(questionList, multipleChoicesQuestList, copy_IDs);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }*/
            }
        });
        panel_for_copy.add(send_questions_button);

        //implement a button to send the highlighted question
        JButton send_questID_button = new JButton("activate the question for students");
        send_questID_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int id_to_send = Integer.valueOf(IDsFromBroadcastedQuestions.get(rightJlist.getSelectedIndex()));
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

        //implement a button to remove a question from the RIGHT panel
        JButton delete_question_button = new JButton("remove the selected question");
        delete_question_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = rightJlist.getSelectedIndex();
                network_singleton.removeQuestion(index);
                rightQuestionsListModel.remove(index);
                IDsFromBroadcastedQuestions.remove(index);
            }
        });
        panel_for_copy.add(delete_question_button);
    }

    private void broadcastQuestionMultipleChoice(JList list, QuestionMultipleChoice questionMultipleChoice) {
        if (!IDsFromBroadcastedQuestions.contains(String.valueOf(questionMultipleChoice.getID()))) {
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
            own_networkcommunication.getClassroom().addQuestMultChoice(questionMultipleChoice);
            IDsFromBroadcastedQuestions.add(String.valueOf(questionMultipleChoice.getID()));

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
        } else {
            JOptionPane.showMessageDialog(null, "Unfortunately, you cannot use a question twice in the same set", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void broadcastQuestionShortAnswer(JList list, QuestionShortAnswer questionShortAnswer) {
        if (!IDsFromBroadcastedQuestions.contains(String.valueOf(questionShortAnswer.getID()))) {
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
            own_networkcommunication.getClassroom().addQuestShortAnswer(questionShortAnswer);
            IDsFromBroadcastedQuestions.add(String.valueOf(questionShortAnswer.getID()));

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
        } else {
            JOptionPane.showMessageDialog(null, "Unfortunately, you cannot use a question twice in the same set", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}