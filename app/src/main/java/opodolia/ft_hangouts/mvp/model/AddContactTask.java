package opodolia.ft_hangouts.mvp.model;

import android.content.ContentValues;
import android.os.AsyncTask;
import java.util.concurrent.TimeUnit;
import opodolia.ft_hangouts.database.ContactTable;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.CompleteCallback;

public class AddContactTask extends AsyncTask<ContentValues, Void, Void> {

	private final CompleteCallback  callback;
	private final DbHelper          dbHelper;

	AddContactTask(CompleteCallback callback, DbHelper dbHelper) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected Void doInBackground(ContentValues... params) {
		ContentValues cvContact = params[0];
		dbHelper.getWritableDatabase().insert(ContactTable.TABLE, null, cvContact);
		//try {
		//	TimeUnit.SECONDS.sleep(1);
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
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
