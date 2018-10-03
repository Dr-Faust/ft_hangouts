package opodolia.ft_hangouts.utils;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

public class RetainedContactData extends Fragment {
	// data object we want to retain
	private Uri 		mImageUri;
	private MaskWatcher mMaskWatcher;

	// this method is only called once for this fragment
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment
		setRetainInstance(true);
	}

	public void setImageUri(Uri mImageUri) {
		this.mImageUri = mImageUri;
	}

	public Uri getImageUri() {
		return mImageUri;
	}

	public void setMaskWatcher(MaskWatcher mMaskWatcher) {
		this.mMaskWatcher = mMaskWatcher;
	}

	public MaskWatcher getMaskWatcher() {
		return mMaskWatcher;
	}
}