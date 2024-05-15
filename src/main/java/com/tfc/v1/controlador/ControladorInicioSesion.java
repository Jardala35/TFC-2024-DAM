package com.tfc.v1.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.auth.AuthResponse;
import com.tfc.v1.auth.LoginRequest;
import com.tfc.v1.auth.RegisterRequest;
import com.tfc.v1.negocio.Gestor;
import org.springframework.http.ResponseEntity;

import javafx.event.ActionEvent;
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
    @Autowired
    private Gestor gestor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del controlador, se llama automáticamente después de cargar la vista
    }
   
    public void handleIniciarSesion(ActionEvent e) {
        String usuario = usuarioTextField.getText();
        String contrasena = contrasenaTextField.getText();

        ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(usuario, contrasena));
        System.out.println(re.getStatusCode());
//        System.out.println("Iniciando sesión para usuario: " + usuario + contrasena);
    }
    
    public void registro(ActionEvent e) {
    	 String usuario = usuarioTextField.getText();
         String contrasena = contrasenaTextField.getText();
         
         gestor.getAuthcontroller().register(new RegisterRequest(usuario,
        		 contrasena, "Pablo", "Navarro", "pablo@uknown.com"));
    }

}
