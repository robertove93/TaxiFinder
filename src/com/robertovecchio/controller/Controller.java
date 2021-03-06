package com.robertovecchio.controller;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**Controller della view principale
 * @author robertovecchio
 * @version 1.0
 * @since 08/01/2021
 * */
public class Controller {
    //==================================================
    //               Variabili FXML
    //==================================================

    /**
     * Bottone che gestisce il caso in cui si voglia entrare in area aziendale
     * @see Button
     */
    @FXML
    Button companyButton;
    /**
     * Bottone che gestisce il caso in cui si voglia entrare in area utente
     * @see Button
     */
    @FXML
    Button userButton;
    /**
     * GridPane associato alla view
     * @see GridPane
     */
    @FXML
    GridPane gridRoot;

    //==================================================
    //               Variabili Statiche
    //==================================================

    /**
     * Variabile statica che rappresenta il font principale
     * */
    private final static String fontFamily = "Helvetica";
    /**
     * Variabile statica che rappresenta la grandezza del font
     * */
    private final static double fontSize = 50D;
    /**
     * Variabile statica che rappresenta uno dei colori utilizzati nella view
     * */
    private final static Color darkGrayFontColor = Color.web("#494949");
    /**
     * Variabile statica che rappresenta uno dei colori utilizzati nella view
     * */
    private final static Color darkYellowFontColor = Color.web("#F3D833");
    /**
     * Variabile statica che rappresenta il percorso al logo
     * */
    private final static String logoFilePath = "Assets/logo.png";
    /**
     * Variabile statica che rappresenta il percorso alla view dell'area aziendale
     * */
    private final static String companyAreaFile = "src/com/robertovecchio/view/fxml/companyArea.fxml";
    /**
     * Variabile statica che rappresenta il percorso alla view dell'area utente
     * */
    private final static String userFile = "src/com/robertovecchio/view/fxml/customerLogin.fxml";
    /**
     * Variabile statica che rappresenta il percorso al foglio di stile del cliente
     * */
    private final static String customerLoginStyle = "com/robertovecchio/view/fxml/style/customerLogin.css";

    //==================================================
    //               Inizializzazione
    //==================================================

    /**
     * Questo metodo inizializza la view a cui è collegato il controller corrente
     * */
    @FXML
    public void initialize(){
        /* Inizializziamo una nuova imageview */
        ImageView logoImage = new ImageView();

        try {
            /* Inizializziamo il logo */
            Image logo = new Image(new FileInputStream(logoFilePath));

            logoImage.setImage(logo); // impostiamo l'immagine all'imageview
            logoImage.setFitHeight(100); // impostiamo altezza
            logoImage.setPreserveRatio(true); // impostiamo la conservazione delle proporzioni originali
        }catch (FileNotFoundException e){
            /* Catturiamo l'errore */
            System.out.println("File Non trovato");
        }

        /* inizializziamo un textFlow */
        TextFlow textFlow = new TextFlow();

        /* Creiamo la prima parte dell'intestazione */
        Text headerTextFirstPart = new Text("T");
        headerTextFirstPart.setFont(Font.font(fontFamily, FontWeight.BOLD, fontSize));
        headerTextFirstPart.setFill(darkYellowFontColor);

        /* Creiamo la seconda parte dell'intestazione */
        Text headerTextSecondPart = new Text("axi");
        headerTextSecondPart.setFont(Font.font(fontFamily,FontWeight.NORMAL, fontSize));
        headerTextSecondPart.setFill(darkGrayFontColor);

        /* Creiamo la terza parte dell'intestazione */
        Text headerTextThirdPart = new Text("F");
        headerTextThirdPart.setFont(Font.font(fontFamily,FontWeight.BOLD, fontSize));
        headerTextThirdPart.setFill(darkYellowFontColor);

        /* Creiamo la quarta parte dell'intestazione */
        Text headerTextFourthPart = new Text("inder");
        headerTextFourthPart.setFont(Font.font(fontFamily,FontWeight.NORMAL, fontSize));
        headerTextFourthPart.setFill(darkGrayFontColor);

        /* aggiungiamo varie parti dell'intestazione al textFlow */
        textFlow.getChildren().addAll(headerTextFirstPart,
                                      headerTextSecondPart,
                                      headerTextThirdPart,
                                      headerTextFourthPart);

        /* allineiamo il texflow al centroé */
        textFlow.setTextAlignment(TextAlignment.CENTER);

        /* aggiungiamo logo e textflow al gridPane secondo la seguente sintassi: */

        // 1 - indice di colonna
        // 2 - indice di riga
        // 3 - span colonna
        // 4 - span riga
        gridRoot.add(logoImage,0,0,2,1);
        gridRoot.add(textFlow,0,1,2,1);

        /* impostiamo l'allineamento del logo */
        GridPane.setHalignment(logoImage, HPos.CENTER);

        /* impostiamo il testo del bottone che porta all'area aziendale */
        companyButton.setText("Area aziendale");

        /* impostiamo il testo del bottone che porta all'area dei clienti */
        userButton.setText("Area Clienti");

        /* impostiamo l'azione al click del bottone che porta all'area aziendale */
        companyButton.setOnAction(actionEvent -> {
            UtilityController.navigateTo(companyAreaFile,"Area Azienda",
                                   "File interfaccia azienda non trovato", companyButton);
        });

        /* impostiamo l'azione al click del bottone che porta all'area cliente */
        userButton.setOnAction(actionEvent -> {
           UtilityController.navigateTo(userFile,"Area Cliente",
                                  "File interfaccia cliente non trovato",
                                        userButton, customerLoginStyle);
        });
    }
}