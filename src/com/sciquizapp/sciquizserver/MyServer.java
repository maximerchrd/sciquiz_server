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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MyServer extends Application{

    static int screenWidth = 0;
    static int screenHeight = 0;

    public static void main(String[] args) throws Exception {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screenSize.getWidth();
        screenHeight = (int)screenSize.getHeight();

        //does db stuffs
        DBManager dao = new DBManager();
        dao.createDBIfNotExists();
        dao.createTablesIfNotExists();
        dao.getAllQuestions();

        //Display the window.
        /*frame.setContentPane(parent);
        frame.pack();
        frame.setBounds(0, 0, screenWidth, screenHeight);
        frame.setVisible(true);*/

        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Learning Tracker");
        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.WHITE);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        //setup swing embedding
        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);
        swingNode.setTranslateX(-320);

        //setup tabs
        TabPane tabPane = new TabPane();

        BorderPane borderPane = new BorderPane();
        Tab tab = new Tab();
        tab.setText("Classroom activity");
        tab.setContent(swingNode);
        tabPane.getTabs().add(tab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
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