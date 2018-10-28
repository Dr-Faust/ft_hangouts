package opodolia.ft_hangouts.mvp.view;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.Config;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.permissions.PermissionChecker;
import opodolia.ft_hangouts.utils.DividerItemDecorator;
import opodolia.ft_hangouts.common.Item;
import opodolia.ft_hangouts.common.OnItemClick;
import opodolia.ft_hangouts.common.contact_info_adapter.ContactInfoAdapter;

import static opodolia.ft_hangouts.app.Config.MENU_COPY_BIRTHDAY;
import static opodolia.ft_hangouts.app.Config.MENU_COPY_EMAIL;
import static opodolia.ft_hangouts.app.Config.MENU_COPY_PHONE;

public class ContactInfoActivity extends MyAppCompat implements OnItemClick {

	private static String TAG = ContactInfoActivity.class.getSimpleName();
	private Toolbar toolbar;
	private CollapsingToolbarLayout collapsingToolbar;
	private AppBarLayout appBarLayout;
	private Contact contact;
	private ImageView contactPhoto;
	private String contactPhotoUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_info);

		loadSingleContact();
	}


	private ArrayList<Item> createListItems(Contact contact) {
		ArrayList<Item> items = new ArrayList<>();

		Map<String, String> contactInfo = new LinkedHashMap<>();
		contactInfo.put(getString(R.string.phone_number), contact.getPhoneNumber());
		contactInfo.put(getString(R.string.email), contact.getEmail());
		contactInfo.put(getString(R.string.birthday), contact.getBirthday());

		for (Map.Entry entry : contactInfo.entrySet()) {
			if (entry.getValue() != "") {
				Item item = new Item();
				item.setKey(entry.getKey().toString());
				item.setValue(entry.getValue().toString());
				items.add(item);
			}
		}
		return items;
	}

	private void setContactPhoto() {
		contactPhoto = findViewById(R.id.collapsing_contact_photo);
		contactPhotoUri = contact.getPhotoUri();
		Log.i(TAG, "URI = " + contactPhotoUri);
		if (!contactPhotoUri.equals(""))
			Glide.with(ContactInfoActivity.this)
				.load(contactPhotoUri)
				.apply(RequestOptions.centerCropTransform())
				.into(contactPhoto);
	}

	private void initListContactInfo() {
		RecyclerView list_contact_info = findViewById(R.id.list_contact_info);

		RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat
			.getDrawable(this, R.drawable.divider));
		list_contact_info.addItemDecoration(dividerItemDecoration);

		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		list_contact_info.setLayoutManager(llm);

		ContactInfoAdapter adapter = new ContactInfoAdapter(createListItems(contact), this);
		list_contact_info.setAdapter(adapter);
	}

	private void initViews() {
		appBarLayout = findViewById(R.id.toolbar_layout);
		appBarLayout.setBackgroundColor(
			getResources().getColor(currentCollapsingToolbarBackgroundColor));

		toolbar = findViewById(R.id.anim_toolbar);
		toolbar.inflateMenu(R.menu.menu_contact_info);
		toolbar.setTitle(contact.getContactName());

		collapsingToolbar = findViewById(R.id.collapsing_toolbar);

		setSupportActionBar(toolbar);

		setContactPhoto();

		initListContactInfo();

		RecyclerView list_contact_log = findViewById(R.id.list_contact_log);
	}

	private void loadSingleContact() {
		long contactId = getIntent().getLongExtra("contact_id", -1);

		presenter.attachView(ContactInfoActivity.this);
		presenter.loadSingleContact(contactId);
	}

	public void showSingleContact(Contact contact) {
		this.contact = contact;
		initViews();
	}

	@Override
	public void onItemClick(@NonNull Item item) {
		presenter.chooseOption(item);
	}

	@Override
	public void onItemClick() {
		new PermissionChecker(this, MyAppCompat.currentDialogStyle).smsPermissionChecker();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
			|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
			|| ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
			return;
		presenter.openMessageActivity(contact);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_contact_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				presenter.editContact(contact, this);
				return true;
			case R.id.action_delete:
				new AlertDialog.Builder(this, currentDialogStyle)
					.setIcon(R.drawable.ic_warning_black)
					.setTitle(getResources().getString(R.string.question_delete_contact))
					.setPositiveButton(getResources().getString(R.string.action_delete), (dialog, which) -> {
						presenter.removeContactFromDb(contact.getId(), this);
					})
					.setNegativeButton(getResources().getString(R.string.cancel), null)
					.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == Config.REQUEST_CALL_PERMISSION) {
			Log.e(TAG, "Received response for CALL permission request.");

			boolean permission = true;
			// We have requested multiple permissions for contacts, so all of them need to be
			// checked.
			if (grantResults.length < 1)
				permission = false;

			// Verify that each required permission has been granted, otherwise return false.
			for (int result : grantResults)
				if (result != PackageManager.PERMISSION_GRANTED)
					permission = false;

			if (permission) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNumber()));
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				startActivity(intent);
			}
			else
				Toast.makeText(this, getString(R.string.negative_response_call_permission),
					Toast.LENGTH_SHORT).show();
		}
		else if (requestCode == Config.REQUEST_SMS_PERMISSION)
		{
			Log.e(TAG, "Received response for SMS permissions request.");

			boolean permission = true;
			// We have requested multiple permissions for contacts, so all of them need to be
			// checked.
			if (grantResults.length < 1)
				permission = false;

			// Verify that each required permission has been granted, otherwise return false.
			for (int result : grantResults)
				if (result != PackageManager.PERMISSION_GRANTED)
					permission = false;

			if (permission) {
				presenter.openMessageActivity(contact);
			}
			else
				Toast.makeText(this, getString(R.string.negative_response_sms_permission),
					Toast.LENGTH_SHORT).show();
		}
		else
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getItemId() == MENU_COPY_PHONE)
		{
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("phone_number", contact.getPhoneNumber());
			if (clipboard != null) {
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, getText(R.string.text_copied), Toast.LENGTH_SHORT).show();
			}
		}
		else if (item.getItemId() == MENU_COPY_EMAIL)
		{
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("email", contact.getEmail());
			if (clipboard != null) {
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, getText(R.string.text_copied), Toast.LENGTH_SHORT).show();
			}
		}
		else if (item.getItemId() == MENU_COPY_BIRTHDAY)
		{
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("birthday", contact.getBirthday());
			if (clipboard != null) {
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, getText(R.string.text_copied), Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			return false;
		}
		return true;
	}
}
