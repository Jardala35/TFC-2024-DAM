package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

@Component
public class ControladorTabla implements Initializable {
    @Autowired
    private Gestor gestor;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;
    @FXML
    private TableView<String[]> tableView;
    @FXML
    private TableView<Producto> tableView2;
    @FXML
    private Button archivos;
    @FXML
    private Button btnAtras;
    @FXML
    private Label lblusr;
    @FXML
    private MenuButton menuBtn;
    @FXML
    private MenuItem menuItem1;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private TextField filterField;

    private File file;
    private String delimiter = ",";

    @FXML
    public void archivosSubir(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona los archivos a subir");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        List<File> archivos = fileChooser.showOpenMultipleDialog(null);

        if (archivos != null && !archivos.isEmpty()) {
            file = archivos.get(0);
            try {
                CSVTableView();
            } catch (IOException ex) {
                ex.printStackTrace();
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
    public void exportarCSV2(ActionEvent e) {
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
    public void abrirVentanaprincipal(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void cargarProductos() {
        List<Producto> productos = gestor.getContRest().listarProductos().getBody();

        tableView2.setEditable(true);
        
        if (productos != null) {
            tableView2.getColumns().clear();
            tableView2.getItems().clear();
            if (!productos.isEmpty()) {
                TableColumn<Producto, Integer> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                idColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setId(event.getNewValue());
                });

                TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
                nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));
                nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                nombreColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setNombre_producto(event.getNewValue());
                });

                TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
                precioColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));
                precioColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
                precioColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setValor_producto_unidad(event.getNewValue());
                });

                TableColumn<Producto, Integer> cantidadColumn = new TableColumn<>("Cantidad");
                cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
                cantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                cantidadColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setCantidad(event.getNewValue());
                });

                TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
                pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
                pesoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
                pesoColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setPeso(event.getNewValue());
                });

                TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
                descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
                descColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                descColumn.setOnEditCommit(event -> {
                    Producto producto = event.getRowValue();
                    producto.setDescripcion(event.getNewValue());
                });

                tableView2.getColumns().addAll(idColumn, nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
                ObservableList<Producto> items = FXCollections.observableArrayList(productos);
                FilteredList<Producto> filteredData = new FilteredList<>(items, p -> true);

                filterField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(producto -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return producto.getNombre_producto().toLowerCase().contains(lowerCaseFilter) ||
                               producto.getDescripcion().toLowerCase().contains(lowerCaseFilter);
                    });
                });

                SortedList<Producto> sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(tableView2.comparatorProperty());
                tableView2.setItems(sortedData);
            }
        }
    }

    @FXML
    public void commitearCambios(ActionEvent event) {
        ObservableList<Producto> productos = tableView2.getItems();
        for (Producto producto : productos) {
            gestor.actualizarProducto(producto.getId(), producto);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarProductos();
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
    }
    private void logout(ActionEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        Stage stage = (Stage) menuBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
