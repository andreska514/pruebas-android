package com.andres.sun4all;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Segunda extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segunda);

	}
	
	static List <Integer> listaCoordenadas = new ArrayList <Integer>();
	static int [] par = new int[2];
	static int envia[][];

	
	
}
/* useless
 
 *void guardaCoordenadas(int x, int y){
		listaCoordenadas.add(x);
		listaCoordenadas.add(y);
		//probando, las imprime en la app
		//Main.txtCont.setText("X :"+x+" , "+"Y :"+y);
		Main.txtCont.setText("X :"+x+" , "+"Y :"+y);
	}
	static void enviaCoordenadas(){
		envia = new int[listaCoordenadas.size()/2][2];
		Log.i("imprimiendo coordenadas", "total de coordenadas: "+listaCoordenadas.size());
		Log.i("","-------------------------------------");
		int [] lasX = new int[envia.length];
		int [] lasY = new int[envia.length];
		for(int x = 0; x<lasX.length;x++){
			//listaCoordenadas
			if(x%2==0){
				lasX[x]=listaCoordenadas.get(x*2);
			}
			else{
				if(x == 0)
					lasY[1]=listaCoordenadas.get(1);
				else
				lasY[x]=listaCoordenadas.get((x*2)-1);
			}
			
		}
		for(int x = 0; x<envia.length;x++){
			envia[x][0]=lasX[x];
			envia[x][1]=lasY[x];
			Log.i("coordenada "+(x+1)+" de "+envia.length,"X :"+envia[x][0]+" , "+"Y :"+envia[x][1]);
		}
		
		//enviamos el array --> (envia)
		
		//reseteamos el arraylist		
		listaCoordenadas.clear();
	}
 */