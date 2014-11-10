package com.andres.sun4all;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Download extends AsyncTask<String, String, String>{
	
	String cad[];
	static Bitmap base;
	static Bitmap nega;
    @Override
    protected String doInBackground(String... aurl) {
    	cad = aurl;
    	if(cad != null){
    		
    		try {
    			URL url_base = new URL(cad[0]);
    			base = BitmapFactory.decodeStream(url_base.openConnection().getInputStream());
    			URL url_inv = new URL(cad[1]);
    			nega = BitmapFactory.decodeStream(url_inv.openConnection().getInputStream());
    			Log.d("Download","intentando descargar ok");
    		} catch (IOException e) {
    			Log.d("Download,background","catch 1");
    		}
    	}
    	else{
    		Log.d("Download","term=null");
    	}
		return null;
    }
    
    @Override
    protected void onPostExecute(String result) {
    	Main.imagenReady=true;
    }
    /*void compCadena(String s){
    	cad = new String[2];
    	cad[0] = Main.urlBase.concat(s);
    	cad[1]= Main.urlBaseNeg.concat(s);
    }*/
}

//Imagenes para probar
//k1v_01_07_00_09h_35.jpg
//k1v_01_07_03_12h_40_E_C.jpg
//k1v_01_07_85_09h_34_E_C.jpg
//k1v_01_07_90_08h_02_E_C.jpg
//k1v_01_07_91_09h_05_E_C.jpg
//k1v_01_07_95_08h_31_E_C.jpg
//k1v_01_07_96_08h_28_E_C.jpg
//k1v_01_08_01_09h_25.jpg
//k1v_01_08_02_08h_27_E_C.jpg
//k1v_01_08_03_09h_30_E_C.jpg