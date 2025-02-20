package tarea13;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Inventario inventario = new Inventario();
        PuntoDeVenta puntoDeVenta = new PuntoDeVenta(inventario);
        Menu menu = new Menu(inventario, puntoDeVenta);
        
        try {
            menu.mostrarMenu();
        } catch (IOException e) {
            System.out.println("Error al ejecutar el menú: " + e.getMessage());
        }
    }
}