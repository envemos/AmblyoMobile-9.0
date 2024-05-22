package soft.evm.amblyopiamobilegames.ads;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/*import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;*/

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;

import static soft.evm.amblyopiamobilegames.BuildConfig.ADMOB_APP_ID;
import static soft.evm.amblyopiamobilegames.BuildConfig.InterstitialAd_ID;
import static soft.evm.amblyopiamobilegames.BuildConfig.RewardedVideoAd_ID;

public class MiAdMob {

    //AHORA USAMOS YODO MOBILE

    //public static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";// -> demo
    //public static final String APP_ID = ADMOB_APP_ID;

    //public static final String mInterstitialAd_ID = "ca-app-pub-3940256099942544/1033173712";// -> demo
    //public static final String mInterstitialAd_ID = InterstitialAd_ID;

    //public static final String mRewardedVideoAd_ID = "ca-app-pub-3940256099942544/5224354917";// -> demo
    //public static final String mRewardedVideoAd_ID = RewardedVideoAd_ID;

    //public static InterstitialAd mInterstitialAd;

    //public static RewardedVideoAd mRewardedVideoAd;

    //para los banners
    //public static AdRequest adRequest;

    //public static boolean adRequested = false;

    //iniciado
    //public static boolean inicializado = false;

    /*public static void iniciarAdMob(Context context){
        /*if(!inicializado) {
            MobileAds.initialize(context, APP_ID);
            inicializado = true;
        }*/
    //}

    /*public static void iniInterstitialAd(Context context){
        /*if(!MainActivity.mIsPremium) {
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(mInterstitialAd_ID);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });

            // Use an activity context to get the rewarded video instance.
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            //listener en GameActivity
            mRewardedVideoAd.loadAd(mRewardedVideoAd_ID, new AdRequest.Builder().build());
        }*/
    /*}

    public static void showInterstitialAd(){
        /*if(!MainActivity.mIsPremium) {
            try {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }catch (NullPointerException e){
                //version 85
            }
        }*/
    /*}

    public static void showRewardedVideoAd(){
        /*if (mRewardedVideoAd.isLoaded())
            mRewardedVideoAd.show();*/
    /*}

    public static void cargarbanner(AdView mAdView) {
        /*if(!MainActivity.mIsPremium) {
            adRequestBanner();
            mostrarBanner(mAdView, adRequest);
        }
        else
            mAdView.setVisibility(View.GONE);*/
    //}

    private static void adRequestBanner() {
        /*if(!adRequested) {
            adRequest = new AdRequest.Builder().build();
            adRequested = true;
        }*/
    }

    /*private static void mostrarBanner(AdView mAdView, AdRequest adRequest) {
        //mAdView.loadAd(adRequest);
    }

    private static void cargarBanners(View view){
        /*cargarbanner(view.findViewById(R.id.bannerSuperior));
        cargarbanner(view.findViewById(R.id.bannerInferior));*/
    //}

    // metodos para cargar anuncios
    public static void mostrarAds(View view){
        /*if (!MainActivity.mIsPremium)
            cargarBanners(view);
        else
            ocultarEspacioAnuncios(view);*/
    }

    private static void ocultarEspacioAnuncios(View view){
        /*LinearLayout linearLayout = view.findViewById(R.id.LayoutBannerSuperior);
        linearLayout.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.LayoutBannerInferior);
        linearLayout.setVisibility(View.GONE);
        View line = view.findViewById(R.id.lineSuperior);
        line.setVisibility(View.GONE);
        line = view.findViewById(R.id.lineInferior);
        line.setVisibility(View.GONE);*/
    }

}
