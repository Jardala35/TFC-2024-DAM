package com.tfc.v1.conexion;

import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.negocio.Gestor;
/**
 * Clase que implementa un servidor de sockets. Escucha en un puerto específico y maneja conexiones de clientes.
 * Implementa la interfaz Runnable para permitir su ejecución en un hilo separado.
 * 
 * @author Pablo Navarro Duro 
 */
@Component
public class SocketServer implements Runnable {

    private ServerSocket serverSocket;
    @Autowired
    private Gestor gestor;
    @Autowired
    private ColaMovimientos cm;
    @Autowired
    private ColaLeeMovimientos clm;
    @Autowired
    private UsuarioConectado uc;
    /**
     * Constructor de la clase SocketServer. Inicializa el servidor de sockets en el puerto 12345.
     */
    public SocketServer() {
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Servidor de sockets iniciado en el puerto 12345...");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Método principal del hilo que se ejecuta al iniciar el hilo.
     * Acepta conexiones de clientes y lanza un nuevo hilo para manejar cada conexión.
     */
    @Override
    public void run() {
        try {
            while (true) {
            	Socket socketCliente = serverSocket.accept();
				System.out.println("Cliente conectado");
				
				new Thread(new HiloSocket(socketCliente, gestor, cm, clm, uc)).start();
            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
