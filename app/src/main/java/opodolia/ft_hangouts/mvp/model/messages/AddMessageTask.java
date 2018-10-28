package opodolia.ft_hangouts.mvp.model.messages;

import android.content.ContentValues;
import android.os.AsyncTask;

import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.database.TableMessages;
import opodolia.ft_hangouts.mvp.model.CompleteCallback;

public class AddMessageTask extends AsyncTask<ContentValues, Void, Void> {

	private final CompleteCallback callback;
	private final DbHelper dbHelper;

	public AddMessageTask(CompleteCallback callback, DbHelper dbHelper) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected Void doInBackground(ContentValues... params) {
		ContentValues cvMessage = params[0];
		dbHelper.getWritableDatabase().insert(TableMessages.TABLE, null, cvMessage);
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
