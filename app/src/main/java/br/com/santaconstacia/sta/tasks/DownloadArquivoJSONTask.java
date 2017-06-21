package br.com.santaconstacia.sta.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.tasks.interfaces.ProcessarJsonCallback;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoDownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.HttpUtils;

public class DownloadArquivoJSONTask extends AsyncTask<Void, Long, String>{

	private static final String TAG = DownloadArquivoJSONTask.class.getSimpleName();
	
	private File mJsonFile = null;
	private Context mContext = null;
	private String mUsuario = null;
	private String mSenha = null;
	private boolean mShortTimeOut = false;
	private ResultadoDownloadCallback mCallback = null;

	
	public DownloadArquivoJSONTask(Context context,  String usuario, String senha, ResultadoDownloadCallback callback) {
		mContext = context;
		mUsuario = usuario;
		mSenha = senha;
		mCallback = callback;
		mJsonFile = AndroidUtils.getRepositorioArquivos( context );
		mJsonFile = new File(mJsonFile, "json.zip");
	}
	
	@Override
	protected String doInBackground(Void... params) {
	    String resultado = Constantes.ERRO;
	    
		try {
			Configuracao conf = DAOFactory.instance( mContext ).configuracaoDAO().find();
			
				mJsonFile.delete();

				FileOutputStream fos = null;
				InputStream is = null;

				try {
					Date ultimaAtualizacao = null; 
					if ( conf != null ) {
						ultimaAtualizacao = conf.ultimaAtualizacao;
					}
					
					fos = new FileOutputStream(mJsonFile);
					
					if(ultimaAtualizacao == null) {
						is = HttpUtils.get("/ServerEstampas2/export_json_zip", mShortTimeOut, "user", mUsuario);
					}
					else { 
						is = HttpUtils.get("/ServerEstampas2/export_json_zip", mShortTimeOut, "user", mUsuario, 
							"lastupdate", AndroidUtils.DATE_FORMAT.format(ultimaAtualizacao));
					}
					
					IOUtils.copy(is, fos);
					resultado = Constantes.OK;
					
				} catch (FileNotFoundException e) {
					mJsonFile.delete();
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally{
					IOUtils.closeQuietly(fos);
					IOUtils.closeQuietly(is);
					
					if ( resultado.equals(Constantes.OK) ) {
						mCallback.onTransferCompleted( "json.zip" );
					}
					else {
						mCallback.onTransferError( "json.zip" );
					}
				}
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: " + e.getMessage() );
		}
		return resultado;
	}

	private InputStream openJson() throws FileNotFoundException{
		return new BufferedInputStream(new FileInputStream(mJsonFile));
	}
	
	
}
