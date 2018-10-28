package opodolia.ft_hangouts.mvp.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import java.util.Objects;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.contact_adapter.ContactAdapter;
import opodolia.ft_hangouts.common.contact_adapter.OnContactClickListener;

import static opodolia.ft_hangouts.app.Config.MENU_DELETE;
import static opodolia.ft_hangouts.app.Config.MENU_EDIT;

public class ContactsActivity extends MyAppCompat implements OnContactClickListener {

	private static String           TAG = ContactsActivity.class.getSimpleName();

	private ContactAdapter          contactAdapter;
	private RecyclerView            listContacts;
	public BroadcastReceiver        addContactBroadcast;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        loadContacts();

	    initViews();

	    setContactAdapter();
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		loadContacts();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "DESTROYING!!!");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
			unregisterReceiver(addContactBroadcast);
		}
	}

	private void setContactAdapter() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		contactAdapter = new ContactAdapter(this);

		listContacts.setLayoutManager(layoutManager);
	}

    private void initViews() {
	    Toolbar toolbar = findViewById(R.id.toolbar_contacts);
	    setSupportActionBar(toolbar);

	    listContacts = findViewById(R.id.list_contacts);
	    FloatingActionButton btnAddContact = findViewById(R.id.add_contact);
	    btnAddContact.setOnClickListener(v -> presenter.createNewContact());

	    initNewContactReceiver();
    }

    private void initNewContactReceiver() {
	    addContactBroadcast = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
			    Log.w(TAG, "RECEIVED = " + intent.getAction());
			    if (Objects.equals(intent.getAction(), "contact.added")) {
			    	presenter.loadContacts();
			    }
		    }
	    };
	    IntentFilter intentFilter = new IntentFilter("contact.added");
	    registerReceiver(addContactBroadcast, intentFilter);
    }

    public void loadContacts() {
	    presenter.attachView(this);
	    presenter.loadContacts();
    }

	public void showContacts(List<Contact> contacts) {
		contactAdapter.setContactData(contacts);
		listContacts.setAdapter(contactAdapter);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_change_theme)
        {
            changeTheme();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeTheme()
    {
        // setup the alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, currentDialogStyle);
	    builder.setIcon(R.drawable.ic_color_lens_black);
        builder.setTitle(getString(R.string.select_theme));
        // addContactToDb list of radio buttons
        String[] themes = {getString(R.string.color_green), getString(R.string.color_blue),
							getString(R.string.color_grey), getString(R.string.color_brown),
                           getString(R.string.color_yellow)};
        builder.setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
			switch (which)
			{
				case 0: // Green
					currentTheme = 0;
					break;
				case 1: // Blue
					currentTheme = 1;
					break;
				case 2: // Orange
					currentTheme = 2;
					break;
				case 3: // Red
					currentTheme = 3;
					break;
				case 4: // Purple
					currentTheme = 4;
					break;
			}
            SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
            editor.putInt("current_theme", currentTheme);
            editor.apply();
		});

        // addContactToDb Ok button
        builder.setPositiveButton("OK", (dialog, which) -> {
//                recreate();
			Intent intent = new Intent(getBaseContext(), ContactsActivity.class);
			startActivity(intent);
			finish();
		});

        // addContactToDb Cancel button
        builder.setNegativeButton(this.getString(R.string.cancel), null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

	@Override
	public void onContactClicked(long contactId) {
    	presenter.showContactInfo(contactId);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		Contact contact = contactAdapter.getContact();
		Log.i(TAG, "CONTACT NAME = " + contact.getFirstName());

		if (item.getItemId() == MENU_EDIT)
			presenter.editContact(contact, this);
		else if (item.getItemId() == MENU_DELETE)
		{
			new AlertDialog.Builder(this, currentDialogStyle)
				.setIcon(R.drawable.ic_warning_black)
				.setTitle(getResources().getString(R.string.question_delete_contact))
				.setPositiveButton(getResources().getString(R.string.action_delete), (dialog, which) -> {
					presenter.removeContactFromDb(contact.getId(), this);
				})
				.setNegativeButton(getResources().getString(R.string.cancel), null)
				.show();
		}
		else
		{
			return false;
		}
		return true;
	}
}
