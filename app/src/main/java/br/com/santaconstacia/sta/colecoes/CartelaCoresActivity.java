package br.com.santaconstacia.sta.colecoes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Compartilhar;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.MessageAlertDialogFragment;
import br.com.santaconstacia.sta.imagem.ProxyImageView;
import br.com.santaconstacia.sta.pdf.ProxyPdfView;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class CartelaCoresActivity extends BaseActivity {
	
	public static String ARTIGO_ID = "ARTIGO_ID";
	
	private Artigo mArtigo = null;
	private GridView mGridView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cor);
		
		int idArtigo = getIntent().getExtras().getInt( ARTIGO_ID );
		
		try {
			mArtigo = DAOFactory.instance( this ).artigoDAO().queryForId( idArtigo );
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
		
	    mGridView = (GridView) findViewById(R.id.cores_grid);
	    callNewAdapter();
	    
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void callNewAdapter() {
	    mGridView.setAdapter( new CorImageAdapter(this, mArtigo) );
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_cartela_cores, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void showAlertMessageSharedCreated() {
		MessageAlertDialogFragment messageDialog = new MessageAlertDialogFragment();
		messageDialog.setMensagemTexto("Adicionado!");
		messageDialog.show( getFragmentManager(), MessageAlertDialogFragment.TAG );
	}
	
	public void onCompartilharClick( MenuItem item ) {
		View v = this.getWindow().getDecorView();
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache(true);
		Bitmap bitmap = v.getDrawingCache();
		
		File directory = AndroidUtils.getRepositorioArquivos(this);
		directory = new File( directory, "compartilhamento");
		if ( !directory.exists() ) {
			directory.mkdirs();
		}
		
		String fileName ="CT-";
		fileName = fileName.concat( mArtigo.getCode() );
		fileName = fileName.concat(".png");
		
		File file = new File( directory, fileName);
		FileOutputStream fos = null;
		
	    Arquivo arquivo = new Arquivo();
	    Compartilhar compartilhar = new Compartilhar();
		
		try {
		    fos = new FileOutputStream(file);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    fos.flush();
		    fos.close();
		    
		    arquivo.setNome( file.getName() );
		    arquivo.setFlagDeletar( false );
		    arquivo.setFlagPendenteProc( false );
		    arquivo.setTipoTransf( Arquivo.TIPO_TRANSFERENCIA.DOWNLOAD );
		    arquivo.setUrl( "compartilhamento/" + file.getName() );
		    DAOFactory.instance( this ).arquivoDAO().create( arquivo );
		    
		    compartilhar.setArquivo( arquivo );
		    compartilhar.setUsuario( TokenSecurity.instance().getUsuario() );
		    DAOFactory.instance( this ).compartilharDAO().create( compartilhar );
		    
		    showAlertMessageSharedCreated();
		    
		    //MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Screen", "screen");
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			if ( file.exists() ) {
				file.delete();
				
				try {
					DAOFactory.instance( this ).compartilharDAO().delete( compartilhar );
					DAOFactory.instance( this ).arquivoDAO().delete( arquivo );
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		    e.printStackTrace();
		}		
	}
	
	public void onFichaTecnicaClick( MenuItem item ) {
		try {
			List<InformacaoTecnica> infoTechs = DAOFactory.instance( this ).informacaoTecnicaDAO().queryForEq("artigo_id", mArtigo.getId() );
			if ( infoTechs != null && infoTechs.size() > 0 ) {
				InformacaoTecnica it = infoTechs.get(0);
				new ProxyPdfView().load( this, it.getArquivoImagem() );
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

class CorImageAdapter extends BaseAdapter implements DownloadCallback {
    private CartelaCoresActivity activity;
    private List<Imagem> cores;
    private Artigo mArtigo = null;
    private LayoutInflater mInflater;

    public CorImageAdapter(CartelaCoresActivity activity, Artigo artigo) {
        this.activity = activity;
        this.mArtigo = artigo;
        loadImagens();
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void loadImagens() {
		if ( mArtigo != null ) {
			try {
				this.cores = DAOFactory.instance( activity ).imagemDAO().queryForEq( "artigo_id", mArtigo.getId() );
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
    }
    
    public int getCount() {
        return cores.size();
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
        	
        	Imagem cor = cores.get(position);
        	
        	view = mInflater.inflate( R.layout.layout_item_color , null );
        	
        	ImageButton imageButton = (ImageButton)view.findViewById( R.id.id_layout_item_color_image_button );
        	TextView textViewCode = (TextView)view.findViewById(R.id.id_layout_item_color_textview_code);
        	TextView textViewName = (TextView)view.findViewById(R.id.id_layout_item_color_textview_nome);
        	
        	try
        	{
	        	String fileName = getFileName( cor.getName() );
	        	String code = getCode(fileName);
	        	String name = getName(fileName);
	        	
	        	textViewCode.setText( code );
	        	textViewName.setText( name );
	        	
				Arquivo arquivo = cor.getArquivoImagem();
				ProxyImageView proxy = new ProxyImageView();
				proxy.loadImage(imageButton, 110, 150, arquivo);
        	} catch ( RuntimeException ex ) {
        		ex.printStackTrace();
        		return  view;
        	}
        	
        } else {
        	view = convertView;
        }

        return view;
    }
    
    private String getName( String fileName ) {
    	String[] itens = fileName.split("_");
    	String name = "";
    	
    	for(int i=2; i < itens.length; i++ ) {
    		if ( i > 2 ) {
    			name += " ";
    		}
    		name += itens[i];
    	}
    	
    	name = name.substring( 0, name.indexOf('.') );
    	
    	return name;
    }

    private String getCode( String fileName ) {
    	String[] itens = fileName.split("_");
    	return itens[1];
    }
    
    private String getFileName( String url ) {
    	String fileName = url.substring( url.lastIndexOf('/')+1 );
    	return fileName;
    }

	private String toLabel(String text){
		String result = text;
		
		int index = result.indexOf("_");
		
		if(index != -1)
			result = result.substring(index + 1);
		
		index = result.indexOf(".");
		
		if(index != -1)
			result = result.substring(0, index);
		
		return result.replace('_', ' ');
	}

	@Override
	public void onTransferenciaCompletada(int type) {
		
		boolean existeImagemPendente = false;
		
		for (Imagem imagem : cores) {
			Arquivo arquivo = imagem.getArquivoImagem();
			try {
				DAOFactory.instance( activity ).arquivoDAO().refresh( arquivo );
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
					DAOFactory.instance( activity ).artigoDAO().refresh( mArtigo );
					mArtigo.setFlagPendenteProc( false );
					DAOFactory.instance( activity ).artigoDAO().update( mArtigo );
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		activity.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				activity.callNewAdapter();
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