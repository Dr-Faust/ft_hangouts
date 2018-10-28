package opodolia.ft_hangouts.mvp.model.contacts;

import android.content.ContentValues;
import android.os.AsyncTask;

import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.CompleteCallback;

public class AddContactTask extends AsyncTask<ContentValues, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;

	public AddContactTask(CompleteCallback callback, DbHelper dbHelper) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected Void doInBackground(ContentValues... params) {
		ContentValues cvContact = params[0];
		dbHelper.getWritableDatabase().insert(TableContacts.TABLE, null, cvContact);
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
