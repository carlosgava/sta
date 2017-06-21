package br.com.santaconstacia.sta.imagem;

import java.io.File;
import java.sql.SQLException;

import android.util.Log;
import android.view.View;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class ProxyImageView implements ImageViewInterface {

	private static final String TAG = ProxyImageView.class.getSimpleName();
	
	private ViewLocalImage mViewLocalImage = null;
	private ViewRemoteImage mViewRemoteImage = null;
	private DownloadCallback mCallback = null;
	
	public ProxyImageView() {
		mViewLocalImage = new ViewLocalImage();
		mViewRemoteImage = new ViewRemoteImage();
	}
	
	public void setDownloadCallback( DownloadCallback callback ) {
		mCallback = callback;
	}
	
	@Override
	public void loadImage(View view, int height, Arquivo arquivo) {
		
		if ( view == null || arquivo == null ) 
			return;
		
		try {
			DAOFactory.instance( view.getContext() ).arquivoDAO().refresh( arquivo );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel fazer o refresh do arquivo " + arquivo.getNome() );
		}
		
		File directory = AndroidUtils.getRepositorioArquivos(view.getContext());
		File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
		
		if ( arquivoImagem.length() == 0 && !arquivo.getFlagPendenteProc() ) {
			arquivo.setFlagPendenteProc( true );
			try {
				DAOFactory.instance( view.getContext() ).arquivoDAO().update( arquivo );
			} catch (SQLException e) {
				Log.e(TAG, "FALHA: Nao foi possivel alterar o flagPendenteProc para true para o arquivo com tamanho zero");
			}
		}
		if ( arquivo.getFlagPendenteProc() || !arquivoImagem.exists() ) {
			mViewRemoteImage.loadImage(view, height, arquivo);
			mViewRemoteImage.setDownloadCallback( mCallback );
		} else {
			mViewLocalImage.setDownloadCallback( mCallback );
			mViewLocalImage.loadImage(view, height, arquivo);
		}
	}
	
	@Override
	public void loadImage(View view, int height, int width, Arquivo arquivo) {
		if ( view == null || arquivo == null ) 
			return;
		
		try {
			DAOFactory.instance( view.getContext() ).arquivoDAO().refresh( arquivo );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel fazer o refresh do arquivo " + arquivo.getNome() );
		}
		
		File directory = AndroidUtils.getRepositorioArquivos(view.getContext());
		File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
		
		if ( arquivoImagem.length() == 0 && !arquivo.getFlagPendenteProc() ) {
			arquivo.setFlagPendenteProc( true );
			try {
				DAOFactory.instance( view.getContext() ).arquivoDAO().update( arquivo );
			} catch (SQLException e) {
				Log.e(TAG, "FALHA: Nao foi possivel alterar o flagPendenteProc para true para o arquivo com tamanho zero");
			}
		}
		if ( arquivo.getFlagPendenteProc() || !arquivoImagem.exists() ) {
			mViewRemoteImage.setDownloadCallback( mCallback );
			mViewRemoteImage.loadImage(view, height, width, arquivo);
		} else {
			mViewLocalImage.setDownloadCallback( mCallback );
			mViewLocalImage.loadImage(view, height, width, arquivo);
		}
	}

}
