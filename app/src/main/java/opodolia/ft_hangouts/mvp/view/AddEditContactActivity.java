package opodolia.ft_hangouts.mvp.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import opodolia.ft_hangouts.common.Contact;
import opodolia.ft_hangouts.utils.MaskWatcher;
import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.utils.RetainedContactData;
import opodolia.ft_hangouts.app.Config;
import opodolia.ft_hangouts.app.MyAppCompat;
import opodolia.ft_hangouts.utils.CompareDrawablesKt;
import opodolia.ft_hangouts.mvp.ContactData;
import opodolia.ft_hangouts.permissions.PermissionChecker;

public class AddEditContactActivity extends MyAppCompat implements Toolbar.OnMenuItemClickListener,
            View.OnClickListener
{
	private static final int					REQUEST_PHOTO_CAPTURE = 1, REQUEST_PHOTO_PICK = 2;
	private File								photo;
	private Uri 								mImageUri = null;
	private ImageView 							photoContact;
	private static String       				TAG = AddEditContactActivity.class.getSimpleName();
	private RetainedContactData                 retainedContactData;
	private TextInputEditText 					firstName, lastName, phoneNumber, email, birthday;
	private MaskWatcher                         mPhoneMaskWatcher;
	private Calendar 							myCalendar;
	private DatePickerDialog.OnDateSetListener	date;
	private Boolean                             newContact;
	private long                                contactId = -1;
	private Boolean                             calledFromContactsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        initViews();

		// find the retained fragment on activity restarts
		FragmentManager fm = getFragmentManager();
		retainedContactData = (RetainedContactData) fm.findFragmentByTag("retainedContactData");

		// create the fragment and data the first time
		if (retainedContactData == null) {
			// addContactToDb the fragment
			retainedContactData = new RetainedContactData();
			fm.beginTransaction().add(retainedContactData, "retainedContactData").commit();
			retainedContactData.setImageUri(mImageUri);
		}
		else
			resetData();
    }

    private String makeContactName(ContactData contactData) {
	    String[] contactInfo = {contactData.getFirstName(), contactData.getLastName(),
		    contactData.getPhoneNumber(), contactData.getEmail()};

	    StringBuilder contactName = new StringBuilder();
	    String value;
	    int counter = 0;

	    for (int i = 0; i < contactInfo.length; i++) {
		    value = contactInfo[i];
		    if (counter >= 1 && i >= 2)
			    break;
		    if (!value.equals("")) {
			    contactName.append(value).append(" ");
			    counter++;
		    }
	    }
	    return String.valueOf(contactName);
    }

	public ContactData getRetainedContactData() {
		ContactData contactData = new ContactData();
		contactData.setFirstName(firstName.getText().toString());
		contactData.setLastName(lastName.getText().toString());
		contactData.setPhoneNumber(phoneNumber.getText().toString());
		contactData.setEmail(email.getText().toString());
		contactData.setBirthday(birthday.getText().toString());
		contactData.setContactName(makeContactName(contactData));
		if (mImageUri == null)
			contactData.setPhotoUri("");
		else
			contactData.setPhotoUri(mImageUri.toString());

		return contactData;
	}

	public void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// store the data in the fragment
		retainedContactData.setImageUri(mImageUri);
		phoneNumber.removeTextChangedListener(mPhoneMaskWatcher);
	}

	private void resetData()
	{
		mImageUri = retainedContactData.getImageUri();
		if (mImageUri != null)
			Glide.with(this)
				.load(mImageUri.toString())
				.apply(RequestOptions.centerCropTransform())
				.into(photoContact);
	}


	private void setColorForIcons() {
    	ImageView iconFirstName = findViewById(R.id.icon_first_name);
		ImageView iconPhoneNumber = findViewById(R.id.icon_phone_number);
		ImageView iconEmail = findViewById(R.id.icon_email);
		ImageView iconBirthday = findViewById(R.id.icon_birthday);

		iconFirstName.setColorFilter(getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));
		iconPhoneNumber.setColorFilter(getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));
		iconEmail.setColorFilter(getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));
		iconBirthday.setColorFilter(getResources().getColor(
			MyAppCompat.currentCollapsingToolbarBackgroundColor));
	}

	private void initViews()
	{
		//setting up toolbar, back button and save button
		Toolbar toolbar = findViewById(R.id.toolbar_contacts);
		toolbar.inflateMenu(R.menu.menu_add_edit);
		toolbar.setOnMenuItemClickListener(this);

		toolbar.setNavigationIcon(R.drawable.ic_close_white);
		toolbar.setNavigationOnClickListener(v -> onBackPressed());

		newContact = getIntent().getBooleanExtra("new_contact", true);
		if (newContact)
			toolbar.setTitle(getString(R.string.new_contact));
		else
			toolbar.setTitle(getString(R.string.edit_contact));

		photoContact = findViewById(R.id.photo);
		firstName = findViewById(R.id.first_name);
		lastName = findViewById(R.id.last_name);
		phoneNumber = findViewById(R.id.phone_number);
		email = findViewById(R.id.email);
		birthday = findViewById(R.id.birthday);

		setColorForIcons();

		mPhoneMaskWatcher = new MaskWatcher(phoneNumber);
		phoneNumber.addTextChangedListener(mPhoneMaskWatcher);

		photoContact.setOnClickListener(this);
		birthday.setOnClickListener(this);

		RelativeLayout photoLayout = findViewById(R.id.photo_layout);
		photoLayout.setBackgroundColor(
			getResources().getColor(currentCollapsingToolbarBackgroundColor));

		initDatePicker();

		presenter.attachView(this);
		if (!newContact) {
			Contact contact = (Contact) getIntent().getSerializableExtra("contact");
			calledFromContactsActivity = getIntent().getBooleanExtra("called_from_contacts_activity", false);
			loadContact(contact);
		}
		Log.i(TAG, "BOOLEAN = " + calledFromContactsActivity);
	}

	public void loadContact(Contact contact) {
    	contactId = contact.getId();
    	setContactPhoto(contact);
    	firstName.setText(contact.getFirstName());
    	lastName.setText(contact.getLastName());
    	phoneNumber.setText(contact.getPhoneNumber());
    	email.setText(contact.getEmail());
    	birthday.setText(contact.getBirthday());
	}

	private void setContactPhoto(Contact contact) {
		String contactPhotoUri =  contact.getPhotoUri();
		if (!contactPhotoUri.equals("")) {
			mImageUri = Uri.parse(contactPhotoUri);
			Glide.with(this)
				.load(contactPhotoUri)
				.apply(RequestOptions.centerCropTransform())
				.into(photoContact);
		}
	}

	public void initDatePicker()
	{
		myCalendar = Calendar.getInstance();

		date = (view, year, monthOfYear, dayOfMonth) -> {
			// TODO Auto-generated method stub
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		};
	}

	private void updateLabel()
	{
		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		birthday.setText(sdf.format(myCalendar.getTime()));
	}

	@Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
            	if (newContact)
	                presenter.addContactToDb();
            	else
		            presenter.editContactInDb(contactId, calledFromContactsActivity);
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
		switch (v.getId())
		{
			case R.id.photo:
				showPhotoDialog();
				break;
			case R.id.birthday:
				new DatePickerDialog(this, date, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
    }

    private void showPhotoDialog()
	{
		List<String> listItems = new ArrayList<>();
		final boolean photoExists;

		//if (photoContact.getDrawable().getConstantState().equals(this.getResources().getDrawable(R.drawable.ic_person_white, this.getTheme()).getConstantState()))
		//if (photoContact.getDrawable().getConstantState() == Objects.requireNonNull(
		//	ContextCompat.getDrawable(this, R.drawable.ic_person_white)).getConstantState())
		//if ((Integer)photoContact.getTag() == R.drawable.ic_person_white)
		//if (ContextCompat.getDrawable(this, R.drawable.ic_person_white).)
		if (CompareDrawablesKt.bytesEqualTo(photoContact.getDrawable(), ContextCompat.getDrawable(this, R.drawable.ic_person_white)))
		{
			Log.i(TAG, "Photo doesn't exist!");
			photoExists = false;
			listItems.add(getString(R.string.take_photo));
			listItems.add(getString(R.string.choose_photo));
		}
		else
		{
			Log.i(TAG, "Photo exists!");
			photoExists = true;
			listItems.add(getString(R.string.remove_photo));
			listItems.add(getString(R.string.take_new_photo));
			listItems.add(getString(R.string.select_new_photo));
		}

		final CharSequence[] items= listItems.toArray(new CharSequence[listItems.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(this, currentDialogStyle);
		builder.setTitle(getString(R.string.change_photo));
		builder.setItems(items, (dialog, item) -> {
			if (photoExists)
			{
				// Do something with the selection
				switch (item)
				{
					case 0:
						photoContact.setImageResource(R.drawable.ic_person_white);
						mImageUri = null;
						break;
					case 1:
						new PermissionChecker(this, currentDialogStyle).cameraStoragePermissionChecker();
						if (ActivityCompat.checkSelfPermission(AddEditContactActivity.this,
								Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
								|| ActivityCompat.checkSelfPermission(AddEditContactActivity.this,
								Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
							return;
						else
							launchCamera();
						break;
					case 2:
						pickPhotoFromGallery();
						break;
				}
			}
			else
			{
				switch (item)
				{
					case 0:
						new PermissionChecker(this, currentDialogStyle).cameraStoragePermissionChecker();
						if (ActivityCompat.checkSelfPermission(AddEditContactActivity.this,
								Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
								|| ActivityCompat.checkSelfPermission(AddEditContactActivity.this,
								Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
							return;
						else
							launchCamera();
						break;
					case 1:
						pickPhotoFromGallery();
						break;
				}
			}
		});

		String negativeText = this.getString(R.string.cancel);
		builder.setNegativeButton(negativeText, (dialog, which) -> {
			// negative button logic
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if (requestCode == Config.REQUEST_CAMERA_STORAGE_PERMISSION)
		{
			Log.i(TAG, "Received response for Camera and Storage permissions request.");

			boolean permission = true;
			// We have requested multiple permissions for contacts, so all of them need to be
			// checked.
			if (grantResults.length < 1)
				permission = false;

			// Verify that each required permission has been granted, otherwise return false.
			int counter = 0;
			int number = 0;
			for (int result : grantResults)
			{
				counter++;
				if (result != PackageManager.PERMISSION_GRANTED)
				{
					number += counter;
					permission = false;
				}
			}

			if (permission)
				launchCamera();
			else if (number == 1)
				Toast.makeText(this, getString(R.string.negative_response_camera_permission),
						Toast.LENGTH_SHORT).show();
			else if (number == 2)
				Toast.makeText(this, getString(R.string.negative_response_storage_permission),
						Toast.LENGTH_SHORT).show();
			else if (number == 3)
				Toast.makeText(this, getString(R.string.negative_response_storage_camera_permission),
						Toast.LENGTH_SHORT).show();
		}
		else if (requestCode == Config.REQUEST_CAMERA_PERMISSION)
		{
			Log.i(TAG, "Received response for Camera permissions request.");

			boolean permission = true;
			// We have requested multiple permissions for contacts, so all of them need to be
			// checked.
			if (grantResults.length < 1)
				permission = false;

			// Verify that each required permission has been granted, otherwise return false.

			for (int result : grantResults)
				if (result != PackageManager.PERMISSION_GRANTED)
					permission = false;

			if (permission)
				launchCamera();
			else
				Toast.makeText(this, getString(R.string.negative_response_camera_permission),
						Toast.LENGTH_SHORT).show();
		}
		else if (requestCode == Config.REQUEST_STORAGE_PERMISSION)
		{
			Log.i(TAG, "Received response for Storage permissions request.");

			boolean permission = true;
			// We have requested multiple permissions for contacts, so all of them need to be
			// checked.
			if (grantResults.length < 1)
				permission = false;

			// Verify that each required permission has been granted, otherwise return false.

			for (int result : grantResults)
				if (result != PackageManager.PERMISSION_GRANTED)
					permission = false;

			if (permission)
				launchCamera();
			else
				Toast.makeText(this, getString(R.string.negative_response_storage_permission),
						Toast.LENGTH_SHORT).show();
		}
		else
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public void launchCamera()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try
		{
			// place where to store picture taken from camera
			photo = createImageFile();
		}
		catch (Exception e)
		{
			Log.w(TAG, "Can't create file to take picture!");
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			mImageUri = FileProvider.getUriForFile(this, "opodolia.ft_hangouts.activity", photo);
		}
		else
			mImageUri = Uri.fromFile(photo);
		Log.i(TAG, "Uri: " + mImageUri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

		startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
	}

	public void pickPhotoFromGallery()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PHOTO_PICK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		boolean deleted;

		if (requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK)
		{
			//getContentResolver().notifyChange(mImageUri, null);
			//Bitmap reducedSizeBitmap = getBitmap(mImageUri.getPath());
			//if(reducedSizeBitmap != null)
			//	photoContact.setImageBitmap(reducedSizeBitmap);
			//photoContact.setImageURI(mImageUri);
			Glide.with(this)
					.load(mImageUri.toString())
					.apply(RequestOptions.centerCropTransform())
					.into(photoContact);
		}
		else if (requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				Uri tmpUri = data.getData();

				String unusablePath;
				if (tmpUri != null) {
					unusablePath = tmpUri.getPath();
					int startIndex = unusablePath.indexOf("external/");
					int endIndex = unusablePath.indexOf("/ORIGINAL");
					String embeddedPath = unusablePath.substring(startIndex, endIndex);

					Uri.Builder builder = tmpUri.buildUpon();
					builder.path(embeddedPath);
					builder.authority("media");
					mImageUri = builder.build();
				}
			}
			else {
				mImageUri = data.getData();
			}

			Log.i(TAG, "Uri: " + mImageUri);



			//getContentResolver().notifyChange(mImageUri, null);
			//Bitmap reducedSizeBitmap = getBitmap(mImageUri.getPath());
			//if(reducedSizeBitmap != null)
			//	photoContact.setImageBitmap(reducedSizeBitmap);
			//photoContact.setImageURI(mImageUri);
			Glide.with(this)
					.load(mImageUri.toString())
					.apply(RequestOptions.centerCropTransform())
					.into(photoContact);
		}
		else if (photo != null)
		{
			deleted = photo.delete();
			if (!deleted)
				Log.w(TAG, "Deletion of photo was not successful.");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

//	private Bitmap getBitmap(String path)
//	{
//		Uri uri = Uri.fromFile(new File(path));
//		InputStream in;
//		try {
//			final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
//			in = getContentResolver().openInputStream(uri);
//
//			// Decode image size
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(in, null, o);
//			if (in != null)
//				in.close();
//
//			int scale = 1;
//			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE)
//				scale++;
//			Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
//
//			Bitmap b;
//			in = getContentResolver().openInputStream(uri);
//			if (scale > 1)
//			{
//				scale--;
//				// scale to max possible inSampleSize that still yields an image
//				// larger than target
//				o = new BitmapFactory.Options();
//				o.inSampleSize = scale;
//				b = BitmapFactory.decodeStream(in, null, o);
//
//				// resize to desired dimensions
//				int height = b.getHeight();
//				int width = b.getWidth();
//				Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);
//
//				double y = Math.sqrt(IMAGE_MAX_SIZE
//						/ (((double) width) / height));
//				double x = (y / height) * width;
//
//				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
//						(int) y, true);
//				b.recycle();
//				b = scaledBitmap;
//
//				System.gc();
//			}
//			else
//				b = BitmapFactory.decodeStream(in);
//			if (in != null)
//				in.close();
//
//			Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
//					b.getHeight());
//			return b;
//		}
//		catch (IOException e)
//		{
//			Log.e("", e.getMessage(), e);
//			return null;
//		}
//	}

	private File createImageFile() throws IOException
	{
		String path = Environment.getExternalStorageDirectory() + File.separator
				+ "Contact_photo" + File.separator;
		String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss", Locale.US).format(new Date());
		String imageFileName;
		File image = null;
		File tmpDir = new File(path);
		Log.i(TAG, "Time: " + timeStamp);

		imageFileName = "Contact_photo_" + timeStamp + "_";

		Log.i(TAG, "ImgName: " + imageFileName);
		boolean isDirectoryCreated = tmpDir.exists();
		if (!isDirectoryCreated)
			isDirectoryCreated = tmpDir.mkdirs();
		if (isDirectoryCreated)
			image = File.createTempFile(imageFileName, ".jpg", tmpDir);
		return image;
	}
}
