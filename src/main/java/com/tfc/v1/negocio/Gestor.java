package com.tfc.v1.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.persistencia.RepositorioProducto;
import com.tfc.v1.modelo.persistencia.RepositorioSeccion;
import com.tfc.v1.negocio.restcontroller.AuthController;
import com.tfc.v1.negocio.restcontroller.ControladorRest;

import java.util.List;

@Service
public class Gestor {
    @Autowired
    private AuthController authcontroller;
    @Autowired
    private ControladorRest contRest;

    public Gestor(AuthController authcontroller, ControladorRest contRest) {
        super();
        this.authcontroller = authcontroller;
        this.contRest = contRest;
        System.out.println("Gestor creado");
    }

    public AuthController getAuthcontroller() {
        return authcontroller;
    }

    public void setAuthcontroller(AuthController authcontroller) {
        this.authcontroller = authcontroller;
    }

    public ControladorRest getContRest() {
        return contRest;
    }

    public void setContRest(ControladorRest contRest) {
        this.contRest = contRest;
    }

    // Métodos para Producto
    public Producto actualizarProducto(int id, Producto producto) {
        return contRest.updateProducto(id, producto).getBody();
    }

    public Producto insertarProducto(Producto producto) {
        return contRest.altaProducto(producto).getBody();
    }

    public void eliminarProducto(int id) {
        contRest.eliminarProducto(id);
    }

    public int obtenerCantidadProducto(int idProducto) {
        return contRest.obtenerCantidadProducto(idProducto).getBody();
    }

    public double obtenerPrecioProducto(int idProducto) {
        return contRest.obtenerPrecioProducto(idProducto).getBody();
    }

    // Métodos para Movimiento
    public List<Movimiento> listarMovimientos() {
        return contRest.listarMovimientos().getBody();
    }

    public Movimiento insertarMovimiento(Movimiento movimiento) {
        return contRest.altaMovimiento(movimiento).getBody();
    }

    public Movimiento actualizarMovimiento(int id, Movimiento movimiento) {
        return contRest.updateMovimiento(id, movimiento).getBody();
    }

    public void eliminarMovimiento(int id) {
        contRest.eliminarMovimiento(id);
    }
    
    public String obtenerRolUsuario(String username) {
        return contRest.obtenerRolUsuario(username).getBody();
    }
    
    
}
