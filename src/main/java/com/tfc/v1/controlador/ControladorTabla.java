package com.tfc.v1.controlador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
/**
 * Controlador para la vista de inventario.
 * 
 * @author Sergio Rubio Núñez 
 * @author Pablo Navarro Duro 
 * 
 * @see Tabla.fxml
 * 
 */
@Component
public class ControladorTabla implements Initializable {
	@Autowired
	private Gestor gestor;
	@Autowired
	private SpringFXMLLoader springFXMLLoader;
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
	@FXML
	private Button addRowButton = new Button();
	@FXML
	private Button deleteRowButton = new Button();
	@FXML
	private Button btnupdate = new Button();
	@FXML
	private ComboBox<String> cbxSeccion;
	private String delimiter = ",";

	private ObservableList<Producto> productosOriginales;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cargarProductos();
		cargarSecciones();
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

		if (!esUsuarioAdmin()) {
			deleteRowButton.setDisable(true);
			addRowButton.setDisable(true);
			btnupdate.setDisable(true);
			
		}

//		addRowButton.setOnAction(event -> addRow());

		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			buscarProductos(newValue.trim().toLowerCase());
		});

	    cbxSeccion.valueProperty().addListener((observable, oldValue, newValue) -> {
	        filtrarProductosPorSeccion(newValue);
	    });
	}
	
	@FXML
	public void cargarSecciones() {
	    List<Seccion> secciones = gestor.getContRest().listarSecciones().getBody();
	    List<String> seccionestxt = new ArrayList<>();
	    for (Seccion sec : secciones) {
			seccionestxt.add(sec.getNombre_seccion());
		}

	    if (secciones != null) {
	        cbxSeccion.getItems().clear();
	        cbxSeccion.getItems().addAll(seccionestxt);
	    }
	    
	    cbxSeccion.getItems().add(0, "Todas");

	    cbxSeccion.getSelectionModel().select(0);
	}
	
	
	public void filtrarProductosPorSeccion(String seccionSeleccionada) {
	    if (seccionSeleccionada == null || "Todas".equals(seccionSeleccionada)) {
	        tableView2.setItems(productosOriginales);
	    } else {
	        List<Producto> productosFiltrados = productosOriginales.stream()
	                .filter(producto -> seccionSeleccionada.equals(producto.getSeccion().getNombre_seccion()))
	                .collect(Collectors.toList());

	        tableView2.setItems(FXCollections.observableArrayList(productosFiltrados));
	    }
	}
	
	@FXML
	public void exportarCSV2(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar archivo CSV");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
		File file = fileChooser.showSaveDialog(null);

		if (file != null) {
			try (BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
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

	private boolean esUsuarioAdmin() {
		String rol = gestor.obtenerRolUsuario(ControladorMainWindow.usuario).toLowerCase();
		return "admin".equals(rol);
	}

	private String getProdStringValues(Producto p, String delimiter) {
		return String.valueOf(p.getId()) + delimiter + p.getNombre_producto() + delimiter
				+ String.valueOf(p.getValor_producto_unidad()) + delimiter + String.valueOf(p.getCantidad()) + delimiter
				+ String.valueOf(p.getPeso()) + delimiter + p.getDescripcion();
	}

	@FXML
	public void abrirVentanaprincipal(MouseEvent event) throws IOException {
		abrirNuevaVentana(event, "/vistas/main_wind.fxml");
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
				
				  TableColumn<Producto, String> secColumn = new TableColumn<>("Seccion");
		            secColumn.setCellValueFactory(cellData -> {
		                Seccion seccion = cellData.getValue().getSeccion();
		                return new SimpleStringProperty(seccion != null ? seccion.getNombre_seccion() : "");
		            });

		            secColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		            secColumn.setOnEditCommit(event -> {
		                Producto producto = event.getRowValue();
		                String nombreSeccion = event.getNewValue();

		                if (nombreSeccion != null && !nombreSeccion.isEmpty()) {
		                    Optional<Seccion> seccionOpt = Optional.ofNullable(gestor.getContRest().getSeccion(nombreSeccion).getBody());

		                    Seccion nuevaSeccion = seccionOpt.orElseGet(() -> {
		                        return gestor.getContRest().altaSeccion(new Seccion(nombreSeccion)).getBody();
		                    });

		                    producto.setSeccion(nuevaSeccion);
		                } else {
		                    System.err.println("El nombre de la sección es nulo o vacío");
		                }
		            });

				tableView2.getColumns().addAll(idColumn, nombreColumn, precioColumn, cantidadColumn, pesoColumn,
						descColumn, secColumn);

				productosOriginales = FXCollections.observableArrayList(productos);

				ObservableList<Producto> items = FXCollections.observableArrayList(productos);
				tableView2.setItems(items);
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

	@FXML
	public void addRow() {
		Producto newProducto = new Producto();
		newProducto.setNombre_producto("Nuevo producto");
		newProducto.setValor_producto_unidad(0.0);
		newProducto.setCantidad(0);
		newProducto.setPeso(0.0);
		newProducto.setDescripcion("Descripción del nuevo producto");
		
		tableView2.getItems().add(newProducto);

		gestor.insertarProducto(newProducto);
	}

	@FXML
	public void deleteSelectedRow() {
		Producto selectedProduct = tableView2.getSelectionModel().getSelectedItem();

		if (selectedProduct != null) {
			tableView2.getItems().remove(selectedProduct);

			gestor.eliminarProducto(selectedProduct.getId());
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

	private void abrirNuevaVentana(MouseEvent event, String fxmlPath) throws IOException {
		Parent root = springFXMLLoader.load(fxmlPath);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.hide();
		
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setFullScreen(true); 
		stage.setFullScreenExitHint("");
		stage.show();
	}

	private void buscarProductos(String searchTerm) {
		if (searchTerm.isEmpty()) {
			tableView2.setItems(productosOriginales);
			return;
		}
		List<Producto> filteredList = productosOriginales.stream()
				.filter(producto -> producto.getNombre_producto().toLowerCase().contains(searchTerm)
						|| String.valueOf(producto.getId()).contains(searchTerm)
						|| String.valueOf(producto.getValor_producto_unidad()).contains(searchTerm)
						|| String.valueOf(producto.getCantidad()).contains(searchTerm)
						|| String.valueOf(producto.getPeso()).contains(searchTerm)
						|| producto.getDescripcion().toLowerCase().contains(searchTerm))
				.collect(Collectors.toList());

		tableView2.setItems(FXCollections.observableArrayList(filteredList));
	}
	
}
