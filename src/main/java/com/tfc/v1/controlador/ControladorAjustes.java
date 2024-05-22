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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class ControladorAjustes implements Initializable {

	@Autowired
	private Gestor gestor;
	@FXML
	private Stage stage;
	@FXML
	private Scene scene;
	@Autowired
	SpringFXMLLoader springFXMLLoader;

	@FXML
	private Button btnAtras;
	@FXML
	private Label lblusr;
	@FXML
	private MenuButton menuBtn;

	@FXML
	private MenuItem menuItem1;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ImageView imageView = new ImageView(new Image("/vistas/img/usuario.png"));
		imageView.setFitWidth(50); // Ajusta el ancho de la imagen
		imageView.setFitHeight(50); // Ajusta la altura de la imagen
		lblusr = new Label(ControladorMainWindow.usuario);

		// Crear el VBox y agregar la imagen y el texto
		VBox vbox = new VBox();
		vbox.getChildren().addAll(imageView, lblusr);

		// Asignar el VBox como gráfico del MenuButton
		menuBtn.setGraphic(vbox);

		// Configurar el manejador de acción para el MenuItem
		menuItem1.setOnAction(event -> {
			try {
				logout(event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
		btnAtras.setGraphic(imageAtras);


	}

	private void logout(ActionEvent event) throws IOException {
		try {
			// Cargar la nueva vista usando SpringFXMLLoader
			Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");

			// Obtener el Stage desde cualquier nodo de la escena
			Stage stage = (Stage) menuBtn.getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			// Manejar la excepción según sea necesario
		}
	}

	@FXML
	public void abrirVentanaprincipal(ActionEvent event) throws IOException {

		// Usar SpringFXMLLoader para cargar la nueva vista
		Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

}
