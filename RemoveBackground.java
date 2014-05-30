import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class RemoveBackground implements Filter {
    private int threshold;
    private Color color;
    RemoveBackground(Color c, int threshold) {
        this.threshold = threshold;
        this.color = c;
    }

    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        for (int column = 0; column < img.getHeight(); ++column){
            for (int row = 0; row < img.getWidth(); ++row){
                Color c = new Color(img.getRGB(row, column));
                if (isColor(c)){
                    img.setRGB(row, column, new Color(0, 0, 255).getRGB());
                }
            }
        }
        return img;
    }

    private boolean isColor(Color example) {
        int r = Math.abs(color.getRed() - example.getRed());
        int g = Math.abs(color.getGreen() - example.getGreen());
        int b = Math.abs(color.getBlue() - example.getBlue());
//        System.out.println("R:"+color.getRed()+"-"+example.getRed());
//        System.out.println("G:"+color.getGreen()+"-"+example.getGreen());
//        System.out.println("B:"+color.getBlue()+"-"+example.getBlue());
        int difference = r + g + b;
        if (difference < threshold )
            return true;
        else
            return false;
    }
}
