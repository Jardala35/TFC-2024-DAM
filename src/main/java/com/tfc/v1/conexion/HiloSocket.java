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
import com.tfc.v1.negocio.Gestor;

public class HiloSocket implements Runnable {
	private Socket socketCliente;
	private Gestor gestor;

	public HiloSocket(Socket socketCliente, Gestor gestor) {
		this.socketCliente = socketCliente;
		this.gestor = gestor;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
				PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true);
				ObjectOutputStream outO = new ObjectOutputStream(socketCliente.getOutputStream());
				ObjectInputStream inO = new ObjectInputStream(socketCliente.getInputStream())) {
			Boolean flag = false;
			do {
				System.out.println("inicio hilo");
				String[] up = in.readLine().split(",");
				System.out.println("mensaje pillado");
				try {
					System.out.println("pasa");
					System.out.println(up[0].toString() + " " + up[1].toString());
					ResponseEntity<AuthResponse> re = gestor.getAuthcontroller().login(new LoginRequest(up[0], up[1]));
					out.println("y");
					flag = true;

				} catch (Exception e) {
					System.out.println("no tira");
					out.println("n");

					flag = false;
				}
			} while (!flag);

			// implementar salidas y llegadas

		} catch (IOException e) {
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