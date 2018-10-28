package opodolia.ft_hangouts.common.message_adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.common.Message;

import static opodolia.ft_hangouts.app.Config.MENU_DELETE;

public class MessageHolder extends RecyclerView.ViewHolder implements
	View.OnCreateContextMenuListener{

	private static String       TAG = MessageHolder.class.getSimpleName();

	private TextView            messageTextLeft;
	private TextView            messageTextRight;

	public MessageHolder(View itemView, Activity activity) {
		super(itemView);
		messageTextLeft = itemView.findViewById(R.id.tv_left_message);
		messageTextRight = itemView.findViewById(R.id.tv_right_message);
		itemView.setOnCreateContextMenuListener(this);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		messageTextRight.setMaxWidth(width / 2);
		messageTextLeft.setMaxWidth(width / 2);
	}

	void bind(Message message) {
		Log.d(TAG, "MY MESSAGE = " + message.getMyMessage().toString());
		if (message.getMyMessage()) {
			messageTextRight.setText(message.getMessageText());
			messageTextRight.setVisibility(View.VISIBLE);
			messageTextLeft.setVisibility(View.GONE);
		}
		else {
			messageTextLeft.setText(message.getMessageText());
			messageTextLeft.setVisibility(View.VISIBLE);
			messageTextRight.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
	                                ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(itemView.getResources().getString(R.string.select_action));
		menu.add(Menu.NONE, MENU_DELETE, 0, itemView.getResources().getString(R.string.action_delete));
	}
}
