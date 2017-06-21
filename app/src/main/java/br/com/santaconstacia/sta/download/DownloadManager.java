package br.com.santaconstacia.sta.download;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.util.Log;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.tasks.DownloadWorkerThread;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class DownloadManager implements DownloadCallback, Runnable {
	
	private static final String TAG = DownloadManager.class.getSimpleName();
	
	private static final int MAX_AVAILABLE = 5;
	private final Semaphore mSemaphore = new Semaphore(MAX_AVAILABLE, true);	
	
	private int mTipo;
	private int mCountTransferErros = 0;
	private boolean mRodar = true;
	private Queue<Arquivo> mArquivos = null;
	private DownloadCallback mCallback = null;
	private Activity mActivity;
	private WaitNotify mWaitNotify = null;
		
	
	public DownloadManager( Activity activity ) {
		mActivity = activity;
		mWaitNotify = new WaitNotify();
	}
	
	public void putFiles( int tipo, DownloadCallback callback, List<Arquivo> arquivos ) {
		mTipo = tipo;
		mCallback = callback;
		mCountTransferErros = 0;
		mArquivos = new LinkedBlockingQueue<Arquivo>(arquivos);
		mWaitNotify.doNotify();
	}
	
	public int getCountTransferErrors() {
		return mCountTransferErros;
	}
	
	@Override
	public void onTransferenciaCompletada(int type) {
	}

	public void stop() {
		mRodar = false;
	}
	
	@Override
	public void onDownloadArquivoError(int type, Arquivo arquivo) {
		mCountTransferErros++;
		mCallback.onDownloadArquivoError(mTipo, arquivo);
		mSemaphore.release();
		mWaitNotify.doNotify();
	}

	@Override
	public void onDownloadArquivoCompletado(int type, Arquivo arquivo) {
		mCallback.onDownloadArquivoCompletado(mTipo, arquivo);
		mSemaphore.release();
		mWaitNotify.doNotify();
	}

	
 	@Override
	public void run() {
		while( mRodar ) {
			try {
				mSemaphore.acquire();
				Log.d(TAG, "DownloadManager > mSemaphore.acquire() > ");
				
				final Arquivo arquivo = mArquivos.poll();
				
				if ( arquivo == null ) {
					mSemaphore.release();
					mWaitNotify.doWait();
					// Verifica se todas as threads nao foram liberadas
					if ( mSemaphore.availablePermits() != MAX_AVAILABLE ) {
						continue;
					}
					
					break;
				}
				
				try {
					
					DAOFactory.instance( mActivity ).arquivoDAO().refresh( arquivo );
					File rootDirectory = AndroidUtils.getRepositorioArquivos( mActivity );
					
					Thread thread = new Thread(new DownloadWorkerThread( rootDirectory, arquivo,
							TokenSecurity.instance().getUsuario(),
							TokenSecurity.instance().getSenha(), this) );
					
					thread.start();
					
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (RuntimeException ex ) {
					ex.printStackTrace();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		mCallback.onTransferenciaCompletada(mTipo);
	}

}
