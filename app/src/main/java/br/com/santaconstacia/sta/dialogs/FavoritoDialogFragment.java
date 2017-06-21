package br.com.santaconstacia.sta.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.FavoritoDialogFragment.OnFavoritoInformado;
import br.com.santaconstacia.sta.favoritos.FavoritosActivity.OnFavoritoSelecionado;

import com.j256.ormlite.stmt.DeleteBuilder;

public class FavoritoDialogFragment extends DialogFragment {

	public static final String TAG = FavoritoDialogFragment.class.getSimpleName();
	
	private View mViewDialog = null;
	private FavoritosDialogFragmentAdapter mAdapter;
	private List<FavoritoItem> mFavoritos;
	private ListView mListView = null;
	private ImageButton mButtonAddFavorito = null;
	private ImageButton mButtonDeleteFavorito = null;
	private ImageButton mButtonSairFavorito = null;
	
	private OnFavoritoSelecionado onFavoritoSelecionadoListener;
	
	public static interface OnFavoritoInformado{
		public void onSave(Favorito favorito);
	}
	
	public void setOnFavoritoSelecionadoListener( OnFavoritoSelecionado onFavoritoSelecionado ) {
		this.onFavoritoSelecionadoListener = onFavoritoSelecionado;
	}
	
	public OnFavoritoSelecionado getOnFavoritoSelecionadoListener() {
		return this.onFavoritoSelecionadoListener;
	}
	
	private OnFavoritoInformado mOnFavoritoInformado = new OnFavoritoInformado() {
		
		@Override
		public void onSave(Favorito favorito) {
			try {
				DAOFactory.instance( FavoritoDialogFragment.this.getActivity() ).favoritoDAO().createOrUpdate( favorito );
				loadFavoritos();
				carregarListView();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	private OnClickListener mOnClickListenerSairFavoritos = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			FavoritoDialogFragment.this.dismiss();
		}
		
	};
	
	private OnClickListener mOnClickListenerAddFavoritos = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println("Botão adicionar favoritos acionado !!!");
			AdicionaFavoritoFragmentDialog adicionaFavoritoDialog = new AdicionaFavoritoFragmentDialog();
			adicionaFavoritoDialog.setOnFavoritoInformadoListener( mOnFavoritoInformado );
			adicionaFavoritoDialog.show( getFragmentManager(), AdicionaFavoritoFragmentDialog.TAG );
		}
		
	};
	
	private OnClickListener mOnClickListenerDeleteFavoritos = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println("Botão deletar favoritos acionado !!!");
			mAdapter.setVisibleCheckboxDelete( !mAdapter.getVisibleCheckboxDelete() );
			
			for (FavoritoItem item : mFavoritos) {
				if ( item.isExcluir() ) {
					try {
						DeleteBuilder<FavoritoArquivo, Integer> db = DAOFactory.instance( FavoritoDialogFragment.this.getActivity() ).favoritoArquivoDAO().deleteBuilder();
						db.where().eq( FavoritoArquivo.ID_FAVORITO_FIELD_NAME, item.getFavorito().getId() );
						db.delete();
						
						DAOFactory.instance( FavoritoDialogFragment.this.getActivity() ).favoritoDAO().delete( item.getFavorito() );
						loadFavoritos();
						carregarListView();
						
					} catch (SQLException e) {
						Log.w(TAG, "FALHA: Nao possivel excluir o favorito " + item.getFavorito().getNome() )
;					}
					
				}
			}
			
			mAdapter.notifyDataSetChanged();
		}
		
	};
	
	protected void loadFavoritos( ) {
		try {
			mFavoritos = new ArrayList<FavoritoItem>();
			Favorito favoritoAtual = DAOFactory.instance( getActivity() ).configuracaoDAO().favorito();
			List<Favorito> favoritos = DAOFactory.instance( getActivity() ).favoritoDAO().queryForAll();
			
			for (Favorito favorito : favoritos) {
				if ( favoritoAtual != null && favoritoAtual.getId() == favorito.getId() ) {
					mFavoritos.add( new FavoritoItem( favorito, true ) );
				} else {
					mFavoritos.add( new FavoritoItem( favorito, false ) );
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void carregarListView() {
		mAdapter = new FavoritosDialogFragmentAdapter( this, mFavoritos ); 
		mListView.setAdapter( mAdapter );
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mViewDialog = inflater.inflate(R.layout.fragment_favoritos, null);
		
		mButtonAddFavorito = (ImageButton)mViewDialog.findViewById(R.id.id_fragment_listview_favoritos_add_favorito);
		mButtonAddFavorito.setOnClickListener( mOnClickListenerAddFavoritos );
		
		mButtonDeleteFavorito = (ImageButton)mViewDialog.findViewById(R.id.id_fragment_listview_favoritos_delete_favorito);
		mButtonDeleteFavorito.setOnClickListener( mOnClickListenerDeleteFavoritos );
		
		mButtonSairFavorito = (ImageButton)mViewDialog.findViewById(R.id.id_fragment_listview_favoritos_sair_favorito);
		mButtonSairFavorito.setOnClickListener( mOnClickListenerSairFavoritos );
		
		mListView = (ListView) mViewDialog.findViewById(R.id.id_fragment_listview_favoritos);
		mListView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				for (FavoritoItem favoritoItem : FavoritoDialogFragment.this.mFavoritos) {
					favoritoItem.setSelected( false );
				}

	    		FavoritoItem item = mFavoritos.get( position );
				item.setSelected( true );
				
				FavoritoDialogFragment.this.mAdapter.notifyDataSetChanged();
				FavoritoDialogFragment.this.getOnFavoritoSelecionadoListener().onSelecionado( item.getFavorito() );
			}
		});
		
		loadFavoritos();
		carregarListView();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(mViewDialog);
		builder.setTitle(R.string.favoritos);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_fragment_favoritos, menu);
		super.onCreateOptionsMenu(menu, inflater);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}

class FavoritoItem {
	private Favorito mFavorito;
	private boolean mSelected;
	private boolean mExcluir;
	
	public FavoritoItem( Favorito favorito, boolean isSelected ) {
		mFavorito = favorito;
		mSelected = isSelected;
	}

	public Favorito getFavorito() {
		return mFavorito;
	}

	public void setFavorito(Favorito mFavorito) {
		this.mFavorito = mFavorito;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean mSelected) {
		this.mSelected = mSelected;
	}

	public boolean isExcluir() {
		return mExcluir;
	}

	public void setExcluir(boolean mExcluir) {
		this.mExcluir = mExcluir;
	}
}

class FavoritosDialogFragmentAdapter extends BaseAdapter {

	private FavoritoDialogFragment mActivity = null;
	private List<FavoritoItem> mFavoritos = null;
    private LayoutInflater mInflater;
    private boolean mVisibleCheckboxDelete = false;
    public static class ViewHolder {
        int mPosition;
    	CheckBox mCheckBoxDelete;
    	TextView mTextViewName;
    	ImageView mImageViewSelected;
    	ImageView mImageButtonEdit;
    }
	
	public FavoritosDialogFragmentAdapter( FavoritoDialogFragment activity, List<FavoritoItem> favoritos ) {
		mActivity = activity;
		mFavoritos = favoritos;
		mInflater = (LayoutInflater) activity.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setVisibleCheckboxDelete( boolean status ) {
		mVisibleCheckboxDelete = status;
	}
	
	public boolean getVisibleCheckboxDelete() {
		return mVisibleCheckboxDelete;
	}
	
	@Override
	public int getCount() {
		return mFavoritos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private OnClickListener mOnClickListenerDelete = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
	    	View viewLayout = (View)v.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
	    	CheckBox cb = (CheckBox)v;
    		FavoritoItem item = mFavoritos.get( viewHolder.mPosition );
    		item.setExcluir( cb.isChecked() );
		}
		
	};
	
//	private OnClickListener mOnClickListenerSelected = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//	    	View viewLayout = (View)v.getParent();
//	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
//			
//			for (FavoritoItem favoritoItem : mFavoritos) {
//				favoritoItem.setSelected( false );
//			}
//
//    		FavoritoItem item = mFavoritos.get( viewHolder.mPosition );
//			item.setSelected( true );
//			
//			FavoritosDialogFragmentAdapter.this.notifyDataSetChanged();
//			
//			mActivity.getOnFavoritoSelecionadoListener().onSelecionado( item.getFavorito() );
//		}
//		
//	};

	private OnClickListener mOnClickListenerEdit = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
	    	View viewLayout = (View)v.getParent();
	    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
			String name = viewHolder.mTextViewName.getText().toString();
			FavoritoItem item = mFavoritos.get( viewHolder.mPosition );
			
			AdicionaFavoritoFragmentDialog adicionaFavoritoDialog = new AdicionaFavoritoFragmentDialog();
			adicionaFavoritoDialog.setOnFavoritoInformadoListener( mOnFavoritoInformado );
			adicionaFavoritoDialog.setFavorito( item.getFavorito() );
			adicionaFavoritoDialog.show( mActivity.getFragmentManager(), AdicionaFavoritoFragmentDialog.TAG );
		}
		
	};
	
	private OnFavoritoInformado mOnFavoritoInformado = new OnFavoritoInformado() {
		
		@Override
		public void onSave(Favorito favorito) {
			try {
				DAOFactory.instance( mActivity.getActivity() ).favoritoDAO().createOrUpdate( favorito );
				mActivity.loadFavoritos();
				mActivity.carregarListView();
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
        	view = mInflater.inflate( R.layout.layout_item_row_favoritos , null );
        	
			viewHolder = new ViewHolder();
			viewHolder.mPosition = position;
			viewHolder.mImageButtonEdit = (ImageView)view.findViewById( R.id.id_layout_item_row_favoritos_image_edit );
			viewHolder.mCheckBoxDelete = (CheckBox)view.findViewById(R.id.id_layout_item_row_favoritos_checkbox_remove);
			viewHolder.mTextViewName = (TextView)view.findViewById(R.id.id_layout_item_row_favoritos_textview);
			viewHolder.mImageViewSelected = (ImageView)view.findViewById(R.id.id_layout_item_row_favoritos_image_selected);

			//viewHolder.mTextViewName.setOnClickListener( mOnClickListenerSelected );
			viewHolder.mCheckBoxDelete.setOnClickListener( mOnClickListenerDelete );
			viewHolder.mImageButtonEdit.setOnClickListener( mOnClickListenerEdit );

			view.setTag( viewHolder );
		} else {
			view = convertView;
    		viewHolder = (ViewHolder)view.getTag();
		}

		viewHolder.mPosition = position;
		
		FavoritoItem favorito = mFavoritos.get( position );
		viewHolder.mCheckBoxDelete.setVisibility( mVisibleCheckboxDelete ? View.VISIBLE : View.GONE );
		viewHolder.mImageViewSelected.setVisibility( favorito.isSelected() ? View.VISIBLE : View.INVISIBLE );
		viewHolder.mTextViewName.setText( favorito.getFavorito().getNome() );
		viewHolder.mImageButtonEdit.setVisibility( mVisibleCheckboxDelete ? View.GONE : View.VISIBLE );
		
		return view;
	}
	
}
