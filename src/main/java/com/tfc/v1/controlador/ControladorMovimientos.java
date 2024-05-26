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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Movimiento;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateTimeStringConverter;
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
    private TableView<Movimiento> tableView2;
    @FXML
    private Button deleteRowButton = new Button();

    private String delimiter = ",";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        ImageView imageAtras = new ImageView(new Image("/vistas/img/leftarrow.png"));
        btnAtras.setGraphic(imageAtras);

        addRowButton.setOnAction(event -> addRow());

        exportarBtn.setOnAction(event -> exportarCSV2(event));

        cargarMovimientos();
    }

    private void logout(ActionEvent event) throws IOException {
        try {
            Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
            Stage stage = (Stage) menuBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean esUsuarioAdmin() {
        // Obtener el rol del usuario desde el gestor
        String rol = gestor.obtenerRolUsuario(ControladorMainWindow.usuario);
        System.out.println(gestor.obtenerRolUsuario(ControladorMainWindow.usuario));
        return "admin".equals(rol);
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
}
