package br.com.santaconstacia.sta.imagem;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.tasks.DownloadWorkerThread;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class ViewRemoteImage implements ImageViewInterface, DownloadCallback{

	protected int mHeight = 0;
	protected View mView = null;
	protected Arquivo mArquivo = null;
	protected DownloadCallback mCallback = null;
	protected OnClickListener mOldClickListener = null;
	
	protected OnClickListener mDownloadClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println("DOWNLOAD LISTENER PRESSIONADO");
			
			new Thread(new DownloadWorkerThread(
					AndroidUtils.getRepositorioArquivos(mView.getContext()),
					mArquivo, TokenSecurity.instance().getUsuario(),
					TokenSecurity.instance().getSenha(), ViewRemoteImage.this))
					.start();
		}
		
	};
	
	protected void saveOldListener( View view ) {
		try {
			Field f = Class.forName("android.view.View").getDeclaredField("mListenerInfo");
			if (f != null) { 
				f.setAccessible(true);
			}
			Object myLiObject= f.get(view);
			Field ff = Class.forName("android.view.View$ListenerInfo").getDeclaredField("mOnClickListener");
			if (ff != null && myLiObject != null) {
				mOldClickListener = (OnClickListener) ff.get(myLiObject);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void loadImage(View view, int height, Arquivo arquivo) {
		
		mView = view;
		mHeight = height;
		mArquivo = arquivo;
		saveOldListener( view );
		
		if ( view instanceof ImageButton ) {
			ImageButton imageView = (ImageButton)view;
			imageView.setOnClickListener( mDownloadClickListener );
			imageView.setImageResource( R.drawable.icon_download );
		} else {
			view.setBackgroundResource( R.drawable.icon_download );
		}
	}

	@Override
	public void loadImage(View view, int height, int width, Arquivo arquivo ) {
		
		mView = view;
		mHeight = height;
		mArquivo = arquivo;
		
		saveOldListener( view );
		
		if ( view instanceof ImageButton ) {
			ImageButton imageView = (ImageButton)view;
			imageView.setOnClickListener( mDownloadClickListener );
			Drawable imageDownload = view.getResources().getDrawable( R.drawable.icon_download );
			Bitmap bitmap = ((BitmapDrawable)imageDownload).getBitmap();
			Drawable drAjusted = new BitmapDrawable( view.getResources(), Bitmap.createScaledBitmap(bitmap, height, width, true) );
			imageView.setImageDrawable( drAjusted );
///			imageView.setImageResource( R.drawable.icon_download );
		} else {
			view.setBackgroundResource( R.drawable.icon_download );
		}
	}

	@Override
	public void onTransferenciaCompletada(int type) {
		System.out.println("TRANSFERENCIA COMPLETADA");
	}

	@Override
	public void onDownloadArquivoError(int type, Arquivo arquivo) {
		System.out.println("DOWNLOAD ARQUIVO COM ERRO");
	}

	@Override
	public void onDownloadArquivoCompletado(int type, Arquivo arquivo) {
		
		System.out.println("DOWNLOAD ARQUIVO COMPLETADO");
		
		try {
			arquivo.setFlagPendenteProc( false );
			DAOFactory.instance( mView.getContext() ).arquivoDAO().update( arquivo );
			mView.setOnClickListener( mOldClickListener );
			
			File directory = AndroidUtils.getRepositorioArquivos(mView.getContext());
			File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
			
			if ( !arquivoImagem.exists() ) {
				return;
			}
			
			if ( mCallback != null ) {
				mCallback.onTransferenciaCompletada( 0 );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setDownloadCallback(DownloadCallback callback) {
		mCallback = callback;
	}


}
