package opodolia.ft_hangouts.mvp.model;

import android.os.AsyncTask;
import opodolia.ft_hangouts.database.ContactTable;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.CompleteCallback;

public class DeleteContactTask extends AsyncTask<Void, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;
	private final long              contactId;

	DeleteContactTask(CompleteCallback callback, DbHelper dbHelper, long contactId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.contactId = contactId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		dbHelper.getWritableDatabase().delete(ContactTable.TABLE,
			ContactTable.COLUMN.ID + " = " + contactId, null);
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
