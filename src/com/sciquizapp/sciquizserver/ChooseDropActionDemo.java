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
import com.sciquizapp.sciquizserver.database_management.DbTableTests;
import tools.ListEntry;
import tools.ListEntryCellRenderer;
import tools.Scalr;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

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
import java.util.ArrayList;
import java.util.List;

public class ChooseDropActionDemo extends JFrame {
    private int splitpaneWidth = 500;
    private int splitpaneHeight = 200;
    public int question_index = 0;
    //DefaultListModel<String> from_questions = new DefaultListModel<String>();
    DefaultListModel from_questions = new DefaultListModel();
    DefaultListModel<String> from_IDs = new DefaultListModel<String>();
    DefaultListModel copy_question = new DefaultListModel<String>();
    ArrayList<String> copy_IDs = new ArrayList<>();
    private QuestionMultipleChoice questionSelectedNodeTreeFrom;
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

        panel_for_from = new JPanel();
        panel_for_from.setLayout(new BoxLayout(panel_for_from, BoxLayout.Y_AXIS));
        copyFromList = new JList(from_questions);
        copyFromList.setCellRenderer(new ListEntryCellRenderer());
        copyFromList.setTransferHandler(new FromTransferHandler());
        copyFromList.setDragEnabled(true);
        copyFromList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel label = new JLabel("Drag from here:");
        label.setAlignmentX(0f);
        panel_for_from.add(label);
        //JScrollPane sp = new JScrollPane(copyFromList);
        TreeFromQuestions = new JTree(topTreeNode);
        TreeFromQuestions.setToggleClickCount(1);
        TreeFromQuestions.setRootVisible(false);
        TreeFromQuestions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeFromQuestions.setDragEnabled(true);
        TreeFromQuestions.setDropMode(DropMode.ON_OR_INSERT);
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
                    questionSelectedNodeTreeFrom = (QuestionMultipleChoice) nodeInfo;
                } else {
                    testSelectedNodeTreeFrom = (Test) nodeInfo;
                }
            }
        });
        TreeFromQuestions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (questionSelectedNodeTreeFrom != null) {
                        JList list = copyToList;
                        DefaultListModel model = (DefaultListModel) list.getModel();
                        int index = model.size();

                        //resize image from db to icon size
                        ImageIcon icon = new ImageIcon(questionSelectedNodeTreeFrom.getIMAGE());
                        Image img = icon.getImage();
                        ImageIcon newIcon = null;
                        if (img.getWidth(null) > 0) {
                            BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = bi.createGraphics();
                            g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                            BufferedImage scaledImage = Scalr.resize(bi, 40);
                            newIcon = new ImageIcon(scaledImage);
                        }
                        ListEntry newListEntry = new ListEntry(questionSelectedNodeTreeFrom.getQUESTION(), newIcon);
                        model.insertElementAt(newListEntry, index);
                        own_networkcommunication.getClassroom().addQuestMultChoice(questionSelectedNodeTreeFrom);

                        Rectangle rect = list.getCellBounds(index, index);
                        list.scrollRectToVisible(rect);
                        list.setSelectedIndex(index);
                        list.requestFocusInWindow();
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
                } else if (o instanceof Test) {
                    label.setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
                    label.setText("" + ((Test)((DefaultMutableTreeNode) value).getUserObject()).getTestName());
                } else {
                    System.out.println("problem rendering tree cell: object neither question nor test");
                }
                return label;
            }
        });
        JScrollPane sp = new JScrollPane(TreeFromQuestions);
        sp.setAlignmentX(0f);
        panel_for_from.add(sp);

        //implement a button to add a new question to the database
        JButton new_quest_button = new JButton("ajouter une question");
        new_quest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddNewQuestion new_quest = new AddNewQuestion(genericQuestionList, questionList, multipleChoicesQuestList, from_questions, from_IDs, TreeFromQuestions);
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
        label = new JLabel("Drop to COPY to here:");
        label.setAlignmentX(0f);
        panel_for_copy.add(label);
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

        //implement a button to send the highlighted question
        JButton send_quest_button = new JButton("activer la question");
        send_quest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Question question_to_send;
                System.out.println("copyToList.getSelectedIndex() =  " + copyToList.getSelectedIndex());
                question_to_send = questionList.get(copyToList.getSelectedIndex());
                try {
                    network_singleton.SendQuestion(question_to_send, false);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        panel_for_from.add(send_quest_button);

        //implement a button to remove a question from the panel for from
        JButton delete_question_from_button = new JButton("remove the selected question or test");
        delete_question_from_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedNodeTreeFrom != null) {
                    try {
                        if (selectedNodeTreeFrom.getUserObject() instanceof QuestionMultipleChoice) {
                            DbTableQuestionMultipleChoice.removeMultipleChoiceQuestionWithID(String.valueOf(questionSelectedNodeTreeFrom.getID()));
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
                    multipleChoicesQuestList.remove(questionSelectedNodeTreeFrom);
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
        panel_for_from.add(activate_button);

        //implement a button to send the questions from the panel for copy
        JButton send_questions_button = new JButton("envoyer les questions");
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
        JButton send_questID_button = new JButton("activer la question avec ID");
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
                copy_question.remove(index);
                copy_IDs.remove(index);
            }
        });
        panel_for_copy.add(delete_question_button);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        getContentPane().setPreferredSize(new Dimension(320, 315));
    }

    class FromTransferHandler extends TransferHandler {
        public int getSourceActions(JComponent comp) {
            return COPY_OR_MOVE;
        }

        private int index = 0;

        public Transferable createTransferable(JComponent comp) {
            index = copyFromList.getSelectedIndex();
            copy_IDs.add(from_IDs.get(index));
            if (index < 0 || index >= from_questions.getSize()) {
                return null;
            }
            ListEntry tempEntry = copyFromList.getSelectedValue();
            return new StringSelection(tempEntry.getValue());
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
            DefaultListModel model = (DefaultListModel) list.getModel();
            int index = model.size();

            //resize image from db to icon size
            ImageIcon icon = new ImageIcon(multipleChoicesQuestList.get(from_IDs.indexOf(copy_IDs.get(copy_IDs.size() - 1))).getIMAGE());
            Image img = icon.getImage();
            ImageIcon newIcon = null;
            if (img.getWidth(null) > 0) {
                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                BufferedImage scaledImage = Scalr.resize(bi, 40);
                newIcon = new ImageIcon(scaledImage);
            }
            ListEntry newListEntry = new ListEntry(multipleChoicesQuestList.get(from_IDs.indexOf(copy_IDs.get(copy_IDs.size() - 1))).getQUESTION(), newIcon);
            model.insertElementAt(newListEntry, index);
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