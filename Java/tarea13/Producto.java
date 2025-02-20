package tarea13;

public class Producto extends ItemVenta implements ImpuestoAplicable {
    private int stock;

    public Producto(String codigo, String nombre, double precio, int stock) {
        super(codigo, nombre, precio);
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void agregarStock(int cantidad) {
        this.stock += cantidad;
    }

    public boolean reducirStock(int cantidad) {
        if (cantidad > stock) {
            return false;
        }
        this.stock -= cantidad;
        return true;
    }

    @Override
    public double calcularImpuesto() {
        return this.precio * IVA;
    }

    @Override
    public String toString() {
        return super.toString() + ", Stock: " + stock;
    }
}

