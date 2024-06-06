package com.tfc.v1.controlador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.negocio.Gestor;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
public class ControladorInformes implements Initializable {
    @Autowired
    private Gestor gestor;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private RadioButton rbCantidad = new RadioButton();
    @FXML
    private RadioButton rbPrecio = new RadioButton();
    @FXML
    private Button btnAtras = new Button();
    @FXML
    private Label lblusr = new Label();
    @FXML
    private MenuButton menuBtn = new MenuButton();
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
    @FXML
    private ComboBox<String> comboBoxSeccion = new ComboBox<>();

    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;
    private AreaChart<String, Number> areaChart;
    private PieChart pieChart;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private Map<String, Color> colorMap = new HashMap<>();
    
    

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ImageView imageView = new ImageView(new Image("/vistas/img/usuario.png"));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        lblusr = new Label(ControladorMainWindow.usuario);

        rbCantidad.setToggleGroup(toggleGroup);
        rbPrecio.setToggleGroup(toggleGroup);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(imageView, lblusr);
        menuBtn.setGraphic(vbox);
        
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        areaChart = new AreaChart<>(new CategoryAxis(), new NumberAxis());
        pieChart = new PieChart();

        lineChartItem.setOnAction(event -> mostrarLineChart());
        barChartItem.setOnAction(event -> mostrarBarChart());
        areaChartItem.setOnAction(event -> mostrarAreaChart());
        pieChartItem.setOnAction(event -> mostrarPieChart());

        ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
        btnAtras.setGraphic(imageAtras);

        rbCantidad.setSelected(true);
        actualizarGraficaCantidad();

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == rbCantidad) {
                actualizarGraficaCantidad();
            } else if (newValue == rbPrecio) {
                actualizarGraficaPrecio();
            }
        });
        
        comboBoxSeccion.setOnAction(event -> filtrarPorSeccion());

        cargarProductos();
        cargarSecciones();
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Color generarColorAleatorio() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @FXML
    public void mostrarLineChart() {
        if (lineChart != null) {
            chartContainer.getChildren().setAll(lineChart);
        }
    }

    @FXML
    public void mostrarBarChart() {
        if (barChart != null) {
            chartContainer.getChildren().setAll(barChart);
        }
    }

    @FXML
    public void mostrarAreaChart() {
        if (areaChart != null) {
            chartContainer.getChildren().setAll(areaChart);
        }
    }

    @FXML
    public void mostrarPieChart() {
        if (pieChart != null) {
            chartContainer.getChildren().setAll(pieChart);
        }
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        Stage stage = (Stage) menuBtn.getScene().getWindow();
        stage.hide();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.setWidth(620);
        stage.setHeight(660);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void abrirVentanaprincipal(MouseEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    @FXML
    public void cargarProductos() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();
        if (productos != null && !productos.isEmpty()) {
            actualizarGraficos(productos);
        } else {
            chartContainer.getChildren().clear();
        }
    }

    @FXML
    public void cargarSecciones() {
        List<Seccion> secciones = gestor.getContRest().listarSecciones().getBody();
        if (secciones != null && !secciones.isEmpty()) {
            List<String> nombresSecciones = secciones.stream()
                    .map(Seccion::getNombre_seccion)
                    .collect(Collectors.toList());
            nombresSecciones.add(0, "TODAS");
            comboBoxSeccion.setItems(FXCollections.observableArrayList(nombresSecciones));
        }
    }

    @FXML
    public void filtrarPorSeccion() {
        String seccionSeleccionada = comboBoxSeccion.getValue();
        if (seccionSeleccionada != null && !seccionSeleccionada.isEmpty()) {
            List<Producto> productosFiltrados;
            if ("TODAS".equals(seccionSeleccionada)) {
                productosFiltrados = gestor.getContRest().listarProductos().getBody();
            } else {
                productosFiltrados = gestor.getContRest().listarProductos().getBody().stream()
                        .filter(producto -> seccionSeleccionada.equals(producto.getSeccion().getNombre_seccion()))
                        .collect(Collectors.toList());
            }
            actualizarGraficos(productosFiltrados);
        } else {
            cargarProductos();
        }
    }


    private void actualizarGraficos(List<Producto> productos) {
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

            updateChartData(lineChart, lineChartData);
            updateChartData(barChart, barChartData);
            updateChartData(areaChart, areaChartData);
            updatePieChartData(pieChart, pieChartData);
        } else {
            lineChart.setData(null);
            barChart.setData(null);
            areaChart.setData(null);
            pieChart.setData(null);
        }
    }

    @FXML
    public void actualizarGraficaCantidad() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            productos.sort(Comparator.comparingInt(Producto::getCantidad).reversed());

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

                Color color = colorMap.computeIfAbsent(nombreProducto, k -> generarColorAleatorio());

                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, cantidadProducto));

                PieChart.Data pieData = new PieChart.Data(nombreProducto, cantidadProducto);
                pieData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-pie-color: " + colorToHex(color) + ";");
                    }
                });
                pieChartData.add(pieData);
            }

            lineChartData.add(lineChartSeries);
            barChartData.add(barChartSeries);
            areaChartData.add(areaChartSeries);

            updateChartData(lineChart, lineChartData);
            updateChartData(barChart, barChartData);
            updateChartData(areaChart, areaChartData);
            updatePieChartData(pieChart, pieChartData);
        } else {
            chartContainer.getChildren().clear();
        }
    }

    @FXML
    public void actualizarGraficaPrecio() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null && !productos.isEmpty()) {
            productos.sort(Comparator.comparingDouble(Producto::getValor_producto_unidad).reversed());

            ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
            ObservableList<XYChart.Series<String, Number>> areaChartData = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            XYChart.Series<String, Number> lineChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> barChartSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> areaChartSeries = new XYChart.Series<>();

            for (Producto producto : productos) {
                String nombreProducto = producto.getNombre_producto();
                double precioProducto = producto.getValor_producto_unidad();

                Color color = colorMap.computeIfAbsent(nombreProducto, k -> generarColorAleatorio());

                lineChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));
                barChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));
                areaChartSeries.getData().add(new XYChart.Data<>(nombreProducto, precioProducto));

                PieChart.Data pieData = new PieChart.Data(nombreProducto, precioProducto);
                pieData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-pie-color: " + colorToHex(color) + ";");
                    }
                });
                pieChartData.add(pieData);
            }

            lineChartData.add(lineChartSeries);
            barChartData.add(barChartSeries);
            areaChartData.add(areaChartSeries);

            updateChartData(lineChart, lineChartData);
            updateChartData(barChart, barChartData);
            updateChartData(areaChart, areaChartData);
            updatePieChartData(pieChart, pieChartData);
        } else {
            chartContainer.getChildren().clear();
        }
    }

    private void updateChartData(XYChart<String, Number> chart, ObservableList<XYChart.Series<String, Number>> newData) {
        if (chart != null) {
            chart.setData(newData);
        }
    }

    private void updatePieChartData(PieChart chart, ObservableList<PieChart.Data> newData) {
        if (chart != null) {
            chart.setData(newData);
        }
    }

    @FXML
    public void generarPDF(ActionEvent event) {
        WritableImage image = chartContainer.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(chartContainer.getScene().getWindow());

        if (file != null) {
            try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteOutput);
                ImageData imageData = ImageDataFactory.create(byteOutput.toByteArray());

                try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(file)));
                     Document document = new Document(pdfDoc)) {
                    document.add(new Paragraph("Gráfico:"));
                    document.add(new com.itextpdf.layout.element.Image(imageData));
                    document.add(new AreaBreak());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void logoToMenu(MouseEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}