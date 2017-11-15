package trabalho.sine.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import trabalho.sine.R;
import trabalho.sine.dao.VagaDAO;
import trabalho.sine.model.Vaga;

public class ResultActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    @BindView(R.id.favoriteFloat) FloatingActionButton favoriteBtn;
    @BindView(R.id.shareBTN) FloatingActionButton shareBtn;
    private Vaga vaga;
    @BindView(R.id.openLink) FloatingActionButton openLink;

    @BindView(R.id.titleValue) TextView title;
    @BindView(R.id.moneyValue) TextView money;
    @BindView(R.id.cityValue) TextView city;
    @BindView(R.id.addressValue) TextView address;
    @BindView(R.id.companyValue) TextView company;
    @BindView(R.id.functionValue) TextView function;
    @BindView(R.id.descriptionValue) TextView des;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    private int position;
    private FragmentDrawer mDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Define o ButterKnife para gerenciar as activities e ativa o modo de debugação.
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        createToolbar();
        carregaInforActivity(getIntent().getExtras());

    }

    //Responsavel pela criação e definção do toolbar
    private void createToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_result);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer_result, (DrawerLayout) findViewById(R.id.activity_result), mToolbar);
        mDrawerFragment.setDrawerListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position",position);
        setResult(RESULT_OK,returnIntent);
        super.onBackPressed();
    }

    private void carregaInforActivity(Bundle bundle) {
        String vagaJson = (String) bundle.get("vaga");
        position = (int) bundle.get("position");
        vaga = new Gson().fromJson(vagaJson, Vaga.class);

        populaActivity(vaga);

    }

    public void populaActivity(Vaga vaga){
        favoriteBtn.setImageResource(
                (vaga.isFavoritado() == false ?
                        R.drawable.favorite_border : R.drawable.favorite_black));
        if (vaga.getTitulo() != null && vaga.getTitulo().trim().length() > 0)
            title.setText(vaga.getTitulo());
        else
            title.setText(R.string.titulo_nao_informado);

        if (vaga.getSalario() != null && vaga.getSalario().trim().length() > 0)
            money.setText(vaga.getSalario());
        else
            money.setText(R.string.salario_nao_informado);

        if (vaga.getCidade() != null && vaga.getCidade().trim().length() > 0)
            city.setText(vaga.getCidade());
        else
            city.setText(R.string.cidade_nao_informada);

        if (vaga.getEndereco() != null && vaga.getEndereco().trim().length() > 0)
            address.setText(vaga.getEndereco());
        else
            address.setText(R.string.endereco_nao_informado);

        if (vaga.getEmpresa() != null && vaga.getEmpresa().trim().length() > 0)
            company.setText(vaga.getEmpresa());
        else
            company.setText(R.string.empresa_nao_informada);

        if (vaga.getFuncao() != null && vaga.getFuncao().trim().length() > 0)
            function.setText(vaga.getFuncao());
        else
            function.setText(R.string.funcao_nao_informada);

        if (vaga.getDescricao() != null && vaga.getDescricao().trim().length() > 0)
            des.setText(vaga.getDescricao());
        else
            des.setText(R.string.descricao_nao_informada);
    }

    @OnClick(R.id.favoriteFloat)
    public void favoriteClick(View view) {
        VagaDAO vagaDAO = new VagaDAO(this.getApplicationContext());
        if (vaga.isFavoritado() == false) {
            vaga.setFavoritado(true);
            vagaDAO.insert(vaga);
            favoriteBtn.setImageResource(R.drawable.favorite_black);
            //Toast.makeText(this, R.string.toast_msg_result_activity_favoritado, Toast.LENGTH_SHORT).show();
        } else {
            vagaDAO.delete(vaga);
            vaga.setFavoritado(false);
            favoriteBtn.setImageResource(R.drawable.favorite_border);
           // Toast.makeText(this, R.string.toast_msg_result_activity_desfavoritado, Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.shareBTN)
    // Compartilha o link do card relacionado a vaga.
    public void shareClick(View view){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        share.putExtra(Intent.EXTRA_SUBJECT, vaga.getTitulo());
        share.putExtra(Intent.EXTRA_TEXT, vaga.getUrl_sine());

        startActivity(Intent.createChooser(share,"Compartilhar"));
    }

    @OnClick(R.id.openLink)
    // Abre o link da vaga no navegador.
    public void openLink(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vaga.getUrl_sine()));
        startActivity(intent);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position){
            case 0: LoadActivities.home(this); break;
            case 1: LoadActivities.searchActivity(this); break;
            case 2: LoadActivities.favoriteActivity(this); break;
            case 3: LoadActivities.searchForGraphicActivity(this);break;
            case 4: LoadActivities.info(this); break;
            default: Log.i("ERRO","POSITION ERROR"); break;
        }
    }

}