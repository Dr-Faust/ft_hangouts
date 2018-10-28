package opodolia.ft_hangouts.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.Config;

public class PermissionChecker
{
	private Context 	mContext;
	private Activity 	mActivity;
	private PermissionExplanation permissionExplanation;

	public PermissionChecker(Context context, int currentDialogStyle)
	{
		mContext = context;
		mActivity = (Activity) context;
		permissionExplanation = new PermissionExplanation(mContext, currentDialogStyle);
	}

	public void smsPermissionChecker()
	{
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
		{

			boolean requestPermission = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_SMS)
				|| ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.RECEIVE_SMS)
				|| ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.SEND_SMS);

			if (requestPermission)
			{
				String[] PERMISSIONS = {
					Manifest.permission.READ_SMS,
					Manifest.permission.RECEIVE_SMS,
					Manifest.permission.SEND_SMS,
				};
				permissionExplanation.showFirstExplanationDialog(Config.REQUEST_SMS_PERMISSION, PERMISSIONS,
					mContext.getString(R.string.request_sms_permission_1));
			}
			else
				permissionExplanation.showLastExplanationDialog(mContext.getString(R.string.request_sms_permission_2));
		}
	}

	public void callPermissionChecker()
	{
		if (ActivityCompat.checkSelfPermission(mContext,
			Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
		{
			boolean permissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
				mActivity, Manifest.permission.CALL_PHONE);

			if (permissionGranted)
			{
				String[] PERMISSIONS = {
					Manifest.permission.CALL_PHONE
				};
				permissionExplanation.showFirstExplanationDialog(Config.REQUEST_CALL_PERMISSION, PERMISSIONS,
					mContext.getString(R.string.request_call_permission_1));
			}
			else
				permissionExplanation.showLastExplanationDialog(mContext.getString(R.string.request_call_permission_2));
		}
	}

	public void cameraStoragePermissionChecker()
	{
		if (ActivityCompat.checkSelfPermission(mContext,
			Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(mContext,
			Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			boolean permissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
				mActivity, Manifest.permission.CAMERA)
				|| ActivityCompat.shouldShowRequestPermissionRationale(
				mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

			if (permissionGranted)
			{
				String[] PERMISSIONS = {
					Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
				};
				permissionExplanation.showFirstExplanationDialog(Config.REQUEST_CAMERA_STORAGE_PERMISSION,
					PERMISSIONS, mContext.getString(R.string.request_camera_storage_permission_1));
			}
			else
				permissionExplanation.showLastExplanationDialog(mContext.getString(R.string.
					request_camera_storage_permission_2));
		}
		else if (ActivityCompat.checkSelfPermission(mContext,
			Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
		{
			boolean permissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
				mActivity, Manifest.permission.CAMERA);

			if (permissionGranted)
			{
				String[] PERMISSIONS = {
					Manifest.permission.CAMERA
				};
				permissionExplanation.showFirstExplanationDialog(Config.REQUEST_CAMERA_PERMISSION, PERMISSIONS,
					mContext.getString(R.string.request_camera_permission_1));
			}
			else
				permissionExplanation.showLastExplanationDialog(mContext.getString(R.string.
					request_camera_permission_2));
		}
		else if (ActivityCompat.checkSelfPermission(mContext,
			Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
		{
			boolean permissionGranted = ActivityCompat.shouldShowRequestPermissionRationale(
				mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

			if (permissionGranted)
			{
				String[] PERMISSIONS = {
					Manifest.permission.WRITE_EXTERNAL_STORAGE
				};
				permissionExplanation.showFirstExplanationDialog(Config.REQUEST_STORAGE_PERMISSION, PERMISSIONS,
					mContext.getString(R.string.request_storage_permission_1));
			}
			else
				permissionExplanation.showLastExplanationDialog(mContext.getString(R.string.
					request_storage_permission_2));
		}
	}
}
