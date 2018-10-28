package opodolia.ft_hangouts.database;

public class TableMessages {

	public static final String TABLE = "table_messages";

	public static class COLUMN {
		public static final String ID = "_id";
		public static final String MESSAGE_TEXT = "message_text";
		public static final String MESSAGE_TIME = "message_data";
		public static final String PHONE_NUMBER = "phone_number";
		public static final String IS_MY_MESSAGE = "is_my_message";
	}

	public static final String CREATE_SCRIPT =
		String.format("create table %s ("
				+ "%s integer primary key autoincrement,"
				+ "%s text,"
				+ "%s text,"
				+ "%s text,"
				+ "%s text" + ");",
			TABLE, TableMessages.COLUMN.ID, TableMessages.COLUMN.MESSAGE_TEXT, TableMessages.COLUMN.MESSAGE_TIME,
			TableMessages.COLUMN.PHONE_NUMBER, TableMessages.COLUMN.IS_MY_MESSAGE);
}
