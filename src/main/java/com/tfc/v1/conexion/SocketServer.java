package com.tfc.v1.conexion;

import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.negocio.Gestor;

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

    public SocketServer() {
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Servidor de sockets iniciado en el puerto 12345...");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
