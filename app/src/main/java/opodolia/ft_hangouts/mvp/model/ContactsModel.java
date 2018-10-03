package opodolia.ft_hangouts.mvp.model;

import android.content.ContentValues;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.CompleteCallback;
import opodolia.ft_hangouts.mvp.LoadContactsCallback;
import opodolia.ft_hangouts.mvp.LoadSingleContactCallback;

public class ContactsModel {

	private static String       TAG = ContactsModel.class.getSimpleName();
	private DbHelper dbHelper;

	public ContactsModel(DbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void loadContacts(LoadContactsCallback callback) {
		LoadContactsTask loadContactsTask = new LoadContactsTask(callback, dbHelper);
		loadContactsTask.execute();
	}

	public void loadSingleContact(long contactId, LoadSingleContactCallback callback) {
		LoadSingleContactTask loadSingleContactTask = new LoadSingleContactTask(callback, dbHelper, contactId);
		loadSingleContactTask.execute();
	}

	public void addContact(ContentValues contentValues, CompleteCallback callback) {
		AddContactTask addContactTask = new AddContactTask(callback, dbHelper);
		addContactTask.execute(contentValues);
	}

	public void editContact(ContentValues contentValues, long contactId, CompleteCallback callback) {
		EditContactTask editContactTask = new EditContactTask(callback, dbHelper, contactId);
		editContactTask.execute(contentValues);
	}

	public void deleteContact(long contactId, CompleteCallback completeCallback) {
		DeleteContactTask deleteContactTask = new DeleteContactTask(completeCallback, dbHelper, contactId);
		deleteContactTask.execute();
	}
}
