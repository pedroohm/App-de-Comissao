package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ManageConsultantsActivity extends AppCompatActivity {

    private DataCache dataCache = DataCache.getInstance();
    private LinearLayout consultantsContainer;
    private List<User> consultants;
    private View addConsultantFormView; // Referência para a view do formulário de adição

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        setContentView(R.layout.test1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar listener para o botão de adicionar consultor ("+")
        ImageView addConsultantIcon = findViewById(R.id.add_consultant_icon);
        if (addConsultantIcon != null) {
            addConsultantIcon.setOnClickListener(v -> toggleAddConsultantForm());
        }

        consultantsContainer = findViewById(R.id.consultants_container);
        consultants = dataCache.getAllConsultants();

        showConsultants();
    }

    public void showConsultants(){
        // Limpa o container para evitar dados duplicados
        consultantsContainer.removeAllViews();

        // Se o formulário de adição estiver visível, adicione-o primeiro
        if (addConsultantFormView != null && addConsultantFormView.getParent() == null) {
            consultantsContainer.addView(addConsultantFormView, 0); // Adiciona na posição 0 (topo)
        }

        if (consultants != null) {
            for (User consultant : consultants) {
                addConsultantView(consultant);
            }
        }
    }

    public void addConsultantView(User consultant){
        LayoutInflater inflater = LayoutInflater.from(this);
        View consultorView = inflater.inflate(R.layout.test2, consultantsContainer, false);

        // Encontra as Views da caixa
        TextView consultantName = consultorView.findViewById(R.id.consultor_nome);
        ImageView deleteIcon = consultorView.findViewById(R.id.delete_icon);
        ImageView expandArrow = consultorView.findViewById(R.id.expand_arrow);

        // Encontra as Views da caixa expandida
        LinearLayout detailsLayout = consultorView.findViewById(R.id.details_layout); // Layout dos detalhes
        TextView consultantEmail = consultorView.findViewById(R.id.consultor_email);
        TextInputEditText consultantPercentage = consultorView.findViewById(R.id.consultor_fixo);
        TextView consultantRules = consultorView.findViewById(R.id.consultor_regras);
        TextView consultantGoals = consultorView.findViewById(R.id.consultor_metas);

        List<Goal> userGoals;
        List<String> userRules;

        userGoals = dataCache.getGoalsByUserId(consultant.getId());
        Set<String> commissionRulesSet = dataCache.getUserCommissionRules(consultant.getId());
        userRules = new ArrayList<>(commissionRulesSet);

        // Define os dados do consultor nas Views
        consultantName.setText(consultant.getName());
        consultantEmail.setText("Email: " + consultant.getEmail());
        // consultantPercentage.setText("% Fixo: ");
        // consultantRules.setText("Regras: " + consultant.getRegras());
        // consultantGoals.setText("Metas: " + consultant.getMetas());
        consultantPercentage.setText("0"); // Placeholder, substituir quando tiver o dado real

        // Formata as regras e metas para exibição
        StringBuilder rulesText = new StringBuilder("Regras: ");
        if (userRules != null && !userRules.isEmpty()) {
            for (int i = 0; i < userRules.size(); i++) {
                rulesText.append(userRules.get(i));
                if (i < userRules.size() - 1) {
                    rulesText.append(", ");
                }
            }
        } else {
            rulesText.append("Nenhuma regra definida");
        }
        consultantRules.setText(rulesText.toString());

        StringBuilder goalsText = new StringBuilder("Metas: ");
        if (userGoals != null && !userGoals.isEmpty()) {
            for (int i = 0; i < userGoals.size(); i++) {
                // Supondo que Goal tem um metodo toString() ou getName() adequado
                goalsText.append(userGoals.get(i).getDescription()); // Exemplo: se Goal tem getName()
                if (i < userGoals.size() - 1) {
                    goalsText.append(", ");
                }
            }
        } else {
            goalsText.append("Nenhuma meta definida");
        }
        consultantGoals.setText(goalsText.toString());

        detailsLayout.setVisibility(View.GONE);

        // Define o listener de clique para a seta de expansão
        expandArrow.setOnClickListener(v -> {
            if (detailsLayout.getVisibility() == View.GONE) {
                // Se estiver oculto, torna visível
                detailsLayout.setVisibility(View.VISIBLE);
                expandArrow.setImageResource(R.drawable.arrow_drop_up_icon); // Altera para seta para cima
                expandArrow.setContentDescription("Recolher detalhes do consultor"); // Atualiza descrição para acessibilidade
            } else {
                // Se estiver visível, oculta
                detailsLayout.setVisibility(View.GONE);
                expandArrow.setImageResource(R.drawable.arrow_drop_down_icon); // Altera para seta para baixo
                expandArrow.setContentDescription("Expandir detalhes do consultor"); // Atualiza descrição para acessibilidade
            }
        });


        // Adiciona a view do consultor ao container principal
        consultantsContainer.addView(consultorView);

    }

    /**
     * Alterna a visibilidade do formulário de adição de novo consultor.
     * Se o formulário não estiver visível, ele é inflado e adicionado no topo da lista.
     * Se já estiver visível, ele é removido.
     */
    private void toggleAddConsultantForm() {
        if (addConsultantFormView == null || addConsultantFormView.getParent() == null) {
            // O formulário não está visível, então vamos inflar e adicionar
            LayoutInflater inflater = LayoutInflater.from(this);
            addConsultantFormView = inflater.inflate(R.layout.item_add_consultant_form, consultantsContainer, false);

            // Encontra os elementos dentro do formulário inflado
            TextInputEditText editTextName = addConsultantFormView.findViewById(R.id.edit_text_name);
            TextInputEditText editTextEmail = addConsultantFormView.findViewById(R.id.edit_text_email);
            TextInputEditText editTextPercentage = addConsultantFormView.findViewById(R.id.edit_text_percentage);
            ImageView closeAddFormIcon = addConsultantFormView.findViewById(R.id.close_add_form_icon);
            Button buttonCancel = addConsultantFormView.findViewById(R.id.button_cancel_add);
            Button buttonSave = addConsultantFormView.findViewById(R.id.button_save_new_consultant);

            // Listener para o botão de fechar o formulário (ícone X)
            closeAddFormIcon.setOnClickListener(v -> hideAddConsultantForm());

            // Listener para o botão Cancelar do formulário
            buttonCancel.setOnClickListener(v -> hideAddConsultantForm());

            // Listener para o botão Adicionar (Salvar) do formulário
            buttonSave.setOnClickListener(v -> {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String percentage = editTextPercentage.getText().toString().trim(); // Pode ser vazio ou numérico

                if (name.isEmpty()) {
                    Toast.makeText(ManageConsultantsActivity.this, "O nome do consultor não pode ser vazio.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Por enquanto, apenas mostra um Toast/Snackbar. A lógica real de adicionar o usuário virá depois.
                Snackbar.make(v, "Adicionar consultor: " + name + " (Email: " + email + ")", Snackbar.LENGTH_LONG).show();
                // TODO: Aqui você adicionaria a lógica para criar um novo objeto User e adicioná-lo ao dataCache.
                // Exemplo: User newUser = new User(UUID.randomUUID().toString(), name, email, ...);
                // dataCache.addUser(newUser);
                // consultants = dataCache.getAllConsultants(); // Recarrega a lista para incluir o novo
                // showConsultants(); // Atualiza a UI para exibir o novo consultor
                hideAddConsultantForm(); // Oculta o formulário após a "adição"
            });

            consultantsContainer.addView(addConsultantFormView, 0); // Adiciona o formulário na primeira posição
        } else {
            // O formulário já está visível, então vamos escondê-lo
            hideAddConsultantForm();
        }
    }

    /**
     * Oculta o formulário de adição de novo consultor, se estiver visível.
     * Remove a view do container e limpa a referência.
     */
    private void hideAddConsultantForm() {
        if (addConsultantFormView != null && addConsultantFormView.getParent() != null) {
            consultantsContainer.removeView(addConsultantFormView);
            addConsultantFormView = null; // Limpa a referência para que possa ser inflado novamente
        }
    }
}
