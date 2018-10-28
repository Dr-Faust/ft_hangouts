package opodolia.ft_hangouts.mvp.model.messages;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import opodolia.ft_hangouts.common.Message;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.database.TableMessages;
import opodolia.ft_hangouts.mvp.model.messages.callbacks.LoadMessagesCallback;

public class LoadMessagesTask extends AsyncTask<Void, Void, List<Message>> {

	private static String           TAG = LoadMessagesTask.class.getSimpleName();

	private final LoadMessagesCallback  callback;
	private final DbHelper              dbHelper;
	private final String                phoneNumber;

	public LoadMessagesTask(LoadMessagesCallback callback, DbHelper dbHelper, String phoneNumber) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.phoneNumber = phoneNumber;
	}

	@Override
	protected List<Message> doInBackground(Void... params) {
		List<Message> messages = new LinkedList<>();
		Cursor cursor = dbHelper.getReadableDatabase().query(TableMessages.TABLE, null,
			TableMessages.COLUMN.PHONE_NUMBER + " = ?", new String[]{phoneNumber},
			null, null, null);
		while (cursor.moveToNext()) {
			Message message = new Message();
			message.setId(cursor.getLong(cursor.getColumnIndex(TableMessages.COLUMN.ID)));
			message.setMessageText(cursor.getString(cursor.getColumnIndex(TableMessages.COLUMN.MESSAGE_TEXT)));
			message.setMessageTime(cursor.getString(cursor.getColumnIndex(TableMessages.COLUMN.MESSAGE_TIME)));
			message.setPhoneNumber(cursor.getString(cursor.getColumnIndex(TableMessages.COLUMN.PHONE_NUMBER)));
			message.setMyMessage(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(TableMessages.COLUMN.IS_MY_MESSAGE))));
			Log.w(TAG, "MY MESSAGE = " + message.getMyMessage());
			messages.add(message);
		}
		cursor.close();
		return messages;
	}

	@Override
	protected void onPostExecute(List<Message> messages) {
		if (callback != null) {
			callback.onMessagesLoaded(messages);
		}
	}
}
