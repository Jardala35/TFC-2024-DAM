package com.tfc.v1.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.negocio.restcontroller.AuthController;
import com.tfc.v1.negocio.restcontroller.ControladorRest;
/**
 * Clase de servicio que actúa como intermediario entre el controlador y el repositorio,
 * proporcionando métodos para realizar operaciones de negocio relacionadas con la gestión
 * de productos, movimientos y autenticación.
 * 
 * <p>Esta clase contiene métodos para interactuar con los controladores REST.</p>
 * 
 * @author Pablo Navarro Duro 
 * @author Sergio Rubio Núñez 
 */
@Service
public class Gestor {
    @Autowired
    private AuthController authcontroller;
    @Autowired
    private ControladorRest contRest;
    /**
     * Constructor de la clase Gestor.
     * 
     * @param authcontroller Controlador de autenticación REST.
     * @param contRest Controlador REST para la gestión de productos y movimientos.
     */
    public Gestor(AuthController authcontroller, ControladorRest contRest) {
        super();
        this.authcontroller = authcontroller;
        this.contRest = contRest;
        System.out.println("Gestor creado");
    }

    /**
     * Obtiene el controlador de autenticación REST.
     * 
     * @return El controlador de autenticación REST.
     */
    public AuthController getAuthcontroller() {
        return authcontroller;
    }

    /**
     * Establece el controlador de autenticación REST.
     * 
     * @param authcontroller El controlador de autenticación REST a establecer.
     */
    public void setAuthcontroller(AuthController authcontroller) {
        this.authcontroller = authcontroller;
    }

    /**
     * Obtiene el controlador REST para la gestión de productos y movimientos.
     * 
     * @return El controlador REST para la gestión de productos y movimientos.
     */
    public ControladorRest getContRest() {
        return contRest;
    }

    /**
     * Establece el controlador REST para la gestión de productos y movimientos.
     * 
     * @param contRest El controlador REST para la gestión de productos y movimientos a establecer.
     */
    public void setContRest(ControladorRest contRest) {
        this.contRest = contRest;
    }

    // Métodos para la gestión de productos

    /**
     * Actualiza un producto en el sistema.
     * 
     * @param id El ID del producto a actualizar.
     * @param producto El objeto Producto con los datos actualizados.
     * @return El objeto Producto actualizado.
     */
    public Producto actualizarProducto(int id, Producto producto) {
        return contRest.updateProducto(id, producto).getBody();
    }

    /**
     * Inserta un nuevo producto en el sistema.
     * 
     * @param producto El objeto Producto a insertar.
     * @return El objeto Producto insertado.
     */
    public Producto insertarProducto(Producto producto) {
        return contRest.altaProducto(producto).getBody();
    }

    /**
     * Elimina un producto del sistema.
     * 
     * @param id El ID del producto a eliminar.
     */
    public void eliminarProducto(int id) {
        contRest.eliminarProducto(id);
    }

    /**
     * Obtiene la cantidad disponible de un producto.
     * 
     * @param idProducto El ID del producto.
     * @return La cantidad disponible del producto.
     */
    public int obtenerCantidadProducto(int idProducto) {
        return contRest.obtenerCantidadProducto(idProducto).getBody();
    }

    /**
     * Obtiene el precio de un producto.
     * 
     * @param idProducto El ID del producto.
     * @return El precio del producto.
     */
    public double obtenerPrecioProducto(int idProducto) {
        return contRest.obtenerPrecioProducto(idProducto).getBody();
    }

    // Métodos para la gestión de movimientos

    /**
     * Obtiene la lista de todos los movimientos en el sistema.
     * 
     * @return La lista de movimientos.
     */
    public List<Movimiento> listarMovimientos() {
        return contRest.listarMovimientos().getBody();
    }

    /**
     * Inserta un nuevo movimiento en el sistema.
     * 
     * @param movimiento El objeto Movimiento a insertar.
     * @return El objeto Movimiento insertado.
     */
    public Movimiento insertarMovimiento(Movimiento movimiento) {
        return contRest.altaMovimiento(movimiento).getBody();
    }

    /**
     * Actualiza un movimiento existente en el sistema.
     * 
     * @param id El ID del movimiento a actualizar.
     * @param movimiento El objeto Movimiento con los datos actualizados.
     * @return El objeto Movimiento actualizado.
     */
    public Movimiento actualizarMovimiento(int id, Movimiento movimiento) {
        return contRest.updateMovimiento(id, movimiento).getBody();
    }

    /**
     * Elimina un movimiento del sistema.
     * 
     * @param id El ID del movimiento a eliminar.
     */
    public void eliminarMovimiento(int id) {
        contRest.eliminarMovimiento(id);
    }

    // Métodos para la gestión de usuarios y autenticación

    /**
     * Obtiene el rol de un usuario dado su nombre de usuario.
     * 
     * @param username El nombre de usuario del usuario.
     * @return El rol del usuario.
     */
    public String obtenerRolUsuario(String username) {
        return contRest.obtenerRolUsuario(username).getBody();
    }
    
    /**
     * Obtiene la lista de todas las secciones en el sistema.
     * 
     * @return La lista de secciones.
     */
    public List<Seccion> getAllSecciones() {		
		return contRest.listarSecciones().getBody();
	}
    
    
}
