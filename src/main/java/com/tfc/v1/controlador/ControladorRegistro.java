package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.auth.RegisterRequest;
import com.tfc.v1.modelo.entidades.usuario.Rol;
import com.tfc.v1.negocio.Gestor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
	@FXML
	private Button btnRegistro;

	@FXML
	private ChoiceBox<String> cbxRol;

	@FXML
	private Label lblerrorusr;

	public ControladorRegistro() {
		super();
		System.out.println("Controlador registro creado");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println("Gestor inyectado: " + (gestor != null));
		Rol[] roles = Rol.values();
		String[] aux = new String[roles.length];
		for (int i = 0; i < aux.length; i++) {
			aux[i] = roles[i].name();
		}
		cbxRol.getItems().setAll(aux);
		// Deshabilitar el botón al inicio
		btnRegistro.setDisable(true);

		// Añadir listeners a los campos de texto y a la ChoiceBox
		ChangeListener<String> listener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				checkFields();
			}
		};

		textFieldNombre.textProperty().addListener(listener);
		textFieldApellido.textProperty().addListener(listener);
		textFieldUser.textProperty().addListener(listener);
		textFieldPassword.textProperty().addListener(listener);
		textFieldEmail.textProperty().addListener(listener);
		cbxRol.valueProperty().addListener((observable, oldValue, newValue) -> checkFields());
	}

	private void checkFields() {
		boolean disableButton = textFieldNombre.getText().isEmpty() || textFieldApellido.getText().isEmpty()
				|| textFieldUser.getText().isEmpty() || textFieldPassword.getText().isEmpty()
				|| textFieldEmail.getText().isEmpty() || cbxRol.getValue() == null;
		btnRegistro.setDisable(disableButton);
	}

	public Boolean registro() {
		try {
			gestor.getAuthcontroller()
					.register(new RegisterRequest(textFieldUser.getText(), textFieldPassword.getText(),
							textFieldNombre.getText(), textFieldApellido.getText(), textFieldEmail.getText(),
							Rol.valueOf(cbxRol.getValue())));
			this.lblerrorusr.setVisible(false);
			return true;
		} catch (DataIntegrityViolationException e) {
			System.out.println("Error, usuario duplicado");
			this.lblerrorusr.setVisible(true);

		} catch (Exception e) {
			System.out.println("Error buscate la vida");
		}
		return false;
	}

	public void abrirVentanaInicioSesion_Registro(ActionEvent event) throws IOException {
		if (registro()) {
			Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}

	public void abrirVentanaInicioSesion_RegistroCancel(ActionEvent event) throws IOException {
		Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}