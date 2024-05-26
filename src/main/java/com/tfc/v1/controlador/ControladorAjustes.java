package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.negocio.Gestor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

@Component
public class ControladorAjustes implements Initializable {

	@Autowired
	private Gestor gestor;
	@Autowired
	private SpringFXMLLoader springFXMLLoader;

	@FXML
	private Button btnAtras;
	@FXML
	private Button btnguardar_seccion;
	@FXML
	private Label lblusr;
	@FXML
	private MenuButton menuBtn;
	@FXML
	private MenuItem menuItem1;
	@FXML
	private ImageView logo;
	@FXML
	private TableView<Producto> tblProductos;
	@FXML
	private TableView<Producto> tblProductos2;
	@FXML
	private TableView<Producto> tblprodsec;
	@FXML
	private TextArea txtarea_secciones;

	@FXML
	private AnchorPane anch1;
	@FXML
	private AnchorPane anch2;

	private File file;
	private String delimiter = ",";

	@FXML
	private VBox vbox_ini;

	@FXML
	private VBox vbox_conf_ini;
	
	@FXML
	private ComboBox<Seccion> cbxseccion;
	
	// Guarda la lista original de productos
	private ObservableList<Producto> productosOriginales;

	private void showPaneinScroll(String fxmlPath) throws IOException {
		Parent panel = springFXMLLoader.load(fxmlPath);
		// Remueve el último hijo del VBox si es un ScrollPane

//        VBox.setVgrow(panel, Priority.ALWAYS);
		// Agrega el nuevo ScrollPane
		if (vbox_conf_ini.getChildren().size() == 4) {
			vbox_conf_ini.getChildren().remove(3);
		}
		vbox_conf_ini.getChildren().add(panel);

	}

	@FXML
	public void mostrarTabla(ActionEvent event) {
		try {
			showPaneinScroll("/vistas/panel_conf_tabla.fxml");
			archivosSubir(event);
			this.tblProductos.setEditable(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void mostrarTabla2(ActionEvent event) {
		try {
			showPaneinScroll("/vistas/panel_conf_tabla2.fxml");
			this.tblProductos2.setEditable(true);
			confTabla();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void confTabla() {
//    	TableColumn<Producto, Integer> idColumn = new TableColumn<>("ID");
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        idColumn.setOnEditCommit(event -> {
//            Producto producto = event.getRowValue();
//            producto.setId(event.getNewValue());
//        });

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

		tblProductos2.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
	}

	@FXML
	public void commitearCambios(ActionEvent event) {
		gestor.getContRest().truncateTables();
		guardarSeccion();
		ObservableList<Producto> productos = tblProductos.getItems();
		for (Producto producto : productos) {
			gestor.getContRest().altaProducto(producto);
		}
		try {
			showScrollPane("/vistas/panel_conf_relsecprod.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void commitearCambios2(ActionEvent event) {
		gestor.getContRest().truncateTables();
		guardarSeccion();
		ObservableList<Producto> productos = tblProductos2.getItems();
		for (Producto producto : productos) {
			gestor.getContRest().altaProducto(producto);
		}
		try {
			showScrollPane("/vistas/panel_conf_relsecprod.fxml");
		} catch (IOException e) {
			e.printStackTrace();
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
		// Configurar los valores del nuevo producto, por ejemplo:

		// Agregar el nuevo producto a la lista observable de la tabla
		tblProductos.getItems().add(newProducto);

	}

	@FXML
	public void deleteSelectedRow() {
		// Obtener la fila seleccionada
		Producto selectedProduct = tblProductos.getSelectionModel().getSelectedItem();

		// Verificar si se ha seleccionado una fila
		if (selectedProduct != null) {
			// Eliminar la fila de la tabla
			tblProductos.getItems().remove(selectedProduct);

		}
	}

	@FXML
	public void addRow2() {
		Producto newProducto = new Producto();
		// Configurar los valores del nuevo producto, por ejemplo:
		newProducto.setNombre_producto("Nuevo producto");
		newProducto.setValor_producto_unidad(0.0);
		newProducto.setCantidad(0);
		newProducto.setPeso(0.0);
		newProducto.setDescripcion("Descripción del nuevo producto");
		// Configurar los valores del nuevo producto, por ejemplo:

		// Agregar el nuevo producto a la lista observable de la tabla
		tblProductos2.getItems().add(newProducto);

	}

	@FXML
	public void deleteSelectedRow2() {
		// Obtener la fila seleccionada
		Producto selectedProduct = tblProductos2.getSelectionModel().getSelectedItem();

		// Verificar si se ha seleccionado una fila
		if (selectedProduct != null) {
			// Eliminar la fila de la tabla
			tblProductos2.getItems().remove(selectedProduct);

			// Eliminar la fila de la base de datos
//            gestor.eliminarProducto(selectedProduct.getId());
		}
	}

	private void showScrollPane(String fxmlPath) throws IOException {
		Parent panel = springFXMLLoader.load(fxmlPath);

		// Remueve el último hijo del VBox si es un ScrollPane
		if (vbox_ini.getChildren().size() == 1 && vbox_ini.getChildren().get(0) instanceof Parent) {
			vbox_ini.getChildren().remove(0);
		}
		// Agrega el nuevo ScrollPane
		vbox_ini.getChildren().add(panel);

	}

	public void confInicial() {
		try {
			showScrollPane("/vistas/panel_conf_ini.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void confUsuarios() {
		try {
			showScrollPane("/vistas/panel_usuarios.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void salir() {
		vbox_ini.getChildren().remove(0);
	}

//	@FXML
//	public void guardarSalir(ActionEvent e) {
//		commitearCambios();
//	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Configuración del MenuButton
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
//        VBox.setVgrow(vbox_ini, Priority.ALWAYS);

	}

	private void logout(ActionEvent event) throws IOException {
		Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
		Stage stage = (Stage) menuBtn.getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void archivosSubir(ActionEvent e) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecciona los archivos a subir");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
		List<File> archivos = fileChooser.showOpenMultipleDialog(null);

		if (archivos != null && !archivos.isEmpty()) {
			file = archivos.get(0);
			try {
				CSVTableView2();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void CSVTableView2() throws IOException {
		tblProductos.getColumns().clear();
		tblProductos.getItems().clear();
		int key = 1;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(delimiter);
				if (firstLine) {
					// Crear columnas
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

					tblProductos.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn,
							descColumn);
					firstLine = false;
				} else {
					// Crear instancia de Producto y agregarla a la tabla
					Producto producto = new Producto(key, data[0], // Nombre
							Double.parseDouble(data[1]), // Precio
							Integer.parseInt(data[2]), // Cantidad
							Double.parseDouble(data[3]), // Peso
							data[4] // Descripcion
							, null);
					tblProductos.getItems().add(producto);
					key++;
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
	public void archivosSubirSeccion(ActionEvent e) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecciona los archivos a subir");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));
		List<File> archivos = fileChooser.showOpenMultipleDialog(null);

		if (archivos != null && !archivos.isEmpty()) {
			file = archivos.get(0);
			try {
				cargarCSVenTextArea();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void cargarCSVenTextArea() throws IOException {
		StringBuilder contenido = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				contenido.append(line).append("\n");
			}
		}
		txtarea_secciones.setText(contenido.toString());
	}

	@FXML
	public void guardarSeccion() {
		if (!txtarea_secciones.getText().equals("")) {
			String texto = txtarea_secciones.getText();
			String[] lineas = texto.split("\n");
			for (String string : lineas) {
				gestor.getContRest().altaSeccion(new Seccion(string));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	public void cargarProductos() {
		List<Producto> productos = gestor.getContRest().listarProductos().getBody();
		List<Seccion> secciones = gestor.getAllSecciones();  // Method to get all sections
		ObservableList<Seccion> seccionesObservableList = FXCollections.observableArrayList(secciones);

		tblprodsec.setEditable(true);

		if (productos != null) {
			tblprodsec.getColumns().clear();
			tblprodsec.getItems().clear();
			if (!productos.isEmpty()) {
				TableColumn<Producto, Seccion> seccionColumn = new TableColumn<>("Seccion");
				seccionColumn.setCellValueFactory(new PropertyValueFactory<>("seccion"));
				seccionColumn.setCellFactory(new Callback<TableColumn<Producto, Seccion>, TableCell<Producto, Seccion>>() {
				    @Override
				    public TableCell<Producto, Seccion> call(TableColumn<Producto, Seccion> param) {
				        return new TableCell<Producto, Seccion>() {
				            private ComboBox<Seccion> comboBox;

				            @Override
				            protected void updateItem(Seccion item, boolean empty) {
				                super.updateItem(item, empty);
				                if (empty || item == null) {
				                    setGraphic(null);
				                } else {
				                    if (comboBox == null) {
				                        comboBox = new ComboBox<>(cbxseccion.getItems());
				                    }
				                    comboBox.setValue(item);
				                    setGraphic(comboBox);
				                    comboBox.setOnAction(event -> {
				                        Producto producto = getTableView().getItems().get(getIndex());
				                        producto.setSeccion(comboBox.getValue());
				                    });
				                }
				            }
				        };
				    }
				});

				// Add the column to your TableView
				tblprodsec.getColumns().add(seccionColumn);
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

				tblprodsec.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn,
						descColumn);

				// Guarda la lista original de productos
				productosOriginales = FXCollections.observableArrayList(productos);

				ObservableList<Producto> items = FXCollections.observableArrayList(productos);
				tblprodsec.setItems(items);
			}
		}
	}

}
