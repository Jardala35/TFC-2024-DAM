package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.negocio.Gestor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class ControladorMainWindow implements Initializable {

    @Autowired
    private Gestor gestor;

    @FXML
    private ImageView imgBack;

    @FXML
    private MenuButton menuBtn;

    @FXML
    private MenuItem menuItem1;

    @FXML
    private Button botonInv, botonGraf, botonAju, botonMov;

    @FXML
    private ImageView imgInv, imgGraf, imgAju, imgMov;

    @FXML
    private Pane paneid;
    
    @FXML
    private Label lblusr;

    public static String usuario;
    @FXML
    private Text textInv = new Text();
    @FXML
    private Text textMov = new Text();; 
    @FXML
    private Text textGraf = new Text();
    @FXML
    private Text textAju = new Text();

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    private Stage stage;
    private Scene scene;

    public ControladorMainWindow() {
        super();
        System.out.println("Controlador main window creado");
        System.out.println(this.gestor);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("Gestor inyectado en ControladorInicioSesion: " + (gestor != null));
        ImageView imageView = new ImageView(new Image("/vistas/img/usuario.png"));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        lblusr = new Label(usuario);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(imageView, lblusr);

        menuBtn.setGraphic(vbox);

        menuItem1.setOnAction(event -> {
            try {
                logout(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        textInv = new Text("Inventario");
        textMov = new Text("Movimientos");
        textGraf = new Text("Gráficos");
        textAju = new Text("Ajustes");

        // Ajustar tamaño de imágenes y textos al inicializar
     // Ajustar tamaño de imágenes y textos al inicializar
        ajustarTamaño(botonInv, imgInv, textInv);
        ajustarTamaño(botonMov, imgMov, textMov);
        ajustarTamaño(botonGraf, imgGraf, textGraf);
        ajustarTamaño(botonAju, imgAju, textAju);

        // Añadir listeners para ajustar tamaño al redimensionar la ventana
        botonInv.widthProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonInv, imgInv, textInv));
        botonInv.heightProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonInv, imgInv, textInv));

        botonMov.widthProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonMov, imgMov, textMov));
        botonMov.heightProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonMov, imgMov, textMov));

        botonGraf.widthProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonGraf, imgGraf, textGraf));
        botonGraf.heightProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonGraf, imgGraf, textGraf));

        botonAju.widthProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonAju, imgAju, textAju));
        botonAju.heightProperty().addListener((obs, oldVal, newVal) -> ajustarTamaño(botonAju, imgAju, textAju));
    }

    private void ajustarTamaño(Button button, ImageView imageView, Text text) {
        double newSize = Math.min(button.getWidth(), button.getHeight()) / 2.5; // Ajustar según tus necesidades
        imageView.setFitWidth(newSize * 0.7);
        imageView.setFitHeight(newSize * 0.7);
        text.setFont(new Font(newSize * 0.3));
    }





    private void logout(ActionEvent event) throws IOException {
        try {
            Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
            Stage stage = (Stage) menuBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirInventario(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/Tabla.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void abrirGrafico(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/Grafico.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void abrirInformes(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/informes.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void abrirAjustes(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/ajustes.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void abrirMovimientos(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/movimientos.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}