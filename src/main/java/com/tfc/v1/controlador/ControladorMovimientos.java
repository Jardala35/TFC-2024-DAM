package com.tfc.v1.controlador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.conexion.ColaLeeMovimientos;
import com.tfc.v1.conexion.ColaMovimientos;
import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.negocio.Gestor;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

@Component
public class ControladorMovimientos implements Initializable {
	@Autowired
	private Gestor gestor;
	@FXML
	private Stage stage;
	@FXML
	private Scene scene;
	@Autowired
	private SpringFXMLLoader springFXMLLoader;
	@Autowired
	private ColaMovimientos cm;
	@Autowired
	private ColaLeeMovimientos clm;
	@FXML
	private Button btnAtras;
	@FXML
	private Label lblusr;
	@FXML
	private MenuButton menuBtn;
	@FXML
	private MenuItem menuItem1 = new MenuItem();
	@FXML
	private VBox vbox_ini;
	@FXML
	private TableView<Movimiento> tableView2;
	@FXML
	private TableView<Producto> tblprod;
	@FXML
	private TableView<Producto> tblprod1;
	@FXML
	private TableView<Producto> tblprodmov;
	@FXML
	private TableView<Movimiento> tblhistmov;
	@FXML
	private TableView<Movimiento> tblmovpend;
	@FXML
	private ScrollPane scrollPane;

	private String delimiter = ",";

	// Guarda la lista original de movimientos
	private ObservableList<Movimiento> movimientosOriginales;

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
	}

	private void showScrollPane(String fxmlPath) throws IOException {
		Parent panel = springFXMLLoader.load(fxmlPath);

		// Remueve el último hijo del VBox si es un ScrollPane
		if (vbox_ini.getChildren().size() == 1 && vbox_ini.getChildren().get(0) instanceof Parent) {
			vbox_ini.getChildren().removeAll(vbox_ini.getChildrenUnmodifiable());
		}
		// Agrega el nuevo ScrollPane
		vbox_ini.getChildren().add(panel);

	}

	public void enviarMovimiento() {
		List<Producto> listaProductos = new ArrayList<>();

		for (Producto producto : tblprod1.getItems()) {
			listaProductos.add(producto);
		}
		Movimiento mov = new Movimiento("salida", true, LocalDateTime.now().toString(), listaProductos);
		gestor.getContRest().altaMovimiento(mov);

		this.cm.setMovimiento(mov);

	}

	public void altaMovimiento() {
		try {
			showScrollPane("/vistas/panel_mov_tblprod.fxml");
			confTabla(tblprod);
			confTabla1();
			tblprod.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			tblprod.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Producto>) change -> {
				while (change.next()) {
					if (change.wasAdded()) {
						for (Producto producto : change.getAddedSubList()) {
							if (!tblprod1.getItems().contains(producto)) {
								tblprod1.getItems().add(producto);
							}
						}
					}
					if (change.wasRemoved()) {
						for (Producto producto : change.getRemoved()) {
							tblprod1.getItems().remove(producto);
						}
					}
				}
			});
			List<Producto> productos = gestor.getContRest().listarProductos().getBody();
			ObservableList<Producto> items = FXCollections.observableArrayList(productos);
			tblprod.setItems(items);
			tblprod1.setEditable(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void movimientoPendiente() {
		try {
			showScrollPane("/vistas/panel_mov_tblmov.fxml");

			// Configuración de la tabla tblmovpend
			tblmovpend.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			// Listener para detectar cambios en la selección de la tabla tblmovpend
			tblmovpend.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					// Cuando se selecciona un nuevo movimiento, cargamos los productos asociados
					cargarProductosAsociados(newSelection);
				}
			});

			// Cargar movimientos pendientes en la tabla tblmovpend
			cargarMovimientosPendientes();

		} catch (IOException e) {
			e.printStackTrace();
		}

		LinkedBlockingQueue<Movimiento> movimientos = clm.getColaMensajes();
		if (!movimientos.isEmpty()) {
			ObservableList<Movimiento> movimientosObservableList = FXCollections.observableArrayList(movimientos);
			tblmovpend.setItems(movimientosObservableList);

			// Definir las columnas de la tabla tblmovpend
			TableColumn<Movimiento, Integer> idColumn = new TableColumn<>("ID");
			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

			TableColumn<Movimiento, String> descripcionColumn = new TableColumn<>("Tipo");
			descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));

			TableColumn<Movimiento, String> fechaColumn = new TableColumn<>("Fecha");
			fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));

			tblmovpend.getColumns().setAll(idColumn, descripcionColumn, fechaColumn);
		}
	}

	private void cargarProductosAsociados(Movimiento movimiento) {
		if (movimiento == null) {
			return;
		}

		List<Producto> productos = movimiento.getProductos();
		List<Seccion> secciones = gestor.getAllSecciones();
		ObservableList<Seccion> seccionesObservableList = FXCollections.observableArrayList(secciones);

		if (productos != null && !productos.isEmpty()) {
			tblprodmov.getColumns().clear();
			tblprodmov.getItems().clear();

			// Columna de Sección
			TableColumn<Producto, Seccion> seccionColumn = new TableColumn<>("Sección");
			seccionColumn.setCellValueFactory(new PropertyValueFactory<>("seccion"));
			seccionColumn.setCellFactory(param -> new TableCell<Producto, Seccion>() {
				private ComboBox<Seccion> comboBox;

				{
					comboBox = new ComboBox<>(seccionesObservableList);
					comboBox.setConverter(new StringConverter<Seccion>() {
						@Override
						public String toString(Seccion seccion) {
							return seccion != null ? seccion.getNombre_seccion() : "";
						}

						@Override
						public Seccion fromString(String string) {
							return null;
						}
					});
					comboBox.setOnAction(event -> {
						Producto producto = getTableView().getItems().get(getIndex());
						producto.setSeccion(comboBox.getValue());
					});
				}

				@Override
				protected void updateItem(Seccion item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
					} else {
						comboBox.setValue(item);
						setGraphic(comboBox);
					}
				}
			});
			
			seccionColumn.setOnEditCommit(event -> {
                TableView.TableViewSelectionModel<Producto> selectionModel = tblprodmov.getSelectionModel();
                Producto producto = selectionModel.getSelectedItem();
                if (producto != null) {
                    producto.setSeccion(event.getNewValue());
                }
            });

			tblprodmov.getColumns().add(seccionColumn);

			// Otras columnas
			TableColumn<Producto, Integer> idColumn = new TableColumn<>("ID");
			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
			tblprodmov.getColumns().add(idColumn);

			TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
			nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));
			tblprodmov.getColumns().add(nombreColumn);

			TableColumn<Producto, Double> cantidadColumn = new TableColumn<>("Valor Unidad");
			cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));
			cantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // Usamos DoubleStringConverter
			cantidadColumn.setOnEditCommit(event -> {
			    Producto producto = event.getRowValue();
			    producto.setValor_producto_unidad(event.getNewValue());
			});
			tblprodmov.getColumns().add(cantidadColumn);

			TableColumn<Producto, String> caducidadColumn = new TableColumn<>("Cantidad");
			caducidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
			tblprodmov.getColumns().add(caducidadColumn);

			tblprodmov.setItems(FXCollections.observableArrayList(productos));
			tblprodmov.setEditable(true);
		}
	}

	@FXML
	public void historico() {
		try {
			showScrollPane("/vistas/panel_mov_hist.fxml");
			cargarHistoricoMovimientos();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void confTabla(TableView<Producto> table) {
		TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
		nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));

		TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
		precioColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));

		TableColumn<Producto, Integer> cantidadColumn = new TableColumn<>("Cantidad");
		cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

		TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
		pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));

		TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
		descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

		// Configurar la tabla para que no sea editable
		table.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
		table.setEditable(false);
	}

	@SuppressWarnings("unchecked")
	private void confTabla1() {
		TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
		nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));

		TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
		precioColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));

		TableColumn<Producto, Integer> cantidadColumn = new TableColumn<>("Cantidad");
		cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
		cantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		cantidadColumn.setOnEditCommit(event -> {
			Producto producto = event.getRowValue();
			producto.setCantidad(event.getNewValue());
			// Aquí puedes añadir la lógica para actualizar el producto en tu backend
		});

		TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
		pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));

		TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
		descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

		tblprod1.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
		tblprod1.setEditable(true); // Hacer que la tabla en general sea editable
	}

	private void logout(ActionEvent event) throws IOException {
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean esUsuarioAdmin() {
		// Obtener el rol del usuario desde el gestor
		String rol = gestor.obtenerRolUsuario(ControladorMainWindow.usuario).toLowerCase();
		return "admin".equals(rol);
	}

	@FXML
	public void abrirVentanaprincipal(MouseEvent event) throws IOException {
		Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.hide();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.show();
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void cargarMovimientos() {
		List<Movimiento> movimientos = gestor.getContRest().listarMovimientos().getBody();

		tableView2.setEditable(true);

		if (movimientos != null) {
			tableView2.getColumns().clear();
			tableView2.getItems().clear();
			if (!movimientos.isEmpty()) {
				TableColumn<Movimiento, Integer> idColumn = new TableColumn<>("ID");
				idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
				idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
				idColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setId(event.getNewValue());
				});

				TableColumn<Movimiento, String> fechaAltaColumn = new TableColumn<>("Fecha Alta");
				fechaAltaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
				fechaAltaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				fechaAltaColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setFecha(event.getNewValue());
				});

				TableColumn<Movimiento, String> tipoColumn = new TableColumn<>("Tipo");
				tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
				tipoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				tipoColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setTipo(event.getNewValue());
				});

				tableView2.getColumns().addAll(idColumn, fechaAltaColumn, tipoColumn);

				// Guarda la lista original de movimientos
				movimientosOriginales = FXCollections.observableArrayList(movimientos);

				ObservableList<Movimiento> items = FXCollections.observableArrayList(movimientos);
				tableView2.setItems(items);
			}
		}
	}

	@FXML
	public void commitearCambios(ActionEvent event) {
		ObservableList<Movimiento> movimientos = tableView2.getItems();
		for (Movimiento movimiento : movimientos) {
			gestor.actualizarMovimiento(movimiento.getId(), movimiento);
		}
	}

	@FXML
	public void addRow() {
		Movimiento newMovimiento = new Movimiento();
		newMovimiento.setFecha(LocalDateTime.now().toString());
		newMovimiento.setTipo("Nuevo tipo");

		tableView2.getItems().add(newMovimiento);

		gestor.insertarMovimiento(newMovimiento);

		Movimiento movimiento = new Movimiento();
		tblmovpend.getItems().add(movimiento);
	}

	@FXML
	public void deleteSelectedRow() {
		Movimiento selectedMovimiento = tableView2.getSelectionModel().getSelectedItem();

		if (selectedMovimiento != null) {
			tableView2.getItems().remove(selectedMovimiento);
			gestor.eliminarMovimiento(selectedMovimiento.getId());
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
				for (TableColumn<Movimiento, ?> column : tableView2.getColumns()) {
					bw.write(column.getText() + delimiter);
				}
				bw.newLine();

				for (Movimiento row : tableView2.getItems()) {
					bw.write(getMovimientoStringValues(row, delimiter));
					bw.newLine();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private String getMovimientoStringValues(Movimiento m, String delimiter) {
		return String.valueOf(m.getId()) + delimiter + m.getFecha() + delimiter + m.getTipo();
	}

	private void buscarMovimientos(String searchText) {
		ObservableList<Movimiento> movimientos = tableView2.getItems();
		if (searchText == null || searchText.isEmpty()) {
			tableView2.setItems(movimientosOriginales);
		} else {
			List<Movimiento> filteredList = movimientosOriginales.stream()
					.filter(movimiento -> contieneTexto(movimiento, searchText)).collect(Collectors.toList());
			tableView2.setItems(FXCollections.observableArrayList(filteredList));
		}
	}

	private boolean contieneTexto(Movimiento movimiento, String searchText) {
		return String.valueOf(movimiento.getId()).toLowerCase().contains(searchText)
				|| movimiento.getFecha().toLowerCase().contains(searchText)
				|| movimiento.getTipo().toLowerCase().contains(searchText);
	}

	@SuppressWarnings("unchecked")
	private void confTabla_llegadas(Producto movimientoSeleccionado) {
		// List<Producto> productos = movimientoSeleccionado.getProductos();

		tblprodmov.getColumns().clear();

		TableColumn<Producto, String> nombreColumn = new TableColumn<>("Nombre");
		nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre_producto"));

		TableColumn<Producto, Double> precioColumn = new TableColumn<>("Precio");
		precioColumn.setCellValueFactory(new PropertyValueFactory<>("valor_producto_unidad"));

		TableColumn<Producto, Integer> cantidadColumn = new TableColumn<>("Cantidad");
		cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

		TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
		pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));

		TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
		descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

		tblprodmov.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);

		// Establecer los elementos de la tabla con la lista de productos asociados al
		// movimiento
		// ObservableList<Producto> items =
		// FXCollections.observableArrayList(productos);
		// tblmov.setItems(items);
	}

	@FXML
	public void logoToMenu(MouseEvent event) throws IOException {
		Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.show();
	}

	@SuppressWarnings("unchecked")
	private void cargarHistoricoMovimientos() {
		List<Movimiento> movimientos = gestor.getContRest().listarMovimientos().getBody();

		tblhistmov.setEditable(true);

		if (movimientos != null) {
			tblhistmov.getColumns().clear();
			tblhistmov.getItems().clear();
			if (!movimientos.isEmpty()) {
				TableColumn<Movimiento, Integer> idColumn = new TableColumn<>("ID");
				idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
				idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
				idColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setId(event.getNewValue());
				});

				TableColumn<Movimiento, String> fechaAltaColumn = new TableColumn<>("Fecha Alta");
				fechaAltaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
				fechaAltaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				fechaAltaColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setFecha(event.getNewValue());
				});

				TableColumn<Movimiento, String> tipoColumn = new TableColumn<>("Tipo");
				tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
				tipoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				tipoColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setTipo(event.getNewValue());
				});

				tblhistmov.getColumns().addAll(idColumn, fechaAltaColumn, tipoColumn);

				// Guarda la lista original de movimientos
				movimientosOriginales = FXCollections.observableArrayList(movimientos);

				ObservableList<Movimiento> items = FXCollections.observableArrayList(movimientos);
				tblhistmov.setItems(items);
			}

		}

	}
	@SuppressWarnings("unchecked")
	private void cargarMovimientosPendientes() {
		LinkedBlockingQueue<Movimiento> colaMensajes = clm.getColaMensajes();
        List<Movimiento> movimientos = new ArrayList<>(colaMensajes);
		if (!movimientos.isEmpty()) {
			ObservableList<Movimiento> movimientosObservableList = FXCollections.observableArrayList(movimientos);
            tblmovpend.setItems(movimientosObservableList);

			// Definir las columnas de la tabla tblmovpend
			TableColumn<Movimiento, Integer> idColumn = new TableColumn<>("ID");
			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

			TableColumn<Movimiento, String> descripcionColumn = new TableColumn<>("Tipo");
			descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));

			TableColumn<Movimiento, String> fechaColumn = new TableColumn<>("Fecha");
			fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));

			tblmovpend.getColumns().setAll(idColumn, descripcionColumn, fechaColumn);
		}
	}
	
	@FXML
	public void subirProductoPendienteBBDD() {
		 List<Producto> productosPendientes = tblprodmov.getItems();

		    // Verificar si hay productos pendientes
		    if (productosPendientes.isEmpty()) {
		        System.out.println("No hay productos pendientes para subir a la base de datos.");
		        return;
		    }

		    // Iterar sobre los productos pendientes y subirlos a la base de datos
		    for (Producto producto : productosPendientes) {
		        try {
		            // Lógica para subir el producto a la base de datos utilizando el gestor
		            gestor.getContRest().altaProducto(producto);
		            System.out.println("Producto subido a la base de datos: " + producto.getNombre_producto());
		        } catch (Exception e) {
		            System.out.println("Error al subir el producto a la base de datos: " + producto.getNombre_producto());
		            e.printStackTrace();
		        }
		    }

		    // Limpiar la tabla de productos pendientes después de subirlos a la base de datos
		    productosPendientes.clear();

}
}
