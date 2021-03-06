package trabalho.sine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.appus.splash.Splash;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import trabalho.sine.R;
import trabalho.sine.function.Conexao;
import trabalho.sine.utils.Functions;
import trabalho.sine.utils.NavigationSine;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _initSplashScreen();


        //Define o ButterKnife para gerenciar as activities e ativa o modo de debugação.
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        createNavigationView();
    }

    private void _initSplashScreen() {
        Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.perform();
        splash.setBackgroundColor(getResources().getColor(R.color.Sine3));
        splash.setSplashImage(getResources().getDrawable(R.drawable.ic_logo_sive));
        splash.setSplashImageColor(getResources().getColor(R.color.Sine2));
    }

    private void createNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationSine(drawerLayout, R.id.home, this));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Functions.ClosseApp(this);
        }
    }

    @OnClick(R.id.searchActivity)
    public void searchActivity(View view) {
        if (Conexao.isConectado(this)) {
            this.startActivity(new Intent(this, SearchActivity.class));
        } else
            Toast.makeText(this, R.string.conexao_infor, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.favoriteActivity)
    public void favoriteActivity(View view) {
        this.startActivity(new Intent(this, FavoriteActivity.class));
    }

    @OnClick(R.id.searchForGraphicActivity)
    public void searchForGraphicActivity(View view) {
        if (Conexao.isConectado(this)) {
            this.startActivity(new Intent(this, SearchForGraphicActivity.class));
        } else
            Toast.makeText(this, R.string.conexao_infor, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.infoActivity)
    public void infoActivity(View view) {
        this.startActivity(new Intent(this, InfoActivity.class));
    }

}