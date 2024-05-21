package com.tfc.v1.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.util.Callback;

@Component
public class ControladorTabla {

    @FXML
    private TableView<String[]> tableView;
    @FXML
    private Button archivos;

    private File file;

    private String delimiter = ",";

    @FXML
    public void archivosSubir(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona los archivos a subir");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        List<File> archivos = fileChooser.showOpenMultipleDialog(null);

        if (archivos != null && !archivos.isEmpty()) {
            // Tomar el primer archivo seleccionado (podrías manejar múltiples archivos de otra manera)
            file = archivos.get(0);
            try {
                CSVTableView();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Manejar la excepción
            }
        }
    }

    @FXML
    public void CSVTableView() throws IOException {
        tableView.getColumns().clear(); 

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
                        // Alinear el contenido al centro
                        column.setCellFactory(getCenteredCellFactory());
                        tableView.getColumns().add(column);
                    }
                    firstLine = false;
                } else {
                    tableView.getItems().add(data);
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
    public void exportarCSV(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (TableColumn<String[], ?> column : tableView.getColumns()) {
                    bw.write(column.getText() + delimiter);
                }
                bw.newLine();

                for (String[] row : tableView.getItems()) {
                    bw.write(String.join(delimiter, row));
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
