package trabalho.sine.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import trabalho.sine.controller.RequestTask;
import trabalho.sine.model.Cidade;
import trabalho.sine.model.CidadeJSON;

/**
 * @version 0.1
 *          Created by wagner on 15/12/16.
 */
public class CidadeSuggestionAdapter extends ArrayAdapter<Cidade> {
    protected static final String TAG = "CidadeSuggestionAdapter";
    private final String ENDERECO;
    private List<Cidade> suggestions;

    public CidadeSuggestionAdapter(Context context, String nameFilter, String url) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.suggestions = new ArrayList<>();
        this.ENDERECO = url;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Cidade getItem(int index) {
        return suggestions.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                RequestTask requestTask = new RequestTask();

                if (constraint != null) {

                    try {
                        String response = requestTask.execute(ENDERECO + constraint.toString()).get();
                        Gson gson = new Gson();
                        CidadeJSON cidadeJSON = gson.fromJson(response, CidadeJSON.class);
                        suggestions.clear();

                        suggestions = cidadeJSON.getCidades();

                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
