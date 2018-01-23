/*
 * TO DO LIST
 * - afficher une liste des appareils connectés
 * - afficher un avertissement quand un appareil quitte l'app
 * - pouvoir répondre à plusieurs questions de manière non sychronisée (entre les utilisateurs)
 * - écrire un fichier pour excel avec utilisateurs vs questions (et réponses dans les cases)
 * ->1 dans l'interface, faire un tableau pour la session ac utilisateurs vs questions
 * - interface pour choisir les questions: liste
 * - pouvoir choisir à l'avance plusieurs question et faire ensuite "question suivante"
 * - donner le résultat sur chaque appareil utilisateur à la fin de la session
 * - définir une "séquence de questions" (= session)
 */

package com.sciquizapp.sciquizserver;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import com.sciquizapp.sciquizserver.database_management.DBManager;

public class MyServer {



    public static void main(String[] args) throws Exception {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();

        //does db stuffs
        DBManager dao = new DBManager();
        dao.createDBIfNotExists();
        dao.createTablesIfNotExists();
        dao.getAllQuestions();

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
        //frame.setContentPane(TableUserVsQuest);


        //start bluetooth network in new thread
        NetworkCommunication CommunicationWithClients = new NetworkCommunication(TableUserVsQuest);
//        Thread networkThread = new Thread() {
//            public void run() {
        try {
            CommunicationWithClients.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        };
//        networkThread.start();



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

        //Display the window.
        frame.setContentPane(parent);
        frame.pack();
        frame.setBounds(0, 0, screenWidth, screenHeight);
        frame.setVisible(true);

    }
}