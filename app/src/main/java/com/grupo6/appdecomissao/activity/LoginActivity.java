package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.grupo6.appdecomissao.domain.DataCache;

public class LoginActivity extends Activity {

    private EditText passwordTextView, emailTextView;
    private DataCache dataCache = DataCache.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        //setContentView(R.layout.placeholderxml);
    }

    public void login(View view){ }

}