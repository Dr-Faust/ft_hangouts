package opodolia.ft_hangouts.mvp.model.contacts;

import android.os.AsyncTask;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.CompleteCallback;

public class DeleteContactTask extends AsyncTask<Void, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;
	private final long              contactId;

	public DeleteContactTask(CompleteCallback callback, DbHelper dbHelper, long contactId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.contactId = contactId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		dbHelper.getWritableDatabase().delete(TableContacts.TABLE,
			TableContacts.COLUMN.ID + " = " + contactId, null);
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
