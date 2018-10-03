package opodolia.ft_hangouts.utils;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher
{
	private static String 	TAG = MaskWatcher.class.getSimpleName();
	private boolean 		isRunning = false;
	private boolean 		isDeleting = false;
	private String			mask = "###-###-####";
	private TextInputEditText phoneNumber;
	private InputFilter.LengthFilter mLongLengthFilter = new InputFilter.LengthFilter(17);
	private InputFilter.LengthFilter mShortLengthFilter = new InputFilter.LengthFilter(12);

	public MaskWatcher(TextInputEditText phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
	{
		isDeleting = count > after;
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int start, int before, int count)
	{ }

	@Override
	public void afterTextChanged(Editable editable)
	{
		if (isRunning || isDeleting)
			return;

		isRunning = true;
		phoneNumberChangeListener(editable);
		isRunning = false;
	}

	private void phoneNumberChangeListener(Editable editable)
	{
		int editableLength = editable.length();

		if (editableLength == 1)
		{
			if (editable.charAt(0) == '+')
			{
				mask = "#### ## ###-##-##";
				phoneNumber.setFilters(new InputFilter[]{mLongLengthFilter});
			}
			else
			{
				mask = "###-###-####";
				phoneNumber.setFilters(new InputFilter[]{mShortLengthFilter});
			}
		}
		else if (editableLength == 12 && editable.charAt(0) != '+')
		{
			mask = "###-###-####";
			phoneNumber.setFilters(new InputFilter[]{mShortLengthFilter});
		}
		else if (editableLength == 17 && editable.charAt(0) != '+')
		{
			mask = "###-###-####";
			phoneNumber.setFilters(new InputFilter[]{mShortLengthFilter});
		}

		if (editableLength < mask.length() && editableLength > 0)
		{
			if (mask.charAt(editableLength) != '#')
				editable.append(mask.charAt(editableLength));
			else if (mask.charAt(editableLength - 1) != '#')
				editable.insert(editableLength - 1, mask, editableLength - 1, editableLength);
		}
	}

//	private void birthDateChangeListener(Editable editable)
//	{
//		int editableLength = editable.length();
//		mask = "##/##/####";
//
//		if (editableLength < mask.length() && editableLength > 0)
//		{
//			if (mask.charAt(editableLength) != '#')
//				editable.append(mask.charAt(editableLength));
//			else if (mask.charAt(editableLength - 1) != '#')
//				editable.insert(editableLength - 1, mask, editableLength - 1, editableLength);
//		}
//	}
}