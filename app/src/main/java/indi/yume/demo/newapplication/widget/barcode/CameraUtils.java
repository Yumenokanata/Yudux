package indi.yume.demo.newapplication.widget.barcode;

import android.hardware.Camera;

import java.util.List;

public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        return getCameraInstance(-1);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            if(cameraId == -1) {
                c = Camera.open(); // attempt to get a Camera instance
            } else {
                c = Camera.open(cameraId); // attempt to get a Camera instance
            }
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static boolean isFlashSupported(Camera camera) {
        /* Credits: Top answer at http://stackoverflow.com/a/19599365/868173 */
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();

            if (parameters.getFlashMode() == null) {
                return false;
            }

            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}