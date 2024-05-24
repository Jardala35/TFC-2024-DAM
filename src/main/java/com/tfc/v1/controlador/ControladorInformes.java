package com.tfc.v1.controlador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.negocio.Gestor;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


@Component
public class ControladorInformes implements Initializable {
    @Autowired
    private Gestor gestor;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private RadioButton rbCantidad;

    @FXML
    private RadioButton rbPrecio;

    @FXML
    private Button btnAtras;
    @FXML
    private Label lblusr = new Label();
    @FXML
    private MenuButton menuBtn;
    @FXML
    private MenuButton menuBtn_Columnas = new MenuButton();
    @FXML
    private VBox chartContainer = new VBox();
    @FXML
    private MenuItem lineChartItem = new MenuItem();
    @FXML
    private MenuItem barChartItem = new MenuItem();
    @FXML
    private MenuItem areaChartItem = new MenuItem();
    @FXML
    private MenuItem pieChartItem = new MenuItem();

    private LineChart<String, Number> lineChart = null;
    private BarChart<String, Number> barChart = null;
    private AreaChart<String, Number> areaChart = null;
    private PieChart pieChart = null;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ImageView imageView = new ImageView(new Image("/vistas/img/usuario.png"));
        imageView.setFitWidth(50); // Ajusta el ancho de la imagen
        imageView.setFitHeight(50); // Ajusta la altura de la imagen
        lblusr = new Label(ControladorMainWindow.usuario);

        rbCantidad.setToggleGroup(toggleGroup);
        rbPrecio.setToggleGroup(toggleGroup);

        // Crear el VBox y agregar la imagen y el texto
        VBox vbox = new VBox();
        vbox.getChildren().addAll(imageView, lblusr);

        // Asignar el VBox como gráfico del MenuButton
        menuBtn.setGraphic(vbox);

        // Configurar el manejador de acción para el MenuItem
        lineChartItem.setOnAction(event -> mostrarLineChart());
        barChartItem.setOnAction(event -> mostrarBarChart());
        areaChartItem.setOnAction(event -> mostrarAreaChart());
        pieChartItem.setOnAction(event -> mostrarPieChart());

        ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
        btnAtras.setGraphic(imageAtras);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == rbCantidad) {
                actualizarGraficaCantidad();
            } else if (newValue == rbPrecio) {
                actualizarGraficaPrecio();
            }
        });
        
        

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
    public void mostrarPieChart() {
        if (pieChart != null) {
            chartContainer.getChildren().setAll(pieChart);
        } else {
            System.out.println("El gráfico de pastel no está inicializado.");
        }
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
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> areaChartSeries = new XYChart.Series<>();

            for (Producto producto : productos) {
                String nombreProducto = producto.getNombre_producto();
                int cantidadProducto = producto.getCantidad();
                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                pieChartData.add(new PieChart.Data(nombreProducto, cantidadProducto));
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

            if (pieChart == null) {
                pieChart = new PieChart(pieChartData);
            }

            // Mostrar el gráfico inicial
            mostrarPieChart();
        } else {
            // Si no hay datos, limpiar el contenedor
            chartContainer.getChildren().clear();
        }
    }

    @FXML
    private void actualizarGraficaCantidad() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> areaChartData = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> areaChartSeries = new XYChart.Series<>();

            for (Producto producto : productos) {
                String nombreProducto = producto.getNombre_producto();
                int cantidadProducto = producto.getCantidad();
                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                pieChartData.add(new PieChart.Data(nombreProducto, cantidadProducto));
            }

            lineChartData.add(lineChartSeries);
            barChartData.add(barChartSeries);
            areaChartData.add(areaChartSeries);

            // Actualizar los datos en los gráficos
            lineChart.setData(lineChartData);
            barChart.setData(barChartData);
            areaChart.setData(areaChartData);
            pieChart.setData(pieChartData);
        } else {
            // Si no hay datos, limpiar los gráficos
            lineChart.setData(null);
            barChart.setData(null);
            areaChart.setData(null);
            pieChart.setData(null);
        }
    }

    @FXML
    private void actualizarGraficaPrecio() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> areaChartData = FXCollections.observableArrayList();

            XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> areaChartSeries = new XYChart.Series<>();

            // Ordenar la lista de productos por precio de forma descendente
            productos.sort(Comparator.comparingDouble(Producto::getValor_producto_unidad).reversed());

            for (Producto producto : productos) {
                String nombreProducto = producto.getNombre_producto();
                double precioProducto = producto.getValor_producto_unidad();
                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));
            }

            lineChartData.add(lineChartSeries);
            barChartData.add(barChartSeries);
            areaChartData.add(areaChartSeries);

            // Actualizar los datos en los gráficos
            lineChart.setData(lineChartData);
            barChart.setData(barChartData);
            areaChart.setData(areaChartData);
        } else {
            // Si no hay datos, limpiar los gráficos
            lineChart.setData(null);
            barChart.setData(null);
            areaChart.setData(null);
        }
    }

    @FXML
    public void exportarGraficoAPDF(ActionEvent e) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar gráfico como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(chartContainer.getScene().getWindow());

        if (file != null) {
            WritableImage snapshot = chartContainer.snapshot(new SnapshotParameters(), null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", byteArrayOutputStream);

            try (PdfWriter writer = new PdfWriter(new FileOutputStream(file))) {
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.add(new Paragraph("Gráfico" + toggleGroup.getSelectedToggle().getUserData().toString()));
                document.add(new AreaBreak());

                ImageData imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray());
                com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(imageData);
                document.add(image);
                document.close();
            }
        }
    }
}
