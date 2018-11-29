package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.REST.CategoriaRest;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

public class Productos_ofrecidos extends AppCompatActivity {

    private Spinner spinnerCat;
    private ListView listprod;
    private Button agregar;
    private Button gestionprod;
    private EditText cantidad_pedir;
    private Intent i;
    private Integer ID;
    private ArrayAdapter<Producto> productosAdapter;

    private Categoria categoriaSeleccionada = new Categoria();
    private ArrayAdapter<Producto> adaptador_prod;
    private List<Producto> listProd = new ArrayList<>();
    //private List<Producto> listProd1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_ofrecidos);


        spinnerCat = (Spinner) findViewById(R.id.spinner_cat);
        agregar = (Button) findViewById(R.id.btnProdAddPedido);
        spinnerCat.setSelection(0);
        gestionprod = findViewById(R.id.buttongestion);
        listprod = (ListView) findViewById(R.id.listprod);
        listprod.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        cantidad_pedir = (EditText) findViewById(R.id.cantidad_pedir);

        final ProductoRepository prodrepos = new ProductoRepository();

        i = new Intent();
        agregar.setEnabled(false);
        cantidad_pedir.setEnabled(false);


        //--------Analizando si se reciben datos en el Intent---------------------
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getInt("NUEVO_PEDIDO") == 1) {
                agregar.setEnabled(true);
                cantidad_pedir.setEnabled(true);
                gestionprod.setEnabled(false);
            }
        }


        //---------Adapter lista de categorias-------------------------------------------------------------------
        /*List<Categoria> categorias = new ArrayList<>();
        categorias = prodrepos.getCategorias();
        ArrayAdapter<Categoria> adaptador_categoria_prod = new ArrayAdapter<>(this , android.R.layout.simple_spinner_item, categorias);
        adaptador_categoria_prod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adaptador_categoria_prod);*/

        //--------Adapter producto por categoria---------------------------------------------------
        /*spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categoria cat = prodrepos.getCategorias().get(position);
                ArrayAdapter<Producto> adaptador_prod = new ArrayAdapter<Producto>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, prodrepos.buscarPorCategoria(cat));
                adaptador_prod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listprod.setAdapter(adaptador_prod);
                listprod.setItemChecked(0, true );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //CategoriaRest catRest = new CategoriaRest();

                /*
                //Creo esta variable porque tenía problema con la variable final cats
                Categoria[] aux = null;

                try {
                    aux = catRest.listarTodas().toArray(new Categoria[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                //req 05
                MyDatabase.getInstance(getApplicationContext());
                final List<Categoria> cats = MyDatabase.getAll();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //---------Adapter lista de categorias-------------------------------------------------------------------

                        ArrayAdapter<Categoria> categoriasAdapter = new ArrayAdapter<Categoria>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, cats);
                        spinnerCat.setAdapter(categoriasAdapter);
                        spinnerCat.setSelection(0);
                        if(spinnerCat.getItemAtPosition(0) != null){
                            categoriaSeleccionada = (Categoria) spinnerCat.getItemAtPosition(0);
                        }

                        spinnerCat.setOnItemSelectedListener(listenerDeListaCat);
                    }
                });
            }

        };
        Thread hiloCargarComo = new Thread(r);
        hiloCargarComo.start();


        listprod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Producto prod = (Producto) adapterView.getItemAtPosition(i);
                ID = prod.getId();
            }

        });


        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifico que se ingrese cantidad
                if(!cantidad_pedir.getText().toString().isEmpty() && !cantidad_pedir.getText().toString().equals("0")){
                    int cant = Integer.parseInt(cantidad_pedir.getText().toString());
                    i.putExtra("cantidad", cant);
                    i.putExtra("idProducto", ID);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });


        gestionprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), GestionProductoActivity.class);
                startActivity(i);
            }
        });
    }

    AdapterView.OnItemSelectedListener listenerDeListaCat = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            categoriaSeleccionada = (Categoria) parent.getItemAtPosition(position);
            listProd = MyDatabase.buscarPorCategoria(categoriaSeleccionada);

            if (listProd != null) {
                adaptador_prod = new ArrayAdapter<Producto>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, listProd);
                adaptador_prod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listprod.setAdapter(adaptador_prod);
                listprod.setItemChecked(0, true );
            } else {
                Toast.makeText(Productos_ofrecidos.this, "La categoria no dispone de productos", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
