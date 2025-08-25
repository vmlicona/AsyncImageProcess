package com.example.imageprocessor.util;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
public class ImageUtil {

    public static byte[] resizeImage(byte[] originalImage, int maxWidth, int maxHeight, String contentType) {
        try {
            // Convert byte array to BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(originalImage);
            BufferedImage image = ImageIO.read(bais);

            if (image == null) {
                throw new RuntimeException("Failed to decode image");
            }

            log.info("Original dimensions: {}x{}", image.getWidth(), image.getHeight());

            // Only resize if necessary
            if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
                BufferedImage resized = Scalr.resize(image, Scalr.Method.QUALITY,
                        Scalr.Mode.AUTOMATIC, maxWidth, maxHeight);
                log.info("Resized to: {}x{}", resized.getWidth(), resized.getHeight());
                image = resized;
            }

            // Convert back to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = contentType.replace("image/", "").toUpperCase();
            if (!ImageIO.write(image, format, baos)) {
                // Fallback to JPEG if specified format fails
                ImageIO.write(image, "JPEG", baos);
            }

            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Image processing failed", e);
        }
    }
}