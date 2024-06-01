package com.tfc.v1;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.persistencia.RepositorioProducto;

@Component
public class SocketServer implements Runnable {

    private ServerSocket serverSocket;

    @Autowired
    private RepositorioProducto productoRepository;

    public SocketServer() {
        try {
            serverSocket = new ServerSocket(12345); // Puerto en el que el servidor escuchará
            System.out.println("Servidor de sockets iniciado en el puerto 12345...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    // Recibir objeto Movimiento del cliente
                    Movimiento movimiento = (Movimiento) in.readObject();
                    System.out.println("Movimiento recibido del cliente: " + movimiento.toString());

                    // Imprimir los productos añadidos al movimiento
                    System.out.println("Productos añadidos al movimiento:");
                    for (Producto producto : movimiento.getProductos()) {
                        System.out.println(producto.toString());
                    }

                    // Enviar respuesta al cliente
                    out.writeObject("Respuesta del servidor: Movimiento recibido y procesado correctamente");
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
