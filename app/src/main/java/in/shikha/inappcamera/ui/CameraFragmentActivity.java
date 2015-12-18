package in.shikha.inappcamera.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import in.shikha.inappcamera.R;
import in.shikha.inappcamera.constants.AppConstants;
import in.shikha.inappcamera.ui.fragment.CameraPreviewFragment;
import in.shikha.inappcamera.ui.fragment.ImageDetailFragment;
import in.shikha.inappcamera.ui.listener.IFragmentInterface;

public class CameraFragmentActivity extends AppCompatActivity implements IFragmentInterface {

    FragmentManager mFragmentManager;
    Context mContext;

//    CameraPreviewFragment mPreviewFragment;
//    ImageDetailFragment mImageDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_fragment);
        initialize(savedInstanceState);
    }

    private void initialize(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        mContext = this;
        if (savedInstanceState == null) {
            startCameraFragment();
        }
    }

    private void startCameraFragment() {
        //if (mPreviewFragment == null)
        CameraPreviewFragment mPreviewFragment = new CameraPreviewFragment();
        mFragmentManager.beginTransaction().
                replace(R.id.fragContainer, mPreviewFragment, AppConstants.TAG_CAMERA_PREVIEW_FRAGMENT).commit();
    }

    private void startImageFragment(String filePath) {
        // if (mImageDetailFragment == null)
        ImageDetailFragment mImageDetailFragment = new ImageDetailFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(AppConstants.KEY_STORED_FILE_NAME, filePath);
        mImageDetailFragment.setArguments(mBundle);
        mFragmentManager.beginTransaction().
                replace(R.id.fragContainer, mImageDetailFragment, AppConstants.TAG_IMAGE_DETAIL_FRAGMENT).commit();
    }

    @Override
    public void pictureTaken(String filePath) {
        Toast.makeText(mContext, "Path:" + filePath, Toast.LENGTH_LONG).show();
        Log.i("CameraFragmentActivity", "File Path::" + filePath);
        startImageFragment(filePath);
    }

    @Override
    public void reTakePicture() {
        startCameraFragment();
    }

    @Override
    public void done(String filePath) {
        Log.i("CameraFragmentActivity", "on Done File Path::" + filePath);
        Intent intent = new Intent();
        intent.putExtra(AppConstants.KEY_STORED_FILE_NAME, filePath);
        setResult(RESULT_OK, intent);
        finish();

    }


}
