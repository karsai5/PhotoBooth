import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class RecordVideo {
    private boolean recording = false;
    private IMediaWriter writer;
    private long startTime = -1;

    public RecordVideo(int width, int height) {
        String home = System.getProperty("user.home");
        File dir = new File(home + "/PhotoBooth/");
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        Date date = new Date();
        writer = ToolFactory.makeWriter(dir + "/" + dateFormat.format(date) + ".gif");
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_GIF,
                width, height);
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void addFrame(BufferedImage img) {
        BufferedImage bgrImg = convertToType(img,
                BufferedImage.TYPE_3BYTE_BGR);
        // encode the video
        writer.encodeVideo(0, bgrImg, System.nanoTime() - startTime,
                TimeUnit.NANOSECONDS);
    }

    public void stop() {
        writer.close();
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(
                    sourceImage.getWidth(),
                    sourceImage.getHeight(),
                    targetType
            );
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
}
