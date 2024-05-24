package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.negocio.Gestor;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@Component
public class ControladorAjustes implements Initializable {

    @Autowired
    private Gestor gestor;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    private Button btnAtras;
    @FXML
    private Label lblusr;
    @FXML
    private MenuButton menuBtn;
    @FXML
    private MenuItem menuItem1;   
    @FXML
    private ImageView logo;
    @FXML
    private TableView<String[]> tblProductos;
    @FXML
    private TextArea txtarea_secciones;
    
    private File file;
    private String delimiter = ",";
    
    @FXML
    private VBox vbox_ini;
    
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
    
    public void guardarSalir() {
    	vbox_ini.getChildren().remove(0);
    }


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

    }

    private void logout(ActionEvent event) throws IOException {
    	Parent root = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        Stage stage = (Stage) menuBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public void guardarSeccion() {
//    	gestor.getContRest().altaSeccion(null)
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
                CSVTableView();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void CSVTableView() throws IOException {
    	tblProductos.getColumns().clear();

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
                        tblProductos.getColumns().add(column);
                    }
                    firstLine = false;
                } else {
                	tblProductos.getItems().add(data);
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
}
