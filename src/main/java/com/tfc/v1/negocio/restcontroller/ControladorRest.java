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

    // Métodos para Sección
    @PostMapping(value = "seccion")
    public ResponseEntity<Seccion> altaSeccion(@RequestBody Seccion seccion) {
        repoSeccion.save(seccion);
        return new ResponseEntity<Seccion>(seccion, HttpStatus.CREATED);
    }

    @GetMapping(value = "seccion/{nombre}")
    public ResponseEntity<Seccion> getSeccion(@PathVariable("nombre") String nombre) {
        Optional<Seccion> s = Optional.of(repoSeccion.findByNombre(nombre));
        return s.map(seccion -> new ResponseEntity<>(seccion, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "secciones")
    public ResponseEntity<List<Seccion>> listarSecciones() {
        List<Seccion> listaSecciones = repoSeccion.findAll();
        return new ResponseEntity<List<Seccion>>(listaSecciones, HttpStatus.OK);
    }

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

    // Métodos para Producto
    @PostMapping(value = "producto")
    public ResponseEntity<Producto> altaProducto(@RequestBody Producto producto) {
        repoProducto.save(producto);
        return new ResponseEntity<Producto>(producto, HttpStatus.CREATED);
    }

    @GetMapping(value = "producto/{id}")
    public ResponseEntity<Producto> getProducto(@PathVariable("id") int id) {
        Optional<Producto> p = repoProducto.findById(id);
        return p.map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> listaProductos = repoProducto.findAll();
        return new ResponseEntity<List<Producto>>(listaProductos, HttpStatus.OK);
    }

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

    @DeleteMapping(value = "producto/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") int id) {
        if (repoProducto.existsById(id)) {
            repoProducto.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "producto/{id}/cantidad")
    public ResponseEntity<Integer> obtenerCantidadProducto(@PathVariable("id") int id) {
        Optional<Producto> productoOptional = repoProducto.findById(id);
        return productoOptional.map(producto -> new ResponseEntity<>(producto.getCantidad(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "producto/{id}/precio")
    public ResponseEntity<Double> obtenerPrecioProducto(@PathVariable("id") int id) {
        Optional<Producto> productoOptional = repoProducto.findById(id);
        return productoOptional.map(producto -> new ResponseEntity<>(producto.getValor_producto_unidad(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Métodos para Movimiento
    @PostMapping(value = "movimiento")
    public ResponseEntity<Movimiento> altaMovimiento(@RequestBody Movimiento movimiento) {
        repoMovimiento.save(movimiento);
        return new ResponseEntity<Movimiento>(movimiento, HttpStatus.CREATED);
    }

    @GetMapping(value = "movimiento/{id}")
    public ResponseEntity<Movimiento> getMovimiento(@PathVariable("id") int id) {
        Optional<Movimiento> m = repoMovimiento.findById(id);
        return m.map(movimiento -> new ResponseEntity<>(movimiento, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "movimientos")
    public ResponseEntity<List<Movimiento>> listarMovimientos() {
        List<Movimiento> listaMovimientos = repoMovimiento.findAll();
        return new ResponseEntity<List<Movimiento>>(listaMovimientos, HttpStatus.OK);
    }

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

    @DeleteMapping(value = "movimiento/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable("id") int id) {
        if (repoMovimiento.existsById(id)) {
            repoMovimiento.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
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
    
    @GetMapping(value = "usuario/{nombreUsuario}/rol")
    public ResponseEntity<String> obtenerRolUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
        Optional<Usuario> usuarioOptional = repoUsuario.findByNomUsr(nombreUsuario);
        
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            String rol = usuario.getRol().name(); // Obtener el nombre del rol
            return new ResponseEntity<>(rol, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    public void truncateTables() {
        repoProducto.deleteAllProductos();;
        repoSeccion.deleteAllSecciones();
    }

}