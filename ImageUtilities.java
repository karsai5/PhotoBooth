import org.bytedeco.javacv.CanvasFrame;

import java.awt.*;
import java.awt.image.*;
import java.nio.Buffer;

/**
 * Created by linuskarsai on 27/05/2014.
 */
public class ImageUtilities {

    public static BufferedImage convolutionFilter(BufferedImage img, Kernel k){
        // Convert image to grayscale
        BufferedImage original = deepCopyImage(img);
        BufferedImage tmp = null;

        BufferedImageOp op = new ConvolveOp(k);
        tmp = op.filter(original, null);

        return tmp;
    }

    public static BufferedImage toGreyscale(BufferedImage img){
        // Convert image to grayscale
        BufferedImage tmp = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g = tmp.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                int newColour = getGreyPixel(new Color(img.getRGB(row, column)));
                tmp.setRGB(row, column, new Color(newColour,newColour,newColour).getRGB());
            }
        }
        return tmp;
    }

    private static BufferedImage deepCopyImage(BufferedImage bi) {
        BufferedImage copyofImage =
                new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyofImage.createGraphics();
        g.drawImage(bi, 0, 0, null);
        return copyofImage;
    }

    public static int getGreyPixel(Color c){
        int newColour = (int)(
                c.getRed() * 0.299 +
                        c.getGreen() * 0.587 +
                        c.getBlue() * 0.114
        );
        return newColour;
    }

    public static Color getTwoTonePixel(Color pixelColor, int threshold, Color foreground, Color background){
        int greyInt = getGreyPixel(pixelColor);
        if (greyInt > threshold){
            return foreground;
        } else
            return background;
    }

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

    public static void printHistogram(BufferedImage img) {
        System.out.println(getHistogram(img));
    }

    public static BufferedImage contrast(BufferedImage img, float scaleFactor, float brightness) {
        BufferedImage tmp = deepCopyImage(img);
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, brightness, null);
        rescaleOp.filter(tmp,tmp);
        return tmp;
    }

}
