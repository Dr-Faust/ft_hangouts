package opodolia.ft_hangouts.mvp.model.messages.callbacks;

import java.util.List;
import opodolia.ft_hangouts.common.Message;

public interface LoadMessagesCallback {
	void onMessagesLoaded(List<Message> messages);
}
