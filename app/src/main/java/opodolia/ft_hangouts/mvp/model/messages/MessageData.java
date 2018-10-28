package opodolia.ft_hangouts.mvp.model.messages;

public class MessageData {

	private Boolean isMyMessage;
	private String messageText;
	private String messageTime;
	private String phoneNumber;

	public Boolean getMyMessage() {
		return isMyMessage;
	}

	public void setMyMessage(Boolean myMessage) {
		isMyMessage = myMessage;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
