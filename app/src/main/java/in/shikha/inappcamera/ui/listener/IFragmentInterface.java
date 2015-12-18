package in.shikha.inappcamera.ui.listener;

import java.io.File;

/**
 * Created by shikha on 18/12/15.
 */
public interface IFragmentInterface {
    void pictureTaken(String filePath);

    void reTakePicture();

    void done(String filePath);
}
