import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public interface Filter {
    BufferedImage apply(BufferedImage img, Object[] options);
}
