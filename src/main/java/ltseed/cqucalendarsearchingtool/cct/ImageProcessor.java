package ltseed.cqucalendarsearchingtool.cct;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

public class ImageProcessor {

    public static void main(String[] args) {
        File dir = new File("C:\\Users\\LtSeed\\Desktop\\新建文件夹 (2)"); // Replace with your directory path
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Check if the file is an image
                if (isImageFile(child)) {
                    try {
                        BufferedImage originalImage = ImageIO.read(child);
                        BufferedImage processedImage = transformImage(originalImage);
                        File output = new File(child.getParent(), "transformed_" + child.getName());
                        ImageIO.write(processedImage, "png", output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

    private static boolean isImageFile(File file) {
        // Check the file extension to determine if it's an image
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    private static BufferedImage transformImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Color cornerColor = getNewColor(Color.BLACK,
                new Color(originalImage.getRGB(width - 1, height - 1), true));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = new Color(originalImage.getRGB(x, y), true);
                Color newColor = getNewColor(cornerColor, originalColor);
                originalImage.setRGB(x, y, newColor.getRGB());
            }
        }

        return originalImage;
    }

    private static Color getNewColor(Color cornerColor, Color originalColor) {
        // Invert the colors
        int redInverted = 255 - originalColor.getRed();
        int greenInverted = 255 - originalColor.getGreen();
        int blueInverted = 255 - originalColor.getBlue();

        // Calculate the differences between the inverted and the corner color
        int diffRed = Math.abs(redInverted - cornerColor.getRed());
        int diffGreen = Math.abs(greenInverted - cornerColor.getGreen());
        int diffBlue = Math.abs(blueInverted - cornerColor.getBlue());

        // Define a similarity threshold
        int threshold = 20; // This is an example value, adjust it based on your needs

        // Check if the inverted color is similar to the corner color
        if (diffRed <= threshold && diffGreen <= threshold && diffBlue <= threshold) {
            return Color.WHITE;
        } else {
            return new Color(redInverted, greenInverted, blueInverted);
        }
    }

}
