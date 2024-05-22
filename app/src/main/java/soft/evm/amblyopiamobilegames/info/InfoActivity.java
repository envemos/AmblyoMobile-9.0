package soft.evm.amblyopiamobilegames.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.webview.WebViewActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        super.onCreate(savedInstanceState);

        Element page1Element = new Element();
        page1Element.setTitle(getString(R.string.page1_title));
        page1Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.url = getString(R.string.page1);
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });
        Element page2Element = new Element();
        page2Element.setTitle(getString(R.string.page2_title));
        page2Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.url = getString(R.string.page2);
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });
        Element page3Element = new Element();
        page3Element.setTitle(getString(R.string.page3_title));
        page3Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.url = getString(R.string.page3);
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });
        Element page4Element = new Element();
        page4Element.setTitle(getString(R.string.page4_title));
        page4Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.url = getString(R.string.page4);
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });
        Element page5Element = new Element();
        page5Element.setTitle(getString(R.string.page5_title));
        page5Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.url = getString(R.string.page5);
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.logotipo_recortado)
                .setDescription(getString(R.string.mensaje_bienvenida))
                .addGroup(getString(R.string.guia))
                .addItem(page1Element)
                .addItem(page2Element)
                .addItem(page3Element)
                .addItem(page4Element)
                .addItem(page5Element)
                .addGroup(getString(R.string.conect))
                .addEmail("contact@envemos.com")
                .addWebsite(getString(R.string.amblyom))
                .addPlayStore("soft.evm.amblyopiamobilegames")
                .addYoutube("UCZ3NePFbH7NEu0cWsgSB9MQ")
                //.addFacebook("EnvemosS")
                //.addTwitter("EnvemosS")
                //.addGitHub("medyo")
                //.addInstagram("envemossoftware")
                .create();

        setContentView(aboutPage);
    }
}
