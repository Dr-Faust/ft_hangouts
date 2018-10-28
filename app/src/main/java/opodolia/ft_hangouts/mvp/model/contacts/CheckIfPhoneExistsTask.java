package opodolia.ft_hangouts.mvp.model.contacts;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.OnPhoneExistsCallback;

public class CheckIfPhoneExistsTask extends AsyncTask<Void, Void, Boolean> {

//	private static String           TAG = CheckIfPhoneExistsTask.class.getSimpleName();

	private final OnPhoneExistsCallback callback;
	private final DbHelper              dbHelper;
	private final String                phoneNumber;

	public CheckIfPhoneExistsTask(OnPhoneExistsCallback callback, DbHelper dbHelper, String phoneNumber) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		Boolean exists = false;
		String phoneNumberFromDb;
			Cursor cursor = dbHelper.getReadableDatabase().query(TableContacts.TABLE, null,
			null, null, null, null, null);
		while (cursor.moveToNext()) {
			phoneNumberFromDb = cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.PHONE_NUMBER));
			phoneNumberFromDb = phoneNumberFromDb.replaceAll("[^0-9]", "");
			if (phoneNumber.equals(phoneNumberFromDb)) {
				exists = true;
				break;
			}
		}
		cursor.close();
		return exists;
	}

	@Override
	protected void onPostExecute(Boolean exists) {
		if (callback != null) {
			callback.onPhoneExists(exists);
		}
	}
}
