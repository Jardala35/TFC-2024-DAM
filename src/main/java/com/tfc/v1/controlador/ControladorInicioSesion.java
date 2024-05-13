package com.tfc.v1.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
@Component
public class ControladorInicioSesion implements Initializable {

    @FXML
    private TextField usuarioTextField;

    @FXML
    private TextField contrasenaTextField;

    @FXML
    private Button iniciarSesionButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del controlador, se llama automáticamente después de cargar la vista
    }

    @FXML
    private void handleIniciarSesion() {
        String usuario = usuarioTextField.getText();
        String contrasena = contrasenaTextField.getText();

   
        System.out.println("Iniciando sesión para usuario: " + usuario);
    }

}
