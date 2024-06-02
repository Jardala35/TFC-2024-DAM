package com.tfc.v1.conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.http.ResponseEntity;

import com.tfc.v1.auth.AuthResponse;
import com.tfc.v1.auth.LoginRequest;
import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.negocio.Gestor;

public class HiloSocket implements Runnable {
	private Socket socketCliente;
	private Gestor gestor;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private volatile boolean running = true;

	public HiloSocket(Socket socketCliente, Gestor gestor) {
		this.socketCliente = socketCliente;
		this.gestor = gestor;
	}
	
	public void enviar() throws IOException {
		out.writeObject(gestor.getContRest().getMovimiento(1).getBody());
		System.out.println("objeto enviado");
	}
	
	public void recibir() {
		
	}
	
	private void handleClientInput() {
        try {
            while (running) {
                
            	
            }
        } finally {
            stop();
        }
    }

    private void handleClientOutput() {
        try {
            while (running) {
                // Implementa la lógica de envío de datos según lo requerido
                enviarMovimientoPeriodicamente();
                Thread.sleep(1000); // Ajusta el intervalo según sea necesario
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al enviar datos al cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void enviarMovimientoPeriodicamente() throws IOException {
        // Enviar un movimiento específico periódicamente
//        out.writeObject(gestor.getContRest().getMovimiento(1).getBody());
//        out.flush();
        System.out.println("objeto enviado");
    }

    public void stop() {
        running = false;
        try {
            socketCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void run() {
		try {
			in = new ObjectInputStream(socketCliente.getInputStream());
			out = new ObjectOutputStream(socketCliente.getOutputStream());
			Boolean flag = false;
			do {
				System.out.println("inicio hilo");
				String[] up = ((String) in.readObject()).split(",");
				System.out.println("mensaje pillado");
				try {
					System.out.println("pasa");
					System.out.println(up[0].toString() + " " + up[1].toString());
					ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(up[0], up[1]));
					out.writeObject("y");
					flag = true;

				} catch (Exception e) {
					System.out.println("no tira");
					out.writeObject("n");

					flag = false;
				}
			} while (!flag);
			
			// Hilo para lectura
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleClientInput();
                }
            }).start();

            // Hilo para escritura
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleClientOutput();
                }
            }).start();
			
			
			
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error al manejar el cliente: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				socketCliente.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}