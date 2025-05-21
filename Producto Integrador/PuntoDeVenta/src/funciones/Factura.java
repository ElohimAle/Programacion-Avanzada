package funciones;

import java.util.List;

public class Factura {
    private int id;
    private List<ProductoCantidad> products;
    private double total;
    private String fecha;

    public Factura(List<ProductoCantidad> products, double total, String fecha) {
        this.products = products;
        this.total = total;
        this.fecha = fecha;
    }

    public Factura(int id, List<ProductoCantidad> products, double total, String fecha) {
        this.id = id;
        this.products = products;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public List<ProductoCantidad> getProducts() {
        return products;
    }

    public double getTotal() {
        return total;
    }

    public String getFecha() {
        return fecha;
    }

    public static class ProductoCantidad {
        private Producto producto;
        private int cantidad;

        public ProductoCantidad(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public double getSubtotal() {
            return producto.getPrecio() * cantidad;
        }
    }
}