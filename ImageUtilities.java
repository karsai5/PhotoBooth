import org.bytedeco.javacv.CanvasFrame;

import java.awt.*;
import java.awt.image.*;
import java.nio.Buffer;

/**
 * Created by Linus Karsai (312070209) on 27/05/2014.
 */
public class ImageUtilities {

    /**
     * Apply convolution filter to image
     * @param img input
     * @param k convolution filter
     * @return altered image
     */
    public static BufferedImage convolutionFilter(BufferedImage img, Kernel k){
        // Convert image to grayscale
        BufferedImage original = deepCopyImage(img);
        BufferedImage tmp = null;

        BufferedImageOp op = new ConvolveOp(k);
        tmp = op.filter(original, null);

        return tmp;
    }

    /**
     * Create a deep copy of a buffered image
     * @param bi input image
     * @return copy
     */
    public static BufferedImage deepCopyImage(BufferedImage bi) {
        BufferedImage copyofImage =
                new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyofImage.createGraphics();
        g.drawImage(bi, 0, 0, null);
        return copyofImage;
    }

    /**
     * Get the grey pixel value from colour
     * @param c
     * @return
     */
    public static int getGreyPixel(Color c){
        int newColour = (int)(
                c.getRed() * 0.299 +
                        c.getGreen() * 0.587 +
                        c.getBlue() * 0.114
        );
        return newColour;
    }

    /**
     * Get a two-tone version of the pixel back
     * @param pixelColor input pixel
     * @param threshold value
     * @param foreground colour
     * @param background colour
     * @return output colour
     */
    public static Color getTwoTonePixel(Color pixelColor, int threshold, Color foreground, Color background){
        int greyInt = getGreyPixel(pixelColor);
        if (greyInt > threshold){
            return foreground;
        } else
            return background;
    }

    /**
     * Create an array with the number of each luminosity
     * according to index
     * @param img
     * @return
     */
    public static double[] histogramArray(BufferedImage img) {
        String output = "";
        double histogram[] = new double[256];

        int sensitivity = 5;
        for (int column = 0; column < img.getHeight(); column += sensitivity){
            for (int row = 0; row < img.getWidth(); row += sensitivity){
                int greyLevel = getGreyPixel(new Color(img.getRGB(row, column)));
                ++histogram[greyLevel];
            }
        }
        return histogram;
    }

    /**
     * Get string version of histogram
     * @param img input image
     * @return string
     */
    public static String getHistogram(BufferedImage img) {
        String output = "";
        double histogram[] = new double[256];

        int sensitivity = 5;
        for (int column = 0; column < img.getHeight(); column += sensitivity){
            for (int row = 0; row < img.getWidth(); row += sensitivity){
                int greyLevel = getGreyPixel(new Color(img.getRGB(row, column)));
                ++histogram[greyLevel];
            }
        }
        output += "HISTOGRAM\n";
        double tally = 0;
        int steps = 25;
        int lastStep = 0;
        for (int greyLevel = 0; greyLevel <= 255; ++greyLevel) {
            tally += histogram[greyLevel];
            if ((greyLevel%steps == 0 && greyLevel != 0) || greyLevel == 255) {
                String start = String.format("%03d", lastStep);
                String end = String.format("%03d", greyLevel);
                output += start + "-" + end + ":";
                tally = tally/steps;
                for (int i = 0; i <= tally; i += 100/(sensitivity*sensitivity)) {
                    output += "*";
                }
                output += "\n";
                lastStep = greyLevel+1;
            }
        }
        int total = 0;

        for (int i = 0; i <= 255; ++i){
            total += histogram[i];
        }
        return output;
    }

    /**
     * print histogram to standard output
     * @param img
     */
    public static void printHistogram(BufferedImage img) {
        System.out.println(getHistogram(img));
    }

    /**
     * Apply contrast/brightness changes to image
     * @param img input image
     * @param scaleFactor contrast
     * @param brightness
     * @return output img
     */
    public static BufferedImage contrast(BufferedImage img, float scaleFactor, float brightness) {
        BufferedImage tmp = deepCopyImage(img);
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, brightness, null);
        rescaleOp.filter(tmp,tmp);
        return tmp;
    }

}
