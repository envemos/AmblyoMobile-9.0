package soft.evm.amblyopiamobilegames.layoutElements;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.lang.reflect.Field;

import soft.evm.amblyopiamobilegames.R;

import static android.graphics.Typeface.BOLD;

public class Element {

    //title
    private final String _04B_19__ = "title/04B_19__.TTF";//setButtonGameMenu
    private final String ALDRICH = "title/aldrich.ttf";//setTitle
    private final String BLOCKTOPIA =  "title/Blocktopia.ttf";//setTitleGames, setTtileGameName, setRulesName, setRulesName, setRulesText
    private final String MINECRAFT = "title/Minecraft.ttf";//setActivityTextContent
    private final String REDUCTION = "title/Reduction.ttf";//setButtonMain

    //text
    private final String FIERY_TURK = "text/Fiery_Turk.ttf";//setWelcome, setTextLevel, setSesionTextTitle, setSesionText, setAutoText
    private final String PIXELYOURLIFE = "text/pixelyourlife.ttf";//setAutoText
    private final String PIXILATOR =  "text/Pixilator.ttf";//setLogin, setActivityTitle, setCopyright
    private final String PX10 =  "text/px10.ttf";//setActivityText, setTableTitle, setTableContent, setHeaderText, setChildText, setItemSelectedText, PX10
    private final String SYLLADEXFANON = "text/minus/SylladexFanon.ttf";//setSettingsText, setSettingsCheckBox

    //etc
    private final String PIXEL_INVADERS = "etc/pixel_invaders.ttf";
    private final String SCUMSKULLZ_BOX = "etc/scumskullz_box.ttf";
    private final String SCUMSKULLZ_REGULAR = "etc/scumskullz_regular.ttf";

    private int blanco;
    private int grisClaro;
    private int grisMedioClaro;
    private int grisMedioMedioClaro;
    private int gris;
    private int grisMedioOscuro;
    private int grisOscuro;
    private int negro;
    private int verde;
    private int azul;
    private int azulado;
    private int rojo;
    private int rosa;
    private int amarillo;

    private final boolean MAYUS = true;

    private Context context;

    public Element(Context context) {
        this.context = context;
        colores();
    }

    private void colores() {
        blanco = context.getResources().getColor(R.color.Blanco);
        grisClaro = context.getResources().getColor(R.color.grisClaro);
        grisMedioClaro = context.getResources().getColor(R.color.grisMedioClaro);
        grisMedioMedioClaro = context.getResources().getColor(R.color.grisMedioMedioClaro);
        gris = context.getResources().getColor(R.color.gris);
        grisMedioOscuro = context.getResources().getColor(R.color.grisMedioOscuro);
        grisOscuro = context.getResources().getColor(R.color.grisOscuro);
        negro = context.getResources().getColor(R.color.Negro);
        verde = context.getResources().getColor(R.color.colorVerde);
        azul = context.getResources().getColor(R.color.colorAzul);
        azulado = context.getResources().getColor(R.color.azulado);
        rojo = context.getResources().getColor(R.color.colorRojo);
        rosa = context.getResources().getColor(R.color.colorRosa);
        amarillo = context.getResources().getColor(R.color.colorAmarillo);
    }

    //************************************************************************

    //Main Title
    public void setTitle(TextView title){
        setFont(title, ALDRICH);
        SpannableString mensaje = new SpannableString(context.getString(R.string.amblyo)+context.getString(R.string.mobile));
        setBold(mensaje);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);// Puedes usar tambien .. new ForegroundColorSpan(Color.RED); Color.parseColor("#FF0040")
        mensaje.setSpan(colorSpan, 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        colorSpan = new ForegroundColorSpan(Color.BLUE);// Puedes usar tambien .. new ForegroundColorSpan(Color.RED); Color.parseColor("#FF0040")
        mensaje.setSpan(colorSpan, 6, 12, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        title.setText(mensaje);
    }


    //Welcome
    public void setLogin(Button login) {
        set(login, PIXILATOR, BOLD, grisOscuro);
    }

    public void setCopyright(TextView tvCopyright) {
        set(tvCopyright, PIXILATOR, BOLD, negro);
    }

    //MainActivity
    public void setButtonMain(Button jugar) {
        set(jugar, REDUCTION, BOLD, negro);
    }

    public void setWelcome(TextView bienvenido) {
        set(bienvenido, FIERY_TURK, BOLD, gris);
    }


    //MenuActivity
    public void setButtonGameMenu(Button buttonGame, int color) {
        //set(buttonGame, _04B_19__, BOLD, blanco);
        buttonGame.setBackgroundColor(color);
        buttonGame.setTextColor(negro);
        set(buttonGame, _04B_19__, BOLD);
    }

    public void setTitleGames(TextView titulo) {
        set(titulo, BLOCKTOPIA, BOLD, negro);
    }


    //GameActivity
    public void setTtileGameName(TextView title) {
        set(title, BLOCKTOPIA, BOLD, negro, MAYUS);
    }


    //GameConfig
    public void setTextLevel(TextView textView) {
        set(textView, FIERY_TURK, BOLD, negro);
    }

    public void setAutoText(CheckBox autoInc) {
        set(autoInc, FIERY_TURK, BOLD, negro);
        //set(autoInc, PIXELYOURLIFE, BOLD, negro);
        setCheckBoxColor(autoInc, verde);
    }


    //Actividad
    public void setActivityText(TextView textView) {
        set(textView, PX10, BOLD, grisMedioOscuro);
    }

    public void setActivityTextContent(TextView textView) {
        set(textView, MINECRAFT, BOLD);
    }

    public void setActivityTitle(TextView tVmiActividad) {
        set(tVmiActividad, PIXILATOR, BOLD, grisOscuro);
    }

    public void setActivityButton(Button bThistorial) {
        set(bThistorial, PIXILATOR, BOLD);
    }


    //Sesion
    public void setSesionTextTitle(TextView tVmicuenta) {
        set(tVmicuenta, FIERY_TURK, BOLD, grisOscuro);
    }

    public void setSesionText(TextView tVemail) {
        set(tVemail, FIERY_TURK, BOLD, grisOscuro);
    }

    public void setRadioButtonM(RadioButton radioButton) {
        setRadioButtonColor(radioButton, azul);
    }

    public void setRadioButtonF(RadioButton radioButton) {
        setRadioButtonColor(radioButton, rosa);
    }


    //Settings
    public void setSettingsText(TextView left_eye) {
        set(left_eye, SYLLADEXFANON, BOLD, grisOscuro);
    }

    public void setSettingsCheckBox(CheckBox sonido) {
        set(sonido, SYLLADEXFANON, BOLD, grisOscuro);
        setCheckBoxColor(sonido, azul);
    }

    //Rules
    public void setRulesName(TextView nombre) {//BLOCKTOPIA
        set(nombre, BLOCKTOPIA, BOLD, negro, MAYUS);
    }

    public void setRulesText(TextView reglas) {
        set(reglas, BLOCKTOPIA, 2, grisOscuro);
    }


    //Table
    public void setTableTitle(TextView tv) {
        set(tv, PX10, BOLD);
    }


    public void setTableContent(TextView tv4) {
        set(tv4, PX10, BOLD);
    }

    public void setAppbarElement(CollapsingToolbarLayout appBarLayout) {
        set(appBarLayout, PX10, BOLD, grisOscuro);
    }

    public void setItemSelectedText(TextView textView) {
        set(textView, PX10, BOLD, grisOscuro);
    }

    public void setQuestionText(TextView question) {
        set(question, PX10, BOLD, negro);
    }

    //********************************************************************************

    //SET THE PARAMETERS
    private void set(TextView textView, String font, int bold, int color, boolean mayus) {
        if(mayus==MAYUS) {
            String texto = textView.getText().toString();
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < texto.length(); i++)
                stringBuilder.append(Character.toUpperCase(texto.charAt(i)));
            textView.setText(stringBuilder);
        }
        set(textView, font, bold, color);
    }

    private void set(TextView textView, String font, int bold, int color) {
        setFont(textView, font);
        SpannableString mensaje = new SpannableString(textView.getText());
        if(bold == 1)
            setBold(mensaje);
        setColor(mensaje, color);
        textView.setText(mensaje);
    }

    private void set(CollapsingToolbarLayout appBarLayout, String font, int bold, int color) {
        //setFont(appBarLayout, font);
        try {
            final Field field = appBarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);
            final Object object = field.get(appBarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);
            setFont((TextPaint) tpf.get(object), font);
        } catch (Exception ignored) {
        }
    }

    private void set(TextView textView, String font, int bold) {
        setFont(textView, font);
        SpannableString mensaje = new SpannableString(textView.getText());
        if(bold == 1)
            setBold(mensaje);
        textView.setText(mensaje);
    }

    private void set(Button button, String font, int bold, int color) {
        setFont(button, font);
        SpannableString mensaje = new SpannableString(button.getText());
        if(bold == 1)
            setBold(mensaje);
        setColor(mensaje, color);
        button.setText(mensaje);
    }

    private void set(Button button, String font, int bold) {
        setFont(button, font);
        SpannableString mensaje = new SpannableString(button.getText());
        if(bold == 1)
            setBold(mensaje);
        button.setText(mensaje);
    }


    //FONTS
    public void setFont(TextView textView, String font){
        String font_path = "fonts/" + font;
        Typeface TF = Typeface.createFromAsset(context.getAssets(),font_path);
        textView.setTypeface(TF);
    }

    public void setFont(Button button, String font){
        String font_path = "fonts/" + font;
        Typeface TF = Typeface.createFromAsset(context.getAssets(),font_path);
        button.setTypeface(TF);
    }

    private void setFont(TextPaint textPaint, String font) {
        String font_path = "fonts/" + font;
        Typeface TF = Typeface.createFromAsset(context.getAssets(),font_path);
        textPaint.setTypeface(TF);
    }

    //NEGRITA
    public void setBold(SpannableString mensaje){
        StyleSpan boldSpan = new StyleSpan(BOLD);
        mensaje.setSpan(boldSpan, 0, mensaje.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    //COLOR
    private void setColor(SpannableString mensaje, int color) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        mensaje.setSpan(colorSpan, 0, mensaje.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    private void setCheckBoxColor(CheckBox checkBox, int color) {
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(color));
    }

    private void setRadioButtonColor(RadioButton radioButton, int color) {
        CompoundButtonCompat.setButtonTintList(radioButton, ColorStateList.valueOf(color));
    }

    //int to COLOR
    public int toColor(int n) {
        int color = 0;
        switch (n){
            case 0:
                color =  Color.RED;
                break;
            case 1:
                color =  Color.BLUE;
                break;
            case 2:
                color =  Color.YELLOW;
                break;
            case 3:
                color =  Color.CYAN;
                break;
            case 4:
                color =  Color.MAGENTA;
                break;
            case 5:
                color =  Color.GREEN;
                break;
        }
        return color;
    }

}
