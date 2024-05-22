package soft.evm.amblyopiamobilegames.analytics;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

//import static soft.evm.amblyopiamobilegames.BuildConfig.AMPLITUDE;
//import static soft.evm.amblyopiamobilegames.BuildConfig.SEGMENT;

public class MisMetricas {

    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void iniciar(Context context, Application application){
        new Thread() {
            public void run() {
                try {
                    iniciarFirebase(context);

                    //iniciarSegment(context);
                    //iniciarAmplitudeNativeSDK(context, application);
                }catch (RuntimeException e) {
                    // Ignore, most likely error during development
                    e.printStackTrace();
                }
            }


        }.start();
    }

    private static void iniciarFirebase(Context context) {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }


    /** firstUserExperienceGeneral
     *
     * --¿CUÁNTOS USUARIOS LLEGAN A JUGAR LA PRIMERA VEZ?
     *
     * First_Welcome -- 100%
     * First_Sesion -- ????
     * First_Main
     * First_Menu
     * First_Juego
     * First_Play
     *
     *
     * VALUES -> SharedPreferences firstUserExperienceGeneral
     *
     */
    public static final String FIRST_WELCOME = "First_Welcome";
    public static final String FIRST_SESION = "First_Sesion";
    public static final String FIRST_MAIN = "First_Main";
    public static final String FIRST_MENU = "First_Menu";
    public static final String FIRST_JUEGO = "First_Juego";
    public static final String FIRST_PLAY = "First_Play";
    public static void firstUserExperienceGeneral(Context context, String fase){
        new Thread() {
            public void run() {
                try {
                    SharedPreferences firstUserExperience = context.getSharedPreferences("FirstUserExperienceGeneral", Context.MODE_PRIVATE); //-> Primera Experiencia de Usauario
                    if (firstUserExperience.getBoolean(fase, true)) {
                        //Analytics.with(context).track(fase);
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, fase);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        SharedPreferences.Editor editor = firstUserExperience.edit();
                        editor.putBoolean(fase, false);
                        editor.apply();
                    }
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                    //59
                }catch (RuntimeException e){
                    e.printStackTrace();
                    //59
                }
            }
        }.start();
    }

}
