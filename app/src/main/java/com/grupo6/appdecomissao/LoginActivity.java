package com.grupo6.appdecomissao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends Activity {

    private EditText passwordTextView, emailTextView;
    private DataCache dataCache = DataCache.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        setContentView(R.layout.placeholderxml);
    }

    public void login(View view){ }

}
