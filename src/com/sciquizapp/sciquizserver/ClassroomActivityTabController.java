package com.sciquizapp.sciquizserver;

import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by maximerichard on 19.02.18.
 */
public class ClassroomActivityTabController {

    static int screenWidth = 0;
    static int screenHeight = 0;
    @FXML
    private Tab classroom_activity_tab;

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
                        false, screenWidth, screenHeight, TableUserVsQuest);
                GridBagConstraints tablePanelConstraints = new GridBagConstraints();
                tablePanelConstraints.gridx = 1;
                tablePanelConstraints.gridy = 0;
                parent.add(displayTablePanel, tablePanelConstraints);

                DisplayStats displayStats = new DisplayStats();
                GridBagConstraints displayStatsConstraints = new GridBagConstraints();
                displayStatsConstraints.gridx = 1;
                displayStatsConstraints.gridy = 1;
                displayStats.setPreferredSize(new Dimension((int)(screenWidth*0.5),(int)(screenHeight*0.4)));
                parent.add(displayStats,displayStatsConstraints);

                swingNode.setContent(parent);
            }
        });
    }
}
