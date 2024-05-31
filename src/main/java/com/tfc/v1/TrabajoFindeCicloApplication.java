package com.tfc.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SpringBootApplication(scanBasePackages = "com.tfc.v1")
public class TrabajoFindeCicloApplication extends Application {
    private ConfigurableApplicationContext contexto;
    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);  
    }

    @Override
    public void init() throws Exception {
        contexto = SpringApplication.run(TrabajoFindeCicloApplication.class);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        SpringFXMLLoader springFXMLLoader = contexto.getBean(SpringFXMLLoader.class);
        rootNode = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Stock Maven");
        primaryStage.setResizable(false);
        primaryStage.show();
        
        Image icon = new Image(getClass().getResourceAsStream("/vistas/img/logo.png"));
        primaryStage.getIcons().add(icon);
        
        
        
        primaryStage.setOnCloseRequest(event -> {
        	event.consume();
        	try {
				salir(primaryStage);
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
    }

    @Override
    public void stop() throws Exception {
        contexto.close();
        System.exit(0);
    }
    
    public void salir(Stage stage) throws Exception {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Salir");
    	alert.setHeaderText("¿Seguro que desea salir?");
    	
	    	if(alert.showAndWait().get() == ButtonType.OK) {
    		stop();
    	}
    }
}
