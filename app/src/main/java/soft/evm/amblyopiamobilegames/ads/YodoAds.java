package soft.evm.amblyopiamobilegames.ads;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.yodo1.mas.Yodo1Mas;

import soft.evm.amblyopiamobilegames.ActividadActivity;
import soft.evm.amblyopiamobilegames.BuildConfig;
import soft.evm.amblyopiamobilegames.DailyTimeActivity;
import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.RemindMeActivity;
import soft.evm.amblyopiamobilegames.SesionActivity;
import soft.evm.amblyopiamobilegames.SettingsActivity;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Reglas;
import soft.evm.amblyopiamobilegames.juegos.YoutubeActivity;
import soft.evm.amblyopiamobilegames.juegos.breaker.BreakerConfig;
import soft.evm.amblyopiamobilegames.juegos.flappy.game.FlappyGame;
import soft.evm.amblyopiamobilegames.juegos.snake.SnakeConfig;
import soft.evm.amblyopiamobilegames.juegos.snake.game.SnakeGame;
import soft.evm.amblyopiamobilegames.juegos.space.SpaceConfig;
import soft.evm.amblyopiamobilegames.juegos.tetris.TetrisConfig;
import soft.evm.amblyopiamobilegames.juegos.tetris.game.TetrisGame;
import soft.evm.amblyopiamobilegames.table.TableViewHistorial;
import soft.evm.amblyopiamobilegames.table.TableViewRanking;

import static soft.evm.amblyopiamobilegames.BuildConfig.ADMOB_APP_ID;

public class YodoAds {

    //public static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";// -> demo
    public static final String APP_ID = ADMOB_APP_ID;

    public static final String AppKey = BuildConfig.AppKey;

    private static void ocultarBanner(View view){
        LinearLayout linearLayout = view.findViewById(R.id.LayoutBannerInferior);
        linearLayout.setVisibility(View.GONE);
        View line = view.findViewById(R.id.lineInferior);
        line.setVisibility(View.GONE);
    }

    // metodos para cargar anuncios
    public static void mostrarAds(View view){
        //borramos la zona del baner superior porque no lo usaremos en Yodo
        LinearLayout linearLayout = view.findViewById(R.id.LayoutBannerSuperior);
        linearLayout.setVisibility(View.GONE);
        View line = view.findViewById(R.id.lineSuperior);
        line.setVisibility(View.GONE);
        if (MainActivity.mIsPremium)
            ocultarBanner(view);
    }

    //no mostrar en el mainactivity
    /*public static void showInterstitialAd(MainActivity mainActivity) {
        if (!MainActivity.mIsPremium) {
            /*boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(mainActivity);
            }
        }
    }*/

    public static void showInterstitialAd(TetrisConfig tetrisConfig) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(tetrisConfig);
            }
        }
    }

    public static void showInterstitialAd(ActividadActivity actividadActivity) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(actividadActivity);
            }
        }
    }

    public static void showInterstitialAd(SettingsActivity settingsActivity) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(settingsActivity);
            }
        }
    }

    public static void showInterstitialAd(GameActivity gameActivity) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(gameActivity);
            }
        }
    }

    public static void showInterstitialAd(BreakerConfig breakerConfig) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(breakerConfig);
            }
        }
    }

    public static void showInterstitialAd(Reglas reglas) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(reglas);
            }
        }
    }

    public static void showInterstitialAd(SnakeConfig snakeConfig) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(snakeConfig);
            }
        }
    }

    public static void showInterstitialAd(SpaceConfig spaceConfig) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(spaceConfig);
            }
        }
    }

    public static void showInterstitialAd(DailyTimeActivity dailyTimeActivity) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(dailyTimeActivity);
            }
        }
    }

    public static void showInterstitialAd(RemindMeActivity remindMeActivity) {
        if (!MainActivity.mIsPremium) {
            boolean hasAds = Yodo1Mas.getInstance().isInterstitialAdLoaded();
            if (hasAds) {
                Yodo1Mas.getInstance().showInterstitialAd(remindMeActivity);
            }
        }
    }

    public static void showBannerAd(SettingsActivity settingsActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(settingsActivity);
        }
    }

    //no mostrar banner en el mainactivity
    /*public static void showBannerAd(MainActivity mainActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(mainActivity);
        }
    }*/

    public static void showBannerAd(FlappyGame flappyGame) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(flappyGame);
        }
    }

    public static void showBannerAd(GameActivity gameActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(gameActivity);
        }
    }

    public static void showBannerAd(BreakerConfig breakerConfig) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(breakerConfig);
        }
    }

    public static void showBannerAd(Reglas reglas) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(reglas);
        }
    }

    public static void showBannerAd(SnakeGame snakeGame) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(snakeGame);
        }
    }

    public static void showBannerAd(SnakeConfig snakeConfig) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(snakeConfig);
        }
    }

    public static void showBannerAd(SpaceConfig spaceConfig) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(spaceConfig);
        }
    }

    public static void showBannerAd(TetrisConfig tetrisConfig) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(tetrisConfig);
        }
    }

    public static void showBannerAd(TetrisGame tetrisGame) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(tetrisGame);
        }
    }

    public static void showBannerAd(DailyTimeActivity dailyTimeActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(dailyTimeActivity);
        }
    }

    public static void showBannerAd(ActividadActivity actividadActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(actividadActivity);
        }
    }

    public static void showBannerAd(RemindMeActivity remindMeActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(remindMeActivity);
        }
    }

    public static void showBannerAd(SesionActivity sesionActivity) {
        if (!MainActivity.mIsPremium) {
            Yodo1Mas.getInstance().showBannerAd(sesionActivity);
        }
    }

}
