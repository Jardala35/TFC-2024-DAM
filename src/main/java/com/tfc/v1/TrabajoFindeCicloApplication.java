package com.tfc.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class TrabajoFindeCicloApplication extends Application{
	private ConfigurableApplicationContext contexto;
	private Parent rootNode;
	public static void main(String[] args) {
		launch();
		SpringApplication.run(TrabajoFindeCicloApplication.class, args);
	}	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		contexto = SpringApplication.run(TrabajoFindeCicloApplication.class);
	    FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/ini_sesion.fxml"));	
	    fxmlloader.setControllerFactory(contexto::getBean);
	    rootNode = fxmlloader.load();
		primaryStage.setScene(new Scene(rootNode));
		primaryStage.setTitle("Stock Maven");
		primaryStage.show();
	}
	@Override
	public void stop() throws Exception {
		contexto.close();
		System.exit(0);
	}
}
