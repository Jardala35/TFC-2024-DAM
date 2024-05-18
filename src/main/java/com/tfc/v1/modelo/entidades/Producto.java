package com.tfc.v1.modelo.entidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Entity
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private String nombre_producto;	
	private double peso;
	private double valor_producto_unidad;
	private String descripcion;
	@ManyToOne
	@JoinColumn(name="fk_seccion", referencedColumnName="id")
	private Seccion seccion;
	
	public Producto(int id, String nombre_producto, double peso, double valor_producto_unidad,
			String descripcion) {
		super();
		this.id = id;
		this.nombre_producto = nombre_producto;		
		this.peso = peso;
		this.valor_producto_unidad = valor_producto_unidad;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre_producto() {
		return nombre_producto;
	}

	public void setNombre_producto(String nombre_producto) {
		this.nombre_producto = nombre_producto;
	}
	
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public double getValor_producto_unidad() {
		return valor_producto_unidad;
	}

	public void setValor_producto_unidad(double valor_producto_unidad) {
		this.valor_producto_unidad = valor_producto_unidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public ObservableList<Producto> getTabla() {
        ObservableList<Producto> productos = FXCollections.observableArrayList();

        String url = "jdbc:mysql://database-tfc.c7ueouasy3yg.us-east-1.rds.amazonaws.com/StockMaven";
        String user = "admin";
        String password = "TFCdam2024";
        
        String query = "SELECT id, nombre_producto, peso, valor_producto_unidad, descripcion FROM producto";
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombreProducto = resultSet.getString("nombre_producto");
                double peso = resultSet.getDouble("peso");
                double valorProductoUnidad = resultSet.getDouble("valor_producto_unidad");
                String descripcion = resultSet.getString("descripcion");
                productos.add(new Producto(id, nombreProducto, peso, valorProductoUnidad, descripcion));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return productos;
    }
}
