package com.tfc.v1.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private Button btnAtras;
    @FXML
    private Label lblusr = new Label();
    @FXML
    private MenuButton menuBtn;
    @FXML
    private VBox chartContainer = new VBox();
    @FXML
    private MenuItem lineChartItem = new MenuItem();
    @FXML
    private MenuItem barChartItem = new MenuItem();
    @FXML
    private MenuItem areaChartItem = new MenuItem();

    private LineChart<String, Number> lineChart = null;
    private BarChart<String, Number> barChart = null;
    private AreaChart<String, Number> areaChart = null;

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
        lineChartItem.setOnAction(event -> mostrarLineChart());
        barChartItem.setOnAction(event -> mostrarBarChart());
        areaChartItem.setOnAction(event -> mostrarAreaChart());

        ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
        btnAtras.setGraphic(imageAtras);

        cargarProductos();
    }

    @FXML
    public void mostrarLineChart() {
        if (lineChart != null) {
            chartContainer.getChildren().setAll(lineChart);
        } else {
            System.out.println("El gráfico de línea no está inicializado.");
        }
    }

    
    @FXML
    public void mostrarBarChart() {
        chartContainer.getChildren().setAll(barChart);
    }
    
    @FXML
    public void mostrarAreaChart() {
        chartContainer.getChildren().setAll(areaChart);
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        Stage stage = (Stage) menuBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void abrirVentanaprincipal(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void cargarProductos() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> areaChartData = FXCollections.observableArrayList();

            // Ordenar la lista de productos por cantidad de forma descendente
            productos.sort(Comparator.comparingInt(Producto::getCantidad).reversed());

            // Tomar solo los primeros 5 productos
            List<Producto> top5Productos = productos.subList(0, Math.min(5, productos.size()));

            XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> areaChartSeries = new XYChart.Series<>();

            for (Producto producto : top5Productos) {
                String nombreProducto = producto.getNombre_producto();
                int cantidadProducto = producto.getCantidad();
                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
            }

            lineChartData.add(lineChartSeries);
            barChartData.add(barChartSeries);
            areaChartData.add(areaChartSeries);

            // Crear los gráficos solo si no son nulos
            if (lineChart == null) {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                lineChart = new LineChart<>(xAxis, yAxis, lineChartData);
            }

            if (barChart == null) {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                barChart = new BarChart<>(xAxis, yAxis, barChartData);
            }

            if (areaChart == null) {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                areaChart = new AreaChart<>(xAxis, yAxis, areaChartData);
            }

            // Mostrar el gráfico inicial
            mostrarLineChart();
        } else {
            // Si no hay datos, limpiar el contenedor
            chartContainer.getChildren().clear();
        }
    }


}
