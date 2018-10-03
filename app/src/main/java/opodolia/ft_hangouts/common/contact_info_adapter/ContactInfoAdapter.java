package opodolia.ft_hangouts.common.contact_info_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.common.Item;
import opodolia.ft_hangouts.common.OnItemClick;

public class ContactInfoAdapter extends RecyclerView.Adapter<ContactInfoHolder> {

	private final OnItemClick   mOnItemClick;
	private static String       TAG = ContactInfoAdapter.class.getSimpleName();
	private ArrayList<Item>     items;

	public ContactInfoAdapter(ArrayList<Item> items, OnItemClick mOnItemClick) {
		this.items = items;
		this.mOnItemClick = mOnItemClick;
	}

	@Override
	public ContactInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_info,
			parent, false);
		return new ContactInfoHolder(view, mOnItemClick);
	}

	@Override public void onBindViewHolder(@NonNull ContactInfoHolder holder, int position) {
		Item item = items.get(position);
		holder.bind(item);
		holder.itemView.setTag(item);
		holder.itemView.setOnClickListener(mInternalListener);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	private final View.OnClickListener mInternalListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Item item = (Item) view.getTag();
			mOnItemClick.onItemClick(item);
		}
	};
}
