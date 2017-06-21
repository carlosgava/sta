
package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.AdicionaFavoritoFragmentDialog;
import br.com.santaconstacia.sta.dialogs.FavoritoDialogFragment.OnFavoritoInformado;
import br.com.santaconstacia.sta.dialogs.QuantidadeFavoritoFragmentDialog;
import br.com.santaconstacia.sta.favoritos.FavoritosActivity.OnQuantidadeInformada;
import br.com.santaconstacia.sta.imagem.ProxyImageView;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

public class ArtigoActivity extends BaseActivity {
	
	public static final String TAG = ArtigoActivity.class.getSimpleName();
	
	public static final String ARTIGO_ID = "ARTIGO_ID";
	
	private Artigo artigo;
	private GridView mGridview;
	
	public OnQuantidadeInformada onQuantidadeInformada = new OnQuantidadeInformada() {
		
		@Override
		public void onQuantidadeInformada(FavoritoArquivo favoritoArquivo) {
			try {
				DAOFactory.instance( ArtigoActivity.this ).favoritoArquivoDAO().update( favoritoArquivo );
				mGridview.setAdapter( new ColecaoAdapter( ArtigoActivity.this, artigo ) );
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colecao_colecao);

		int artigoId = -1;
		
		if ( savedInstanceState != null ) {
			artigoId = savedInstanceState.getInt(ARTIGO_ID);
		} else {
			artigoId = getIntent().getExtras().getInt(ARTIGO_ID);
		}
		
		try {
			artigo = DAOFactory.instance( this ).artigoDAO().queryForId( artigoId );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		atualizarImagens();
	    
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void atualizarImagens() {
		mGridview = (GridView) findViewById(R.id.colecoes_grid);
		mGridview.setAdapter( new ColecaoAdapter( this, artigo ) );
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_artigo, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onCartelaCoresClick(MenuItem menuItem){

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d( TAG, "onPause");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d( TAG, "onResume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d( TAG, "onStop");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d( TAG, "onStart");
	}
	
}

class ColecaoAdapter extends BaseAdapter implements DownloadCallback {
	private static final String TAG = ColecaoAdapter.class.getSimpleName();
	
    private ArtigoActivity mActivity;
    private Artigo mArtigo;
    private List<Imagem> mImagens;
    private LayoutInflater mInflater;
    private Arquivo mArquivoSavedInstance;
    private ViewHolder mViewHolderSavedInstance;

    public static class ViewHolder {
        int mPosition;
    	ImageButton mImageButton;
    	ImageButton mImageButtonQtd;
    	ImageButton mImageButtonFav;
    }
    
    public ColecaoAdapter(ArtigoActivity activity, Artigo artigo) {
    	mActivity = activity;
    	mArtigo = artigo;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    	try {
			mImagens = DAOFactory.instance( activity ).imagemDAO().queryForEq("artigo_id", artigo.getId() );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel carregar as imagens do artigo " + artigo.getId() );
			return;
		}
    	
        System.out.println("ColecaoAdapter > artigo_id=" + artigo.getId() + ", qtde="+mImagens.size());
    	
    }

    public int getCount() {
        return ( mImagens == null ? 0 : mImagens.size() );
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
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
    
    protected FavoritoArquivo findFavoritoByArquivo( Arquivo arquivo ) {

    	FavoritoArquivo registro = null;
    	
    	try {
			QueryBuilder<FavoritoArquivo, Integer> qb = DAOFactory.instance( mActivity ).favoritoArquivoDAO().queryBuilder();
			qb.where().eq( FavoritoArquivo.ID_ARQUIVO_FIELD_NAME, arquivo.getId() );
			
			registro = qb.queryForFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return registro;
    }
    
    protected boolean deletaArquivoDosFavoritos( Arquivo arquivo ) {
    	try {
    		DeleteBuilder<FavoritoArquivo, Integer> db = DAOFactory.instance( mActivity ).favoritoArquivoDAO().deleteBuilder();
    		db.where().eq( FavoritoArquivo.ID_ARQUIVO_FIELD_NAME, arquivo.getId() );
    		db.delete();
		} catch (SQLException e) {
			e.printStackTrace();
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
    
    protected OnClickListener mButtonImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
	    	View viewLayout = (View)view.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
			Intent intent = new Intent( mActivity, ImpressaoGalleryActivity.class );
			intent.putExtra( ImpressaoGalleryActivity.ARTIGO_ID, mArtigo.getId() );
			intent.putExtra( ImpressaoGalleryActivity.IMAGE_POSITION, viewHolder.mPosition );
			mActivity.startActivity(intent);
			
			Imagem imagem = mImagens.get( viewHolder.mPosition );
			Arquivo arquivo = imagem.getArquivoImagem();
			
		}
		
	};
	
    protected OnClickListener mButtonImageQtdOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
	    	System.out.println("QTDE Pressionada");
	    	
	    	View viewLayout = (View)view.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
	    	
			Imagem imagem = mImagens.get( viewHolder.mPosition );
			Arquivo arquivo = imagem.getArquivoImagem();

			FavoritoArquivo favoritoArquivo = findFavoritoByArquivo( arquivo );
			if ( favoritoArquivo != null ) {
				try {
					Favorito favorito = favoritoArquivo.getFavorito();
					
					if ( favorito.getNome() == null ) {
						DAOFactory.instance( mActivity ).favoritoDAO().refresh( favorito );
					}
					
					QuantidadeFavoritoFragmentDialog dialog = new QuantidadeFavoritoFragmentDialog();
					dialog.setFavoritoArquivo( favoritoArquivo);
					dialog.setOnQuantidadeInformada( mActivity.onQuantidadeInformada );
					dialog.show( mActivity.getFragmentManager(), QuantidadeFavoritoFragmentDialog.TAG );
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	
	protected OnFavoritoInformado mOnFavoritoInformado = new OnFavoritoInformado() {
		
		@Override
		public void onSave(Favorito favorito) {
			try {
				DAOFactory.instance( mActivity ).favoritoDAO().createOrUpdate( favorito );
				DAOFactory.instance( mActivity ).configuracaoDAO().salvaFavorito( favorito );
				FavoritoArquivo favoritoArquivo = new FavoritoArquivo( favorito, mArquivoSavedInstance );
				DAOFactory.instance( mActivity ).favoritoArquivoDAO().create( favoritoArquivo );
				mudarStatusBotao( mViewHolderSavedInstance, true );
				ColecaoAdapter.this.notifyDataSetChanged();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	};
	
    protected OnClickListener mButtonImageFavOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
	    	View viewLayout = (View)view.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
	    	System.out.println("FAVORITO Pressionado");
	    	
			Imagem imagem = mImagens.get( viewHolder.mPosition );
			Arquivo arquivo = imagem.getArquivoImagem();
	    	
			if ( verificaSeFavoritosSelecionado( arquivo ) ) {
				deletaArquivoDosFavoritos( arquivo );
				mudarStatusBotao( viewHolder, false );
			} else {
				try {
					Favorito favorito = DAOFactory.instance( mActivity ).configuracaoDAO().favorito();
					
					if ( favorito == null ) {
						mArquivoSavedInstance = arquivo;
						mViewHolderSavedInstance = viewHolder;
						AdicionaFavoritoFragmentDialog adicionaFavoritoDialog = new AdicionaFavoritoFragmentDialog();
						adicionaFavoritoDialog.setOnFavoritoInformadoListener( mOnFavoritoInformado );
						adicionaFavoritoDialog.show( mActivity.getFragmentManager(), AdicionaFavoritoFragmentDialog.TAG );
					} else {
						FavoritoArquivo favoritoArquivo = new FavoritoArquivo( favorito, arquivo );
						DAOFactory.instance( mActivity ).favoritoArquivoDAO().create( favoritoArquivo );
						mudarStatusBotao( viewHolder, true );
						ColecaoAdapter.this.notifyDataSetChanged();
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	};
	
    public View getView(final int position, View convertView, ViewGroup parent) {
    	View view = null;
    	ViewHolder viewHolder = null;
    	
    	if ( convertView == null ) {
    		view = mInflater.inflate(R.layout.layout_item_atigo, null);
    		viewHolder = new ViewHolder();
    		view.setTag( viewHolder );
    		
    		viewHolder.mImageButton = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_image_button );
    		viewHolder.mImageButtonQtd = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_botao_pedido );
    		viewHolder.mImageButtonFav = (ImageButton) view.findViewById( R.id.id_layout_item_artigo_botao_favorito );
    		
    		viewHolder.mImageButton.setOnClickListener( mButtonImageOnClickListener );
    		viewHolder.mImageButtonQtd.setOnClickListener( mButtonImageQtdOnClickListener );
    		viewHolder.mImageButtonFav.setOnClickListener( mButtonImageFavOnClickListener );
    		
    	} else {
    		view = convertView;
    		viewHolder = (ViewHolder)view.getTag();
    	} 
    		
		viewHolder.mPosition = position;
		
		Imagem imagem = mImagens.get( viewHolder.mPosition );
		Arquivo arquivo = imagem.getArquivoImagem();
		mudarStatusBotao( viewHolder, verificaSeFavoritosSelecionado(arquivo) );
		ProxyImageView proxy = new ProxyImageView();
		proxy.setDownloadCallback( this );
		proxy.loadImage(viewHolder.mImageButton, 260, arquivo);
		
		System.out.println("getView > position=" + viewHolder.mPosition + " > id=" + arquivo.getId() + " > nome=" + arquivo.getNome() );
			
    	return view;
    }

	@Override
	public void onTransferenciaCompletada(int type) {
		
		boolean existeImagemPendente = false;
		
		for (Imagem imagem : mImagens) {
			Arquivo arquivo = imagem.getArquivoImagem();
			try {
				DAOFactory.instance( mActivity ).arquivoDAO().refresh( arquivo );
				if ( arquivo.getFlagPendenteProc() ) {
					existeImagemPendente = true;
					break;
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if ( mArtigo.getflagPendenteProc() ) {
			if ( !existeImagemPendente ) {
				try {
					DAOFactory.instance( mActivity ).artigoDAO().refresh( mArtigo );
					mArtigo.setFlagPendenteProc( false );
					DAOFactory.instance( mActivity ).artigoDAO().update( mArtigo );
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		mActivity.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				mActivity.atualizarImagens();
			}
		});
	}

	@Override
	public void onDownloadArquivoError(int type, Arquivo arquivo) {
	}

	@Override
	public void onDownloadArquivoCompletado(int type, Arquivo arquivo) {
	}
    
}