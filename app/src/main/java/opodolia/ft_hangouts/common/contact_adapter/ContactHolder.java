package opodolia.ft_hangouts.common.contact_adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.common.Contact;
import static opodolia.ft_hangouts.app.Config.MENU_DELETE;
import static opodolia.ft_hangouts.app.Config.MENU_EDIT;

public class ContactHolder extends RecyclerView.ViewHolder implements
	View.OnCreateContextMenuListener {

	private static String       TAG = ContactHolder.class.getSimpleName();
	private TextView    contactName;
	private ImageView   contactPhoto;

	public ContactHolder(View itemView) {
		super(itemView);
		contactName = itemView.findViewById(R.id.contact_name);
		contactPhoto = itemView.findViewById(R.id.contact_photo);
		itemView.setOnCreateContextMenuListener(this);
	}

	void bind(Contact contact) {
		this.contactName.setText(contact.getContactName());
		String contactPhotoUri =  contact.getPhotoUri();
		Log.i(TAG, "URI = " + contactPhotoUri);
		Glide.with(itemView)
			.load(contactPhotoUri)
			.apply(RequestOptions.circleCropTransform())
			.into(contactPhoto);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
		ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(itemView.getResources().getString(R.string.select_action));
		menu.add(Menu.NONE, MENU_EDIT, 0, itemView.getResources().getString(R.string.action_edit_contact));
		menu.add(Menu.NONE, MENU_DELETE, 0, itemView.getResources().getString(R.string.action_delete_contact));
	}
}