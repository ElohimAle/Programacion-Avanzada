package modelo;

import java.util.List;

public class Factura {
    private int id;
    private List<ProductoCantidad> products;
    private double total;
    private double descuento;
    private double totalPagar;
    private String fecha;

    public Factura(List<ProductoCantidad> products, double total, String fecha) {
        this(-1, products, total, 0, total, fecha);
    }

    public Factura(int id, List<ProductoCantidad> products, double total, String fecha) {
        this(id, products, total, 0, total, fecha);
    }

    public Factura(int id, List<ProductoCantidad> products, double total, double descuento, double totalPagar, String fecha) {
        this.id = id;
        this.products = products;
        this.total = total;
        this.descuento = descuento;
        this.totalPagar = totalPagar;
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

    public double getDescuento() {
        return descuento;
    }

    public double getTotalPagar() {
        return totalPagar;
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