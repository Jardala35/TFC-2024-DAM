package com.tfc.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        contexto.close();
        System.exit(0);
    }
}
