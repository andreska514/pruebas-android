package com.andres.sun4all;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Segunda extends Activity{
	Button btnLoad;
	ImageView img;
	Bitmap bitmap;
	ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segunda);
		btnLoad = (Button)findViewById(R.id.btnProgressBar);
		img = (ImageView)findViewById(R.id.my_image);


		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new LoadImage().execute("https://pybossa.socientize.eu/sun4all/sunimages/inv/k1v_01_08_03_09h_30_E_C.jpg");
			}
		});

	}
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Segunda.this);
			pDialog.setMessage("Loading Image ....");
			pDialog.show();
		}
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
		protected void onPostExecute(Bitmap image) {
			if(image != null){
				img.setImageBitmap(image);
				pDialog.dismiss();
			}else{
				pDialog.dismiss();
				Toast.makeText(Segunda.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
