package opodolia.ft_hangouts.mvp.model.contacts;

import android.database.Cursor;
import android.os.AsyncTask;
import java.util.LinkedList;
import java.util.List;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.LoadContactsCallback;
import opodolia.ft_hangouts.mvp.model.messages.callbacks.LoadMessagesCallback;

public class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {

	private final LoadContactsCallback  callback;
	private final DbHelper              dbHelper;

	public LoadContactsTask(LoadContactsCallback callback, DbHelper dbHelper) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected List<Contact> doInBackground(Void... params) {
		List<Contact> contacts = new LinkedList<>();
		Cursor cursor = dbHelper.getReadableDatabase().query(TableContacts.TABLE, null,
			null, null, null, null, null);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			contact.setId(cursor.getLong(cursor.getColumnIndex(TableContacts.COLUMN.ID)));
			contact.setFirstName(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.FIRST_NAME)));
			contact.setLastName(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.LAST_NAME)));
			contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.PHONE_NUMBER)));
			contact.setEmail(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.EMAIL)));
			contact.setBirthday(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.BIRTHDAY)));
			contact.setContactName(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.CONTACT_NAME)));
			contact.setPhotoUri(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.PHOTO_URI)));
			contacts.add(contact);
		}
		cursor.close();
		return contacts;
	}

	@Override
	protected void onPostExecute(List<Contact> contacts) {
		if (callback != null) {
			callback.onContactsLoaded(contacts);
		}
	}
}
