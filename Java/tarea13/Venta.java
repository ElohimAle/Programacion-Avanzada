package tarea13;

import java.util.Date;

public class Venta extends ItemVenta {
    private int cantidad;
    private Date fecha;

    public Venta(String codigo, String nombre, double precio, int cantidad, Date fecha) {
        super(codigo, nombre, precio);
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    @Override
    public double calcularImpuesto() {
        return this.precio * cantidad * ImpuestoAplicable.IVA;
    }

    @Override
    public String toString() {
        return super.toString() + ", Cantidad: " + cantidad + ", Fecha: " + fecha;
    }
}

