package in.shikha.inappcamera.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import in.shikha.inappcamera.R;
import in.shikha.inappcamera.constants.AppConstants;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    Button mCaptureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        mCaptureImage = (Button) findViewById(R.id.captureImage);
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCapture();
            }
        });
    }

    private void performCapture() {
        Intent startCamera = new Intent(MainActivity.this, CameraFragmentActivity.class);
        startActivityForResult(startCamera, AppConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Inside onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == AppConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra(AppConstants.KEY_STORED_FILE_NAME);
                Toast.makeText(getApplicationContext(),
                        "Image captured Sucessfully::" + path, Toast.LENGTH_SHORT)
                        .show();
                Log.i(TAG, "Path::::" + path);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
}
