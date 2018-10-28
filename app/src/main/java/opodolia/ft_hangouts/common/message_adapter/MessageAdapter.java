package opodolia.ft_hangouts.common.message_adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.Message;
import opodolia.ft_hangouts.common.contact_adapter.OnContactClickListener;
import opodolia.ft_hangouts.mvp.view.MessagesActivity;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder>
	implements View.OnLongClickListener {

	private static String           TAG = MessageAdapter.class.getSimpleName();

	private List<Message>   messages = new ArrayList<>();
	private Message         message;
	private Activity        activity;

	public MessageAdapter(Activity activity ) {
		this.activity = activity;
	}

	@NonNull
	@Override
	public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
		return new MessageHolder(view, activity);
	}

	@Override public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
		Message message = messages.get(position);
		holder.bind(message);
		holder.itemView.setTag(message);
	}

	@Override
	public int getItemCount() {
		return messages.size();
	}

	@Override
	public boolean onLongClick(View view) {
		setMessage((Message) view.getTag());
		return false;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setMessages(List<Message> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		notifyDataSetChanged();
		Log.d(TAG, "MESSAGES SIZE = " + getItemCount());
	}
}
