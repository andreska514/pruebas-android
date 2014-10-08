package com.andres.sun4all;

import java.io.IOException;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class Main extends Activity {

	static public int contador = 0;
	
	//variables
	Imagen imagen;
	ImageView img;
	PaintView pView;
	TextView txtCont;
	Button btnAdd, btnRmv, btnInv, btnFin, btnRes;
	
	//añado
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//imagen(zoom/pintar)
		img = (ImageView) findViewById(R.id.ImgFoto);
		//img.setOnTouchListener(clickImagen);
		//imagen = new Imagen(img);
			//añado
			//pView = (PaintView) findViewById(R.id.ImgFoto);
			//pView.setOnTouchListener(clickPinta);
		
		//el contador
		txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		
		//Botones
		btnAdd =(Button)findViewById(R.id.btnAdd);
		btnRmv =(Button)findViewById(R.id.btnRmv);
		btnAdd =(Button)findViewById(R.id.btnAdd);
		btnInv =(Button)findViewById(R.id.btnInv);
		btnRes =(Button)findViewById(R.id.btnRes);
		
		btnAdd.setOnClickListener(clickBoton);
		btnRmv.setOnClickListener(clickBoton);
		btnAdd.setOnClickListener(clickBoton);
		btnInv.setOnClickListener(clickBoton);
		btnRes.setOnClickListener(clickBoton);
	}
	
	//Clicks en botones
	View.OnClickListener clickBoton = (new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnAdd:
				//prueba new activity
				Intent i = new Intent(Main.this, Segunda.class);
				startActivity(i);
				
				//activa el modo pintar
				//desactivar trazados, solo puntos
				//dibuja un sunspot donde tocas(modo escribir)
				//guardar coordenada de cada punto
				break;
			case R.id.btnRmv:
				//desactiva modo pintar
				//borra un sunspot donde tocas
				
				//prueba contador(BORRAR) ----------------------------
				contador++;
				txtCont.setText(String.valueOf(contador));
				
				
				break;
			case R.id.btnFin://envia coordenadas de los sunspot y cambia de imagen
				//envia
				//descarga nueva imagen
				//borra imagen anterior del dispositivo
				break;
			case R.id.btnInv://coger el negativo de esa imagen(finish the task)
				break;
			case R.id.btnRes://reinicia la misma imagen sin sunspots(start over)
				Imagen.logMatrix(Imagen.matrix, Imagen.imageView);
				break;
			}
		}
	});
	//Clicks en imagen
	View.OnTouchListener clickImagen = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return imagen.touch(v, event);
		}
	};
	View.OnTouchListener clickPinta = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return imagen.pinta(v, event);
		}
	};
		
}
//PRUEBAS

//***************************************************
//prueba new activity
//Intent i = new Intent(Main.this, Segunda.class);
//startActivity(i);
//***************************************************
//prueba zoom con botones 1/2
/*float[]valores = new float[9];
Imagen.matrix.getValues(valores);
valores[0]+=0.1;
valores[4]+=0.1;
Imagen.matrix.setValues(valores);
imagen.compruebaValores();*/
//***************************************************
//prueba zoom con botones 2/2
/*valores = new float[9];
Imagen.matrix.getValues(valores);
valores[0]-=0.1;
valores[4]-=0.1;
Imagen.matrix.setValues(valores);
imagen.compruebaValores();*/
//***************************************************



