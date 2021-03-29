package com.example.week10;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    WebView webThing;
    EditText addressBar;
    Button fwdBut;
    Button backBut;
    Button refresh;
    Button init;
    Button so;
    String[] history;
    String defaultUrl = "http://www.google.fi";
    int ind = 0;
    int webUpdateVal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fwdBut = findViewById(R.id.backBut);
        backBut = findViewById(R.id.fwdBut);
        refresh = findViewById(R.id.refBut);
        webThing = findViewById(R.id.webView);
        addressBar = findViewById(R.id.addressBar);
        init = findViewById(R.id.javaRunInit);
        so = findViewById(R.id.javaRunSo);

        addressBar.setText(defaultUrl);
        webThing.setWebViewClient(new WebViewClient());
        webThing.getSettings().setJavaScriptEnabled(true);
        webThing.loadUrl(defaultUrl);
        history = new String[10];
        history[0] = defaultUrl;

        // loads a new page on "enter"
        addressBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == event.KEYCODE_ENTER)) {
                    loadAddress();
                }
                return false;
            }
        });

        // follows address changes and updates the history
        webThing.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equals(history[ind]) != true) {
                    switch (webUpdateVal) {
                        case (1):
                            history[ind] = url;
                            break;
                        case (-1):
                            break;
                        case (0):
                            if (ind < 9) {
                                ind++;
                            } else {
                                updateHistory();
                            }
                            System.out.println("Future cleared.");
                            clearFuture();
                            history[ind] = url;
                            break;
                    }
                }
                addressBar.setText(url);
                webUpdateVal = 0;
                System.out.println("Loaded: " + url);
                System.out.println("History ind: " + ind);
            }
        });
    }

    // loads the typed address when "enter" is pressed
    public void loadAddress() {
        String newAddress = addressBar.getText().toString();
        if (newAddress.equals("index.html")){
            newAddress = "file:///android_asset/index.html";
        // if the string doesn't contain
        } else if (newAddress.startsWith("http://www.") || newAddress.startsWith("https://www.")) {
            // do nothing // all addresses typed in fully should go through here
        } else if (newAddress.startsWith("www.")) {
            // add https://
            newAddress = "https://" + addressBar.getText().toString();
        } else {
            // add https://www.
            newAddress = "https://www." + addressBar.getText().toString();
        }
        webThing.loadUrl(newAddress);
        System.out.println("Loaded: " + newAddress);
    }

    // reloads the current web page
    public void refresh(View v) {
        webThing.reload();
    }

    // loads the previous page on the list
    public void loadPrev(View v) {
        if (ind != 0) {
            ind--;
            webUpdateVal = -1;
            webThing.loadUrl(history[ind]);
        }
    }

    // loads the next page on the list
    public void loadNext(View v) {
        if (ind < 9) {
            if (history[ind + 1] != null) {
                ind++;
                webUpdateVal = 1;
                webThing.loadUrl(history[ind]);
            }
        }
    }

    // updates the history when necessary
    public void updateHistory() {
        for (int i = 0; i < ind; i++) {
            history[i] = history[i+1];
        }
    }

    // updates the future when necessary
    public void clearFuture() {
        for (int i = ind+1; i < 10; i++) {
            history[i] = null;
        }
    }

    // javascript functions
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runSo(View v) {
        if (webThing.getUrl().equals("file:///android_asset/index.html")) {
            webThing.evaluateJavascript("javascript:shoutOut()", null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runInit(View v) {
        if (webThing.getUrl().equals("file:///android_asset/index.html")) {
            webThing.evaluateJavascript("javascript:initialize()", null);
        }
    }
}