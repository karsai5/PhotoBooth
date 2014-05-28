/**
 * Created by linuskarsai on 27/05/2014.
 */

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.swing.*;

import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSetCaptureProperty;

public class ImageGrabber implements Runnable {

    private PostureApp postureApp;
    opencv_core.IplImage image;

    public ImageGrabber(PostureApp pa) {
        postureApp = pa;
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
                    postureApp.imageGrabberHook(img.getBufferedImage());

                }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
