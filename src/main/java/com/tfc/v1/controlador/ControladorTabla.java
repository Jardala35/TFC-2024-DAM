package com.tfc.v1.controlador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
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

	private String delimiter = ",";

	// Guarda la lista original de productos
	private ObservableList<Producto> productosOriginales;

	
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
				e.printStackTrace();
			}
		});

		if (!esUsuarioAdmin()) {
			deleteRowButton.setDisable(true);
		}

		// Configurar el botón para añadir filas
		addRowButton.setOnAction(event -> addRow());

		// Configurar el TextField de búsqueda
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			buscarProductos(newValue.trim().toLowerCase());
		});
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
		// Obtener el rol del usuario desde el gestor
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

				tableView2.getColumns().addAll(idColumn, nombreColumn, precioColumn, cantidadColumn, pesoColumn,
						descColumn);

				// Guarda la lista original de productos
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
		// Configurar los valores del nuevo producto, por ejemplo:
		newProducto.setNombre_producto("Nuevo producto");
		newProducto.setValor_producto_unidad(0.0);
		newProducto.setCantidad(0);
		newProducto.setPeso(0.0);
		newProducto.setDescripcion("Descripción del nuevo producto");

		// Agregar el nuevo producto a la lista observable de la tabla
		tableView2.getItems().add(newProducto);

		// Insertar el nuevo producto en la base de datos
		gestor.insertarProducto(newProducto);
	}

	@FXML
	public void deleteSelectedRow() {
		// Obtener la fila seleccionada
		Producto selectedProduct = tableView2.getSelectionModel().getSelectedItem();

		// Verificar si se ha seleccionado una fila
		if (selectedProduct != null) {
			// Eliminar la fila de la tabla
			tableView2.getItems().remove(selectedProduct);

			// Eliminar la fila de la base de datos
			gestor.eliminarProducto(selectedProduct.getId());
		}
	}

	private void logout(ActionEvent event) throws IOException {
		abrirNuevaVentana(event, "/vistas/ini_sesion.fxml");
	}

	private void abrirNuevaVentana(ActionEvent event, String fxmlPath) throws IOException {
		Parent root = springFXMLLoader.load(fxmlPath);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		// Obtener el tamaño actual de la pantalla
		double width = stage.getWidth();
		double height = stage.getHeight();

		Scene scene = new Scene(root);

		// Ajustar la nueva pantalla al tamaño actual
		stage.setScene(scene);
		stage.setWidth(width);
		stage.setHeight(height);
		stage.show();
	}

	private void abrirNuevaVentana(MouseEvent event, String fxmlPath) throws IOException {
		Parent root = springFXMLLoader.load(fxmlPath);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		// Obtener el tamaño actual de la pantalla
		double width = stage.getWidth();
		double height = stage.getHeight();

		Scene scene = new Scene(root);

		// Ajustar la nueva pantalla al tamaño actual
		stage.setScene(scene);
		stage.setWidth(width);
		stage.setHeight(height);
		stage.show();
	}

	private void buscarProductos(String searchTerm) {
		// Verificar si el término de búsqueda está vacío
		if (searchTerm.isEmpty()) {
			tableView2.setItems(productosOriginales);
			return;
		}

		// Filtrar los productos según el término de búsqueda
		List<Producto> filteredList = productosOriginales.stream()
				.filter(producto -> producto.getNombre_producto().toLowerCase().contains(searchTerm)
						|| String.valueOf(producto.getId()).contains(searchTerm)
						|| String.valueOf(producto.getValor_producto_unidad()).contains(searchTerm)
						|| String.valueOf(producto.getCantidad()).contains(searchTerm)
						|| String.valueOf(producto.getPeso()).contains(searchTerm)
						|| producto.getDescripcion().toLowerCase().contains(searchTerm))
				.collect(Collectors.toList());

		// Actualizar la tabla con los productos filtrados
		tableView2.setItems(FXCollections.observableArrayList(filteredList));
	}
}
