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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
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
    private TableView<String[]> tblProductos;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }   
    
    @FXML
    public void mostrarTabla2(ActionEvent event) {
    	try {
			showPaneinScroll("/vistas/panel_conf_tabla.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    public void commitearCambios(ActionEvent event) {
//        ObservableList<Producto> productos = tblProductos.getItems();
//        for (Producto producto : productos) {
//            gestor.actualizarProducto(producto.getId(), producto);
//        }
    }

    @FXML
    public void addRow() {
        String[] newProducto = new String[] {"Nuevo producto", "0.0", "0", "0.0", "Descripcion del nuevo producto"};
        // Configurar los valores del nuevo producto, por ejemplo:
        

        // Agregar el nuevo producto a la lista observable de la tabla
        tblProductos.getItems().add(newProducto);       
        
    }
    
    @FXML
    public void deleteSelectedRow() {
        // Obtener la fila seleccionada
        String[] selectedProduct = tblProductos.getSelectionModel().getSelectedItem();
        
        // Verificar si se ha seleccionado una fila
        if (selectedProduct != null) {
            // Eliminar la fila de la tabla
        	tblProductos.getItems().remove(selectedProduct);
            
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
        VBox.setVgrow(anch1, javafx.scene.layout.Priority.ALWAYS);
        AnchorPane.setTopAnchor(anch1, 0.0);
        AnchorPane.setBottomAnchor(anch1, 0.0);
        AnchorPane.setLeftAnchor(anch1, 0.0);
        AnchorPane.setRightAnchor(anch1, 0.0);
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
    public void guardarSeccion(ActionEvent e) {
    	String[] aux = getTextArea();
    	// guardar secciones en la bd
    	
    }
    
    private String[] getTextArea() {
    	String texto = txtarea_secciones.getText();
        // Dividir el texto en líneas y guardarlas en un array
        String[] lineas = texto.split("\n");
        return lineas;
    }
}
