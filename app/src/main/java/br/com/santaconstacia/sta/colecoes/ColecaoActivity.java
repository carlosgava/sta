package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;

public class ColecaoActivity extends BaseActivity {
	
	public static final String CATEGORIA_NOME = "CATEGORIA_NOME";
	public static final String CATEGORIA_ID = "CATEGORIA_ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_colecao);
		int idCategoria = getIntent().getExtras().getInt(CATEGORIA_ID);
		
		List<Artigo> artigos;
		
		try {
			artigos = DAOFactory.instance( this ).artigoDAO().queryForEq("colecao_id", idCategoria);
		    GridView gridview = (GridView) findViewById(R.id.cores_grid);
		    gridview.setAdapter(new ColecaoImageAdapter(this, artigos));
		    getActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

class ColecaoImageAdapter extends BaseAdapter {
    private Activity context;
    private List<Artigo> artigos;

    public ColecaoImageAdapter(Activity c, List<Artigo> artigos) {
        context = c;
        this.artigos = artigos;
    }

    public int getCount() {
        return artigos.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            view = buildArtigo(artigos.get(position));
            view.setLayoutParams(new GridView.LayoutParams(150, 150));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setPadding(8, 8, 8, 8);
        } else {
            view = (View) convertView;
        }

        return view;
    }
    
	private View buildArtigo(final Artigo artigo){
		Button btn = new Button(context);
		btn.setText(artigo.getCode() + "\n" + artigo.getName());
		btn.setTextColor(Color.BLACK);
		
		if ( artigo.getflagPendenteProc() ) {
			btn.setBackgroundResource(R.drawable.stackborder_download);
		} else {
			btn.setBackgroundResource(R.drawable.stackborder);
		}
		
		btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Intent intent = null;
					
					if ( artigo.getIsColorTable() ) {
						intent = new Intent(context, CartelaCoresActivity.class);
						intent.putExtra(CartelaCoresActivity.ARTIGO_ID, artigo.getId());
					}
					else {
						intent = new Intent(context, ArtigoActivity.class);
						intent.putExtra(ArtigoActivity.ARTIGO_ID, artigo.getId());
					}
					
					if ( intent != null ) {
						context.startActivity(intent);
					}
			        
				}
			}
		);
		return btn;
	}
}