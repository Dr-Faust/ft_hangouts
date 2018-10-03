package opodolia.ft_hangouts.mvp.model;

import android.database.Cursor;
import android.os.AsyncTask;
import java.util.LinkedList;
import java.util.List;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.database.ContactTable;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.LoadContactsCallback;

public class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {

	private final LoadContactsCallback callback;
	private final DbHelper              dbHelper;

	LoadContactsTask(LoadContactsCallback callback, DbHelper dbHelper) {
		this.callback = callback;
		this.dbHelper = dbHelper;
	}

	@Override
	protected List<Contact> doInBackground(Void... params) {
		List<Contact> contacts = new LinkedList<>();
		Cursor cursor = dbHelper.getReadableDatabase().query(ContactTable.TABLE, null,
			null, null, null, null, null);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			contact.setId(cursor.getLong(cursor.getColumnIndex(ContactTable.COLUMN.ID)));
			contact.setFirstName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.FIRST_NAME)));
			contact.setLastName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.LAST_NAME)));
			contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.PHONE_NUMBER)));
			contact.setEmail(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.EMAIL)));
			contact.setBirthday(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.BIRTHDAY)));
			contact.setContactName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.CONTACT_NAME)));
			contact.setPhotoUri(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN.PHOTO_URI)));
			contacts.add(contact);
		}
		cursor.close();
		return contacts;
	}

	@Override
	protected void onPostExecute(List<Contact> contacts) {
		if (callback != null) {
			callback.onLoad(contacts);
		}
	}
}
