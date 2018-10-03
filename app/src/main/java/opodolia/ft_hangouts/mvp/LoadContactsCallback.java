package opodolia.ft_hangouts.mvp;

import java.util.List;
import opodolia.ft_hangouts.common.Contact;

public interface LoadContactsCallback {
	void onLoad(List<Contact> contacts);
}
