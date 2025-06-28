package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.domain.Process;
import com.grupo6.appdecomissao.domain.Stage;
import com.grupo6.appdecomissao.remote.ApiCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.util.Log;

public class CadastroRegraActivity extends AppCompatActivity {
    private EditText etNome, etDescricao, etPercentual;
    private Spinner spProcesso, spEtapa, spConsultoresRegra;
    private Button btnAdicionarConsultor, btnSalvar;
    private TextView tvConsultoresSelecionados;
    private Set<String> consultoresSelecionados = new HashSet<>();
    private List<User> todosConsultores = new ArrayList<>();
    private List<Process> todosProcessos = new ArrayList<>();
    private List<Stage> todasEtapas = new ArrayList<>();
    private Process processoSelecionado = null;
    private Stage etapaSelecionada = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_regra);

        String userId = DataCache.getInstance().getCurrentId();
        User user = DataCache.getInstance().getUserById(userId);
        
        // Debug: verificar se o usuário está sendo carregado
        if (user != null) {
            android.util.Log.d("CadastroRegra", "Usuário logado: ID=" + user.getId() + ", Nome=" + user.getName() + ", Perfil=" + user.getProfile());
        } else {
            android.util.Log.e("CadastroRegra", "Usuário não encontrado no DataCache. CurrentId: " + userId);
        }
        
        // Debug: listar todos os usuários no DataCache
        List<User> todosUsuarios = DataCache.getInstance().getAllUsers();
        android.util.Log.d("CadastroRegra", "Total de usuários no DataCache: " + todosUsuarios.size());
        for (User u : todosUsuarios) {
            android.util.Log.d("CadastroRegra", "Usuário: ID=" + u.getId() + ", Nome=" + u.getName() + ", Perfil=" + u.getProfile());
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_cadastro_regra);

        // Adiciona a ação de clique para o ícone de navegação (voltar)
        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Este comando fecha a tela atual, simulando o botão "voltar"
        });

        etNome = findViewById(R.id.etNomeRegra);
        etDescricao = findViewById(R.id.etDescricaoRegra);
        etPercentual = findViewById(R.id.etPercentualRegra);
        spProcesso = findViewById(R.id.spProcesso);
        spEtapa = findViewById(R.id.spEtapa);
        spConsultoresRegra = findViewById(R.id.spConsultoresRegra);
        btnAdicionarConsultor = findViewById(R.id.btnAdicionarConsultor);
        btnSalvar = findViewById(R.id.btnSalvarRegra);
        tvConsultoresSelecionados = findViewById(R.id.tvConsultoresSelecionados);

        // Carregar processos disponíveis
        carregarProcessos();
        
        // Carregar consultores disponíveis
        carregarConsultores();

        // Configurar listener do spinner de processo
        spProcesso.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= todosProcessos.size()) {
                    processoSelecionado = todosProcessos.get(position - 1);
                    Log.d("CADASTRO_REGRA", "Processo selecionado: " + processoSelecionado.getName() + " (ID: " + processoSelecionado.getId() + ")");
                    carregarEtapas(processoSelecionado.getId());
                } else {
                    processoSelecionado = null;
                    spEtapa.setEnabled(false);
                    spEtapa.setAdapter(new ArrayAdapter<>(CadastroRegraActivity.this, 
                        android.R.layout.simple_spinner_item, new ArrayList<>()));
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                processoSelecionado = null;
                spEtapa.setEnabled(false);
            }
        });

        // Configurar listener do spinner de etapa
        spEtapa.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= todasEtapas.size()) {
                    etapaSelecionada = todasEtapas.get(position - 1);
                    Log.d("CADASTRO_REGRA", "Etapa selecionada: " + etapaSelecionada.getName() + " (ID: " + etapaSelecionada.getId() + ")");
                } else {
                    etapaSelecionada = null;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                etapaSelecionada = null;
            }
        });

        btnAdicionarConsultor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spConsultoresRegra.getSelectedItem() != null) {
                    String consultorSelecionado = spConsultoresRegra.getSelectedItem().toString();
                    
                    // Encontrar o usuário pelo nome
                    User consultorEncontrado = null;
                    for (User consultor : todosConsultores) {
                        if (consultor.getName().equals(consultorSelecionado)) {
                            consultorEncontrado = consultor;
                            break;
                        }
                    }
                    
                    if (consultorEncontrado != null) {
                        if (!consultoresSelecionados.contains(consultorEncontrado.getId())) {
                            consultoresSelecionados.add(consultorEncontrado.getId());
                            atualizarTextoConsultores();
                            Log.d("CADASTRO_REGRA", "Consultor adicionado: " + consultorEncontrado.getName() + " (ID: " + consultorEncontrado.getId() + ")");
                        } else {
                            Toast.makeText(CadastroRegraActivity.this, "Consultor já selecionado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultoresSelecionados.isEmpty()) {
                    Toast.makeText(CadastroRegraActivity.this, "Selecione pelo menos um consultor!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (processoSelecionado == null) {
                    Toast.makeText(CadastroRegraActivity.this, "Selecione um processo!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etapaSelecionada == null) {
                    Toast.makeText(CadastroRegraActivity.this, "Selecione uma etapa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String nome = etNome.getText().toString();
                String descricao = etDescricao.getText().toString();
                String percentualStr = etPercentual.getText().toString();
                
                if (nome.isEmpty() || descricao.isEmpty() || percentualStr.isEmpty()) {
                    Toast.makeText(CadastroRegraActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double percentual = Double.parseDouble(percentualStr);
                
                CommissionRule regra = new CommissionRule(
                        UUID.randomUUID().toString(),
                        processoSelecionado.getId(),
                        etapaSelecionada.getId(),
                        nome,
                        descricao,
                        consultoresSelecionados,
                        "Percentual Fixo",
                        null,
                        percentual,
                        processoSelecionado.getName() + " - " + etapaSelecionada.getName()
                );
                
                // Salvar apenas no DataCache local
                DataCache.getInstance().putCommissionRule(regra);
                
                Log.d("CADASTRO_REGRA", "Regra cadastrada com sucesso:");
                Log.d("CADASTRO_REGRA", "- Nome: " + nome);
                Log.d("CADASTRO_REGRA", "- Processo: " + processoSelecionado.getName() + " (ID: " + processoSelecionado.getId() + ")");
                Log.d("CADASTRO_REGRA", "- Etapa: " + etapaSelecionada.getName() + " (ID: " + etapaSelecionada.getId() + ")");
                Log.d("CADASTRO_REGRA", "- Percentual: " + percentual + "%");
                Log.d("CADASTRO_REGRA", "- Consultores: " + consultoresSelecionados.size());
                
                Toast.makeText(CadastroRegraActivity.this, "Regra cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void carregarConsultores() {
        Log.d("CADASTRO_REGRA", "Iniciando carregamento de consultores");
        
        String currentUserId = DataCache.getInstance().getCurrentId();
        User currentUser = DataCache.getInstance().getUserById(currentUserId);
        
        if (currentUser != null) {
            Log.d("CADASTRO_REGRA", "Usuário atual - ID: " + currentUserId + 
                  ", Nome: " + currentUser.getName() + 
                  ", Profile: " + currentUser.getProfile());
        } else {
            Log.d("CADASTRO_REGRA", "Usuário atual não encontrado - ID: " + currentUserId);
        }
        
        List<User> allUsers = DataCache.getInstance().getAllUsers();
        Log.d("CADASTRO_REGRA", "Total de usuários no cache: " + allUsers.size());
        
        List<User> consultants = new ArrayList<>();
        
        for (User user : allUsers) {
            Log.d("CADASTRO_REGRA", "Verificando usuário - ID: " + user.getId() + 
                  ", Nome: " + user.getName() + 
                  ", Profile: " + user.getProfile() + 
                  ", É supervisor atual: " + (user.getId().equals(currentUserId)));
            
            // Considera como consultor se profile for null ou diferente de supervisor, exceto o usuário atual
            if (!user.getId().equals(currentUserId) && 
                (user.getProfile() == null || !user.getProfile().equalsIgnoreCase("supervisor"))) {
                consultants.add(user);
                Log.d("CADASTRO_REGRA", "Usuário adicionado como consultor: " + user.getName());
            } else {
                Log.d("CADASTRO_REGRA", "Usuário NÃO adicionado como consultor: " + user.getName() + 
                      " (motivo: " + (user.getId().equals(currentUserId) ? "é o usuário atual" : "é supervisor") + ")");
            }
        }
        
        Log.d("CADASTRO_REGRA", "Total de consultores filtrados: " + consultants.size());
        
        // Criar lista de nomes para o Spinner
        List<String> consultantNames = new ArrayList<>();
        for (User consultant : consultants) {
            consultantNames.add(consultant.getName());
            Log.d("CADASTRO_REGRA", "Nome do consultor para Spinner: " + consultant.getName());
        }
        
        // Configurar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, consultantNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsultoresRegra.setAdapter(adapter);
        
        // Armazenar lista de consultores para uso posterior
        this.todosConsultores = consultants;
        
        Log.d("CADASTRO_REGRA", "Spinner configurado com " + consultantNames.size() + " consultores");
    }

    private void atualizarTextoConsultores() {
        if (consultoresSelecionados.isEmpty()) {
            tvConsultoresSelecionados.setText("Consultores selecionados: Nenhum");
        } else {
            StringBuilder texto = new StringBuilder("Consultores selecionados:\n");
            for (String consultorId : consultoresSelecionados) {
                User consultor = DataCache.getInstance().getUserById(consultorId);
                if (consultor != null) {
                    texto.append("• ").append(consultor.getName()).append("\n");
                }
            }
            tvConsultoresSelecionados.setText(texto.toString());
        }
    }

    private void carregarProcessos() {
        try {
            Log.d("CADASTRO_REGRA", "Iniciando carregamento de processos da API Rubeus");
            
            // Configurar Spinner inicial com "Carregando..."
            List<String> initialProcessNames = new ArrayList<>();
            initialProcessNames.add("Carregando processos...");
            ArrayAdapter<String> initialAdapter = new ArrayAdapter<>(CadastroRegraActivity.this, 
                android.R.layout.simple_spinner_item, initialProcessNames);
            initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spProcesso.setAdapter(initialAdapter);
            
            DataCache.getInstance().loadProcessesFromApi("8", "b116d29f1252d2ce144d5cb15fb14c7f", new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    try {
                        Log.d("CADASTRO_REGRA", "Processos carregados com sucesso");
                        
                        todosProcessos = DataCache.getInstance().getAllProcesses();
                        Log.d("CADASTRO_REGRA", "Total de processos carregados: " + todosProcessos.size());
                        
                        // Criar lista de nomes para o Spinner (com opção "Selecione um processo")
                        List<String> processNames = new ArrayList<>();
                        processNames.add("Selecione um processo");
                        
                        for (Process process : todosProcessos) {
                            if (process != null && process.isActive()) {
                                processNames.add(process.getName());
                                Log.d("CADASTRO_REGRA", "Processo adicionado ao Spinner: " + process.getName());
                            }
                        }
                        
                        // Configurar Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroRegraActivity.this, 
                            android.R.layout.simple_spinner_item, processNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spProcesso.setAdapter(adapter);
                        
                        Log.d("CADASTRO_REGRA", "Spinner de processos configurado com " + processNames.size() + " opções");
                    } catch (Exception e) {
                        Log.e("CADASTRO_REGRA", "Erro ao processar processos: " + e.getMessage());
                        handleProcessError("Erro ao processar processos");
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("CADASTRO_REGRA", "Erro ao carregar processos: " + error);
                    handleProcessError("Erro ao carregar processos: " + error);
                }
            });
        } catch (Exception e) {
            Log.e("CADASTRO_REGRA", "Erro crítico ao carregar processos: " + e.getMessage());
            handleProcessError("Erro crítico ao carregar processos");
        }
    }

    private void handleProcessError(String errorMessage) {
        try {
            Toast.makeText(CadastroRegraActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            
            // Configurar Spinner vazio em caso de erro
            List<String> processNames = new ArrayList<>();
            processNames.add("Erro ao carregar processos");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroRegraActivity.this, 
                android.R.layout.simple_spinner_item, processNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spProcesso.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("CADASTRO_REGRA", "Erro ao tratar erro de processos: " + e.getMessage());
        }
    }

    private void carregarEtapas(String processId) {
        Log.d("CADASTRO_REGRA", "Iniciando carregamento de etapas para processo: " + processId);
        
        // Desabilitar spinner de etapas durante carregamento
        spEtapa.setEnabled(false);
        
        DataCache.getInstance().loadStagesFromApi("8", "b116d29f1252d2ce144d5cb15fb14c7f", processId, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("CADASTRO_REGRA", "Etapas carregadas com sucesso para processo: " + processId);
                
                todasEtapas = DataCache.getInstance().getStagesByProcessId(processId);
                Log.d("CADASTRO_REGRA", "Total de etapas carregadas: " + todasEtapas.size());
                
                // Criar lista de nomes para o Spinner (com opção "Selecione uma etapa")
                List<String> stageNames = new ArrayList<>();
                stageNames.add("Selecione uma etapa");
                
                for (Stage stage : todasEtapas) {
                    if (stage.isActive()) {
                        stageNames.add(stage.getName());
                        Log.d("CADASTRO_REGRA", "Etapa adicionada ao Spinner: " + stage.getName());
                    }
                }
                
                // Configurar Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroRegraActivity.this, 
                    android.R.layout.simple_spinner_item, stageNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEtapa.setAdapter(adapter);
                spEtapa.setEnabled(true);
                
                Log.d("CADASTRO_REGRA", "Spinner de etapas configurado com " + stageNames.size() + " opções");
            }

            @Override
            public void onError(String error) {
                Log.e("CADASTRO_REGRA", "Erro ao carregar etapas: " + error);
                Toast.makeText(CadastroRegraActivity.this, "Erro ao carregar etapas: " + error, Toast.LENGTH_SHORT).show();
                
                // Configurar Spinner vazio em caso de erro
                List<String> stageNames = new ArrayList<>();
                stageNames.add("Erro ao carregar etapas");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroRegraActivity.this, 
                    android.R.layout.simple_spinner_item, stageNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEtapa.setAdapter(adapter);
                spEtapa.setEnabled(false);
            }
        });
    }
} 