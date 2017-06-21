package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.imagem.ProxyImageView;

public class ImpressaoActivity extends FragmentActivity {
	
	public static final String TAG = ImpressaoActivity.class.getSimpleName();
	
	public static final String IMAGEM_ID = "IMAGEM_ID";
	
	private boolean selecionado;
	private Imagem mImagem = null;
	private ImageView imageView;
	private int lado = 600;
    private ImageView mascaraView;
    private PointF delta = new PointF();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_impressao);
		
		int idImagem = getIntent().getExtras().getInt(IMAGEM_ID);
		
		try {
			mImagem = DAOFactory.instance( this ).imagemDAO().queryForId( idImagem );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
	    imageView = (ImageView)findViewById(R.id.imagem);
	    //mascaraView = (ImageView)findViewById(R.id.mascara);
	    
	    ProxyImageView proxyImageView = new ProxyImageView();
	    proxyImageView.loadImage(imageView, lado, mImagem.getArquivoImagem() );
	    
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
//        mascaraView.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				
//				if(event.getAction() != MotionEvent.ACTION_DOWN)
//					return false;
//				
//				mascaraView.startDrag(new ClipData("-", new String[]{"text/plain"}, 
//						new ClipData.Item("-")), new DragShadowBuilder(null), null, 0);
//				delta.x = event.getX();
//				delta.y = event.getY();
//				return true;
//			}
//		});
        
        imageView.setOnDragListener(new OnDragListener() {
			
			@Override
			public boolean onDrag(View v, DragEvent event) {
				
				if(event.getAction() == DragEvent.ACTION_DROP || event.getAction() == DragEvent.ACTION_DRAG_LOCATION){
					mascaraView.setX(event.getX() + imageView.getX() - delta.x);
					mascaraView.setY(event.getY() + imageView.getY() - delta.y);
				}
				
				return true;
			}
		});
	}
	
	private void verificaSelecao(){
		//selecionado = ServiceFactory.getFavoritosService(dbHelper).isSelecionado(impressao);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_impressao, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	Intent intent = NavUtils.getParentActivityIntent(this); 
		    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		    	NavUtils.navigateUpTo(this, intent);
		    	return true;
	    }
		
	    return super.onOptionsItemSelected(item);
	}
	
	
}