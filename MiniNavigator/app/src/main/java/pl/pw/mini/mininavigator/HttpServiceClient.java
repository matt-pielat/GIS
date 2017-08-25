package pl.pw.mini.mininavigator;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.pw.mini.mininavigator.utilities.Callback;


public class HttpServiceClient {

    private String mAddress;

    private int mConnectTimeout;
    private int mReadTimeout;

    public HttpServiceClient(String address, int connectTimeout, int readTimeout) {
        mAddress = address;
        mConnectTimeout = connectTimeout;
        mReadTimeout = readTimeout;
    }

    public void sendPostRequest(String method, JSONObject param, Callback<JSONObject> cb)
            throws IOException, InterruptedException {

        new PostRequestTask(cb).execute(method, param);
    }

    private class PostRequestTask extends AsyncTask<Object, String, JSONObject> {

        private Callback<JSONObject> mCallback;

        public PostRequestTask(Callback<JSONObject> responseCallback) {
            mCallback = responseCallback;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {

            JSONObject responseJson = null;

            try {
                String method = (String) params[0];
                JSONObject param = (JSONObject) params[1];

                URL url = new URL(mAddress + method);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // HTTPS or HTTP ?
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(param != null);
                conn.setReadTimeout(mReadTimeout);
                conn.setConnectTimeout(mConnectTimeout);
                conn.setRequestProperty("Content-Type", "application/json");

                if (param != null) {
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(param.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                }

                conn.connect();

                StringBuilder stringBuilder = new StringBuilder();

                Log.d("apka", "Response code: " + conn.getResponseCode());
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                try {
                    responseJson = new JSONObject(stringBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                conn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseJson;
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mCallback.callback(jsonObject);
        }
    }
}
