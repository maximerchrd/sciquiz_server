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
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MyServer extends Application{


    public static void main(String[] args) throws Exception {


        //does db stuffs
        DBManager dao = new DBManager();
        dao.createDBIfNotExists();
        dao.createTablesIfNotExists();
        dao.getAllQuestions();

        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Learning Tracker");

        Scene scene = new Scene(new StackPane());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("LearningTracker.fxml"));
        scene.setRoot(loader.load());
        ClassroomActivityTabController controller = loader.getController();
        controller.init();


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

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

}