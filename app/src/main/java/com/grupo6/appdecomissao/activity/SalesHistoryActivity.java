package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.activity.SalesHistoryAdapter;
import com.grupo6.appdecomissao.domain.Sale;
import java.util.ArrayList;

public class SalesHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // Ação do botão voltar

        RecyclerView recyclerView = findViewById(R.id.rv_sales_history);

        // Pega a lista de vendas que foi enviada pela tela do Dashboard
        ArrayList<Sale> salesList = getIntent().getParcelableArrayListExtra("SALES_LIST");

        if (salesList != null) {
            SalesHistoryAdapter adapter = new SalesHistoryAdapter(salesList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
}