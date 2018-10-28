package opodolia.ft_hangouts.mvp.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.Config;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.Message;
import opodolia.ft_hangouts.common.message_adapter.MessageAdapter;
import opodolia.ft_hangouts.mvp.model.messages.MessageData;
import opodolia.ft_hangouts.permissions.PermissionChecker;

import static opodolia.ft_hangouts.app.Config.MENU_DELETE;

public class MessagesActivity extends MyAppCompat implements Toolbar.OnMenuItemClickListener,
	View.OnClickListener {

	private static String TAG = MessagesActivity.class.getSimpleName();

	private Contact contact;
	private EditText etMessage;
	private ImageView ivSentMessage;
	private RecyclerView listMessages;
	private MessageAdapter messageAdapter;
	public BroadcastReceiver receiveSmsBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		initViews();
	}

	private void loadMessages() {
		presenter.attachView(MessagesActivity.this);
		presenter.loadMessages(contact.getPhoneNumber());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "RESUMING!!!");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
			|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
			|| ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
			new PermissionChecker(this, MyAppCompat.currentDialogStyle).smsPermissionChecker();
		}

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
			loadMessages();
			initSmsReceiver();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "PAUSING!!!");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "HERE!!!!!");
			unregisterReceiver(receiveSmsBroadcast);
		}
	}

	private void initViews() {
		contact = (Contact) getIntent().getSerializableExtra("contact");
		Toolbar toolbar = findViewById(R.id.toolbar_message);
		toolbar.setTitle(contact.getContactName());

		toolbar.inflateMenu(R.menu.menu_message);
		toolbar.setOnMenuItemClickListener(this);

		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		toolbar.setNavigationOnClickListener(v -> onBackPressed());

		listMessages = findViewById(R.id.list_messages);
		etMessage = findViewById(R.id.et_text_message);
		ivSentMessage = findViewById(R.id.iv_send_sms);
		ivSentMessage.setOnClickListener(this);
		initKeyboardListener();

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		listMessages.setLayoutManager(layoutManager);
		messageAdapter = new MessageAdapter(this);
	}

	private void initKeyboardListener() {
		final View activityRootView = findViewById(R.id.messages_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
			Rect r = new Rect();

			activityRootView.getWindowVisibleDisplayFrame(r);

			int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
			if (heightDiff > 100) {
				listMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
			}
		});
	}

	private void initSmsReceiver() {
		receiveSmsBroadcast = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w(TAG, "RECEIVED = " + intent.getAction());
				if (Objects.equals(intent.getAction(), "sms.received")) {
					Log.w(TAG, "PHONE NUMBER = " + contact.getPhoneNumber());
					presenter.loadMessages(contact.getPhoneNumber());
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter("sms.received");
		registerReceiver(receiveSmsBroadcast, intentFilter);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_call:
				new PermissionChecker(this, MyAppCompat.currentDialogStyle).callPermissionChecker();
				if (ActivityCompat.checkSelfPermission(this,
					Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
					return false;
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNumber()));
				startActivity(intent);
				return true;
			case R.id.action_delete:
				new AlertDialog.Builder(this, currentDialogStyle)
					.setIcon(R.drawable.ic_warning_black)
					.setTitle(getResources().getString(R.string.question_delete_contact))
					.setPositiveButton(getResources().getString(R.string.action_delete), (dialog, which) ->
						presenter.removeContactFromDb(contact.getId(), this))
					.setNegativeButton(getResources().getString(R.string.cancel), null)
					.show();
				return true;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		presenter.addMessageToDb();
		etMessage.setText("");
	}

	public void showMessages(List<Message> messages) {
		messageAdapter.setMessages(messages);
		listMessages.setAdapter(messageAdapter);
		listMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
	}

	public MessageData getRetainedMessageData() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		String dateStr = dateFormat.format(calendar.getTime());

		MessageData messageData = new MessageData();
		messageData.setMessageText(etMessage.getText().toString());
		messageData.setMessageTime(dateStr);
		messageData.setPhoneNumber(contact.getPhoneNumber());
		messageData.setMyMessage(true);

		return messageData;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Message message = messageAdapter.getMessage();
		Log.i(TAG, "CONTACT PHONE = " + message.getPhoneNumber());

		if (item.getItemId() == MENU_DELETE) {
			new AlertDialog.Builder(this, currentDialogStyle)
				.setIcon(R.drawable.ic_warning_black)
				.setTitle(getResources().getString(R.string.question_delete_message))
				.setPositiveButton(getResources().getString(R.string.action_delete), (dialog, which) ->
					presenter.removeMessageFromDb(message.getId()))
				.setNegativeButton(getResources().getString(R.string.cancel), null)
				.show();
		} else {
			return false;
		}
		return true;
	}

	public String getPhoneNumber() {
		return contact.getPhoneNumber();
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
				loadMessages();
				Toast.makeText(this, getString(R.string.positive_response_sms_permission),
					Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(this, getString(R.string.negative_response_sms_permission),
					Toast.LENGTH_SHORT).show();
		}
		else
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	}
}
