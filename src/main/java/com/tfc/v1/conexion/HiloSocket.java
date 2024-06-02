package com.tfc.v1.conexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.http.ResponseEntity;

import com.tfc.v1.auth.AuthResponse;
import com.tfc.v1.auth.LoginRequest;
import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.negocio.Gestor;

public class HiloSocket implements Runnable {
    private Socket socketCliente;
    private Gestor gestor;
    private ColaMovimientos cm;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    private volatile boolean running = true;
    private LinkedBlockingQueue<Movimiento> colaMensajes = new LinkedBlockingQueue<>();
    
    public HiloSocket(Socket socketCliente, Gestor gestor, ColaMovimientos cm) {
        this.socketCliente = socketCliente;
        this.gestor = gestor;
        this.cm = cm;
    }

    public void enviarMovimiento() throws IOException {
//        Movimiento movimiento = gestor.getContRest().getMovimiento(1).getBody();
    	Movimiento movimiento = new Movimiento(0, "tipo 1", LocalDateTime.now().toString());
        System.out.println(movimiento.toString());
        out.writeObject(movimiento);
        out.flush();
        System.out.println("Objeto enviado: " + movimiento);
    }
    
    public void enviarMovimiento2(Movimiento movimiento) throws IOException {
        // Agregar el movimiento a la cola de mensajes
        colaMensajes.add(movimiento);
    }

    public void recibir() {
        // Implementar lógica de recepción si es necesario
    }

    private void handleClientInput() {
        try {
            while (running) {
                // Lógica de manejo de datos de entrada
                recibir();
            }
        } finally {
            stop();
        }
    }

    private void handleClientOutput() {
    	try {
            while (running) {
            	
                if (!cm.getColaMensajes().isEmpty()) {
                    Movimiento movimiento = cm.getColaMensajes().poll();
                    out.writeObject(movimiento);
                    out.flush();
                    System.out.println("Objeto enviado: " + movimiento);
                }
                Thread.sleep(3000); // Ajustar el intervalo según sea necesario
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al enviar datos al cliente: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socketCliente.getInputStream());
            out = new ObjectOutputStream(socketCliente.getOutputStream());
            boolean flag = false;
            do {
                System.out.println("Inicio del hilo de autenticación");
                String[] up = ((String) in.readObject()).split(",");
                System.out.println("Mensaje recibido: " + up[0] + ", " + up[1]);
                try {
                    System.out.println("Intentando autenticación");
                    ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(up[0], up[1]));
                    out.writeObject("y");
                    flag = true;
                    System.out.println("Autenticación exitosa");
                } catch (Exception e) {
                    System.out.println("Fallo de autenticación");
                    out.writeObject("n");
                    flag = false;
                }
            } while (!flag);

            // Hilo para lectura
            Thread inputThread = new Thread(this::handleClientInput);

            // Hilo para escritura
            Thread outputThread = new Thread(this::handleClientOutput);

            inputThread.start();
            outputThread.start();

            // Esperar a que los hilos terminen
            inputThread.join();
            outputThread.join();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Error al manejar el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }
}
