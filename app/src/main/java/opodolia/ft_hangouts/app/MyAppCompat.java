package opodolia.ft_hangouts.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import opodolia.ft_hangouts.R;
import opodolia.ft_hangouts.database.DbHelper;
import opodolia.ft_hangouts.mvp.model.Model;
import opodolia.ft_hangouts.mvp.presenter.Presenter;

@SuppressLint("Registered")
public class MyAppCompat extends AppCompatActivity
{
    protected static int        currentTheme = 0;
    public static int           currentDialogStyle = R.style.DialogTheme_Green;
	public static int           currentCollapsingToolbarBackgroundColor = R.color.light_green;
    private Calendar            date = null;
    protected Presenter         presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        currentTheme = prefs.getInt("current_theme", 0);
        switch (currentTheme)
        {
            case 0: // Green
                setTheme(R.style.AppTheme_Green_NoActionBar);
                currentDialogStyle = R.style.DialogTheme_Green;
                currentCollapsingToolbarBackgroundColor = R.color.green;
                break;
            case 1: // Blue
                setTheme(R.style.AppTheme_Blue_NoActionBar);
                currentDialogStyle = R.style.DialogTheme_Blue;
	            currentCollapsingToolbarBackgroundColor = R.color.blue;
                break;
            case 2: // Grey
                setTheme(R.style.AppTheme_Grey_NoActionBar);
                currentDialogStyle = R.style.DialogTheme_Grey;
	            currentCollapsingToolbarBackgroundColor = R.color.grey;
                break;
            case 3: // Brown
                setTheme(R.style.AppTheme_Brown_NoActionBar);
                currentDialogStyle = R.style.DialogTheme_Brown;
	            currentCollapsingToolbarBackgroundColor = R.color.brown;
                break;
            case 4: // Yellow
                setTheme(R.style.AppTheme_Yellow_NoActionBar);
                currentDialogStyle = R.style.DialogTheme_Yellow;
	            currentCollapsingToolbarBackgroundColor = R.color.yellow;
                break;
        }
        super.onCreate(savedInstanceState);

        DbHelper dbHelper = new DbHelper(this);
        Model model = new Model(dbHelper);
	    presenter = new Presenter(model);
	}

    @Override
    protected void onPause()
    {
        super.onPause();

        //save time and date at the moment when app paused
        date = Calendar.getInstance();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (date != null)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String dateStr = dateFormat.format(date.getTime());

            Toast.makeText(getApplicationContext(), dateStr, Toast.LENGTH_SHORT).show();
        }
    }
}
