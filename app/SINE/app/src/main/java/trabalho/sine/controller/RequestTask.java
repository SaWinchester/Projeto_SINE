package trabalho.sine.controller;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @version 0.2
 * Created by wagner on 25/09/16.
 */

public class RequestTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        try {
            return makeRequest(params[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }//doInBackground()

    //Processa a url recebida, fazendo a requisição e obtendo as informações.
    private String makeRequest(String urlAdress) {
        HttpURLConnection con = null;
        URL url;
        String response = null;

        try {
            //Inicializa a conexão.
            url = new URL(urlAdress);
            con = (HttpURLConnection) url.openConnection();

            response = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert con != null;
            con.disconnect();
        }
        return response;
    }//makeRequest()

    //Processa a resposta da requisição HTTP recebida por parâmetro.
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line).append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }//readStream()
}
