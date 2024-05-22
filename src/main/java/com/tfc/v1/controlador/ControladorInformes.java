package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.negocio.Gestor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class ControladorInformes implements Initializable {
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
    private PieChart pieChart = new PieChart();

    @FXML
    private MenuItem menuItem1;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ImageView imageView = new ImageView(new Image("/vistas/img/usuario.png"));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        lblusr = new Label(ControladorMainWindow.usuario);

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

        ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
        btnAtras.setGraphic(imageAtras);

        cargarProductos();
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
    public void abrirVentanaprincipal(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void cargarProductos() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            // Limpiar los datos existentes en el gráfico
            pieChart.getData().clear();

            // Crear una lista de datos para el gráfico
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Recorrer la lista de productos y agregar los datos al gráfico
            for (Producto producto : productos) {
                // Obtener el nombre del producto y su valor
                String nombreProducto = producto.getNombre_producto();
                double valorProducto = producto.getValor_producto_unidad();

                // Agregar los datos al gráfico
                pieChartData.add(new PieChart.Data(nombreProducto, valorProducto));
            }

            // Agregar los datos al gráfico
            pieChart.setData(pieChartData);
        } else {
            // Si no hay productos, limpiar los datos del gráfico
            pieChart.getData().clear();
        }
    }
}
