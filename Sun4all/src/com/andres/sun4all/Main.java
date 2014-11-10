//how to use external jars 
//http://stackoverflow.com/questions/1334802/how-can-i-use-external-jars-in-an-android-project

package com.andres.sun4all;




import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Main extends FragmentActivity {
	
	static boolean imagenReady = false;
	static boolean inicio = true;
	Bitmap bitmapBase, bitmapBaseNeg;

	
	static String urlBase = "https://pybossa.socientize.eu/sun4all/sunimages/";
	static String urlBaseNeg="https://pybossa.socientize.eu/sun4all/sunimages/inv/";
	
	static public int contador = 0;
	static TextView txtCont;
	//variables
	Imagen imagen;
	ImageView img;
	Button btnInv, btnFin, btnRes;
	ToggleButton btnAdd, btnRmv;

	LinearLayout layout1;
	String cadena;
	Editable strMove;
	Editable strAdd;

	//hasta aqui funciona
	static int width;
	static int height;
	static Drawable d;
	static int viewWidth;
	static int viewHeight;

	static boolean check;
	static boolean finish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inicio=true;
		img = (ImageView) findViewById(R.id.ImgFoto);

		imagen = new Imagen(this);

		//el contador
		txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));

		//Botones
		btnAdd =(ToggleButton)findViewById(R.id.btnAdd);
		btnRmv =(ToggleButton)findViewById(R.id.btnRmv);
		btnFin =(Button)findViewById(R.id.btnFin);
		btnInv =(Button)findViewById(R.id.btnInv);
		btnRes =(Button)findViewById(R.id.btnRes);

		btnAdd.setOnClickListener(clickToggle);
		btnRmv.setOnClickListener(clickToggle);
		btnFin.setOnClickListener(clickBoton);
		btnInv.setOnClickListener(clickBoton);
		btnRes.setOnClickListener(clickBoton);

		layout1 = (LinearLayout)findViewById(R.id.layout1);
		layout1.setBackgroundColor(Color.TRANSPARENT);
		/** Change the button add/move depending the language*/
		idiomas();
		activaBotonesInicio(false);
	}
	
	//Clicks en botones
	View.OnClickListener clickToggle = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnAdd:
				imagen.borra=false;
				if(btnAdd.isChecked())//add sunspot
				{
					btnAdd.setText(strAdd);
					imagen.pinta = true;
					Log.d("btnAdd.isChecked()",""+imagen.pinta);
				}
				else//move
				{
					btnAdd.setText(strMove);
					imagen.pinta = false;
					Log.d("btnAdd.isChecked()",""+imagen.pinta);
				}
				break;//fin case btnAdd

			case R.id.btnRmv:
				if(btnRmv.isChecked())
				{
					btnRmv.setText("OK");
					activaBotonesBorrar(false);
					imagen.borra=true;
					imagen.pinta = true;
				}
				else
				{
					btnRmv.setText("Remove Sunspot");
					activaBotonesBorrar(true);
					imagen.borra=false;
					if(btnAdd.isChecked())
						imagen.pinta=true;
					else
						imagen.pinta=false;
				}
				break;
			}
		}
	});
	View.OnClickListener clickBoton = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnFin:
				if(inicio){
					//try{
						//new Download().execute(params);
						imagenReady=false;
						new Download().execute(getRandomUrl());
						while(!imagenReady){}
						bitmapBase=Download.base;
						bitmapBaseNeg=Download.nega;
						imagen.changeBitmap(bitmapBase);
						//imagen.changeBitmap("k1v_01_07_00_09h_35.jpg");
					/*}catch(IOException e){
						toast(e.getStackTrace().toString(), 3000);
					}*/
					btnFin.setText(R.string.btnFin);
					activaBotonesInicio(true);
					inicio=false;
				}else{
					Dialogo dialogo = new Dialogo();
					dialogo.show(getSupportFragmentManager(), "tagAlerta");
				}
				break;
			case R.id.btnInv://cambiar bitmap a negativo
				Intent i = new Intent(Main.this, Segunda.class);
				startActivity(i);
				break;
			case R.id.btnRes://reinicia imagen sin sunspots(start over)
				vaciaCoordenadas();
				break;
			}
		}
	});
	void vaciaCoordenadas(){
		imagen.listaPtos.clear();//vacio listaPtos
		imagen.listaMarcas.clear();
		cambiaAdd(false);//paso el boton add a move
	}
	void cambiaAdd(boolean b){
		btnAdd.setChecked(b);
		imagen.pinta=b;
		if(b)
			btnAdd.setText(strAdd);
		else
			btnAdd.setText(strMove);
		Log.d("btnAdd.isChecked()",""+imagen.pinta);
	}
	void activaBotonesBorrar(boolean b){
		btnAdd.setEnabled(b);
		btnInv.setEnabled(b);
		btnFin.setEnabled(b);
		btnRes.setEnabled(b);
	}
	void activaBotonesInicio(boolean b){
		btnAdd.setEnabled(b);
		btnRmv.setEnabled(b);
		btnInv.setEnabled(b);
		btnRes.setEnabled(b);
	}

	void toast(String s, int ms){
		Toast.makeText(getApplicationContext(), s, ms).show();
	}
	void ok(){
		DialogoOk ok = new DialogoOk();
		ok.show(getSupportFragmentManager(), "tagAlerta");
	}
	/** Set the String and font of btnAdd depending user language*/
	void idiomas(){
		Locale current = getResources().getConfiguration().locale;
		Log.i("getLanguage","-"+current.getLanguage());
		/** español*/
		if (current.getLanguage().equals("es")){
			cadena = "Mover imagen/añadir mancha";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,12,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),13,26,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** italiano*/
		else if(current.getLanguage().equals("it")){
			cadena = "Spostare l'immagine/Aggiungi macchia solare";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),20,43,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** français*/
		else if(current.getLanguage().equals("fr")){
			cadena = "Déplacez l'image/Ajouter taches solaires";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,16,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),17,39,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		/** english(default)*/
		else{
			cadena = "Move image/Add Sunspot";
			strMove = Editable.Factory.getInstance().newEditable(cadena);
			strAdd = Editable.Factory.getInstance().newEditable(cadena);
			strMove.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,10,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			strAdd.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),11,22,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		btnAdd.setText(strMove);
	}
	class Dialogo extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Did you finish this task and want start another?")
			.setTitle("Finish the task")
			.setPositiveButton("yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Log.i("Confirmacion","Aceptada");
					//envia listaMarcas()+id Imagen
					
					//borra ptos y pasa a modo mover
					vaciaCoordenadas();
					btnFin.setText(R.string.btnFin2);
					activaBotonesInicio(false);
					inicio=true;
					//descarga nueva imagen
					//borra imagen anterior del dispositivo
					dialog.cancel();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Log.i("Confirmacion","Rechazada");
					dialog.cancel();	
					
				}
			});
			/*btnFin.setText(R.string.btnFin);
					activaBotonesInicio(true);
					inicio=false;*/
			return builder.create();
		}
	}
	class DialogoOk extends DialogFragment {
		private boolean boton=false;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Done")
			.setTitle("Accept")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					
					dialog.cancel();
				}
			});
			return builder.create();
		}
		public boolean getBoton() {
			return boton;
		}
		public void setBoton(boolean boton) {
			this.boton = boton;
		}
	}
	String[] getRandomUrl(){
		String cad[] = new String[2];
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
		String s = "k1v_01_08_03_09h_30_E_C.jpg";
		cad[0]=urlBase.concat(s);
		cad[0]=urlBaseNeg.concat(s);
		return cad;
	}
}

//Intent i = new Intent(Main.this, Segunda.class);
//startActivity(i);

//pruebas refresco imagen
//refresco imagen(NO VA)
//imagen.postInvalidate();
//imagen.postInvalidate(0, 0, 1000, 1000);
//imagen.refreshDrawableState();
//imagen.onDraw(new Canvas());
//toast("all the sunspots deleted",2000);
//imagen.onTouchEvent(null);
//ok();
/*Imagen touchView = (Imagen) findViewById(R.id.ImgFoto);
touchView.clear();
(imagen.clickImagen).clear();*/
//imagen.onTouchEvent(imagen.lastEvent);

