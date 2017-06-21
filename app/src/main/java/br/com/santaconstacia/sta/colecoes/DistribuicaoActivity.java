package br.com.santaconstacia.sta.colecoes;

import java.io.File;
import java.sql.SQLException;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.colecoes.MascarasFragmentDialog.OnMascaraOpacidade;
import br.com.santaconstacia.sta.colecoes.MascarasFragmentDialog.OnMascaraSelecionada;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Compartilhar;
import br.com.santaconstacia.sta.database.bean.Distribuicao;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.MessageAlertDialogFragment;
import br.com.santaconstacia.sta.gestures.RotateGestureDetector;
import br.com.santaconstacia.sta.imagem.ProxyImageView;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.utils.AndroidUtils;

import com.j256.ormlite.stmt.QueryBuilder;

public class DistribuicaoActivity extends FragmentActivity {

	public static final String TAG = DistribuicaoActivity.class.getSimpleName();
	public static final String IMAGEM_ID = "IMAGEM_ID";
	
	private Imagem mImagem = null;
	private Distribuicao mDistribuicao = null;
	
	// Mascara
	private ImageView imageView;
    private ImageView mascaraView;
    private PointF delta = new PointF();
    
    private float mScaleFactor = 1.0f;
    private float mRotationDegrees = 0.f;
    private ScaleGestureDetector mScaleDetector = null;    
	private RotateGestureDetector mRotateDetector = null;
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
		    mScaleFactor *= ( detector.getScaleFactor() );
		    mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));
		    imageView.setScaleX( mScaleFactor);
		    imageView.setScaleY( mScaleFactor );
		    System.out.println("scaleFactor=" + mScaleFactor + ", getScaleFactor()=" + detector.getScaleFactor() );
			return true;
		}
	}
	
	private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			mRotationDegrees -= detector.getRotationDegreesDelta();
			imageView.setRotation( mRotationDegrees );
			return true;
		}
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView( R.layout.activity_distribuicao );
		
		int imagemId = getIntent().getExtras().getInt( IMAGEM_ID );
		
		try {
			mImagem = DAOFactory.instance( this ).imagemDAO().queryForId( imagemId );
			Arquivo arquivoImagem = mImagem.getArquivoImagem(); 
			DAOFactory.instance( this ).arquivoDAO().refresh( arquivoImagem );
			
			String nomeArquivo = arquivoImagem.getNome();
			String extensao = nomeArquivo.substring( nomeArquivo.indexOf(".") );
			nomeArquivo = nomeArquivo.replace(extensao, "_DIST" + extensao );
			
			QueryBuilder<Distribuicao, Integer> qbDist = DAOFactory.instance( this ).distribuicaoDAO().queryBuilder();
			qbDist.where().eq("name", nomeArquivo).and().eq("artigo_id", mImagem.getArtigo().getId() );
			
			mDistribuicao = qbDist.queryForFirst();
			if ( mDistribuicao == null )  {
				finish();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		imageView = (ImageView)findViewById( R.id.imagem_distribuicao );
	    ProxyImageView proxyImageView = new ProxyImageView();
	    proxyImageView.loadImage(imageView, 600, mDistribuicao.getArquivoImagem() );
	    
	    mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());	    
		mRotateDetector = new RotateGestureDetector(this, new RotateListener());		
	    
	    mascaraView = (ImageView)findViewById(R.id.mascara);
        mascaraView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() != MotionEvent.ACTION_DOWN)
					return false;
				
				System.out.println( "mascaraView > onTouchListener > onTouch > " + event.toString() );
				
				mascaraView.startDrag(new ClipData("-", new String[]{"text/plain"}, 
						new ClipData.Item("-")), new DragShadowBuilder(null), null, 0);
				delta.x = event.getX();
				delta.y = event.getY();
				return true;
			}
		});
        
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
	    
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void mostraMascara(Mascara mascara){
		
		if(mascara == null){
			mascaraView.setVisibility(View.INVISIBLE);
			return;
		}
		
		Rect bitmapArea = imageView.getDrawable().getBounds();
		PointF imageArea = AndroidUtils.scaleToFit(imageView.getWidth(), imageView.getHeight(), 
				bitmapArea.width(), bitmapArea.height());
		
		mascaraView.setVisibility(View.VISIBLE);
		
		Arquivo arquivo = mascara.getArquivo();
		
		try {
			DAOFactory.instance( this ).arquivoDAO().refresh( arquivo );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		File directory = AndroidUtils.getRepositorioArquivos( this );
		File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
		
		Bitmap bitmap = AndroidUtils.toBitmap(this, 
				arquivoImagem.getAbsolutePath(), (int)imageArea.y);
		mascaraView.setImageDrawable(new BitmapDrawable(bitmap));
		mascaraView.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mascaraView.getLayoutParams();
		params = new RelativeLayout.LayoutParams(params);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.height=(int)imageArea.y;
		imageArea = AndroidUtils.scaleToFit(imageArea.y, imageArea.y, bitmap.getWidth(), bitmap.getHeight());
		params.width=(int)imageArea.x;
		mascaraView.setLayoutParams(params);
		
		
	}
	
	
	public void onMascaraClick( MenuItem menuItem ) {
		MascarasFragmentDialog mascarasDialog = new MascarasFragmentDialog();
		mascarasDialog.setOnClienteSelecionadoListener(new OnMascaraSelecionada() {
			
			@Override
			public void onSelecao(Mascara mascara) {
				mostraMascara(mascara);				
			}
		});
		
		mascarasDialog.setOnCallbackOpacityListener( new OnMascaraOpacidade() {
			
			@Override
			public void onOpacityChanged(int opacity) {
				float value = (float)(opacity+70) / 170.0f; 
				mascaraView.setAlpha( value );
			}
			
		});
		
		mascarasDialog.show(getFragmentManager(), MascarasFragmentDialog.OPCOES_DIALOG_TAG);
	}
	
	public void showAlertMessageSharedCreated() {

		MessageAlertDialogFragment messageDialog = new MessageAlertDialogFragment();
		messageDialog.setMensagemTexto("Adicionado!");
		messageDialog.show( getFragmentManager(), MessageAlertDialogFragment.TAG );
		
	}
	
	public void onCompartilharClick( MenuItem menuItem ) {
		Compartilhar compartilhar = new Compartilhar();
		compartilhar.setArquivo( mImagem.getArquivoImagem() );
		compartilhar.setUsuario( TokenSecurity.instance().getUsuario() );
		
		try {
			DAOFactory.instance( this ).compartilharDAO().create( compartilhar );
		} catch (SQLException e) {
			return;
		}
		
		showAlertMessageSharedCreated();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_distribuicao, menu);
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

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		System.out.println( "onTouch > " + event.toString() );
//		mScaleDetector.onTouchEvent(event);
//		mRotateDetector.onTouchEvent(event);
//		return true;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
		mRotateDetector.onTouchEvent(event);
		return true;
	}
	
}
