package in.shikha.inappcamera.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import in.shikha.inappcamera.R;
import in.shikha.inappcamera.constants.AppConstants;
import in.shikha.inappcamera.ui.customview.Preview;
import in.shikha.inappcamera.ui.listener.IFragmentInterface;
import in.shikha.inappcamera.utils.CameraUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class CameraPreviewFragment extends Fragment {

    private IFragmentInterface mCallback;
    private View mFragmentView;
    private Activity mContext;
    private Preview mPreview;
    private Camera mCamera;


    public CameraPreviewFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (IFragmentInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_preview, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mFragmentView = getView();
        if (CameraUtils.checkCameraHardware(mContext)) {
            initializePreview();
            getCameraInstance();
//            if (mCamera != null) {
//
//            } else {
//                Toast.makeText(mContext, "Unable to find camera !!!", Toast.LENGTH_LONG).show();
//            }
        } else {
            Toast.makeText(mContext, "Unable to find camera !!!", Toast.LENGTH_LONG).show();
        }


    }

    private void getCameraInstance() {
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.startPreview();
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                public void onError(int error, Camera mcamera) {

                    mCamera.release();
                    mCamera = Camera.open();
                    Log.d("Camera died", "error camera");

                }
            });
        }
        if (mCamera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(mContext,
                        Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mPreview.setCamera(mCamera);
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
            Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Inside onShutter");
        }
    };

    public void takeFocusedPicture() {
        mCamera.autoFocus(mAutoFocusCallback);

    }

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Inside Picture Taken raw callback");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Inside Picture Taken jpeg callback");

            File pictureFile = CameraUtils.getOutputMediaFile(AppConstants.MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Error accessing file: " + e.getMessage());
            }

            mCallback.pictureTaken(pictureFile.getPath());
        }
    };


    private void initializePreview() {
        mPreview = new Preview(mContext,
                (SurfaceView) mFragmentView.findViewById(R.id.cameraView));
        FrameLayout frame = (FrameLayout) mFragmentView.findViewById(R.id.preview);
        frame.addView(mPreview);
        mPreview.setKeepScreenOn(true);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takeFocusedPicture();
                } catch (Exception e) {
                    Log.e(AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT, "Exception:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }
}
