package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.auth.RegisterRequest;
import com.tfc.v1.negocio.Gestor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
public class ControladorRegistro implements Initializable {
    private Stage stage;
    private Scene scene;

    @Autowired
    private Gestor gestor;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellido;
    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldEmail;

    public ControladorRegistro() {
        super();
        System.out.println("Controlador registro creado");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("Gestor inyectado: " + (gestor != null));
    }

    public void registro() {
        gestor.getAuthcontroller().register(new RegisterRequest(
            textFieldUser.getText(),
            textFieldPassword.getText(),
            textFieldNombre.getText(),
            textFieldApellido.getText(),
            textFieldEmail.getText()
        ));
    }

    public void abrirVentanaInicioSesion_Registro(ActionEvent event) throws IOException {
        registro();
        Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void abrirVentanaInicioSesion_RegistroCancel(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}