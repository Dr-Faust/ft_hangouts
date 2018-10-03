package opodolia.ft_hangouts.mvp.model;

import android.content.ContentValues;
import android.os.AsyncTask;
import opodolia.ft_hangouts.database.ContactTable;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.CompleteCallback;

public class EditContactTask extends AsyncTask<ContentValues, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;
	private final long              contactId;

	EditContactTask(CompleteCallback callback, DbHelper dbHelper, long contactId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.contactId = contactId;
	}

	@Override
	protected Void doInBackground(ContentValues... params) {
		ContentValues cvContact = params[0];
		dbHelper.getWritableDatabase().update(ContactTable.TABLE, cvContact,
			ContactTable.COLUMN.ID + " = ?", new String[]{String.valueOf(contactId)});
		dbHelper.close();
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		if (callback != null) {
			callback.onComplete();
		}
	}
}