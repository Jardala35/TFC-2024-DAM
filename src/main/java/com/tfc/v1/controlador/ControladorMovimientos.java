package com.tfc.v1.controlador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Movimiento;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
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
import javafx.util.converter.LocalDateTimeStringConverter;

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

    @FXML
    private Button btnAtras;
    @FXML
    private Label lblusr;
    @FXML
    private MenuButton menuBtn;
    @FXML
    private MenuItem menuItem1 = new MenuItem();
    @FXML
    private Button exportarBtn = new Button();
    @FXML
    private Button addRowButton = new Button();
    @FXML
    private VBox vbox_ini;
    @FXML
    private TableView<Movimiento> tableView2;
    @FXML
    private TableView<Producto> tblprod;
    @FXML
    private TableView<Producto> tblmov;
    @FXML
    private TableView<Movimiento> tblhistmov;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button deleteRowButton = new Button();
    @FXML
    private TextField searchTextField;

    private String delimiter = ",";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        
        if (!esUsuarioAdmin()) {
            deleteRowButton.setDisable(true);
        }

        addRowButton.setOnAction(event -> addRow());

        exportarBtn.setOnAction(event -> exportarCSV2(event));

        // Cargar movimientos
        //cargarMovimientos();

        // Configuración del TextField de búsqueda
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarMovimientos(newValue.trim().toLowerCase());
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
    
    public void altaMovimiento() {
    	try {
			showScrollPane("/vistas/panel_mov_tblprod.fxml");
			confTabla();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void movimientoPendiente() {
    	try {
			showScrollPane("/vistas/panel_mov_tblmov.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void historico() {
    	try {
			showScrollPane("/vistas/panel_mov_hist.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unchecked")
    private void confTabla() {
    	List<Producto> productos = gestor.getContRest().listarProductos().getBody();
    	
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
        tblprod.setEditable(false);

        tblprod.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
        
        ObservableList<Producto> items = FXCollections.observableArrayList(productos);
        tblprod.setItems(items);
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

                TableColumn<Movimiento, LocalDateTime> fechaAltaColumn = new TableColumn<>("Fecha Alta");
                fechaAltaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
                fechaAltaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter(formatter, null)));
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
        newMovimiento.setFecha_alta(LocalDateTime.now());
        newMovimiento.setTipo("Nuevo tipo");

        tableView2.getItems().add(newMovimiento);

        gestor.insertarMovimiento(newMovimiento);
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
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
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
        return String.valueOf(m.getId()) + delimiter + m.getFecha_alta().format(formatter) + delimiter + m.getTipo();
    }

    private void buscarMovimientos(String searchText) {
        ObservableList<Movimiento> movimientos = tableView2.getItems();
        if (searchText == null || searchText.isEmpty()) {
            tableView2.setItems(movimientosOriginales);
        } else {
            List<Movimiento> filteredList = movimientosOriginales.stream()
                    .filter(movimiento -> contieneTexto(movimiento, searchText))
                    .collect(Collectors.toList());
            tableView2.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    private boolean contieneTexto(Movimiento movimiento, String searchText) {
        return String.valueOf(movimiento.getId()).toLowerCase().contains(searchText)
                || movimiento.getFecha_alta().format(formatter).toLowerCase().contains(searchText)
                || movimiento.getTipo().toLowerCase().contains(searchText);
    }
}
