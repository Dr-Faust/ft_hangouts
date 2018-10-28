package opodolia.ft_hangouts.mvp.presenter;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.Item;
import opodolia.ft_hangouts.database.TableMessages;
import opodolia.ft_hangouts.mvp.model.messages.MessageData;
import opodolia.ft_hangouts.mvp.view.AddEditContactActivity;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.database.TableContacts;
import opodolia.ft_hangouts.mvp.model.contacts.ContactData;
import opodolia.ft_hangouts.mvp.view.ContactInfoActivity;
import opodolia.ft_hangouts.mvp.view.ContactsActivity;
import opodolia.ft_hangouts.mvp.model.Model;
import opodolia.ft_hangouts.mvp.view.MessagesActivity;
import opodolia.ft_hangouts.permissions.PermissionChecker;
import opodolia.ft_hangouts.utils.SmsReceiver;


public class Presenter {

    private ContactsActivity        contactsActivity;
	private AddEditContactActivity  addEditContactActivity;
	private ContactInfoActivity     contactInfoActivity;
	private MessagesActivity        messagesActivity;
	private SmsReceiver             smsReceiver;
    private Model                   model;
	private static String           TAG = Presenter.class.getSimpleName();

	public Presenter() {
	}

    public Presenter(Model model) {
        this.model = model;
    }

    public void attachView(ContactsActivity contactsActivity) {
        this.contactsActivity = contactsActivity;
    }

	public void attachView(AddEditContactActivity addEditContactActivity) {
		this.addEditContactActivity = addEditContactActivity;
	}

	public void attachView(ContactInfoActivity contactInfoActivity) {
		this.contactInfoActivity = contactInfoActivity;
	}

	public void attachView(MessagesActivity messagesActivity) {
		this.messagesActivity= messagesActivity;
	}

	public void attachView(SmsReceiver smsReceiver) {
		this.smsReceiver= smsReceiver;
	}

    public void loadContacts() {
        model.loadContacts(contacts -> contactsActivity.showContacts(contacts));
    }

	public void loadSingleContact(long contactId) {
		model.loadSingleContact(contactId, contact -> contactInfoActivity.showSingleContact(contact));
	}

	public void showContactInfo(long contactId) {
		Intent intent = new Intent(contactsActivity, ContactInfoActivity.class);
		intent.putExtra("contact_id", contactId);
		contactsActivity.startActivity(intent);
	}

    public void createNewContact() {
        Intent intent = new Intent(contactsActivity, AddEditContactActivity.class);
	    intent.putExtra("new_contact", true);
        contactsActivity.startActivity(intent);
    }

	public void editContact(Contact contact, Context context) {
		Intent intent = new Intent(context, AddEditContactActivity.class);
		intent.putExtra("new_contact", false);
		intent.putExtra("contact", contact);
		intent.putExtra("called_from_contacts_activity", context.getClass() == ContactsActivity.class);
		context.startActivity(intent);
	}

    public void addContactToDb() {
        ContactData contactData = addEditContactActivity.getRetainedContactData();
        if (TextUtils.isEmpty(contactData.getFirstName()) &&
	        TextUtils.isEmpty(contactData.getLastName()) &&
	        TextUtils.isEmpty(contactData.getPhoneNumber()) &&
	        TextUtils.isEmpty(contactData.getEmail())) {
            addEditContactActivity.showToast(R.string.fill_in_please);
            return;
        }

        ContentValues cv = new ContentValues(2);
        cv.put(TableContacts.COLUMN.FIRST_NAME, contactData.getFirstName());
	    cv.put(TableContacts.COLUMN.LAST_NAME, contactData.getLastName());
	    cv.put(TableContacts.COLUMN.PHONE_NUMBER, contactData.getPhoneNumber());
        cv.put(TableContacts.COLUMN.EMAIL, contactData.getEmail());
	    cv.put(TableContacts.COLUMN.BIRTHDAY, contactData.getBirthday());
	    cv.put(TableContacts.COLUMN.CONTACT_NAME, contactData.getContactName());
	    cv.put(TableContacts.COLUMN.PHOTO_URI, contactData.getPhotoUri());
        model.addContact(cv, () -> {
	        Log.i(TAG, "CONTACT ADDED!");
	        Intent intent = new Intent(addEditContactActivity, ContactsActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        addEditContactActivity.startActivity(intent);
        });
    }

	public void addContactToDb(ContactData contactData) {
		ContentValues cv = new ContentValues(2);
		cv.put(TableContacts.COLUMN.FIRST_NAME, contactData.getFirstName());
		cv.put(TableContacts.COLUMN.LAST_NAME, contactData.getLastName());
		cv.put(TableContacts.COLUMN.PHONE_NUMBER, contactData.getPhoneNumber());
		cv.put(TableContacts.COLUMN.EMAIL, contactData.getEmail());
		cv.put(TableContacts.COLUMN.BIRTHDAY, contactData.getBirthday());
		cv.put(TableContacts.COLUMN.CONTACT_NAME, contactData.getContactName());
		cv.put(TableContacts.COLUMN.PHOTO_URI, contactData.getPhotoUri());
		model.addContact(cv, () -> {
			Log.i(TAG, "CONTACT ADDED!");
		});
	}

	public void editContactInDb(long contactId, Boolean calledFromContactsActivity) {
		ContactData contactData = addEditContactActivity.getRetainedContactData();
		if (TextUtils.isEmpty(contactData.getFirstName()) &&
			TextUtils.isEmpty(contactData.getLastName()) &&
			TextUtils.isEmpty(contactData.getPhoneNumber()) &&
			TextUtils.isEmpty(contactData.getEmail())) {
			addEditContactActivity.showToast(R.string.fill_in_please);
			return;
		}

		ContentValues cv = new ContentValues(2);
		cv.put(TableContacts.COLUMN.FIRST_NAME, contactData.getFirstName());
		cv.put(TableContacts.COLUMN.LAST_NAME, contactData.getLastName());
		cv.put(TableContacts.COLUMN.PHONE_NUMBER, contactData.getPhoneNumber());
		cv.put(TableContacts.COLUMN.EMAIL, contactData.getEmail());
		cv.put(TableContacts.COLUMN.BIRTHDAY, contactData.getBirthday());
		cv.put(TableContacts.COLUMN.CONTACT_NAME, contactData.getContactName());
		cv.put(TableContacts.COLUMN.PHOTO_URI, contactData.getPhotoUri());
		model.editContact(cv, contactId, () -> {
			Intent intent;
			if (calledFromContactsActivity) {
				intent = new Intent(addEditContactActivity, ContactsActivity.class);
			}
			else {
				intent = new Intent(addEditContactActivity, ContactInfoActivity.class);
				intent.putExtra("contact_id", contactId);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			addEditContactActivity.startActivity(intent);
		});
	}

	public void removeContactFromDb(long contactId, Context context) {
		model.deleteContact(contactId, () -> {
			Log.i(TAG, "CONTACT REMOVED!");
			if (context.getClass() ==  ContactsActivity.class)
				contactsActivity.loadContacts();
			else {
				Intent intent = new Intent(context, ContactsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
	}

	public void openMessageActivity(Contact contact) {
		Intent intent = new Intent(contactInfoActivity, MessagesActivity.class);
		intent.putExtra("contact", contact);
		contactInfoActivity.startActivity(intent);
	}

    public void chooseOption(Item item) {
		String phoneNumber = contactInfoActivity.getString(R.string.phone_number);
		String email = contactInfoActivity.getString(R.string.email);
		String birthday = contactInfoActivity.getString(R.string.birthday);

		if (Objects.equals(item.getKey(), phoneNumber)) {
			new PermissionChecker(contactInfoActivity, MyAppCompat.currentDialogStyle).callPermissionChecker();
			if (ActivityCompat.checkSelfPermission(contactInfoActivity,
				Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
				return;
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getValue()));
			contactInfoActivity.startActivity(intent);
		}
		else if (Objects.equals(item.getKey(), email)) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, new String[]{item.getValue()});
			intent.setType("text/plain");
			intent.setPackage("com.google.android.gm");
			contactInfoActivity.startActivity(intent);
		}
		else if (Objects.equals(item.getKey(), birthday)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
				Date date = sdf.parse(item.getValue());
				long startMillis = date.getTime();

				Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
				builder.appendPath("time");
				ContentUris.appendId(builder, startMillis);
				Intent intent = new Intent(Intent.ACTION_VIEW)
					.setData(builder.build());
				contactInfoActivity.startActivity(intent);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
    }

	public void loadMessages(String phoneNumber) {
		model.loadMessages(phoneNumber.replaceAll("[^0-9]", ""), messages -> messagesActivity.showMessages(messages));
	}

	public void addMessageToDb() {
		MessageData messageData = messagesActivity.getRetainedMessageData();
		if (TextUtils.isEmpty(messageData.getMessageText())) {
			return;
		}

		ContentValues cv = new ContentValues(2);
		cv.put(TableMessages.COLUMN.MESSAGE_TEXT, messageData.getMessageText());
		cv.put(TableMessages.COLUMN.MESSAGE_TIME, messageData.getMessageTime());
		cv.put(TableMessages.COLUMN.PHONE_NUMBER, messageData.getPhoneNumber().replaceAll("[^0-9]", ""));
		cv.put(TableMessages.COLUMN.IS_MY_MESSAGE, messageData.getMyMessage().toString());
		Log.w(TAG, "MY MESSAGE = " + messageData.getMyMessage());
		model.addMessage(cv, () -> {
			Log.i(TAG, "MESSAGE ADDED!");
			loadMessages(messagesActivity.getPhoneNumber());
			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(messageData.getPhoneNumber(), null,
				messageData.getMessageText(), null, null);
		});
	}

	public void addMessageToDb(MessageData messageData) {
		if (TextUtils.isEmpty(messageData.getMessageText())) {
			return;
		}

		ContentValues cv = new ContentValues(2);
		cv.put(TableMessages.COLUMN.MESSAGE_TEXT, messageData.getMessageText());
		cv.put(TableMessages.COLUMN.MESSAGE_TIME, messageData.getMessageTime());
		cv.put(TableMessages.COLUMN.PHONE_NUMBER, messageData.getPhoneNumber().replaceAll("[^0-9]", ""));
		cv.put(TableMessages.COLUMN.IS_MY_MESSAGE, messageData.getMyMessage().toString());
		Log.w(TAG, "MY MESSAGE = " + messageData.getMyMessage());
		model.addMessage(cv, () -> {
			Log.i(TAG, "MESSAGE ADDED!");
		});
	}

	public void removeMessageFromDb(long messageId) {
		model.deleteMessage(messageId, () -> {
			Log.i(TAG, "MESSAGE REMOVED!");
			loadMessages(messagesActivity.getPhoneNumber());
		});
	}

	public void checkIfPhoneExists(String phoneNumber) {
		model.checkIfPhoneExists(phoneNumber, exists -> smsReceiver.createNewContact(exists));
	}
}
