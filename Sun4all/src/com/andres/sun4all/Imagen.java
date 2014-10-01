package com.andres.sun4all;

//https://github.com/sephiroth74/ImageViewZoom
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Imagen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagen);
		
	}

}
