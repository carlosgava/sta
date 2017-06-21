package br.com.santaconstacia.sta.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.download.DownloadManager;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.HttpUtils;

public class DownloadWorkerThread implements Runnable {

	private String mUsuario;
	private String mSenha;
	private Arquivo mArquivo = null;
	private File mRootDirectory = null;
	private DownloadCallback mCallback;

	public DownloadWorkerThread( File rootDirectory, Arquivo arquivo, String usuario, String senha, DownloadCallback callback ) {
		mUsuario = usuario;
		mSenha = senha;
		mCallback = callback;
		mArquivo = arquivo;
		mRootDirectory = rootDirectory;
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority( Thread.MAX_PRIORITY );
		download( mArquivo );
	}
	
	private void download(Arquivo arquivo) {
		
		File pathDestino = AndroidUtils.getDirectoryByUrl(mRootDirectory, mArquivo.getUrl() );
		
		File file = null;
		FileOutputStream fos = null;
		InputStream is = null;
		String resultado = Constantes.ERRO;
		
		try {
			byte[] buffer = new byte[ 16384 ];
			file = new File( pathDestino, mArquivo.getNome() );
			fos = new FileOutputStream( file );
			is = HttpUtils.get("/ServerEstampas2/download", false, "file", mArquivo.getUrl().replace('\\', '/'));

			System.out.println( "Iniciando copia do arquivo " + file.getName() );
			//IOUtils.copy(is, fos);
			IOUtils.copyLarge(is, fos, buffer);
			System.out.println( "Copia finalizada do arquivo " + file.getName() );

			resultado = Constantes.OK;
			
		} catch (FileNotFoundException e) {
			resultado += " - " + Constantes.ERRO_ARQUIVO_NAO_ENCONTRADO;
		} catch (ConnectTimeoutException e) {
			resultado += " - " + Constantes.ERRO_CONEXAO_TIMEOUT;
		} catch (SecurityException e) {
			resultado += " - " + Constantes.AUTENTICACAO;
		} catch (IOException e) {
			resultado += " - GENERICO"; 
		} 
//		finally {
//			IOUtils.closeQuietly(is);
//			IOUtils.closeQuietly(fos);
//			
//			try {
//				if ( resultado.contains(Constantes.OK) ) {
//					mCallback.onDownloadArquivoCompletado(Constantes.TIPO_INDEFINIDO, mArquivo);
//				}
//				else {
//					mCallback.onDownloadArquivoError(Constantes.TIPO_INDEFINIDO, mArquivo);
//				}
//			}
//			catch( RuntimeException ex ) {
//				
//			}
//		}
		
		if ( is != null ) {
			IOUtils.closeQuietly(is);
		}
		
		if ( fos != null ) {
			IOUtils.closeQuietly(fos);
		}

		try {
			if ( resultado.contains(Constantes.OK) ) {
				mCallback.onDownloadArquivoCompletado(Constantes.TIPO_INDEFINIDO, mArquivo);
			}
			else {
				mCallback.onDownloadArquivoError(Constantes.TIPO_INDEFINIDO, mArquivo);
			}
		}
		catch( RuntimeException ex ) {
			ex.printStackTrace();
		}
		
	}

}
