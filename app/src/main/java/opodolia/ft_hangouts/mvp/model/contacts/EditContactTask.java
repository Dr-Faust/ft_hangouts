package opodolia.ft_hangouts.mvp.model.contacts;

import android.content.ContentValues;
import android.os.AsyncTask;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.CompleteCallback;

public class EditContactTask extends AsyncTask<ContentValues, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;
	private final long              contactId;

	public EditContactTask(CompleteCallback callback, DbHelper dbHelper, long contactId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.contactId = contactId;
	}

	@Override
	protected Void doInBackground(ContentValues... params) {
		ContentValues cvContact = params[0];
		dbHelper.getWritableDatabase().update(TableContacts.TABLE, cvContact,
			TableContacts.COLUMN.ID + " = ?", new String[]{String.valueOf(contactId)});
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