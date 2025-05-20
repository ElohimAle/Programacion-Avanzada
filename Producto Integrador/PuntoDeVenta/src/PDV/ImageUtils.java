package PDV;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
}
