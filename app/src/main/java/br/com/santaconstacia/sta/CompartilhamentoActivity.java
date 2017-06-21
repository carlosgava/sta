package br.com.santaconstacia.sta;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Compartilhar;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.imagem.ProxyImageView;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.IOUtils;
import br.com.santaconstacia.sta.vudroid.PdfViewerBuilder;

public class CompartilhamentoActivity extends BaseActivity {
	
	private File[] files;
	private boolean mFlagExcluir = false;
	private GridView gridview = null;
	private List<Compartilhar> arquivosCompartilhados;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compartilhamento);

		try {
			//DAOFactory.instance( this ).compartilharDAO().clearAll();
			
			arquivosCompartilhados = DAOFactory.instance( this )
					.compartilharDAO()
						.queryForEq("usuario", TokenSecurity.instance().getUsuario() );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		TextView textView = (TextView) findViewById(R.id.id_compartilhamento_textview);

		gridview = (GridView) findViewById(R.id.compartilhamento_grid);

	    gridview.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("CompartilhamentoActivity > onItemClick > view=" + view + ", position=" + position);
////				View view = (View)v.getParent();
////				ViewHolder viewHolder = (ViewHolder)view.getTag();
//				Compartilhar compartilhar = arquivosCompartilhados.get( position );
////				
////				System.out.println("ImageButtonExcluir > apagando o registro > position > " + position + " > mPosition > " + viewHolder.mPosition);
////				
//				Arquivo arquivo = compartilhar.getArquivo();
//				if ( arquivo.getUrl().contains("compartilhamento") ) {
//					try {
//						DAOFactory.instance( CompartilhamentoActivity.this ).arquivoDAO().delete( arquivo );
//						AndroidUtils.removeArquivoLocal( CompartilhamentoActivity.this, arquivo.getUrl() );
//					} catch (SQLException e) {
//						e.printStackTrace();
//						return;
//					}
//				}
////				
//				try {
//					DAOFactory.instance( CompartilhamentoActivity.this ).compartilharDAO().delete( compartilhar );
//					arquivosCompartilhados.remove( position );
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
		});
	    
		if ( arquivosCompartilhados.isEmpty() ) {
			textView.setVisibility( TextView.VISIBLE );
			gridview.setVisibility( GridView.GONE );
		} 
		else {
			textView.setVisibility( TextView.GONE );
			gridview.setVisibility( GridView.VISIBLE );
		    gridview.setAdapter(new CompartilhamentoAdapter(this, arquivosCompartilhados));
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	public void invalidateViews( ) {
		gridview.setAdapter( new CompartilhamentoAdapter(this, arquivosCompartilhados) );
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_compartilhamento, menu);
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
	
	public void onEnviarClick(MenuItem menuItem){
		ArrayList<Uri> uris = new ArrayList<Uri>();
		
		for (Compartilhar compartilhar : arquivosCompartilhados) {

			Arquivo arquivo = compartilhar.getArquivo();
			
			try {
				DAOFactory.instance( this ).arquivoDAO().refresh( arquivo );
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			
			File directory = AndroidUtils.getRepositorioArquivos( this );
			File file = new File( directory, arquivo.getUrl().replace('\\', '/') );
			
			uris.add(Uri.fromFile(file));
		}
		
		Configuracao config = null;
		try {
			config = DAOFactory.instance(this).configuracaoDAO().find();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
//				ServiceFactory.getConfiguracaoService(
//				dbHelper).find();

		String corpoEmail = "";

		if (config != null && config.templateEmail != null)
			corpoEmail = config.templateEmail;

		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, this
				.getString(R.string.assunto_email));
		i.putExtra(Intent.EXTRA_TEXT, corpoEmail);
		i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		try {
			startActivity(Intent.createChooser(i,
					this.getString(R.string.enviar_email)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(
					this, this.getString(R.string.sem_aplicativo_email),
					Toast.LENGTH_SHORT).show();
		}
	}
}

class CompartilhamentoAdapter extends BaseAdapter {
    private CompartilhamentoActivity activity;
    private LayoutInflater mInflater;
    private List<Compartilhar> mArquivosCompartilhados;
    private boolean mFlagExcluir = false;
	
    public static class ViewHolder {
    	int mPosition;
    	TextView mTextViewName;
    	ImageButton mImageButton;
    	ImageButton mImageButtonDelete;
    }
    
    public CompartilhamentoAdapter(Activity activity, List<Compartilhar> arquivosCompartilhados) {
    	this.activity = (CompartilhamentoActivity)activity;
        mArquivosCompartilhados = arquivosCompartilhados;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	public int isEnableExcluir() {
		return ( mFlagExcluir ? View.VISIBLE : View.INVISIBLE );
	}
	
    
    public int getCount() {
        return mArquivosCompartilhados.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void removeCompartilhamento( View view ) {
    	View viewLayout = (View)view.getParent();
    	ViewHolder viewHolder = (ViewHolder)viewLayout.getTag();
		Compartilhar compartilhar = mArquivosCompartilhados.get( viewHolder.mPosition );
		
		Arquivo arquivo = compartilhar.getArquivo();
		if ( arquivo.getUrl().contains("compartilhamento") ) {
			try {
				DAOFactory.instance( activity ).arquivoDAO().delete( arquivo );
				AndroidUtils.removeArquivoLocal( activity, arquivo.getUrl() );
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		
		try {
			DAOFactory.instance( activity ).compartilharDAO().delete( compartilhar );
			mArquivosCompartilhados.remove( viewHolder.mPosition );
			activity.invalidateViews();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = null;
        
        if (convertView == null) {
        	
        	view = mInflater.inflate( R.layout.layout_item_compartilhado , null );
        	viewHolder = new ViewHolder();
        	view.setTag( viewHolder );
        	
        	viewHolder.mPosition = position;
        	viewHolder.mTextViewName = (TextView)view.findViewById(R.id.id_layout_item_compartilhado_textview_name);
        	viewHolder.mImageButton = (ImageButton)view.findViewById( R.id.id_layout_item_compartilhado_image_button);
        	viewHolder.mImageButtonDelete = (ImageButton)(ImageButton)view.findViewById(R.id.id_layout_item_compartilhado_botao_excluir);
        	
        	viewHolder.mImageButtonDelete.setVisibility( isEnableExcluir() );
        	viewHolder.mImageButtonDelete.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("CompartilhamentoAdapter > onClick => " + v);
					CompartilhamentoAdapter.this.removeCompartilhamento( v );
				}
				
			});
        	
        	viewHolder.mImageButton.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("imageButton > OnLongClickListener");
					if ( mFlagExcluir ) {
						mFlagExcluir = false;
						CompartilhamentoAdapter.this.notifyDataSetChanged();
					}
				}
				
			});
        	
        	viewHolder.mImageButton.setOnLongClickListener(  new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					System.out.println("imageButton > OnLongClickListener");
					if ( !mFlagExcluir ) {
						mFlagExcluir = true;
						CompartilhamentoAdapter.this.notifyDataSetChanged();
					}
					return false;
				}
				
			});
        	
        	try
        	{
				Arquivo arquivo = mArquivosCompartilhados.get(position).getArquivo();
				
				try {
					DAOFactory.instance( this.activity ).arquivoDAO().refresh( arquivo );
		        	viewHolder.mTextViewName.setText( arquivo.getNome() );
					ProxyImageView proxy = new ProxyImageView();
					proxy.loadImage(viewHolder.mImageButton, 250, arquivo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        	
        	} catch ( RuntimeException ex ) {
        		ex.printStackTrace();
        		return  view;
        	}
        	
        } else {
        	view = convertView;
        	viewHolder = (ViewHolder)view.getTag();
        	viewHolder.mImageButtonDelete.setVisibility( isEnableExcluir() );
        }
        
        return view;
        	
//            button = new ImageView(activity);
//            button.setLayoutParams(new GridView.LayoutParams(220, 260));
//            button.setScaleType(ImageView.ScaleType.FIT_XY);
//            button.setPadding(0, 4, 0, 4);
//            
//            Compartilhar compartilhar = mArquivosCompartilhados.get( position );
//            Arquivo arquivo = compartilhar.getArquivo();
//            
//            try {
//				DAOFactory.instance( activity ).arquivoDAO().refresh( arquivo );
//			} catch (SQLException e) {
//				e.printStackTrace();
//				return button;
//			}
//            
//            if ( arquivo.getUrl().endsWith(".pdf") ) { 
//            //if(files[position].getAbsolutePath().endsWith(".pdf.png"))
//            	button.setImageResource(R.drawable.icon_pdf);
//            }
//            else if ( arquivo.getUrl().endsWith(".mp3") ) {
//            	button.setImageResource(R.drawable.icon_video);
//            }
//            else{
////	            BitmapDrawable drawable = AndroidUtils.toDrawable(activity, 
////						files[position].getAbsolutePath(), 300);			
////				button.setImageDrawable(drawable);
//            	ProxyImageView imageView = new ProxyImageView();
//            	imageView.loadImage( button, 260, arquivo );
//            }
//        } else
//            button = (ImageView) convertView;
//        
//        return button;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView_BKP(final int position, View convertView, ViewGroup parent) {
    	Uri uri = Uri.fromFile(IOUtils.copyResourceToPublicLocation(activity, R.raw.fichatecnica_2836, "fichatecnica286.pdf"));
    	
		PdfViewerBuilder pdfViewer = new PdfViewerBuilder(activity, uri);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 400);
        View view = pdfViewer.build();
        view.setLayoutParams(params);
        
        return view;
    }
}