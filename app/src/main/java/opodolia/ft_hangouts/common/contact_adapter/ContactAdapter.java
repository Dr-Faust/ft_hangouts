package opodolia.ft_hangouts.common.contact_adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.common.OnContactClick;

public class ContactAdapter extends RecyclerView.Adapter<ContactHolder>
	implements View.OnClickListener, View.OnLongClickListener {

	private final OnContactClick mOnContactClick;
	private List<Contact> contactData = new ArrayList<>();
	private Contact contact;

	public ContactAdapter(OnContactClick mOnContactClick) {
		this.mOnContactClick = mOnContactClick;
	}

	@Override
	public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_summary, parent, false);
		return new ContactHolder(view);
	}

	@Override
	public void onBindViewHolder(ContactHolder holder, int position) {
		Contact contact = contactData.get(position);
		holder.bind(contact);
		holder.itemView.setTag(contact);
		holder.itemView.setOnClickListener(this);
		holder.itemView.setOnLongClickListener(this);
	}

	@Override
	public int getItemCount() {
		return contactData.size();
	}

	public void setContactData(List<Contact> contacts) {
		contactData.clear();
		contactData.addAll(contacts);
		notifyDataSetChanged();
		Log.d("QWEEE", "size  = " + getItemCount());
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public void onClick(View view) {
		Contact contact = (Contact) view.getTag();
		mOnContactClick.onContactClick(contact.getId());
	}

	@Override
	public boolean onLongClick(View view) {
		setContact((Contact) view.getTag());
		return false;
	}
}
