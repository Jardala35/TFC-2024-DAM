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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.conexion.ColaLeeMovimientos;
import com.tfc.v1.conexion.ColaMovimientos;
import com.tfc.v1.conexion.UsuarioConectado;
import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.negocio.Gestor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
/**
 * Controlador para la vista de movimientos.
 * 
 * @author Pablo Navarro Duro 
 * @author Sergio Rubio Núñez 
 * 
 * @see movimientos2.fxml
 * @see panel_mov_hist.fxml
 * @see panel_mov_tblmov.fxml
 * @see panel_mov_tblprod.fxml
 *
 */
@Component
public class ControladorMovimientos implements Initializable {
	@Autowired
	private Gestor gestor;
	@Autowired
	private UsuarioConectado uc;
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
	private Button btnEnvmov;
	@FXML
	private Button btnRecmov;	
	@FXML
	private Button btnewmov;
	@FXML
	private Button btnmovpend;
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
	@FXML
	private ListView<String> listview;

	private String delimiter = ",";

	private ObservableList<Movimiento> movimientosOriginales;
	
	private ObservableList<String> usuariosObservableList;

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
		
		if (!esUsuarioAdmin()) {
			btnewmov.setDisable(true);
			btnmovpend.setDisable(true);
			
			
		}
		
		usuariosObservableList = FXCollections.observableArrayList();

        listview.setItems(usuariosObservableList);
        listview.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            Circle circle = new Circle(5, Color.GREEN);
                            setGraphic(circle);
                        }
                    }
                };
            }
        });

        uc.getListaconect().addListener((MapChangeListener.Change<? extends Integer, ? extends String> change) -> {
            actualizarListaUsuarios();
        });
        
        actualizarListaUsuarios();
    }

    private void actualizarListaUsuarios() {
    	Platform.runLater(() -> {
            Set<String> usuariosConectados = new HashSet<>(uc.getListaconect().values());
            Iterator<String> iterator = usuariosObservableList.iterator();
            while (iterator.hasNext()) {
                String usuarioEnLista = iterator.next();
                if (!usuariosConectados.contains(usuarioEnLista)) {
                    iterator.remove(); 
                }
            }
            for (String usuario : usuariosConectados) {
                if (!usuariosObservableList.contains(usuario)) {
                    usuariosObservableList.add(usuario); 
                }
            }
        });
    }

	private void showScrollPane(String fxmlPath) throws IOException {
		Parent panel = springFXMLLoader.load(fxmlPath);

		if (vbox_ini.getChildren().size() == 1 && vbox_ini.getChildren().get(0) instanceof Parent) {
			vbox_ini.getChildren().removeAll(vbox_ini.getChildrenUnmodifiable());
		}
		vbox_ini.getChildren().add(panel);

	}
	
	private boolean esUsuarioAdmin() {
		String rol = gestor.obtenerRolUsuario(ControladorMainWindow.usuario).toLowerCase();
		return "admin".equals(rol);
	}

	public void enviarMovimiento() {
	    List<Producto> listaProductos = new ArrayList<>();

	    for (Producto producto : tblprod1.getItems()) {
	        listaProductos.add(producto);
	    }
	    
	    	
	    	Movimiento mov = new Movimiento("salida", true, LocalDateTime.now().toString(), listaProductos);
	    	gestor.getContRest().altaMovimiento(mov);
	    	
	    	for (Producto producto : listaProductos) {
	    		try {
	    			ResponseEntity<Producto> response = gestor.getContRest().getProducto(producto.getId());
	    			if (response.getStatusCode() == HttpStatus.OK) {
	    				Producto productoEnBBDD = response.getBody();
	    				if (productoEnBBDD != null) {
	    					System.out.println("Cantidad en BBDD: " + productoEnBBDD.getCantidad());
	    					System.out.println("Cantidad en TableView: " + producto.getCantidad());
	    					int nuevaCantidad = productoEnBBDD.getCantidad() - producto.getCantidad();
	    					if (nuevaCantidad < 0) {
	    						gestor.getContRest().eliminarProducto(productoEnBBDD.getId());
	    						nuevaCantidad = 0;
	    					} else if (nuevaCantidad == 0) {
	    						gestor.getContRest().eliminarProducto(productoEnBBDD.getId());
	    					}
	    					productoEnBBDD.setCantidad(nuevaCantidad);
	    					gestor.getContRest().updateProducto(productoEnBBDD.getId(), productoEnBBDD);
	    				}
	    			}
	    		} catch (Exception e) {
	    			System.out.println("Error al actualizar la cantidad del producto en la base de datos: " + producto.getNombre_producto());
	    			e.printStackTrace();
	    		}
	    	}
	    	
	    	this.cm.setMovimiento(mov);
	    	vbox_ini.getChildren().removeAll(vbox_ini.getChildrenUnmodifiable());
	    
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
			 tblprod1.getItems().addListener((ListChangeListener<Producto>) change -> {
	                if (tblprod1.getItems().isEmpty()) {
	                	btnEnvmov.setDisable(true);
	                } else {
	                	btnEnvmov.setDisable(false);
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

			tblmovpend.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			tblmovpend.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					cargarProductosAsociados(newSelection);
				}
			});
			cargarMovimientosPendientes();
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}

		LinkedBlockingQueue<Movimiento> movimientos = clm.getColaMensajes();
		if (!movimientos.isEmpty()) {
			ObservableList<Movimiento> movimientosObservableList = FXCollections.observableArrayList(movimientos);
			tblmovpend.setItems(movimientosObservableList);

			TableColumn<Movimiento, Integer> idColumn = new TableColumn<>("ID");
			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

			TableColumn<Movimiento, String> descripcionColumn = new TableColumn<>("Tipo");
			descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));

			TableColumn<Movimiento, String> fechaColumn = new TableColumn<>("Fecha");
			fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));

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
						checkAllSectionsFilled();
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
                    checkAllSectionsFilled();
                }
            });

			tblprodmov.getColumns().add(seccionColumn);

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
						 
			checkAllSectionsFilled();
			
			 tblprodmov.getItems().addListener((ListChangeListener<Producto>) change -> {
		            while (change.next()) {
		                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
		                    checkAllSectionsFilled();
		                }
		            }
		        });
		}
	}
	
	private void checkAllSectionsFilled() {
		 Movimiento movimientoSeleccionado = tblmovpend.getSelectionModel().getSelectedItem();

		    if (movimientoSeleccionado == null) {
		        btnRecmov.setDisable(true);
		        return;
		    }

		    boolean allFilled = movimientoSeleccionado.getProductos().stream()
		            .allMatch(producto -> producto.getSeccion() != null);

		    btnRecmov.setDisable(!allFilled);
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
		});

		TableColumn<Producto, Double> pesoColumn = new TableColumn<>("Peso (Kg)");
		pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));

		TableColumn<Producto, String> descColumn = new TableColumn<>("Descripcion");
		descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

		tblprod1.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
		tblprod1.setEditable(true);
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
					movimiento.setFecha_alta(event.getNewValue());
				});

				TableColumn<Movimiento, String> tipoColumn = new TableColumn<>("Tipo");
				tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
				tipoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				tipoColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setTipo(event.getNewValue());
				});

				tableView2.getColumns().addAll(idColumn, fechaAltaColumn, tipoColumn);

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
		newMovimiento.setFecha_alta(LocalDateTime.now().toString());
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
		return String.valueOf(m.getId()) + delimiter + m.getFecha_alta() + delimiter + m.getTipo();
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
					movimiento.setFecha_alta(event.getNewValue());
				});

				TableColumn<Movimiento, String> tipoColumn = new TableColumn<>("Tipo");
				tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
				tipoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				tipoColumn.setOnEditCommit(event -> {
					Movimiento movimiento = event.getRowValue();
					movimiento.setTipo(event.getNewValue());
				});

				tblhistmov.getColumns().addAll(idColumn, fechaAltaColumn, tipoColumn);

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

			TableColumn<Movimiento, String> descripcionColumn = new TableColumn<>("Tipo");
			descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));

			TableColumn<Movimiento, String> fechaColumn = new TableColumn<>("Fecha");
			fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));

			tblmovpend.getColumns().setAll(descripcionColumn, fechaColumn);
		}
		 // hilo para verificar cambios en la cola de movimientos
        new Thread(() -> {
            int previousSize = colaMensajes.size();
            while (true) {
                try {
                    Thread.sleep(1000);
                    int currentSize = colaMensajes.size();
                    if (currentSize != previousSize) {
                        previousSize = currentSize;
                        Platform.runLater(() -> actualizarTablaMovimientos());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        
	}
	private void actualizarTablaMovimientos() {
        LinkedBlockingQueue<Movimiento> colaMensajes = clm.getColaMensajes();
        List<Movimiento> movimientos = new ArrayList<>(colaMensajes);
        ObservableList<Movimiento> movimientosObservableList = FXCollections.observableArrayList(movimientos);
        tblmovpend.setItems(movimientosObservableList);
    }
	
	@FXML
	public void subirProductoPendienteBBDD() {
		 List<Producto> productosPendientes = tblprodmov.getItems();
		 Movimiento movimientoSeleccionado = tblmovpend.getSelectionModel().getSelectedItem();

		    if (productosPendientes.isEmpty()) {
		        System.out.println("No hay productos pendientes para subir a la base de datos.");
		        return;
		    }

		    for (Producto producto : productosPendientes) {
		        try {
		            gestor.getContRest().altaProducto(producto);
		            System.out.println("Producto subido a la base de datos: " + producto.getNombre_producto());
		        } catch (Exception e) {
		            System.out.println("Error al subir el producto a la base de datos: " + producto.getNombre_producto());
		            e.printStackTrace();
		        }
		    } 
		    tblprodmov.getItems().clear();
		    tblmovpend.getItems().remove(movimientoSeleccionado);
		    clm.getColaMensajes().remove(movimientoSeleccionado);

	}
}
