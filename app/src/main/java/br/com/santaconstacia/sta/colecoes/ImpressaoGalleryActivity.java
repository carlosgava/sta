package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.adapter.ImageGalleryAdapter;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.pdf.ProxyPdfView;

public class ImpressaoGalleryActivity extends FragmentActivity {

	public static final String TAG = ImpressaoGalleryActivity.class.getSimpleName();
	public static final String ARTIGO_ID = "ARTIGO_ID";
	public static final String IMAGE_POSITION = "IMAGE_POSITION";
	
	private boolean selecionado;
	private int mArtigoId = -1;
	private List<Imagem> mImagens = null;
	private ViewPager mViewPager = null;
	private ImageGalleryAdapter mImageGalleryAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_impressao_galery);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mArtigoId = getIntent().getExtras().getInt( ARTIGO_ID );
		int image_position = getIntent().getExtras().getInt( IMAGE_POSITION );
		
		try {
			mImagens = DAOFactory.instance( this ).imagemDAO().queryForEq( "artigo_id", mArtigoId );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		mViewPager = (ViewPager) findViewById( R.id.view_pager_gallery );
		mImageGalleryAdapter = new ImageGalleryAdapter(this, mImagens);
		mViewPager.setAdapter( mImageGalleryAdapter );
		mViewPager.setCurrentItem( image_position );
	}
	
//	private void atualizaSelecao(MenuItem menuItem){
//		int resourceId = selecionado 
//					? android.R.drawable.checkbox_on_background
//					: android.R.drawable.checkbox_off_background;
//
//		menuItem.setIcon(resourceId);
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_impressao, menu);
//		atualizaSelecao(menu.findItem(R.id.selecionado));
		
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
	
	public void onFichaTecnicaClick( MenuItem menuItem ) {
		List<InformacaoTecnica> infoTechs = null;
		try {
			infoTechs = DAOFactory.instance( this ).informacaoTecnicaDAO().queryForEq("artigo_id", mArtigoId);
			if ( infoTechs != null && infoTechs.size() > 0 ) {
				ProxyPdfView proxyView = new ProxyPdfView();
				proxyView.load( this, infoTechs.get(0).getArquivoImagem() );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void onPedidoClick( MenuItem menuItem ) {
	}
	
	public void onFavoritosClick( MenuItem menuItem ) {
	}
	
	public void onDistribuicaoClick( MenuItem menuItem ) {
		Imagem imagem = mImagens.get( mViewPager.getCurrentItem() );
		Intent intent = new Intent( this, DistribuicaoActivity.class );
		intent.putExtra( DistribuicaoActivity.IMAGEM_ID, imagem.getId() );
		startActivity( intent );
	}
	
}
