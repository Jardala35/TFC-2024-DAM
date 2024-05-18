package com.tfc.v1.controlador;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfc.v1.modelo.entidades.Producto;

public class ControladorTabla {
    
    @FXML
    private TableView<Producto> tableView;
    @FXML
    private TableColumn<Producto, Integer> idColumn;
    @FXML
    private TableColumn<Producto, String> nombreColumn;
    @FXML
    private TableColumn<Producto, Double> pesoColumn;
    @FXML
    private TableColumn<Producto, Double> valorColumn;
    @FXML
    private TableColumn<Producto, String> descripcionColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorProductoUnidad"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    @FXML
    public void tablaBBDD(ActionEvent event) throws IOException {
        ObservableList<Producto> productos = getTabla();
        tableView.setItems(productos);
    }
    
    public ObservableList<Producto> getTabla() {
        ObservableList<Producto> productos = FXCollections.observableArrayList();

        String url = "jdbc:mysql://database-tfc.c7ueouasy3yg.us-east-1.rds.amazonaws.com/StockMaven";
        String user = "admin";
        String password = "TFCdam2024";
        
        String query = "SELECT id, nombre_producto, peso, valor_producto_unidad, descripcion FROM producto";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombreProducto = resultSet.getString("nombre_producto");
                double peso = resultSet.getDouble("peso");
                double valorProductoUnidad = resultSet.getDouble("valor_producto_unidad");
                String descripcion = resultSet.getString("descripcion");
                productos.add(new Producto(id, nombreProducto, peso, valorProductoUnidad, descripcion));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return productos;
    }
}
