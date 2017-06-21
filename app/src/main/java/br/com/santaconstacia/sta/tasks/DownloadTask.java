package br.com.santaconstacia.sta.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.os.AsyncTask;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.download.DownloadManager;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.HttpUtils;

public class DownloadTask  extends AsyncTask<Void, Long, String> {

	private String mUsuario;
	private String mSenha;
	private File mRootDirectory = null;
	private Arquivo mArquivo = null;
	private DownloadCallback mCallback;

	public DownloadTask( File rootDirectory, String usuario, String senha, Arquivo arquivo, DownloadCallback callback ) {
		mUsuario = usuario;
		mSenha = senha;
		mArquivo = arquivo;
		mCallback = callback;
		mRootDirectory = rootDirectory;
	}

	@Override
	protected String doInBackground(Void... params) {
		
		File pathDestino = AndroidUtils.getDirectoryByUrl(mRootDirectory, mArquivo.getUrl() );
		
		File file = null;
		FileOutputStream fos = null;
		InputStream is = null;
		String resultado = Constantes.ERRO;
		
		try {
			file = new File( pathDestino, mArquivo.getNome() );
			fos = new FileOutputStream( file );
			is = HttpUtils.get("/ServerEstampas2/download", false, "file", mArquivo.getUrl().replace('\\', '/'));

			IOUtils.copy(is, fos);

			resultado = Constantes.OK;
			
		} catch (FileNotFoundException e) {
			resultado += " - " + Constantes.ERRO_ARQUIVO_NAO_ENCONTRADO;
		} catch (ConnectTimeoutException e) {
			resultado += " - " + Constantes.ERRO_CONEXAO_TIMEOUT;
		} catch (SecurityException e) {
			resultado += " - " + Constantes.AUTENTICACAO;
		} catch (IOException e) {
			resultado += " - GENERICO"; 
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(fos);	
			
			if ( resultado.contains(Constantes.OK) ) {
				mCallback.onDownloadArquivoCompletado(Constantes.TIPO_INDEFINIDO, mArquivo);
			}
			else {
				mCallback.onDownloadArquivoError(Constantes.TIPO_INDEFINIDO, mArquivo);
			}
		}
		
		return resultado;
	}
	

}
