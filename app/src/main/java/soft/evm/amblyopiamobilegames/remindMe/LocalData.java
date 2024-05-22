package soft.evm.amblyopiamobilegames.remindMe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jaison on 18/06/17.
 */

public class LocalData {

    private static final String APP_SHARED_PREFS = "RemindMePref";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus="reminderStatus";
    private static final String hour="hour";
    private static final String min="min";

    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, true);//nosotros lo modificamos a true porque nos interesa para la retención
    }

    public void setReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.apply();
    }

    // Settings Page Reminder Time (Hour)

    public int get_hour()
    {
        return appSharedPrefs.getInt(hour, 20);//lo dejamos por defecto porque las 8 de la tarde es una buena hora
    }

    public void set_hour(int h)
    {
        prefsEditor.putInt(hour, h);
        prefsEditor.apply();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_min()
    {
        return appSharedPrefs.getInt(min, 0);//lo dejamos a 0
    }

    public void set_min(int m)
    {
        prefsEditor.putInt(min, m);
        prefsEditor.apply();
    }

    public void reset()
    {
        prefsEditor.clear();
        prefsEditor.apply();

    }

}
