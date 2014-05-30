import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class AndyWorhol implements Filter {
    int threshold = -1;
    int thresholdCount = 0;
    @Override
    /**
     * Apply andy wohol filter effect
     */
    public BufferedImage apply(BufferedImage img, Object[] options) {
        int threshold = findThreshold(img);
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

                Color color1 = ImageUtilities.getTwoTonePixel(originalColor, threshold, new Color(251, 255, 168), new Color(227, 3, 125));
                Color color2 = ImageUtilities.getTwoTonePixel(originalColor, threshold, new Color(236, 120, 7), new Color(36, 32, 127));
                Color color3 = ImageUtilities.getTwoTonePixel(originalColor, threshold, new Color(155, 188, 47), new Color(230, 29, 22));
                Color color4 = ImageUtilities.getTwoTonePixel(originalColor, threshold, new Color(255, 255, 255), new Color(0, 0, 0));

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

    /**
     * Find threshold by calculating
     * middle value from histogram
     * @param img as input image
     * @return luminosity value to use as threshold
     */
    private int findThreshold(BufferedImage img) {
        if (thresholdCount%14 == 0 || threshold < 0) {
            this.threshold = 0;
            int threshold = 0;
            int count = 0;
            double histogram[] = ImageUtilities.histogramArray(img);
            int histogramlength = 0;
            for (int i = 0; i < histogram.length; ++i){
                histogramlength += histogram[i];
            }
            int median = (int)(histogramlength/3);

            for (int i = 1; i < histogram.length-1; ++i) {
                threshold = i;
                count += histogram[i];
                if (count >= median)
                    break;
            }
            return threshold;
        } else {
            ++thresholdCount;
            return threshold;
        }
    }
}
