package com.example.composi;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.example.composi.MainActivity.audioBytes;
import static com.example.composi.MainActivity.ctx;

public class FetchAudioTask extends AsyncTask<String, Void, byte[]> {
    private onTaskCompleted listener;

    String notes;

    public FetchAudioTask(String notes,onTaskCompleted listener) {
        this.listener = listener;
        this.notes = notes;

    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        audioBytes = bytes;
        Log.i("VOLLEY", "this is in onpostexecute" + audioBytes);


    }


    @Override
    protected byte[] doInBackground(String... strings) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.ctx);
            String URL = "http://mighty-sea-91319.herokuapp.com/gettrack";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("notes", notes.toUpperCase());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    MainActivity.playBtn.setVisibility(View.VISIBLE);
                    MainActivity.fetchBtn.setVisibility(View.VISIBLE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    listener.onTaskFailed();
                    Toast.makeText(ctx, "error try again", Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {


                        Log.i("VOLLEY", String.valueOf(response.headers));

                        // playMp3(response.data);
                        audioBytes = response.data;
                        listener.onTaskCompleted();

                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };


            int socketTimeout = 1000000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return audioBytes;

    }



}
