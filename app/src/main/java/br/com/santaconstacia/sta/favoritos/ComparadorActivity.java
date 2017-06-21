package br.com.santaconstacia.sta.favoritos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.imagem.ProxyImageView;

public class ComparadorActivity extends BaseActivity {
	private List<ImageView> buttons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparacao);

	    final LinearLayout gridview = (LinearLayout) findViewById(R.id.imagens_grid);
	    
	    LayoutParams params = new LayoutParams(120, 120);
	    final LinearLayout comparador = (LinearLayout) findViewById(R.id.comparador);
	    

	    final OnClickListener lstn = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(int i = 0; i < comparador.getChildCount(); i++)
					if(comparador.getChildAt(i).equals(v)){
						comparador.removeViewAt(i);
						gridview.addView(buttons.remove(i));
					}
			}
		};
	    
	    OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageView button = (ImageView)v;
				ImageView btn = new ImageView(ComparadorActivity.this);
		    	btn.setScaleType(ScaleType.CENTER_CROP);
				btn.setOnClickListener(lstn);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
					params.weight = 1.0f;
				btn.setLayoutParams( params );	
				ProxyImageView proxy = new ProxyImageView();
				proxy.loadImage(btn, comparador.getHeight(), ( (FavoritoArquivo)button.getTag() ).getArquivo() );
				comparador.addView(btn);
				
				if(comparador.getChildCount() > 3){
					comparador.removeViewAt(0);
					gridview.addView(buttons.remove(0));
				}
				
				buttons.add(button);
				gridview.removeView(button);
			}
		};
        
		Favorito favorito;
		try {
			favorito = DAOFactory.instance( this ).configuracaoDAO().favorito();
			if ( favorito != null ) {
				List<FavoritoArquivo> favoritos = DAOFactory.instance( this ).favoritoArquivoDAO().queryForEq( FavoritoArquivo.ID_FAVORITO_FIELD_NAME, favorito.getId() );
				for (FavoritoArquivo favoritoArquivo : favoritos) {
			    	ImageView button = new ImageView(this);
			    	button.setScaleType(ScaleType.CENTER_INSIDE);
				    button.setLayoutParams(params);
				    ProxyImageView proxy = new ProxyImageView();
				    proxy.loadImage( button, 120, favoritoArquivo.getArquivo() );
		            button.setTag( favoritoArquivo ); 
				    button.setOnClickListener(listener);
				    gridview.addView(button);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	    buttons = new ArrayList<ImageView>();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}