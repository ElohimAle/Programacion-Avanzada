package modelo;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private int cantidad;
    private int stockMinimo;
    private String imagen;

    public Producto(String codigo, String nombre, double precio, int cantidad) {
        this(codigo, nombre, precio, cantidad, 0, "/resources/producto.png");
    }

    public Producto(String codigo, String nombre, double precio, int cantidad, int stockMinimo, String imagen) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.imagen = (imagen == null || imagen.isBlank()) ? "/resources/producto.png" : imagen;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setImagen(String imagen) {
        this.imagen = (imagen == null || imagen.isBlank()) ? "/resources/producto.png" : imagen;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ") - $" + precio + " - Stock: " + cantidad + " - Mínimo: " + stockMinimo;
    }
}