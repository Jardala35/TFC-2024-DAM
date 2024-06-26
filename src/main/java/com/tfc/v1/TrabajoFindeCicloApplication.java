package com.tfc.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.tfc.v1.conexion.SocketServer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 * Clase principal, inicializa el contexto de spring y muestra la ventana del login
 * @author Pablo Navarro Duro 
 * @author Sergio Rubio Núñez 
 */
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

        SocketServer socketServer = contexto.getBean(SocketServer.class);
        new Thread(socketServer).start();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        SpringFXMLLoader springFXMLLoader = contexto.getBean(SpringFXMLLoader.class);
        rootNode = springFXMLLoader.load("/vistas/ini_sesion.fxml");
        primaryStage.setScene(new Scene(rootNode));
        primaryStage.setTitle("Stock Maven");
        primaryStage.setResizable(false);
        primaryStage.setWidth(620);
        primaryStage.setHeight(660);
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
    /**
     * 
     * @param stage
     * @throws Exception
     * 
     * metodo para cerrar la aplicación.
     * se pide confirmacion atraves de una alerta
     */
    public void salir(Stage stage) throws Exception {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Salir");
    	alert.setHeaderText("¿Seguro que desea salir?");
    	
    	if (alert.showAndWait().get() == ButtonType.OK) {
    	    stop();
    	}
    }
}
