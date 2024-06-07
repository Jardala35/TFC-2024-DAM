package com.tfc.v1.negocio.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfc.v1.modelo.entidades.Movimiento;
import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.modelo.entidades.usuario.Usuario;
import com.tfc.v1.modelo.persistencia.RepositorioMovimientos;
import com.tfc.v1.modelo.persistencia.RepositorioProducto;
import com.tfc.v1.modelo.persistencia.RepositorioSeccion;
import com.tfc.v1.modelo.persistencia.RepositorioUsuario;

/**
 * Controlador REST que maneja las solicitudes relacionadas con la gestión de
 * recursos, como secciones, productos y movimientos.
 * 
 * <p>
 * Este controlador proporciona endpoints para realizar operaciones CRUD (Crear,
 * Leer, Actualizar, Eliminar) en los recursos gestionados por la aplicacion.
 * </p>
 * 
 * @author Pablo Navarro Duro 
 * @author Sergio Rubio Núñez 
 */
@RestController
@RequestMapping("/v1")
public class ControladorRest {

	@Autowired
	private RepositorioSeccion repoSeccion;
	@Autowired
	private RepositorioProducto repoProducto;
	@Autowired
	private RepositorioMovimientos repoMovimiento;
	@Autowired
	private RepositorioUsuario repoUsuario;

	/**
	 * Maneja las solicitudes para agregar una nueva sección.
	 * 
	 * @param seccion La sección que se va a agregar.
	 * @return Una respuesta ResponseEntity que contiene la sección agregada y el
	 *         código de estado HTTP correspondiente.
	 */

	@PostMapping(value = "seccion")
	public ResponseEntity<Seccion> altaSeccion(@RequestBody Seccion seccion) {
		repoSeccion.save(seccion);
		return new ResponseEntity<Seccion>(seccion, HttpStatus.CREATED);
	}

	/**
	 * Maneja las solicitudes para obtener una sección por su nombre.
	 * 
	 * @param nombre El nombre de la sección que se desea obtener.
	 * @return Una respuesta ResponseEntity que contiene la sección encontrada y el
	 *         código de estado HTTP correspondiente, o un estado NOT_FOUND si la
	 *         sección no se encuentra.
	 */

	@GetMapping(value = "seccion/{nombre}")
	public ResponseEntity<Seccion> getSeccion(@PathVariable("nombre") String nombre) {
		Seccion seccion = repoSeccion.findByNombre(nombre);
		if (seccion != null) {
			return new ResponseEntity<>(seccion, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para obtener todas las secciones.
	 * 
	 * @return Una respuesta ResponseEntity que contiene la lista de secciones y el
	 *         código de estado HTTP correspondiente.
	 */

	@GetMapping(value = "secciones")
	public ResponseEntity<List<Seccion>> listarSecciones() {
		List<Seccion> listaSecciones = repoSeccion.findAll();
		return new ResponseEntity<List<Seccion>>(listaSecciones, HttpStatus.OK);
	}

	/**
	 * Maneja las solicitudes para actualizar una sección existente.
	 * 
	 * @param id El ID de la sección que se desea actualizar.
	 * @param s  La sección actualizada.
	 * @return Una respuesta ResponseEntity que contiene la sección actualizada y el
	 *         código de estado HTTP correspondiente, o un estado NOT_FOUND si la
	 *         sección no se encuentra.
	 */

	@PutMapping(value = "seccion/{id}")
	public ResponseEntity<Seccion> updateSeccion(@PathVariable("id") int id, @RequestBody Seccion s) {
		s.setId(id);
		Optional<Seccion> sUpdate = repoSeccion.findById(id);
		if (sUpdate.isPresent()) {
			repoSeccion.save(s);
			return new ResponseEntity<Seccion>(s, HttpStatus.OK);
		} else {
			return new ResponseEntity<Seccion>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para agregar un nuevo producto.
	 * 
	 * @param producto El producto que se va a agregar.
	 * @return Una respuesta ResponseEntity que contiene el producto agregado y el
	 *         código de estado HTTP correspondiente.
	 */

	@PostMapping(value = "producto")
	public ResponseEntity<Producto> altaProducto(@RequestBody Producto producto) {
		repoProducto.save(producto);
		return new ResponseEntity<Producto>(producto, HttpStatus.CREATED);
	}

	/**
	 * Maneja las solicitudes para obtener un producto por su ID.
	 * 
	 * @param id El ID del producto que se desea obtener.
	 * @return Una respuesta ResponseEntity que contiene el producto encontrado y el
	 *         código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         producto no se encuentra.
	 */

	@GetMapping(value = "producto/{id}")
	public ResponseEntity<Producto> getProducto(@PathVariable("id") int id) {
		Optional<Producto> p = repoProducto.findById(id);
		return p.map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Maneja las solicitudes para obtener todos los productos.
	 * 
	 * @return Una respuesta ResponseEntity que contiene la lista de productos y el
	 *         código de estado HTTP correspondiente.
	 */

	@GetMapping(value = "productos")
	public ResponseEntity<List<Producto>> listarProductos() {
		List<Producto> listaProductos = repoProducto.findAll();
		return new ResponseEntity<List<Producto>>(listaProductos, HttpStatus.OK);
	}

	/**
	 * Maneja las solicitudes para actualizar un producto existente.
	 * 
	 * @param id El ID del producto que se desea actualizar.
	 * @param p  El producto actualizado.
	 * @return Una respuesta ResponseEntity que contiene el producto actualizado y
	 *         el código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         producto no se encuentra.
	 */

	@PutMapping(value = "producto/{id}")
	public ResponseEntity<Producto> updateProducto(@PathVariable("id") int id, @RequestBody Producto p) {
		p.setId(id);
		Optional<Producto> pUpdate = repoProducto.findById(id);
		if (pUpdate.isPresent()) {
			repoProducto.save(p);
			return new ResponseEntity<Producto>(p, HttpStatus.OK);
		} else {
			return new ResponseEntity<Producto>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para eliminar un producto por su ID.
	 * 
	 * @param id El ID del producto que se desea eliminar.
	 * @return Una respuesta ResponseEntity con el código de estado HTTP
	 *         correspondiente, NO_CONTENT si el producto se eliminó con éxito, o
	 *         NOT_FOUND si el producto no se encuentra.
	 */

	@DeleteMapping(value = "producto/{id}")
	public ResponseEntity<Void> eliminarProducto(@PathVariable("id") int id) {
		if (repoProducto.existsById(id)) {
			repoProducto.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para obtener la cantidad de un producto por su ID.
	 * 
	 * @param id El ID del producto del cual se desea obtener la cantidad.
	 * @return Una respuesta ResponseEntity que contiene la cantidad del producto y
	 *         el código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         producto no se encuentra.
	 */

	@GetMapping(value = "producto/{id}/cantidad")
	public ResponseEntity<Integer> obtenerCantidadProducto(@PathVariable("id") int id) {
		Optional<Producto> productoOptional = repoProducto.findById(id);
		return productoOptional.map(producto -> new ResponseEntity<>(producto.getCantidad(), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Maneja las solicitudes para obtener el precio de un producto por su ID.
	 * 
	 * @param id El ID del producto del cual se desea obtener el precio.
	 * @return Una respuesta ResponseEntity que contiene el precio del producto y el
	 *         código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         producto no se encuentra.
	 */

	@GetMapping(value = "producto/{id}/precio")
	public ResponseEntity<Double> obtenerPrecioProducto(@PathVariable("id") int id) {
		Optional<Producto> productoOptional = repoProducto.findById(id);
		return productoOptional
				.map(producto -> new ResponseEntity<>(producto.getValor_producto_unidad(), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Maneja las solicitudes para agregar un nuevo movimiento.
	 * 
	 * @param movimiento El movimiento que se va a agregar.
	 * @return Una respuesta ResponseEntity que contiene el movimiento agregado y el
	 *         código de estado HTTP correspondiente.
	 */

	@PostMapping(value = "movimiento")
	public ResponseEntity<Movimiento> altaMovimiento(@RequestBody Movimiento movimiento) {
		repoMovimiento.save(movimiento);
		return new ResponseEntity<Movimiento>(movimiento, HttpStatus.CREATED);
	}

	/**
	 * Maneja las solicitudes para obtener un movimiento por su ID.
	 * 
	 * @param id El ID del movimiento que se desea obtener.
	 * @return Una respuesta ResponseEntity que contiene el movimiento encontrado y
	 *         el código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         movimiento no se encuentra.
	 */

	@GetMapping(value = "movimiento/{id}")
	public ResponseEntity<Movimiento> getMovimiento(@PathVariable("id") int id) {
		Optional<Movimiento> m = repoMovimiento.findById(id);
		return m.map(movimiento -> new ResponseEntity<>(movimiento, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Maneja las solicitudes para obtener todos los movimientos.
	 * 
	 * @return Una respuesta ResponseEntity que contiene la lista de movimientos y
	 *         el código de estado HTTP correspondiente.
	 */

	@GetMapping(value = "movimientos")
	public ResponseEntity<List<Movimiento>> listarMovimientos() {
		List<Movimiento> listaMovimientos = repoMovimiento.findAll();
		return new ResponseEntity<List<Movimiento>>(listaMovimientos, HttpStatus.OK);
	}

	/**
	 * Maneja las solicitudes para actualizar un movimiento existente.
	 * 
	 * @param id El ID del movimiento que se desea actualizar.
	 * @param m  El movimiento actualizado.
	 * @return Una respuesta ResponseEntity que contiene el movimiento actualizado y
	 *         el código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         movimiento no se encuentra.
	 */

	@PutMapping(value = "movimiento/{id}")
	public ResponseEntity<Movimiento> updateMovimiento(@PathVariable("id") int id, @RequestBody Movimiento m) {
		m.setId(id);
		Optional<Movimiento> mUpdate = repoMovimiento.findById(id);
		if (mUpdate.isPresent()) {
			repoMovimiento.save(m);
			return new ResponseEntity<Movimiento>(m, HttpStatus.OK);
		} else {
			return new ResponseEntity<Movimiento>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para eliminar un movimiento por su ID.
	 * 
	 * @param id El ID del movimiento que se desea eliminar.
	 * @return Una respuesta ResponseEntity con el código de estado HTTP
	 *         correspondiente, NO_CONTENT si el movimiento se eliminó con éxito, o
	 *         NOT_FOUND si el movimiento no se encuentra.
	 */

	@DeleteMapping(value = "movimiento/{id}")
	public ResponseEntity<Void> eliminarMovimiento(@PathVariable("id") int id) {
		if (repoMovimiento.existsById(id)) {
			repoMovimiento.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Maneja las solicitudes para actualizar varios movimientos.
	 * 
	 * @param movimientos La lista de movimientos que se van a actualizar.
	 * @return Una respuesta ResponseEntity con el código de estado HTTP
	 *         correspondiente, OK si los movimientos se actualizaron con éxito, o
	 *         NOT_FOUND si algún movimiento no se encuentra.
	 */

	@PutMapping(value = "movimientos")
	public ResponseEntity<Void> actualizarMovimientos(@RequestBody List<Movimiento> movimientos) {
		for (Movimiento movimiento : movimientos) {
			Optional<Movimiento> mUpdate = repoMovimiento.findById(movimiento.getId());
			if (mUpdate.isPresent()) {
				repoMovimiento.save(movimiento);
			} else {
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	/**
	 * Maneja las solicitudes para obtener el rol de un usuario por su nombre de
	 * usuario.
	 * 
	 * @param nombreUsuario El nombre de usuario del usuario del cual se desea
	 *                      obtener el rol.
	 * @return Una respuesta ResponseEntity que contiene el rol del usuario y el
	 *         código de estado HTTP correspondiente, o un estado NOT_FOUND si el
	 *         usuario no se encuentra.
	 */

	@GetMapping(value = "usuario/{nombreUsuario}/rol")
	public ResponseEntity<String> obtenerRolUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
		Optional<Usuario> usuarioOptional = repoUsuario.findByNomUsr(nombreUsuario);

		if (usuarioOptional.isPresent()) {
			Usuario usuario = usuarioOptional.get();
			String rol = usuario.getRol().name();
			return new ResponseEntity<>(rol, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Metodo para borrar los registros en las tablas producto y seccion. Usado a la
	 * hora de hacer la configuracion inicial.
	 * 
	 */

	public void truncateTables() {
		repoProducto.deleteAllProductos();
		;
		repoSeccion.deleteAllSecciones();
	}

}