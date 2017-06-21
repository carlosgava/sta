package br.com.santaconstacia.sta.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ConnectTimeoutException;

import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoDownloadCallback;
import br.com.santaconstacia.sta.utils.HttpUtils;
import android.os.AsyncTask;

public class DownloadArquivoTask  extends AsyncTask<Void, Long, String> {

	private static final String TAG = DownloadArquivoTask.class.getSimpleName();
	
	private String mUsuario;
	private String mSenha;
	private String mUrlOrigem;
	private String mUrlDestino;
	private ResultadoDownloadCallback mCallback;
	
	public DownloadArquivoTask( String usuario, String senha, String urlOrigem, String urlDestino, ResultadoDownloadCallback callback ) {
		mUsuario = usuario;
		mSenha = senha;
		mUrlOrigem = urlOrigem;
		mUrlDestino = urlDestino;
		mCallback = callback;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		File arquivo = null;
		FileOutputStream fos = null;
		InputStream is = null;
		String resultado = Constantes.ERRO;
		
		try {
			arquivo = new File( mUrlDestino );
			fos = new FileOutputStream( arquivo );
			is = HttpUtils.get("/ServerEstampas2/download", false, "file", mUrlOrigem.replace('\\', '/'));

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
				mCallback.onTransferCompleted( mUrlOrigem );
			}
			else {
				mCallback.onTransferError( mUrlOrigem );
			}
		}
		
		return resultado;
	}

}
