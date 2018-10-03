package opodolia.ft_hangouts.common.contact_info_adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.common.Item;
import opodolia.ft_hangouts.common.OnItemClick;
import opodolia.ft_hangouts.mvp.view.ContactInfoActivity;

import static opodolia.ft_hangouts.app.Config.MENU_COPY_BIRTHDAY;
import static opodolia.ft_hangouts.app.Config.MENU_COPY_EMAIL;
import static opodolia.ft_hangouts.app.Config.MENU_COPY_PHONE;

public class ContactInfoHolder extends RecyclerView.ViewHolder implements
	View.OnCreateContextMenuListener, View.OnClickListener {

	private static String           TAG = ContactInfoHolder.class.getSimpleName();

	private ImageView   labelIcon;
	private TextView    labelKey;
	private TextView    labelValue;
	private ImageView   smsIcon;
	private final OnItemClick mOnItemClick;

	public ContactInfoHolder(View view, OnItemClick mOnItemClick) {
		super(view);
		this.mOnItemClick = mOnItemClick;
		labelIcon = view.findViewById(R.id.label_icon);
		labelKey = view.findViewById(R.id.label_key);
		labelValue = view.findViewById(R.id.label_value);
		smsIcon = view.findViewById(R.id.sms_icon);

		view.setOnCreateContextMenuListener(this);
	}

	void bind(Item item) {
		if (Objects.equals(item.getKey(),
			itemView.getResources().getString(R.string.phone_number))) {
			this.labelIcon.setImageDrawable(itemView.getResources()
				.getDrawable(R.drawable.ic_phone_black));
			smsIcon.setVisibility(View.VISIBLE);
			this.smsIcon.setOnClickListener(this);
		}
		else if (Objects.equals(item.getKey(),
			itemView.getResources().getString(R.string.email)))
			this.labelIcon.setImageDrawable(itemView.getResources()
				.getDrawable(R.drawable.ic_email_black));
		else if (Objects.equals(item.getKey(),
			itemView.getResources().getString(R.string.birthday)))
			this.labelIcon.setImageDrawable(itemView.getResources()
				.getDrawable(R.drawable.ic_cake_black));

		this.labelIcon.setColorFilter(itemView.getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));
		this.smsIcon.setColorFilter(itemView.getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));

		this.labelKey.setText(item.getKey());
		this.labelValue.setText(item.getValue());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
		ContextMenu.ContextMenuInfo contextMenuInfo) {
		menu.setHeaderTitle(labelValue.getText());
		if (Objects.equals(labelKey.getText(),
			itemView.getResources().getString(R.string.phone_number)))
			menu.add(Menu.NONE, MENU_COPY_PHONE, 0, itemView.getResources().getString(R.string.action_copy));
		else if (Objects.equals(labelKey.getText(),
			itemView.getResources().getString(R.string.email)))
			menu.add(Menu.NONE, MENU_COPY_EMAIL, 0, itemView.getResources().getString(R.string.action_copy));
		else if (Objects.equals(labelKey.getText(),
			itemView.getResources().getString(R.string.birthday)))
			menu.add(Menu.NONE, MENU_COPY_BIRTHDAY, 0, itemView.getResources().getString(R.string.action_copy));
	}

	@Override
	public void onClick(View view) {
		mOnItemClick.onItemClick();
	}
}
