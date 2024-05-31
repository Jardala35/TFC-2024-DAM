package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.auth.AuthResponse;
import com.tfc.v1.auth.LoginRequest;
import com.tfc.v1.negocio.Gestor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

	@Autowired
	private SpringFXMLLoader springFXMLLoader;

	@FXML
	private Button registroButton;
	@FXML
	private Label lblerroracceso;		

	private Stage stage;
	private Scene scene;

	public ControladorInicioSesion() {
		super();
		System.out.println("Controlador inicio creado");
		System.out.println(this.gestor);		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("Gestor inyectado en ControladorInicioSesion: " + (gestor != null));
	}
	
	public void handleIniciarSesion(ActionEvent e) throws IOException {
		String usuario = usuarioTextField.getText();
		String contrasena = contrasenaTextField.getText();

		try {
			ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(usuario, contrasena));
			System.out.println(re.getBody() + " " + re.getStatusCodeValue());
			this.lblerroracceso.setVisible(false);	
			ControladorMainWindow.usuario = usuario;
			 // Usar SpringFXMLLoader para cargar la nueva vista
			Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");			
			stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.setFullScreen(true);
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e2) {
			this.lblerroracceso.setVisible(true);
		}

	}
	
	

	
	public void abrirVentanaRegistro(ActionEvent event) throws IOException {

		// Usar SpringFXMLLoader para cargar la nueva vista
		Parent root = springFXMLLoader.load("/vistas/Registro.fxml");
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
}

