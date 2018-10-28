package opodolia.ft_hangouts.mvp.model;

import android.content.ContentValues;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.contacts.CheckIfPhoneExistsTask;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.LoadContactsCallback;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.LoadSingleContactCallback;
import opodolia.ft_hangouts.mvp.model.contacts.AddContactTask;
import opodolia.ft_hangouts.mvp.model.contacts.DeleteContactTask;
import opodolia.ft_hangouts.mvp.model.contacts.EditContactTask;
import opodolia.ft_hangouts.mvp.model.contacts.LoadContactsTask;
import opodolia.ft_hangouts.mvp.model.contacts.LoadSingleContactTask;
import opodolia.ft_hangouts.mvp.model.contacts.callbacks.OnPhoneExistsCallback;
import opodolia.ft_hangouts.mvp.model.messages.AddMessageTask;
import opodolia.ft_hangouts.mvp.model.messages.DeleteMessageTask;
import opodolia.ft_hangouts.mvp.model.messages.LoadMessagesTask;
import opodolia.ft_hangouts.mvp.model.messages.callbacks.LoadMessagesCallback;

public class Model {

	private static String       TAG = Model.class.getSimpleName();
	private DbHelper dbHelper;

	public Model(DbHelper dbHelper) {
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

	public void loadMessages(String phoneNumber, LoadMessagesCallback callback) {
		LoadMessagesTask loadMessagesTask = new LoadMessagesTask(callback, dbHelper, phoneNumber);
		loadMessagesTask.execute();
	}

	public void addMessage(ContentValues contentValues, CompleteCallback callback) {
		AddMessageTask addMessageTask = new AddMessageTask(callback, dbHelper);
		addMessageTask.execute(contentValues);
	}

	public void deleteMessage(long messageId, CompleteCallback completeCallback) {
		DeleteMessageTask deleteMessageTask = new DeleteMessageTask(completeCallback, dbHelper, messageId);
		deleteMessageTask.execute();
	}

	public void checkIfPhoneExists(String phoneNumber, OnPhoneExistsCallback callback) {
		CheckIfPhoneExistsTask checkIfPhoneExistsTask = new CheckIfPhoneExistsTask(callback, dbHelper, phoneNumber);
		checkIfPhoneExistsTask.execute();
	}
}
