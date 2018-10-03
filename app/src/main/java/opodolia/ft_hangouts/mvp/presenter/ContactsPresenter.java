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
import opodolia.ft_hangouts.mvp.view.AddEditContactActivity;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.database.ContactTable;
import opodolia.ft_hangouts.mvp.ContactData;
import opodolia.ft_hangouts.mvp.view.ContactInfoActivity;
import opodolia.ft_hangouts.mvp.view.ContactsActivity;
import opodolia.ft_hangouts.mvp.model.ContactsModel;
import opodolia.ft_hangouts.mvp.view.MessagesActivity;
import opodolia.ft_hangouts.permissions.PermissionChecker;


public class ContactsPresenter {

    private ContactsActivity        contactsActivity;
	private AddEditContactActivity  addEditContactActivity;
	private ContactInfoActivity     contactInfoActivity;
    private ContactsModel           model;
	private static String           TAG = ContactsPresenter.class.getSimpleName();

	public ContactsPresenter() {
	}

    public ContactsPresenter(ContactsModel model) {
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
        ContactData contactData = addEditContactActivity. getRetainedContactData();
        if (TextUtils.isEmpty(contactData.getFirstName()) &&
	        TextUtils.isEmpty(contactData.getLastName()) &&
	        TextUtils.isEmpty(contactData.getPhoneNumber()) &&
	        TextUtils.isEmpty(contactData.getEmail())) {
            addEditContactActivity.showToast(R.string.fill_in_please);
            return;
        }

        ContentValues cv = new ContentValues(2);
        cv.put(ContactTable.COLUMN.FIRST_NAME, contactData.getFirstName());
	    cv.put(ContactTable.COLUMN.LAST_NAME, contactData.getLastName());
	    cv.put(ContactTable.COLUMN.PHONE_NUMBER, contactData.getPhoneNumber());
        cv.put(ContactTable.COLUMN.EMAIL, contactData.getEmail());
	    cv.put(ContactTable.COLUMN.BIRTHDAY, contactData.getBirthday());
	    cv.put(ContactTable.COLUMN.CONTACT_NAME, contactData.getContactName());
	    cv.put(ContactTable.COLUMN.PHOTO_URI, contactData.getPhotoUri());
        model.addContact(cv, () -> {
	        Intent intent = new Intent(addEditContactActivity, ContactsActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        addEditContactActivity.startActivity(intent);
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
		cv.put(ContactTable.COLUMN.FIRST_NAME, contactData.getFirstName());
		cv.put(ContactTable.COLUMN.LAST_NAME, contactData.getLastName());
		cv.put(ContactTable.COLUMN.PHONE_NUMBER, contactData.getPhoneNumber());
		cv.put(ContactTable.COLUMN.EMAIL, contactData.getEmail());
		cv.put(ContactTable.COLUMN.BIRTHDAY, contactData.getBirthday());
		cv.put(ContactTable.COLUMN.CONTACT_NAME, contactData.getContactName());
		cv.put(ContactTable.COLUMN.PHOTO_URI, contactData.getPhotoUri());
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
}
