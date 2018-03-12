package com.sciquizapp.sciquizserver.controllers;

import com.sciquizapp.sciquizserver.*;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import javax.swing.text.TableView;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by maximerichard on 19.02.18.
 */
public class ClassroomActivityTabController implements Initializable {

    static int screenWidth = 0;
    static int screenHeight = 0;

    @FXML private AnchorPane studentsQuestionsTable;
    @FXML private StudentsVsQuestionsTableController studentsQuestionsTableController;
    @FXML private Tab classroom_activity_tab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screenSize.getWidth();
        screenHeight = (int)screenSize.getHeight();

        //setup swing embedding
        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);
        swingNode.setTranslateX(-320);

        classroom_activity_tab.setContent(swingNode);

    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //declares jpanels for different parts of the window
                JPanel panel_for_questlist = new JPanel(); // useless now, do something later?
                JPanel panel_for_counter = new JPanel();
                JPanel panel_for_displayquest = new JPanel();
                JPanel panel_for_stats = new JPanel();



                //Setup the table

                //Create and set up the window.
                JFrame frame = new JFrame("Learning Tracker");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Create and set up the content pane.
                Table TableUserVsQuest = new Table();
                TableUserVsQuest.setOpaque(true); //content panes must be opaque


                //start bluetooth network in new thread
                NetworkCommunication CommunicationWithClients = new NetworkCommunication(TableUserVsQuest);
                try {
                    CommunicationWithClients.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                //Turn off metal's use of bold fonts
                QuestionsBrowser newChooseDropAction = new QuestionsBrowser(frame, panel_for_questlist, panel_for_displayquest, CommunicationWithClients);
                UIManager.put("swing.boldMetal", Boolean.FALSE);




                //implement the division of the window with Gridbaglayout
                JPanel parent = new JPanel(new GridBagLayout());
                panel_for_questlist.setPreferredSize(new Dimension((int)(screenWidth*0.4),(int)(screenHeight*0.8)));
                GridBagConstraints questionsBrowserConstraints = new GridBagConstraints();
                questionsBrowserConstraints.gridheight = 2;
                questionsBrowserConstraints.gridx = 0;
                questionsBrowserConstraints.gridy = 0;
                parent.add(panel_for_questlist,questionsBrowserConstraints);

                DisplayTableUsersVsQuestions displayTablePanel = new DisplayTableUsersVsQuestions(new GridBagLayout(),
                        false, screenWidth, screenHeight, TableUserVsQuest, CommunicationWithClients);
                GridBagConstraints tablePanelConstraints = new GridBagConstraints();
                tablePanelConstraints.gridx = 1;
                tablePanelConstraints.gridy = 0;
                parent.add(displayTablePanel, tablePanelConstraints);

                swingNode.setContent(parent);
            }
        });
    }

    public void addQuestion(String question, Integer id) {
        studentsQuestionsTableController.addQuestion(question,id);
    }
}
