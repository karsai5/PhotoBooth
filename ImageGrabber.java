/**
 * Created by linuskarsai on 27/05/2014.
 */

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

public class ImageGrabber implements Runnable {

    private PhotoBooth photoBooth;
    opencv_core.IplImage image;

    public ImageGrabber(PhotoBooth pa) {
        photoBooth = pa;
    }

    @Override
    public void run() {

        FrameGrabber grabber = new OpenCVFrameGrabber(0);

        float scaler = (float)1;
        grabber.setImageWidth((int) (640 * scaler));
        grabber.setImageHeight((int) (480 * scaler));
        int i = 0;

        try {
            grabber.start();
            opencv_core.IplImage img;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                    cvFlip(img, img, 1);
                    //cvSaveImage((i++) + "-capture.jpg", img);
                    //forward image to hook
                    photoBooth.imageGrabberHook(img.getBufferedImage());
                }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
