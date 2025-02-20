package tarea13;

import java.io.*;
import java.util.List;

public class PersistenciaDatos<T> {
    public void guardarCSV(String archivo, List<T> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (T item : lista) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarJSON(String archivo, List<T> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write("[");
            for (int i = 0; i < lista.size(); i++) {
                writer.write(lista.get(i).toString());
                if (i < lista.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
