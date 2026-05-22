package vista;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

public class ImageUtils {
    public static BufferedImage resizeImage(String imagePath, int width, int height) throws IOException {
        // Cargar la imagen original
        BufferedImage originalImage = ImageIO.read(VentanaPrincipal.class.getResource(imagePath));

        // Crear una nueva imagen con transparencia
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public static ImageIcon loadIcon(String imagePath, int width, int height) {
        BufferedImage image = loadImage(imagePath);
        if (image == null && !"/resources/producto.png".equals(imagePath)) {
            image = loadImage("/resources/producto.png");
        }
        if (image == null) {
            return new ImageIcon();
        }

        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static BufferedImage loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            imagePath = "/resources/producto.png";
        }

        try {
            URL resource = VentanaPrincipal.class.getResource(imagePath);
            if (resource != null) {
                return ImageIO.read(resource);
            }

            for (File candidate : getCandidateFiles(imagePath)) {
                if (candidate.exists()) {
                    return ImageIO.read(candidate);
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private static List<File> getCandidateFiles(String imagePath) {
        String relativePath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        List<File> candidates = new ArrayList<>();

        File current = new File(System.getProperty("user.dir")).getAbsoluteFile();
        while (current != null) {
            candidates.add(new File(current, relativePath));
            candidates.add(new File(new File(current, "bin"), relativePath));
            candidates.add(new File(new File(current, "src"), relativePath));
            candidates.add(new File(new File(current, "Producto Integrador/PuntoDeVenta/bin"), relativePath));
            candidates.add(new File(new File(current, "Producto Integrador/PuntoDeVenta/src"), relativePath));
            current = current.getParentFile();
        }

        return candidates;
    }
}
