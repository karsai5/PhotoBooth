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

    private static int getGreyPixel(Color c){
        int newColour = (int)(
                c.getRed() * 0.299 +
                        c.getGreen() * 0.587 +
                        c.getBlue() * 0.114
        );
        return newColour;
    }

    public static BufferedImage toAndyWorhol(BufferedImage img, int threshold){

        Image toolkitImage = img.getScaledInstance(img.getWidth()/2,img.getHeight()/2, Image.SCALE_FAST);
        int width = toolkitImage.getWidth(null);
        int height = toolkitImage.getHeight(null);

        BufferedImage newImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(toolkitImage,0,0,null);
        g.dispose();

        img = newImage;

        //create new twice the size
        BufferedImage tmp = new BufferedImage(img.getWidth() * 2, img.getHeight() * 2,
                BufferedImage.TYPE_INT_ARGB);

        //copy old image and apply effect
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                Color originalColor = new Color(img.getRGB(row,column));

                Color color1 = getTwoTonePixel(originalColor,threshold,new Color(251,255,168),new Color(227,3,125));
                Color color2 = getTwoTonePixel(originalColor,threshold,new Color(236,120,7),new Color(36,32,127));
                Color color3 = getTwoTonePixel(originalColor,threshold,new Color(155,188,47),new Color(230,29,22));
                Color color4 = getTwoTonePixel(originalColor,threshold,new Color(255,255,255),new Color(0,0,0));

                tmp.setRGB(                      //top left
                        row,
                        column,
                        color4.getRGB());

                tmp.setRGB(                     //top right
                        row + tmp.getWidth()/2,
                        column,
                        color1.getRGB());

                tmp.setRGB(                     //bottom left
                        row,
                        column + tmp.getHeight()/2,
                        color2.getRGB());

                tmp.setRGB(                     //bottom right
                        row + tmp.getWidth()/2,
                        column + tmp.getHeight()/2,
                        color3.getRGB());
            }
        }
        return tmp;
    }

    private static Color getTwoTonePixel(Color pixelColor, int threshold, Color foreground, Color background){
        int greyInt = getGreyPixel(pixelColor);
        if (greyInt > threshold){
            return foreground;
        } else
            return background;
    }

    public static BufferedImage edgeDetect(BufferedImage img) {
//        float edgeDetect[] = {
//                0, 1, 0,
//                1, -4, 1,
//                0, 1, 0
//        };
        float edgeDetect[] = {
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        };
        return convolutionFilter(img, new Kernel(3, 3, edgeDetect));
    }

    public static BufferedImage sharpen(BufferedImage img) {
        float sharpen[] = {
                0, 0, 0, 0, 0,
                0, 0, -1, 0, 0,
                0, -1, 5, -1, 0,
                0, 0, -1, 0, 0,
                0, 0, 0, 0, 0
        };
        return convolutionFilter(img, new Kernel(5, 5, sharpen));
    }

    public static BufferedImage emboss(BufferedImage img) {
        float emboss[] = {
                -2, -1, 0,
                -1, 1, 1,
                0, 1, 2
        };
        return convolutionFilter(img, new Kernel(3, 3, emboss));
    }

    public static String getHistogram(BufferedImage img) {
        String output = "";
        img = toGreyscale(img);
        double histogram[] = new double[256];
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                int greyLevel = new Color(img.getRGB(row, column)).getRed();
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
                for (int i = 0; i <= tally; i +=100) {
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
        output += total;
        return output;
    }

    public static void printHistogram(BufferedImage img) {
        System.out.println(getHistogram(img));
    }

    public static BufferedImage blur(BufferedImage img) {
        float emboss[] = {
                1, 1, 1,
                1, 1, 1,
                1, 1, 1
        };
        return convolutionFilter(img, new Kernel(3, 3, emboss));
    }

    public static BufferedImage contrast(BufferedImage img, float scaleFactor, float brightness) {
        BufferedImage tmp = deepCopyImage(img);
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, brightness, null);
        rescaleOp.filter(tmp,tmp);
        return tmp;
    }

}
