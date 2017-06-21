package org.vudroid.pdfdroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.vudroid.core.BaseViewerActivity;
import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.pdfdroid.codec.PdfContext;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Compartilhar;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.dialogs.MessageAlertDialogFragment;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.utils.AndroidUtils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class PdfViewerActivity extends BaseViewerActivity
{
    private static final int MENU_SHARE = 3;
	private static final String ARQUIVO_ID = "ARQUIVO_ID";
	private Arquivo mArquivo = null;
	private View mView = null;
    
	@Override
    protected DecodeService createDecodeService()
    {
        return new DecodeServiceBase(new PdfContext());
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content);
		int arquivo_id = getIntent().getExtras().getInt( ARQUIVO_ID );
		
		try {
			mArquivo = DAOFactory.instance( this ).arquivoDAO().queryForId( arquivo_id );
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		//view.setDrawingCacheEnabled(true); //necess�rio para gerar o bitmap a partir da view
	}
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	super.onCreateOptionsMenu(menu);
//    	menu.removeItem(2);
//    	MenuItem menuItem = menu.add(0, MENU_SHARE, 0, R.string.compartilhar);
//    	menuItem.setIcon(R.drawable.icon_share);
//    	return true;
//    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_informacao_tecnica, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if(item.getItemId() == MENU_SHARE){
//    		createBitmap();
//    		return true;
//    	}
//    	else return super.onOptionsItemSelected(item);
//    }
	
	public void onCompartilharClick( MenuItem item ) {
		//View v = super.getWindow().getDecorView();
		View v = mView;
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache(true);
		Bitmap bitmap = v.getDrawingCache();
		
		File directory = AndroidUtils.getRepositorioArquivos(this);
		directory = new File( directory, "compartilhamento");
		if ( !directory.exists() ) {
			directory.mkdirs();
		}
		
		String name = mArquivo.getNome().substring( mArquivo.getNome().lastIndexOf("/") + 1 );
		name = name.substring(0, name.lastIndexOf(".") );
		
		String fileName ="IT-";
		fileName = fileName.concat( name );
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
		    
			MessageAlertDialogFragment messageDialog = new MessageAlertDialogFragment();
			messageDialog.setMensagemTexto("Adicionado!");
			messageDialog.show( getFragmentManager(), MessageAlertDialogFragment.TAG );
		    
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
    
    public void createBitmap(){
    	String path = getIntent().getData().getPath();
    	path=path.substring(path.lastIndexOf('/') + 1);
    	path="compartilhamento/" + path + ".png";
    	File file = br.com.santaconstacia.sta.utils.IOUtils.toPublicLocation(path);
    	file.getParentFile().mkdirs();
    	FileOutputStream fos = null;
    	
		try {
			fos = new FileOutputStream(file);
			fos.write(1);
	    	AndroidUtils.makeToast(this, R.string.arquivo_compartilhado);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			IOUtils.closeQuietly(fos);
		}
    }
    
    public void createBitmapBkp(){
    	View view = this.findViewById(android.R.id.content);
    	Bitmap bm = view.getDrawingCache();
    	String path = getIntent().getData().getPath();
    	path=path.substring(path.lastIndexOf('/') + 1);
    	path="compartilhamento/" + path + ".png";
    	File file = br.com.santaconstacia.sta.utils.IOUtils.toPublicLocation(path);
    	file.getParentFile().mkdirs();
    	FileOutputStream fos = null;
    	
		try {
			fos = new FileOutputStream(file);
	    	bm.compress(CompressFormat.PNG, 100, fos);
	    	AndroidUtils.makeToast(this, R.string.arquivo_compartilhado);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}finally{
			IOUtils.closeQuietly(fos);
		}		
    }
}