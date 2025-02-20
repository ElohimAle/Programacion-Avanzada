package tarea13;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu {
    private Inventario inventario;
    private PuntoDeVenta puntoDeVenta;

    public Menu(Inventario inventario, PuntoDeVenta puntoDeVenta) {
        this.inventario = inventario;
        this.puntoDeVenta = puntoDeVenta;
    }

    public void mostrarMenu() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String opcion;
        do {
            System.out.println("Menu Principal:");
            System.out.println("1. Agregar Producto");
            System.out.println("2. Realizar Venta");
            System.out.println("3. Listar Productos");
            System.out.println("4. Listar Ventas");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = reader.readLine();

            switch (opcion) {
                case "1":
                    agregarProducto(reader);
                    break;
                case "2":
                    realizarVenta(reader);
                    break;
                case "3":
                    listarProductos();
                    break;
                case "4":
                    listarVentas();
                    break;
                case "5":
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }
        } while (!opcion.equals("5"));
    }

    private void agregarProducto(BufferedReader reader) throws IOException {
        System.out.print("Código: ");
        String codigo = reader.readLine();
        System.out.print("Nombre: ");
        String nombre = reader.readLine();
        System.out.print("Precio: ");
        double precio = Double.parseDouble(reader.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(reader.readLine());
        Producto producto = new Producto(codigo, nombre, precio, stock);
        inventario.agregarProducto(producto);
        System.out.println("Producto agregado exitosamente.");
    }

    private void realizarVenta(BufferedReader reader) throws IOException {
        System.out.print("Código del producto: ");
        String codigo = reader.readLine();
        System.out.print("Cantidad: ");
        int cantidad = Integer.parseInt(reader.readLine());
        if (puntoDeVenta.realizarVenta(codigo, cantidad)) {
            System.out.println("Venta realizada con éxito.");
        } else {
            System.out.println("No se pudo realizar la venta. Verifique el stock.");
        }
    }

    private void listarProductos() {
        System.out.println("Lista de productos:");
        for (Producto p : inventario.listarProductos()) {
            System.out.println(p);
        }
    }

    private void listarVentas() {
        System.out.println("Lista de ventas:");
        for (Venta v : puntoDeVenta.listarVentas()) {
            System.out.println(v);
        }
    }
}
