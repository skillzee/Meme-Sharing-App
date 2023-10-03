package com.example.memeappbyparth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.memeappbyparth.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getMeme();


        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMeme();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharememe();
            }
        });
    }

    private void sharememe() {
        Bitmap image = getBipmapFromView(binding.memeImage);
        shareImageAndText(image);
    }

    private void shareImageAndText(Bitmap image) {
        Uri uri = getImageToShare(image);
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_STREAM,uri);

        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Image Via: "));
    }

    private Uri getImageToShare(Bitmap image) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;

        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder, "meme.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri= FileProvider.getUriForFile(this, "com.example.memeappbyparth", file);
        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  uri;
    }

    private Bitmap getBipmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable background = view.getBackground();
        if(background!=null){
            background.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void getMeme() {
        String url = "https://meme-api.com/gimme";
        binding.loader.setVisibility(View.VISIBLE);
        binding.memeImage.setVisibility(View.GONE);

        RequestQueue que = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imageUrl = response.getString("url");
                            Glide.with(getApplicationContext()).load(imageUrl).into(binding.memeImage);
                            binding.loader.setVisibility(View.GONE);
                            binding.memeImage.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        que.add(jsonObjectRequest);

        
    }
}