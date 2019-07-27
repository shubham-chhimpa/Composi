package com.example.composi;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class FetchAudioBytes {

   private byte[] songByteArray;

    public FetchAudioBytes(Context ctx) {





    }


    public byte[] getSongByteArray()
    {
        return songByteArray;
    }
}
