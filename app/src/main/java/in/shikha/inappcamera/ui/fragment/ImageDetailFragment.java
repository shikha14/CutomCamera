package in.shikha.inappcamera.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import in.shikha.inappcamera.R;
import in.shikha.inappcamera.constants.AppConstants;
import in.shikha.inappcamera.ui.listener.IFragmentInterface;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class ImageDetailFragment extends Fragment implements View.OnClickListener {

    private IFragmentInterface mCallback;
    private Context mContext;
    private View mFragmentView;

    private ImageView mImageView;
    private Button mDone;
    private Button mDelete;

    private String filePath;
    private Bundle mBundle;

    public ImageDetailFragment() {
        // Required empty public constructor
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

        return inflater.inflate(R.layout.fragment_image_detail, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mFragmentView = getView();
        mImageView= (ImageView) mFragmentView.findViewById(R.id.imageview);
        mDone= (Button) mFragmentView.findViewById(R.id.btnDone);
        mDone.setOnClickListener(this);

        mDelete= (Button) mFragmentView.findViewById(R.id.btnDelete);
        mDelete.setOnClickListener(this);

        mBundle = getArguments();
        if (mBundle != null) {
            filePath = mBundle.getString(AppConstants.KEY_STORED_FILE_NAME);
            setImage();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                mCallback.reTakePicture();
                break;
            case R.id.btnDone:
                mCallback.done(filePath);
                break;
        }
    }


    private void setImage() {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath,
                    options);
            mImageView.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
