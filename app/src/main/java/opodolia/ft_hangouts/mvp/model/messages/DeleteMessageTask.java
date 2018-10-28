package opodolia.ft_hangouts.mvp.model.messages;

import android.os.AsyncTask;

import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.database.TableMessages;
import opodolia.ft_hangouts.mvp.model.CompleteCallback;

public class DeleteMessageTask extends AsyncTask<Void, Void, Void> {
	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;
	private final long              messageId;

	public DeleteMessageTask(CompleteCallback callback, DbHelper dbHelper, long messageId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.messageId = messageId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		dbHelper.getWritableDatabase().delete(TableMessages.TABLE,
			TableMessages.COLUMN.ID + " = " + messageId, null);
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
