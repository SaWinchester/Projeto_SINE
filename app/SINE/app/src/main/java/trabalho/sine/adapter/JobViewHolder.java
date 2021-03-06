package trabalho.sine.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import trabalho.sine.R;
import trabalho.sine.activity.ResultActivity;
import trabalho.sine.model.Vaga;

/**
 * @version 0.1
 *          Created by Samuel Cunha on 25/11/17.
 */
class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final int ACTIVITY_REQUEST = 1;
    final TextView vagaNome;
    final TextView vagaEmpresa;
    final TextView vagaEndereco;
    final ImageButton favoriteBtn;
    private final Context context;
    private int position;
    private Vaga vaga;

    public JobViewHolder(View itemView, Context context) {
        super(itemView);
        vagaNome = itemView.findViewById(R.id.vaga_nome);
        vagaEmpresa = itemView.findViewById(R.id.vaga_empresa);
        vagaEndereco = itemView.findViewById(R.id.vaga_endereco);
        favoriteBtn = itemView.findViewById(R.id.favorito);
        this.context = context;
        itemView.setOnClickListener(this);
    }

    //Método responsavel pelo Click.
    @Override
    public void onClick(View v) {
        Intent resultadoActivity = new Intent(context, ResultActivity.class);
        resultadoActivity.putExtra("vaga", transformaVagaJson(vaga));
        resultadoActivity.putExtra("position", position);
        ((Activity) context).startActivityForResult(resultadoActivity, ACTIVITY_REQUEST);
    }

    private String transformaVagaJson(Vaga vaga) {
        Gson gson = new Gson();
        return gson.toJson(vaga);
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
