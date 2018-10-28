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
