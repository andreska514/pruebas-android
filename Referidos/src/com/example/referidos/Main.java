package com.example.referidos;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display a indeterminate progress bar on title bar
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    
		setContentView(R.layout.activity_main);
		//final ProgressDialog progressDialog = new ProgressDialog(this);
		final ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView adapter, View view, int position, long arg) {
		 
		        // Sets the visibility of the indeterminate progress bar in the
		        // title
		        setProgressBarIndeterminateVisibility(true);
		 
		        // Show progress dialog
		        ProgressDialog progressDialog = ProgressDialog.show(Main.this, "ProgressDialog", "Loading!");
		 
		        // Tells JavaScript to open windows automatically.
		        //webView.getSettings().setJavaScriptEnabled(true);
		 
		        // Sets our custom WebViewClient.
		        //webView.setWebViewClient(new myWebClient());
		 
		        // Loads the given URL
		        Item item = (Item) listView.getAdapter().getItem(position);
		           // webView.loadUrl(item.getUrl());
		    }
		});
	}

	class ItemAdapter extends BaseAdapter {

		private Context context;
		private List<Item> items;

		public ItemAdapter(Context context, List<Item> items) {
			this.context = context;
			this.items = items;
		}

		@Override
		public int getCount() {
			return this.items.size();
		}
		@Override
		public Object getItem(int position) {
			return this.items.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (convertView == null) {
				// Create a new view into the list.
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.list, parent, false);
			}

			EditText refNombre = (EditText)rowView.findViewById(R.id.refNombre);
			EditText refTelefono = (EditText)rowView.findViewById(R.id.refTelefono);
			Spinner spinner_parentesco = (Spinner)rowView.findViewById(R.id.spinner_parentesco);
			CheckBox check_estudio = (CheckBox)rowView.findViewById(R.id.check_estudio);
			CheckBox check_trabajar = (CheckBox)rowView.findViewById(R.id.check_trabajar);
			
			Item item = this.items.get(position);
			refNombre.setText(item.getNombre());
			refTelefono.setText(item.getTelefono());
			check_estudio.setChecked(item.getEstudio());
			check_trabajar.setChecked(item.getTrabajar());
			
			return rowView;
		}
	}//fin class itemAdapter

	class Item {
		
		private int parentesco;
		private String nombre;
		private String telefono;
		private boolean estudio;
		private boolean trabajar;
		
		public Item() {
			super();
		}

		public Item(int parentesco, String nombre, String telefono,
				boolean estudio, boolean trabajar) {
			super();
			this.parentesco=parentesco;
			this.nombre = nombre;
			this.telefono = telefono;
			this.estudio=estudio;
			this.trabajar=trabajar;
		}
		public int getParentesco(){
			return parentesco;
		}
		public void setParentesco(int x){
			parentesco=x;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getTelefono() {
			return telefono;
		}
		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}
		public boolean getEstudio(){
			return estudio;
		}
		public void setEstudio (boolean b){
			estudio=b;
		}
		public boolean getTrabajar(){
			return trabajar;
		}
		public void setTrabajar (boolean b){
			trabajar=b;
		}
	}
}//fin class main