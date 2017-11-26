package trabalho.sine.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.List;

import trabalho.sine.R;
import trabalho.sine.controller.RequestURL;
import trabalho.sine.dao.VagaDAO;
import trabalho.sine.model.Vaga;
import trabalho.sine.model.VagasJSON;
import trabalho.sine.utils.Constantes;

/**
 * Created by saw on 15/12/16.
 */

public class AdpterScrollListener extends RecyclerView.OnScrollListener{

    private Context context;
    private RecyclerView mRecyclerView;
    private JobAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Long cidadeEstado, funcao;
    private int filtroIndex;
    private int totalItemCount;
    private int pos;
    private ProgressDialog dialog;

    public AdpterScrollListener(Context context, RecyclerView mRecyclerView, JobAdapter mAdapter,
                                RecyclerView.LayoutManager mLayoutManager, Long cidadeEstado, Long funcao,
                                int filtroIndex, int pos) {
        this.context = context;
        this.mRecyclerView = mRecyclerView;
        this.mAdapter = mAdapter;
        this.mLayoutManager = mLayoutManager;
        this.cidadeEstado = cidadeEstado;
        this.funcao = funcao;
        this.filtroIndex = filtroIndex;
        this.pos = pos;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //Verifica se o scroll moveu
        if(dy > 0){
            //Obtem as informações referente aos itens do RecyclerView.
            int pastVisiblesItems, visibleItemCount;
            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
            //Verifica se chegou no ultimo elemento do recyclerview.
            if((visibleItemCount + pastVisiblesItems) == totalItemCount) {
                pos++;
                RequestURL req = new RequestURL(context);
                mostrarDialogoCarregando();
                totalItemCount--;
                mRecyclerView.scrollToPosition(totalItemCount);
                req.requestURL(String.format(Constantes.URL_API + Constantes.URL_API_VAGAS_,
                        funcao, cidadeEstado, pos, filtroIndex), new RequestURL.VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {
                        int position = totalItemCount;
                        Gson gson = new Gson();
                        VagasJSON vagasJSON = gson.fromJson(response, VagasJSON.class);

                        VagaDAO vagaDAO = new VagaDAO(context);
                        List<Vaga> vagasBd = vagaDAO.getAll();

                        for(Vaga v : vagasJSON.getVagas()) {
                            for (Vaga vs : vagasBd) {
                                if (vs.getId().toString().equalsIgnoreCase(v.getId().toString()))
                                    v.setFavoritado(true);
                            }
                            mAdapter.addItem(v, position++);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Log.e("Error",error.getMessage());
                        }catch (Exception e){
                            Log.e("Error",error.getMessage());
                        }
                    }
                });

            }
        }
    }

    private void mostrarDialogoCarregando(){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Carregando dados");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }
}
