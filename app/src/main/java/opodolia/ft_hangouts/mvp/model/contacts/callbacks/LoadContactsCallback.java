package opodolia.ft_hangouts.mvp.model.contacts.callbacks;

import java.util.List;
import opodolia.ft_hangouts.common.Contact;

public interface LoadContactsCallback {
	void onContactsLoaded(List<Contact> contacts);
}
