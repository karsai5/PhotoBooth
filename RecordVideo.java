import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class RecordVideo {
    private boolean recording = false;
    private GifSequenceWriter writer;
    private ImageOutputStream output;

    /**
     * Create GIF from webcam, GifSequenceWriter care of Elliot Kroo
     */
    public RecordVideo() {
    }

    public void start() {
        // start creating img from GifSequence Writer
        String home = System.getProperty("user.home");
        File dir = new File(home + "/PhotoBooth/");
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();
        try {
            output = new FileImageOutputStream(new File(dir + "/" + dateFormat.format(date) + ".gif"));
            writer = new GifSequenceWriter(output, 1, 1, true);
            recording = true;
        } catch (Exception e) {
            System.out.println("error creating file..");
        }
    }

    public void addFrame(BufferedImage img) {
        // add image to writer
        synchronized (this) {
            try {
                writer.writeToSequence(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        // finish writing
        synchronized (this) {
            try {
                writer.close();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
