package com.tfc.v1.conexion;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.tfc.v1.modelo.entidades.Movimiento;
/**
 * Componente auxiliar para mantener un control en el contexto de spring de los movimientos recibidos
 * 
 * @author Sergio Rubio Núñez 
 */
@Component
public class ColaLeeMovimientos {
	private Movimiento movimiento;
	private LinkedBlockingQueue<Movimiento> colaMensajes = new LinkedBlockingQueue<>();
	
	public ColaLeeMovimientos() {
		
	}
	
	

	public LinkedBlockingQueue<Movimiento> getColaMensajes() {
		return colaMensajes;
	}



	public void setColaMensajes(LinkedBlockingQueue<Movimiento> colaMensajes) {
		this.colaMensajes = colaMensajes;
	}



	public Movimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
		this.colaMensajes.add(movimiento);
	}
	
	

}

