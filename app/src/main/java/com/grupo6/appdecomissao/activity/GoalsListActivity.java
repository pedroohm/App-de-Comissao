package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.activity.GoalsAdapter;
import com.grupo6.appdecomissao.domain.Goal;
import java.util.ArrayList;

public class GoalsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.rv_goals_list);
        ArrayList<Goal> goalsList = getIntent().getParcelableArrayListExtra("GOALS_LIST");

        if (goalsList != null) {
            GoalsAdapter adapter = new GoalsAdapter(goalsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
}