package br.com.santaconstacia.sta.favoritos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.EmailFavoritosFragmentDialog;
import br.com.santaconstacia.sta.dialogs.FavoritoDialogFragment;
import br.com.santaconstacia.sta.dialogs.QuantidadeFavoritoFragmentDialog;
import br.com.santaconstacia.sta.imagem.ProxyImageView;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

public class FavoritosActivity extends Activity {

	private Favorito m_favorito;
	private TextView m_textView;
	private GridView m_gridView;
	private List<FavoritoArquivo> m_favoritos;
	
	public static interface OnFavoritoSelecionado{
		public void onSelecionado(Favorito favorito);
	}
	
	public static interface OnQuantidadeInformada{
		public void onQuantidadeInformada(FavoritoArquivo favoritoArquivo);
	}
	
	public OnFavoritoSelecionado onFavoritoSelecionado = new OnFavoritoSelecionado() {
		
		@Override
		public void onSelecionado(Favorito favorito) {
			try {
				DAOFactory.instance( FavoritosActivity.this ).configuracaoDAO().salvaFavorito(favorito);
				loadFavoritos();
				showComponentes();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	public OnQuantidadeInformada onQuantidadeInformada = new OnQuantidadeInformada() {
		
		@Override
		public void onQuantidadeInformada(FavoritoArquivo favoritoArquivo) {
			try {
				DAOFactory.instance( FavoritosActivity.this ).favoritoArquivoDAO().update( favoritoArquivo );
				loadFavoritos();
				showComponentes();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	protected void loadFavoritos() {
		try {
			Configuracao conf = DAOFactory.instance( this ).configuracaoDAO().find();
			if ( conf != null ) {
				
				if ( conf.favorito != null ) {
					m_favorito = DAOFactory.instance( this ).favoritoDAO().queryForId( conf.favorito.getId() );
				}
				
				if ( m_favorito != null ) {
					m_favoritos = DAOFactory
							.instance(this)
							.favoritoArquivoDAO()
							.queryForEq(FavoritoArquivo.ID_FAVORITO_FIELD_NAME,
									m_favorito.getId());
				} else {
					m_favoritos = new ArrayList<FavoritoArquivo>();
				}
			}
			else {
				m_favoritos = new ArrayList<FavoritoArquivo>();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	protected void showComponentes() {
		if ( m_favoritos.isEmpty() ) {
			m_gridView.setVisibility( View.GONE );
			m_textView.setVisibility( View.VISIBLE );
		} else {
			m_textView.setVisibility( View.GONE );
			m_gridView.setVisibility( View.VISIBLE );
			m_gridView.setAdapter( new FavoritosAdapter( this, m_favoritos ) );
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_favoritos );
		
		m_textView = (TextView) findViewById(R.id.id_favoritos_textview);
		m_gridView = (GridView) findViewById(R.id.id_favoritos_grid);

		loadFavoritos();
		showComponentes();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void invalidateViews() {
		showComponentes();
		
		if ( !m_favoritos.isEmpty() ) {
			m_gridView.setAdapter( new FavoritosAdapter( this, m_favoritos ) );
		}
	}
	
	public void onEnviarClick(MenuItem menuItem) {
		EmailFavoritosFragmentDialog dialog = new EmailFavoritosFragmentDialog();
		dialog.show( getFragmentManager(), EmailFavoritosFragmentDialog.TAG );
	}
	
	public void onCompararClick(MenuItem menuItem) {
		Intent intent = new Intent(this, ComparadorActivity.class);
        startActivity(intent);
	}
	
	public void onOpcoesClick(MenuItem menuItem) {
		FavoritoDialogFragment dialog = new FavoritoDialogFragment();
		dialog.setHasOptionsMenu(true);
		dialog.setOnFavoritoSelecionadoListener( onFavoritoSelecionado );
		dialog.show( getFragmentManager(), FavoritoDialogFragment.TAG);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_favoritos, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
}

class FavoritosAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
	private FavoritosActivity mActivity;
	private List<FavoritoArquivo> mFavoritos;

    public static class ViewHolder {
        int mPosition;
    	ImageButton mImageButton;
    	ImageButton mImageButtonQtd;
    	ImageButton mImageButtonFav;
    }
	
	public FavoritosAdapter( FavoritosActivity activity, List<FavoritoArquivo> favoritos ) {
		mActivity = activity;
		mFavoritos = favoritos;
		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mFavoritos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

    protected boolean verificaSeFavoritosSelecionado( Arquivo arquivo ) {
    	
    	try {
			QueryBuilder<FavoritoArquivo, Integer> qb = DAOFactory.instance( mActivity ).favoritoArquivoDAO().queryBuilder();
			qb.where().eq( FavoritoArquivo.ID_ARQUIVO_FIELD_NAME, arquivo.getId() );
			
			FavoritoArquivo registro = qb.queryForFirst();
			if ( registro != null ) {
				return true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return false;
    }
    
    protected boolean deletaArquivoDosFavoritos( Arquivo arquivo ) {
    	try {
    		DeleteBuilder<FavoritoArquivo, Integer> db = DAOFactory.instance( mActivity ).favoritoArquivoDAO().deleteBuilder();
    		db.where().eq( FavoritoArquivo.ID_ARQUIVO_FIELD_NAME, arquivo.getId() );
    		db.delete();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    	
    	return true;
    }
    
    protected void mudarStatusBotao( ViewHolder viewHolder, boolean isFavoritoSelecionado ) {
    	
    	int idRsrcImagemQtde = R.drawable.icon_quantity_disabled;
    	int idRsrcImagemFav = R.drawable.icon_check_disabled;
    	
    	if ( isFavoritoSelecionado ) {
    		idRsrcImagemQtde = R.drawable.icon_quantity;
    		idRsrcImagemFav = R.drawable.icon_check_on;
    		viewHolder.mImageButtonQtd.setEnabled( true );
    	} else {
    		viewHolder.mImageButtonQtd.setEnabled( false );
    	}
    	
    	viewHolder.mImageButtonFav.setBackgroundResource( idRsrcImagemFav );
    	viewHolder.mImageButtonQtd.setBackgroundResource( idRsrcImagemQtde );
    }
	
    protected OnClickListener mButtonImageFavOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
	    	View viewLayout = (View)view.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
	    	System.out.println("FAVORITO Pressionado");
	    	
	    	FavoritoArquivo favoritoArquivo = mFavoritos.get( viewHolder.mPosition );
			Arquivo arquivo = favoritoArquivo.getArquivo();
	    	
			if ( deletaArquivoDosFavoritos( arquivo ) ) {
				mFavoritos.remove( viewHolder.mPosition );
				mActivity.invalidateViews();
				FavoritosAdapter.this.notifyDataSetChanged();
			}
		}
		
	};
	
	protected OnClickListener mButtonImageQtdeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			System.out.println("QTDE BUTTON PRESSIONADO");
			
	    	View viewLayout = (View)view.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
	    	FavoritoArquivo favoritoArquivo = mFavoritos.get( viewHolder.mPosition );
	    	Favorito favorito = favoritoArquivo.getFavorito();
	    	
			try {
				if ( favorito.getNome() == null ) {
					DAOFactory.instance( mActivity ).favoritoDAO().refresh( favorito );
				}
				
				QuantidadeFavoritoFragmentDialog dialog = new QuantidadeFavoritoFragmentDialog();
				dialog.setFavoritoArquivo( favoritoArquivo );
				dialog.setOnQuantidadeInformada( mActivity.onQuantidadeInformada );
				dialog.show( mActivity.getFragmentManager(), QuantidadeFavoritoFragmentDialog.TAG );
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
		}
		
	};
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
		
        if ( convertView == null ) {
        	view = mInflater.inflate( R.layout.layout_item_atigo , null );
        	viewHolder = new ViewHolder();
        	view.setTag( viewHolder );

    		viewHolder.mPosition = position;
    		viewHolder.mImageButton = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_image_button );
    		viewHolder.mImageButtonQtd = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_botao_pedido );
    		viewHolder.mImageButtonFav = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_botao_favorito );
    		
    		viewHolder.mImageButtonFav.setOnClickListener( mButtonImageFavOnClickListener );
    		viewHolder.mImageButtonQtd.setOnClickListener( mButtonImageQtdeOnClickListener );

    		FavoritoArquivo favoritoArquivo = mFavoritos.get( viewHolder.mPosition );
    		Arquivo arquivo = favoritoArquivo.getArquivo();
    		
    		mudarStatusBotao(viewHolder, true);
    		
			try {
				DAOFactory.instance( mActivity ).arquivoDAO().refresh( arquivo );
				ProxyImageView proxy = new ProxyImageView();
				proxy.loadImage(viewHolder.mImageButton, 250, arquivo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
        } else {
        	view = convertView;
        	viewHolder = (ViewHolder)view.getTag();
        	viewHolder.mImageButtonFav.setVisibility( View.VISIBLE );
        }
        
		return view;
	}
	
}

