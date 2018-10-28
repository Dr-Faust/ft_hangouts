package opodolia.ft_hangouts.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.Model;
import opodolia.ft_hangouts.mvp.model.contacts.ContactData;
import opodolia.ft_hangouts.mvp.model.messages.MessageData;
import opodolia.ft_hangouts.mvp.presenter.Presenter;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();
    private Presenter           presenter;
    private String              senderAddress;
    private Context             context;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");
                for (Object aPdusObj : pdusObj != null ? pdusObj : new Object[0]) {
                    SmsMessage currentMessage;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                        currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj, format);
                    else
                        currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

	                this.context = context;
	                DbHelper dbHelper = new DbHelper(context);
	                Model model = new Model(dbHelper);
	                presenter = new Presenter(model);
	                presenter.attachView(this);
	                presenter.checkIfPhoneExists(senderAddress);

	                Calendar calendar = Calendar.getInstance();
	                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
	                String dateStr = dateFormat.format(calendar.getTime());

	                MessageData messageData = new MessageData();
	                messageData.setMessageText(message);
	                messageData.setMessageTime(dateStr);
	                messageData.setPhoneNumber(senderAddress);
	                messageData.setMyMessage(false);

	                presenter.addMessageToDb(messageData);

	                Intent i = new Intent("sms.received");
	                context.sendBroadcast(i);
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

	public void createNewContact(Boolean exists) {
    	if (!exists) {
		    ContactData contactData = new ContactData();
		    contactData.setFirstName("");
		    contactData.setLastName("");
		    contactData.setPhoneNumber(senderAddress);
		    contactData.setEmail("");
		    contactData.setBirthday("");
		    contactData.setContactName("");
		    contactData.setPhotoUri("");
		    presenter.addContactToDb(contactData);
		    Intent i = new Intent("contact.added");
		    context.sendBroadcast(i);
	    }
	}
}
