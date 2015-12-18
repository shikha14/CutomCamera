package in.shikha.inappcamera.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import in.shikha.inappcamera.R;
import in.shikha.inappcamera.constants.AppConstants;
import in.shikha.inappcamera.ui.customview.Preview;

public class CameraActivity extends AppCompatActivity {
    //   ImageView image;
    Activity context;
    Preview preview;
    Camera camera;
    // Button exitButton;
    // ImageView fotoButton;
    // LinearLayout progressLayout;
    String path = "/sdcard/KutCamera/cache/images/";


    LinearLayout mImageContainer;
    ImageView imageView;
    Button mDone, mDelete;


    String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;

        // fotoButton = (ImageView) findViewById(R.id.imageView_foto);
//        exitButton = (Button) findViewById(R.id.button_exit);
//        image = (ImageView) findViewById(R.id.imageView_photo);
//        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        mImageContainer = (LinearLayout) findViewById(R.id.llImageContainer);
        imageView = (ImageView) findViewById(R.id.imageview);
        mDelete = (Button) findViewById(R.id.delete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
                preview.setVisibility(View.VISIBLE);
                mImageContainer.setVisibility(View.GONE);

            }
        });

        mDone = (Button) findViewById(R.id.done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(AppConstants.KEY_STORED_FILE_NAME, filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        preview = new Preview(this,
                (SurfaceView) findViewById(R.id.cameraView));
        FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(preview);
        preview.setKeepScreenOn(true);

        preview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    takeFocusedPicture();
                } catch (Exception e) {

                }
//                exitButton.setClickable(false);
//                fotoButton.setClickable(false);
//                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.e("TAG", "Inside on destroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.e("TAG", "Inside on resume");
        super.onResume();
        if (camera == null) {
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new Camera.ErrorCallback() {
                public void onError(int error, Camera mcamera) {

                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera");

                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        Camera.CameraInfo.CAMERA_FACING_BACK, camera);
            preview.setCamera(camera);
        }
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            try {
                camera.takePicture(mShutterCallback, null, jpegCallback);
            } catch (Exception e) {

            }

        }
    };

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);

    }

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @SuppressWarnings("deprecation")
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e("TAG", "Picture Taken");
            FileOutputStream outStream = null;
            Calendar c = Calendar.getInstance();
            File videoDirectory = new File(path);

            if (!videoDirectory.exists()) {
                videoDirectory.mkdirs();
            }

            try {
                // Write to SD Card
                filePath = path + c.getTime().getSeconds() + ".jpg";
                outStream = new FileOutputStream(path + c.getTime().getSeconds() + ".jpg");
                outStream.write(data);
                outStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            // releaseCamera();


            Bitmap realImage;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

            options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future


            realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);

//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(path + c.getTime().getSeconds()
//                        + ".jpg");
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            try {
//                Log.d("EXIF value",
//                        exif.getAttribute(ExifInterface.TAG_ORIENTATION));
//                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("1")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("8")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("3")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("0")) {
//                    realImage = rotate(realImage, 90);
//                }
//            } catch (Exception e) {
//
//            }
//
            imageView.setImageBitmap(realImage);
            preview.setVisibility(View.GONE);
            mImageContainer.setVisibility(View.VISIBLE);

//
//
//
//            fotoButton.setClickable(true);
//            camera.startPreview();
//            progressLayout.setVisibility(View.GONE);
//            exitButton.setClickable(true);

        }
    };

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }

    @Override
    protected void onPause() {
        Log.e("TAG", "Inside on Pause");
        super.onPause();
        //releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

}


