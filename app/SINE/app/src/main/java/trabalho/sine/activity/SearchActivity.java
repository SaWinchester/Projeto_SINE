package trabalho.sine.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import trabalho.sine.R;
import trabalho.sine.adapter.AdapterListView;
import trabalho.sine.adapter.AdpterScrollListener;
import trabalho.sine.adapter.CargoSuggestionAdapter;
import trabalho.sine.adapter.CidadeSuggestionAdapter;
import trabalho.sine.controller.RequestURL;
import trabalho.sine.dao.VagaDAO;
import trabalho.sine.model.Cargo;
import trabalho.sine.model.Cidade;
import trabalho.sine.model.Vaga;
import trabalho.sine.model.VagasJSON;
import trabalho.sine.utils.Constantes;
import trabalho.sine.utils.NavigationSine;

public class SearchActivity extends AppCompatActivity{


    @BindView(R.id.list_empregos) RecyclerView mRecyclerView;
    @BindView(R.id.filterButton) Button filter;

    private AdapterListView mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Vaga> vagas;
    private ProgressDialog dialog;
    private String filtroEscolhido = "";
    private int filtroIndex = 1;
    private AlertDialog alerta;

    private String cityValue = "", functionValue = "";

    private Long cidadeEstado = 0l, funcao = 0l;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private int pos = 1;
    private int totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Define o ButterKnife para gerenciar as activities e ativa o modo de debugação.
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        createNavigationView();

        vagas = new ArrayList<>();
        totalItemCount = 0;

        mostrarDialogoCarregando();
        obtemVagasAPI();

    }

    private void createNavigationView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_search);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationSine(drawerLayout,R.id.searchActivity,this));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void criaAutoComplete(AutoCompleteTextView inputCargo, AutoCompleteTextView inputCidade) {
        inputCargo.setAdapter(new CargoSuggestionAdapter(this, inputCargo.getText().toString(), Constantes.URL_API + "/idfuncao/"));

        inputCargo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cargo c = (Cargo) adapterView.getItemAtPosition(position);
                funcao = c.getId();
                functionValue = c.getDescricao();
            }
        });

        inputCidade.setAdapter(new CidadeSuggestionAdapter(this, inputCidade.getText().toString(), Constantes.URL_API + "/idcidade/"));

        inputCidade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cidade c = (Cidade) adapterView.getItemAtPosition(position);
                cidadeEstado = c.getId();
                cityValue = c.getDescricao();
            }
        });
    }


    private void carregaRecyclerView() {
        verifica();
        createRecyclerView();
        dialog.dismiss();
    }

    private void createRecyclerView(){
        //Remove os itens do Recycler, para add os novos valores.
        mRecyclerView.removeAllViewsInLayout();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterListView(vagas,this);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutFrozen(false);
        mRecyclerView.scrollToPosition(totalItemCount);
        //Define o metodo que ira obter a ação do Scroll.
        mRecyclerView.addOnScrollListener(new AdpterScrollListener(this,mRecyclerView,mAdapter,mLayoutManager,
                cidadeEstado,funcao,filtroIndex,pos));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        verifica();
        createRecyclerView();
        mRecyclerView.scrollToPosition((int)data.getExtras().get("position"));
    }

    // Verifica quais das vagas está no banco de dados.
    public void verifica() {

        VagaDAO vagaDAO = new VagaDAO(getApplicationContext());
        List<Vaga> vagasBd = vagaDAO.getAll();

        //Define todos os favoritos da vagas para false...
        for(Vaga v : vagas)
            v.setFavoritado(false);

        //Depois verificar quais estão no banco e retornado pela api.
        //Definir para true o campo favoritado.
        for (Vaga vbd : vagasBd)
            for (Vaga vs : vagas)
                if (vbd.getId().toString().equalsIgnoreCase(vs.getId().toString()))
                    vs.setFavoritado(true);
    }

    // obtem todas as vagas da api.
    public void obtemVagasAPI(){
        RequestURL req = new RequestURL(this);

        req.requestURL(String.format(Constantes.URL_API + Constantes.URL_API_VAGAS, funcao, cidadeEstado, pos, filtroIndex), new RequestURL.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                VagasJSON vagasJSON = gson.fromJson(response, VagasJSON.class);
                vagas.addAll(vagasJSON.getVagas());
                carregaRecyclerView();
            }
        });

    }

    /************************************* Filtros ******************************** */

    // Constrói uma caixa de diálogo que pede qual filtro o jovem deseja.
    private void dialogFiltro(){

        final int tempFiltroIndex = filtroIndex;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View forms = inflater.inflate(R.layout.alert_dialog, null, false);

        final RadioGroup radioGroup = (RadioGroup) forms.findViewById(R.id.grupo);
        final RadioButton buttonUltimas = (RadioButton) forms.findViewById(R.id.ultimasVagas);
        final RadioButton buttonSalario = (RadioButton) forms.findViewById(R.id.maiorSalario);

        final AutoCompleteTextView city = (AutoCompleteTextView) forms.findViewById(R.id.cidade);
        final AutoCompleteTextView function = (AutoCompleteTextView) forms.findViewById(R.id.funcao);

        city.setText(cityValue);
        function.setText(functionValue);

        criaAutoComplete(function,city);

        if(filtroIndex == 1)
            radioGroup.check(buttonUltimas.getId());
        else
            radioGroup.check(buttonSalario.getId());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(forms);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o filtro?");
        builder.setView(layout);

        // Ação que irá ocorrer quando o jovem clicar no botão ok.
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                alerta.dismiss();

                if(radioGroup.getCheckedRadioButtonId() == buttonUltimas.getId())
                    filtroIndex = 1;
                else
                    filtroIndex = 2;

                mRecyclerView.scrollToPosition(0);
                mRecyclerView.clearOnScrollListeners();

                mostrarDialogoCarregando();
                vagas.clear();
                pos = 1;
                obtemVagasAPI();
            }
        });

        // Ação que irá ocorrer quando o jovem clicar no botão Reseta.
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Reseta o scroll.
                mRecyclerView.scrollToPosition(0);
                mRecyclerView.clearOnScrollListeners();

                vagas.clear();
                pos = 1;
                filtroEscolhido = "";
                filtroIndex = 1;

                cidadeEstado = 0l;
                funcao = 0l;
                cityValue = "";
                functionValue = "";
                city.setText("");
                function.setText("");
                alerta.dismiss();

                mostrarDialogoCarregando();
                obtemVagasAPI();
            }
        });

        filtroIndex = tempFiltroIndex;
        // filtroIndex = tempFiltroIndex;
        builder.setCancelable(true);
        alerta = builder.create();
        alerta.show();
    }

    @OnClick(R.id.filterButton)
    // responsável pelo click do botão filtro.
    public void filterClick(View view){
        dialogFiltro();
    }

    public void mostrarDialogoCarregando(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando dados");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

}
