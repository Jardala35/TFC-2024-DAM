package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.auth.RegisterRequest;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.negocio.Gestor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button registroButton;
    @Autowired
    private Gestor gestor;
    @FXML
    private Text registro;
    
    private Stage stage;
    
    private Parent root;
    
    private Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del controlador, se llama automáticamente después de cargar la vista
    }
   
    public void handleIniciarSesion(ActionEvent e) {
        String usuario = usuarioTextField.getText();
        String contrasena = contrasenaTextField.getText();

//        ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(usuario, contrasena));
//        System.out.println(re.getStatusCode());
        System.out.println("Iniciando sesión para usuario: " + usuario + contrasena);
    }
    
    public void registro(ActionEvent e) {
    	 String usuario = usuarioTextField.getText();
         String contrasena = contrasenaTextField.getText();
         
         gestor.getAuthcontroller().register(new RegisterRequest(usuario,
        		 contrasena, "Pablo", "Navarro", "pablo@uknown.com"));
    }
    
    public void abrirVentanaRegistro(ActionEvent event) throws IOException {  
        root = FXMLLoader.load(getClass().getResource("/vistas/Tabla.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }	
    
    public void abrirVentanaInicioSesion_Registro(ActionEvent event) throws IOException {  
        root = FXMLLoader.load(getClass().getResource("/vistas/ini_sesion.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public void abrirVentanaInicioSesion_RegistroCancel(ActionEvent event) throws IOException {  
        root = FXMLLoader.load(getClass().getResource("/vistas/ini_sesion.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
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
}
