import java.awt.image.BufferedImage;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public interface Filter {
    BufferedImage apply(BufferedImage img, Object[] options);
}
