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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
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
	private Text registro;
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

		// Aquí puedes manejar el inicio de sesión con el gestor
		try {
			ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(usuario, contrasena));

			System.out.println(re.getBody() + " " + re.getStatusCodeValue());

			System.out.println("Iniciando sesión para usuario: " + usuario + contrasena);
			this.lblerroracceso.setVisible(false);
			// Usar SpringFXMLLoader para cargar la nueva vista
			Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
			stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
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

//	public Gestor getGestor() {
//		return gestor;
//	}
//
//	public void setGestor(Gestor gestor) {
//		this.gestor = gestor;
//	}	    

//    public void tablaBBDD (ActionEvent event) throws IOException {
//    	getTabla();
//    }
//    
//    public ObservableList<Producto> getTabla() {
//        ObservableList<Producto> productos = FXCollections.observableArrayList();
//
//        String url = "database-tfc.c7ueouasy3yg.us-east-1.rds.amazonaws.com";
//        String user = "admin";
//        String password = "TFCdam2024";
//        
//        String query = "SELECT id, descripcion, nombre_producto, peso, valor_producto_unidad FROM productos";
//        
//        try (Connection connection = DriverManager.getConnection(url, user, password);
//             PreparedStatement statement = connection.prepareStatement(query);
//             ResultSet resultSet = statement.executeQuery()) {
//            
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String nombre_producto = resultSet.getString("Nombre producto");
//                double peso = resultSet.getDouble("Peso");
//                double valor_producto_unidad = resultSet.getDouble("valor_producto_unidad");
//                String descripcion = resultSet.getString("Descripcion");
//                productos.add(new Producto(id, nombre_producto, peso, valor_producto_unidad, descripcion));
//            }
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        
//        return productos;
//    }
