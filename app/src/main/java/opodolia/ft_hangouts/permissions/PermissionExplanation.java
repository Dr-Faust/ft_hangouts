package opodolia.ft_hangouts.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import opodolia.ft_hangouts.R;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class PermissionExplanation
{
//	private static String               TAG = PermissionExplanation.class.getSimpleName();
	private Context                     context;
	private int							currentDialogStyle;

	public PermissionExplanation(Context context, int currentDialogStyle)
	{
		this.context = context;
		this.currentDialogStyle = currentDialogStyle;
	}

//	public void showLocationExplanationDialog()
//	{
//		AlertDialog.Builder builder = new AlertDialog.Builder(context, currentDialogStyle);
//		builder.setCancelable(false);
//		builder.setTitle(context.getString(R.string.request_location_permission_1_title));
//		builder.setMessage(context.getString(R.string.request_location_permission_1));
//
//		builder.setPositiveButton(context.getString(R.string.str_continue),
//				new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface paramDialogInterface, int paramInt)
//					{
//						Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//						context.startActivity(myIntent);
//					}
//				});
//
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}

	public void showFirstExplanationDialog(final int REQUEST, final String[] PERMISSIONS, String explanation)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context, currentDialogStyle);
		builder.setCancelable(false);
		builder.setMessage(explanation);

		builder.setPositiveButton(context.getString(R.string.str_continue), (dialog, which) -> requestPermissions((Activity)context, PERMISSIONS, REQUEST));

		if (REQUEST != 2)
		{
			builder.setNegativeButton(context.getString(R.string.str_not_now), (dialog, which) -> {
			});
		}

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void showLastExplanationDialog(String explanation)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context, currentDialogStyle);
		builder.setCancelable(false);
		builder.setMessage(explanation);

		builder.setPositiveButton(context.getString(R.string.str_continue), (dialog, which) -> {
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts("package", context.getPackageName(), null);
			intent.setData(uri);
			context.startActivity(intent);
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
