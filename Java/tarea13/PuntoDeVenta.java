package tarea13;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PuntoDeVenta {
    private List<Venta> ventas;
    private Inventario inventario;

    public PuntoDeVenta(Inventario inventario) {
        this.ventas = new ArrayList<>();
        this.inventario = inventario;
    }

    public boolean realizarVenta(String codigo, int cantidad) {
        Producto producto = inventario.buscarProducto(codigo);
        if (producto != null && producto.reducirStock(cantidad)) {
            Venta venta = new Venta(codigo, producto.getNombre(), producto.getPrecio(), cantidad, new Date());
            ventas.add(venta);
            return true;
        }
        return false;
    }

    public List<Venta> listarVentas() {
        return ventas;
    }
}
