package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.SpringFXMLLoader;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.modelo.entidades.usuario.Rol;
import com.tfc.v1.modelo.entidades.usuario.Usuario;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

@Component
public class ControladorAjustes implements Initializable {

    @Autowired
    private Gestor gestor;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
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
    private TableView<Usuario> tblusuarios;
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

    private ObservableList<Producto> productosOriginales;

    private void showPaneinScroll(String fxmlPath) throws IOException {
        Parent panel = springFXMLLoader.load(fxmlPath);
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
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void confTabla() {
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
            cargarProductos();
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
            cargarProductos();
        } catch (IOException e) {
            e.printStackTrace();
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
        tblProductos.getItems().add(newProducto);
    }

    @FXML
    public void deleteSelectedRow() {
        Producto selectedProduct = tblProductos.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            tblProductos.getItems().remove(selectedProduct);
        }
    }

    @FXML
    public void deleteSelectedRowUsr() {
        Usuario selectedProduct = tblusuarios.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            tblusuarios.getItems().remove(selectedProduct);
            gestor.getAuthcontroller().getAuthService().getRepositorioUsuario().delete(selectedProduct);
        }
    }

    @FXML
    public void actualizarUsuarios() {
        ObservableList<Usuario> usuariosActualizados = tblusuarios.getItems();
        for (Usuario usuario : usuariosActualizados) {
            gestor.getAuthcontroller().getAuthService().getRepositorioUsuario().save(usuario);
        }
    }

    @FXML
    public void addRow2() {
        Producto newProducto = new Producto();
        newProducto.setNombre_producto("Nuevo producto");
        newProducto.setValor_producto_unidad(0.0);
        newProducto.setCantidad(0);
        newProducto.setPeso(0.0);
        newProducto.setDescripcion("Descripción del nuevo producto");
        tblProductos2.getItems().add(newProducto);
    }

    @FXML
    public void deleteSelectedRow2() {
        Producto selectedProduct = tblProductos2.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            tblProductos2.getItems().remove(selectedProduct);
        }
    }

    private void showScrollPane(String fxmlPath) throws IOException {
        Parent panel = springFXMLLoader.load(fxmlPath);
        if (vbox_ini.getChildren().size() == 1 && vbox_ini.getChildren().get(0) instanceof Parent) {
            vbox_ini.getChildren().remove(0);
        }
        vbox_ini.getChildren().add(panel);
    }

    public void confInicial() {
        try {
            showScrollPane("/vistas/panel_conf_ini.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void confUsuarios() {
        try {
            showScrollPane("/vistas/panel_usuarios.fxml");
            cargarUsuarios();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salir() {
        vbox_ini.getChildren().remove(0);
    }

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

    private void logout(ActionEvent event) throws IOException {
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

                    tblProductos.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);
                    firstLine = false;
                } else {
                    Producto producto = new Producto(key, data[0], Double.parseDouble(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]), data[4], null);
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
        List<Seccion> secciones = gestor.getAllSecciones();
        ObservableList<Seccion> seccionesObservableList = FXCollections.observableArrayList(secciones);
        List<String> nomsec = new ArrayList<>();
        for (Seccion seccion : seccionesObservableList) {
            nomsec.add(seccion.getNombre_seccion());
        }
        ObservableList<String> nombresSeccion = FXCollections.observableArrayList(nomsec);
        tblprodsec.setEditable(true);
        if (productos != null) {
            tblprodsec.getColumns().clear();
            tblprodsec.getItems().clear();
            if (!productos.isEmpty()) {
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
                            public Seccion fromString(String nombre) {
                                return seccionesObservableList.stream().filter(seccion -> seccion.getNombre_seccion().equals(nombre)).findFirst().orElse(null);
                            }
                        });
                        comboBox.setEditable(false);
                        comboBox.setOnAction(event -> {
                            Seccion selectedSeccion = comboBox.getValue();
                            if (selectedSeccion != null) {
                                commitEdit(selectedSeccion);
                            }
                        });
                    }

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        if (comboBox != null) {
                            comboBox.setValue(getItem());
                        }
                        setGraphic(comboBox);
                        setText(null);
                    }

                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        setGraphic(null);
                        setText(getItem() != null ? getItem().getNombre_seccion() : "");
                    }

                    @Override
                    protected void updateItem(Seccion item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.getNombre_seccion());
                            setGraphic(null);
                        }
                    }
                });

                seccionColumn.setOnEditCommit(event -> {
                    TableView.TableViewSelectionModel<Producto> selectionModel = tblprodsec.getSelectionModel();
                    Producto producto = selectionModel.getSelectedItem();
                    if (producto != null) {
                        producto.setSeccion(event.getNewValue());
                    }
                });

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

                tblprodsec.getColumns().addAll(nombreColumn, precioColumn, cantidadColumn, pesoColumn, descColumn);

                productosOriginales = FXCollections.observableArrayList(productos);

                ObservableList<Producto> items = FXCollections.observableArrayList(productos);
                tblprodsec.setItems(items);
            }
        }
    }

    @FXML
    public void aplicarCambios(ActionEvent event) {
        ObservableList<Producto> productos = tblprodsec.getItems();
        for (Producto producto : productos) {
            gestor.actualizarProducto(producto.getId(), producto);
        }
        try {
            abrirVentanaprincipal(event);
        } catch (IOException e) {
            e.printStackTrace();
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
    public void cargarUsuarios() {
        List<Usuario> usuarios = gestor.getAuthcontroller().getAuthService().getRepositorioUsuario().findAll();
        tblusuarios.setEditable(true);
        if (usuarios != null && !usuarios.isEmpty()) {
            tblusuarios.getColumns().clear();
            tblusuarios.getItems().clear();
            TableColumn<Usuario, Integer> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Usuario, String> nomUsrColumn = new TableColumn<>("Nombre de Usuario");
            nomUsrColumn.setCellValueFactory(new PropertyValueFactory<>("nomUsr"));
            nomUsrColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            nomUsrColumn.setOnEditCommit(event -> {
                Usuario usuario = event.getRowValue();
                usuario.setNomUsr(event.getNewValue());
            });

            TableColumn<Usuario, String> nombreColumn = new TableColumn<>("Nombre");
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            nombreColumn.setOnEditCommit(event -> {
                Usuario usuario = event.getRowValue();
                usuario.setNombre(event.getNewValue());
            });

            TableColumn<Usuario, String> apellidoColumn = new TableColumn<>("Apellido");
            apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
            apellidoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            apellidoColumn.setOnEditCommit(event -> {
                Usuario usuario = event.getRowValue();
                usuario.setApellido(event.getNewValue());
            });

            TableColumn<Usuario, String> emailColumn = new TableColumn<>("Email");
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            emailColumn.setOnEditCommit(event -> {
                Usuario usuario = event.getRowValue();
                usuario.setEmail(event.getNewValue());
            });

            TableColumn<Usuario, Rol> rolColumn = new TableColumn<>("Rol");
            rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
            rolColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RolStringConverter()));
            rolColumn.setOnEditCommit(event -> {
                Usuario usuario = event.getRowValue();
                usuario.setRol(event.getNewValue());
            });

            TableColumn<Usuario, LocalDateTime> fechaAltaColumn = new TableColumn<>("Fecha de Alta");
            fechaAltaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
            fechaAltaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));

            tblusuarios.getColumns().addAll(idColumn, nomUsrColumn, nombreColumn, apellidoColumn, emailColumn, rolColumn, fechaAltaColumn);

            ObservableList<Usuario> items = FXCollections.observableArrayList(usuarios);
            tblusuarios.setItems(items);
        }
    }

    public class RolStringConverter extends StringConverter<Rol> {
        @Override
        public String toString(Rol rol) {
            return rol != null ? rol.name() : "";
        }

        @Override
        public Rol fromString(String string) {
            try {
                return Rol.valueOf(string);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    @FXML
    public void logout_2(MouseEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        Stage stage = (Stage) menuBtn.getScene().getWindow();
        stage.hide();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    @FXML
    public void logoToMenu(MouseEvent event) throws IOException {
        Parent root = springFXMLLoader.load("/vistas/main_wind.fxml");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}
