package com.tfc.v1.negocio.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfc.v1.modelo.entidades.Producto;
import com.tfc.v1.modelo.entidades.Seccion;
import com.tfc.v1.modelo.persistencia.RepositorioProducto;
import com.tfc.v1.modelo.persistencia.RepositorioSeccion;

@RestController
@RequestMapping("/v1")
public class ControladorRest {
	@Autowired
	private RepositorioSeccion repoSeccion;
	@Autowired
	private RepositorioProducto repoProducto;

	@PostMapping(value = "seccion")
	public ResponseEntity<Seccion> altaSeccion(@RequestBody Seccion seccion) {
		repoSeccion.save(seccion);
		return new ResponseEntity<Seccion>(seccion, HttpStatus.CREATED);
	}

	@PostMapping(value = "producto")
	public ResponseEntity<Producto> altaProducto(@RequestBody Producto producto) {
		repoProducto.save(producto);
		return new ResponseEntity<Producto>(producto, HttpStatus.CREATED);
	}

	@GetMapping(value = "seccion/{nombre}")
	public ResponseEntity<Seccion> getSeccion(@PathVariable("nombre") String nombre) {
		Optional<Seccion> s = Optional.of(repoSeccion.findByNombre(nombre));
		if (s.isPresent()) {
			return new ResponseEntity<Seccion>(s.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Seccion>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "secciones")
	public ResponseEntity<List<Seccion>> listarSecciones() {
		List<Seccion> listaSecciones = repoSeccion.findAll();
		return new ResponseEntity<List<Seccion>>(listaSecciones, HttpStatus.OK);
	}

	@GetMapping(value = "producto/{id}")
	public ResponseEntity<Producto> getProducto(@PathVariable("id") int id) {
		Optional<Producto> p = repoProducto.findById(id);
		if (p.isPresent()) {
			return new ResponseEntity<Producto>(p.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Producto>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "productos")
	public ResponseEntity<List<Producto>> listarProductos() {
		List<Producto> listaProductos = repoProducto.findAll();
		return new ResponseEntity<List<Producto>>(listaProductos, HttpStatus.OK);
	}

	@PutMapping(value = "seccion/{id}")
	public ResponseEntity<Seccion> updateSeccion(@PathVariable("id") int id, 
			@RequestBody Seccion s) {
		s.setId(id);
		Optional<Seccion> sUpdate = repoSeccion.findById(id);			
		if (sUpdate.isPresent()) {
			repoSeccion.save(s);
			return new ResponseEntity<Seccion>(s, HttpStatus.OK); // 200 OK
		} else {
			return new ResponseEntity<Seccion>(HttpStatus.NOT_FOUND); // 404 NOT FOUND
		}
	}

	@PutMapping(value = "producto/{id}")
	public ResponseEntity<Producto> updateProducto(@PathVariable("id") int id, 
			@RequestBody Producto p){
		p.setId(id);
		Optional<Producto> pUpdate = repoProducto.findById(id);			
		if (pUpdate.isPresent()) {
			repoProducto.save(p);
			return new ResponseEntity<Producto>(p, HttpStatus.OK); // 200 OK
		} else {
			return new ResponseEntity<Producto>(HttpStatus.NOT_FOUND); // 404 NOT FOUND
		}
	}
	
	public void eliminarProducto(int id) {
	    repoProducto.deleteById(id);
	}
	
	@GetMapping(value = "producto/{id}/cantidad")
    public ResponseEntity<Integer> obtenerCantidadProducto(@PathVariable("id") int id) {
        Optional<Producto> productoOptional = repoProducto.findById(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            int cantidad = producto.getCantidad();
            return new ResponseEntity<>(cantidad, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "producto/{id}/precio")
    public ResponseEntity<Double> obtenerPrecioProducto(@PathVariable("id") int id) {
        Optional<Producto> productoOptional = repoProducto.findById(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            double precio = producto.getValor_producto_unidad();
            return new ResponseEntity<>(precio, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
