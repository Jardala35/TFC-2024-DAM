package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.negocio.Gestor;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@Component
public class ControladorTabla implements Initializable {
	@Autowired
	private Gestor gestor;
	@FXML
	private Stage stage;
	@FXML
	private Scene scene;
	@Autowired
	SpringFXMLLoader springFXMLLoader;
    @FXML
    private TableView<String[]> tableView;
    @FXML
    private TableView<Producto> tableView2;
    @FXML
    private Button archivos;
    
    @FXML
    private LineChart<Number, Number> lineChart;

    private File file;

    private String delimiter = ",";

    @FXML
    public void archivosSubir(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona los archivos a subir");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        List<File> archivos = fileChooser.showOpenMultipleDialog(null);

        if (archivos != null && !archivos.isEmpty()) {
            // Tomar el primer archivo seleccionado (podrías manejar múltiples archivos de otra manera)
            file = archivos.get(0);
            try {
                CSVTableView();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Manejar la excepción
            }
        }
    }

    @FXML
    public void CSVTableView() throws IOException {
        tableView.getColumns().clear(); 

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(delimiter);
                if (firstLine) {
                    for (String columnName : data) {
                        TableColumn<String[], String> column = new TableColumn<>(columnName);
                        final int index = Arrays.asList(data).indexOf(columnName);
                        column.setCellValueFactory(param -> {
                            String[] rowData = param.getValue();
                            return new SimpleStringProperty(rowData[index]);
                        });
                        // Alinear el contenido al centro
                        column.setCellFactory(getCenteredCellFactory());
                        tableView.getColumns().add(column);
                    }
                    firstLine = false;
                } else {
                    tableView.getItems().add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<String[], String>, TableCell<String[], String>> getCenteredCellFactory() {
        return column -> {
            TableCell<String[], String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setStyle("-fx-alignment: CENTER;");
                }
            };
            return cell;
        };
    }
    @FXML
    public void exportarCSV(ActionEvent e) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (TableColumn<String[], ?> column : tableView.getColumns()) {
                    bw.write(column.getText() + delimiter);
                }
                bw.newLine();

                for (String[] row : tableView.getItems()) {
                    bw.write(String.join(delimiter, row));
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    @FXML
    public void enseñarTabla(ActionEvent e) {
        lineChart.getData().clear();

        if (tableView.getItems().isEmpty() || tableView.getColumns().isEmpty()) {
        	System.out.println("No hay datos para mostrar");
            return; // No hay datos para mostrar
            
        }

        // Crear una serie para cada columna
        for (int i = 1; i < 1; i++) { // Comenzar en 1 para evitar usar la primera columna como eje X
            TableColumn<String[], ?> column = tableView.getColumns().get(i);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(column.getText());

            for (int j = 0; j < 2; j++) {
                String[] row = tableView.getItems().get(j);
                try {
                    series.getData().add(new XYChart.Data<>(j + 1, Double.parseDouble(row[i])));
                } catch (NumberFormatException ex) {
                    System.err.println("Error al convertir el dato '" + row[i] + "' a un número en la fila " + (j + 1) + ", columna " + (i + 1));
                    ex.printStackTrace();
                }
            }

            lineChart.getData().add(series);
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
    
    @SuppressWarnings("unchecked")
	@FXML
    public void cargarProductos() {
        // Fetch data from the database through REST controller
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        if (productos != null) {
            // Create columns dynamically based on the Producto class fields
        	 tableView2.getColumns().clear();
             tableView2.getItems().clear();
             if (!productos.isEmpty()) {
            	 TableColumn<Producto, Integer> idColumn = new TableColumn<>("ID");
                 idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                 TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
                 nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));

                 TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
                 precioColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));
                 
                 TableColumn<Producto, Integer> cantidadColumn = new TableColumn<>("Cantidad");
                 precioColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
                 
                 TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
                 precioColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
                 
                 TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
                 precioColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

                 // Add columns to the TableView
                 tableView2.getColumns().addAll(idColumn, nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
            	 ObservableList<Producto> items = FXCollections.observableArrayList(productos);
            	 
                 tableView2.setItems(items);
             }
           
        }
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cargarProductos();
		
	}
	
	@FXML
    public void exportarCSV2(ActionEvent e) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                for (TableColumn<Producto, ?> column : tableView2.getColumns()) {
                    bw.write(column.getText() + delimiter);
                }
                bw.newLine();

                for (Producto row : tableView2.getItems()) {
                    bw.write(getProdStringValues(row, delimiter));
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
	private String getProdStringValues(Producto p, String delimiter) {
		return String.valueOf(p.getId()) + delimiter + p.getNombre_producto() + delimiter
				+ String.valueOf(p.getValor_producto_unidad()) + delimiter + String.valueOf(p.getCantidad()) + delimiter
				+ String.valueOf(p.getPeso()) + delimiter + p.getDescripcion();
	}
}
