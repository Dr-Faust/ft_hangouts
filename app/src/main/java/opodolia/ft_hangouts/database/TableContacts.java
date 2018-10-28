package opodolia.ft_hangouts.database;

public class TableContacts {

    public static final String TABLE = "table_contacts";

    public static class COLUMN {
        public static final String ID = "_id";
        public static final String FIRST_NAME = "first_name";
	    public static final String LAST_NAME = "last_name";
	    public static final String PHONE_NUMBER = "phone_number";
        public static final String EMAIL = "email";
	    public static final String BIRTHDAY = "birthday";
	    public static final String CONTACT_NAME = "contact_name";
	    public static final String PHOTO_URI = "photo_uri";
    }

    public static final String CREATE_SCRIPT =
        String.format("create table %s ("
                + "%s integer primary key autoincrement,"
		        + "%s text,"
                + "%s text,"
		        + "%s text,"
		        + "%s text,"
		        + "%s text,"
		        + "%s text,"
                + "%s text" + ");",
            TABLE, COLUMN.ID, COLUMN.FIRST_NAME, COLUMN.LAST_NAME, COLUMN.PHONE_NUMBER,
	        COLUMN.EMAIL, COLUMN.BIRTHDAY, COLUMN.CONTACT_NAME, COLUMN.PHOTO_URI);
}
