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
    private ColaLeeMovimientos clm;
    private UsuarioConectado uc;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int cont = 0;
    
    private volatile boolean running = true;
    
    public HiloSocket(Socket socketCliente, Gestor gestor, ColaMovimientos cm, ColaLeeMovimientos clm, UsuarioConectado uc) {
        this.socketCliente = socketCliente;
        this.gestor = gestor;
        this.cm = cm;
        this.clm = clm;
        this.uc = uc;
    }    


    private void handleClientInput() {
    	try {	
    		while (running) { 
            Object receivedObject = in.readObject();
            if (receivedObject instanceof Movimiento) {
                Movimiento receivedMovimiento = (Movimiento) receivedObject;
                clm.getColaMensajes().add( gestor.getContRest().altaMovimiento(receivedMovimiento).getBody());                  
                System.out.println("Movimiento recibido: " + receivedMovimiento);
            } else {
                System.out.println("Objeto recibido no es un movimiento.");
            }
    		}
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al recibir datos del cliente: " + e.getMessage());
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
                Thread.sleep(3000);
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
            uc.getListaconect().remove(cont);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAuthentication() {
        try {
            boolean flag = false;
            while (!flag) {
                System.out.println("Inicio del hilo de autenticación");
                String[] up = ((String) in.readObject()).split(",");
                System.out.println("Mensaje recibido: " + up[0] + ", " + up[1]);
                try {
                    System.out.println("Intentando autenticación");
                    ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(up[0], up[1]));
                    out.writeObject("y");
                    flag = true;
                    System.out.println("Autenticación exitosa");
                    cont = uc.getListaconect().size();
                    uc.getListaconect().put(cont, up[0]);
                } catch (Exception e) {
                    System.out.println("Fallo de autenticación");
                    out.writeObject("n");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al manejar la autenticación: " + e.getMessage());
            stop();
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socketCliente.getInputStream());
            out = new ObjectOutputStream(socketCliente.getOutputStream());

            handleAuthentication();

            Thread inputThread = new Thread(this::handleClientInput);

            Thread outputThread = new Thread(this::handleClientOutput);

            inputThread.start();
            outputThread.start();

            inputThread.join();
            outputThread.join();

        } catch (IOException | InterruptedException e) {        	
            System.out.println("Error al manejar el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
       
        }
    }
}