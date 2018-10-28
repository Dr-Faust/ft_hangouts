package opodolia.ft_hangouts.mvp.model.contacts;

import android.database.Cursor;
import android.os.AsyncTask;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.LoadSingleContactCallback;

public class LoadSingleContactTask extends AsyncTask<Void, Void, Contact> {

	private static String                   TAG = LoadSingleContactTask.class.getSimpleName();
	private final LoadSingleContactCallback callback;
	private final DbHelper                  dbHelper;
	private final long                      contactId;

	public LoadSingleContactTask(LoadSingleContactCallback callback, DbHelper dbHelper, long contactId) {
		this.callback = callback;
		this.dbHelper = dbHelper;
		this.contactId = contactId;
	}

	@Override
	protected Contact doInBackground(Void... params) {
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " +
			TableContacts.TABLE + " WHERE _id = ?", new String[]{String.valueOf(contactId)});
		Contact contact = new Contact();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			contact.setId(cursor.getLong(cursor.getColumnIndex(TableContacts.COLUMN.ID)));
			contact.setFirstName(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.FIRST_NAME)));
			contact.setLastName(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.LAST_NAME)));
			contact.setPhoneNumber(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.PHONE_NUMBER)));
			contact.setEmail(cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.EMAIL)));
			contact.setBirthday(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.BIRTHDAY)));
			contact.setContactName(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.CONTACT_NAME)));
			contact.setPhotoUri(
				cursor.getString(cursor.getColumnIndex(TableContacts.COLUMN.PHOTO_URI)));
			cursor.close();
		}
		return contact;
	}

	@Override
	protected void onPostExecute(Contact contact) {
		if (callback != null) {
			callback.onLoad(contact);
		}
	}
}