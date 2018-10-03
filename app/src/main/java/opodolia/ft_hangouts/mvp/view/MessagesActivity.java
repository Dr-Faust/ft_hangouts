package opodolia.ft_hangouts.mvp.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.message_adapter.MessageAdapter;

public class MessagesActivity extends MyAppCompat implements Toolbar.OnMenuItemClickListener,
	View.OnClickListener {

	private Contact         contact;
	private EditText        etMessage;
	private ImageView       ivSentMessage;
	private MessageAdapter  messageAdapter;
	private RecyclerView    listMessages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		initViews();

		setMessageAdapter();
	}

	private void setMessageAdapter() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		messageAdapter = new MessageAdapter();

		listMessages.setLayoutManager(layoutManager);
		listMessages.setAdapter(messageAdapter);
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
		etMessage = findViewById(R.id.text_message);
		ivSentMessage = findViewById(R.id.iv_send_sms);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return false;
	}

	@Override
	public void onClick(View view) {

	}
}
