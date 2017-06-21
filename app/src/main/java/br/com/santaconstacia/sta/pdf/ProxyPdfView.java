package br.com.santaconstacia.sta.pdf;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;

public class ProxyPdfView implements PdfViewInterface {

	private static final String TAG = ProxyPdfView.class.getSimpleName();
	
	private ViewLocalPdf mViewPdfLocal;
	private ViewRemotePdf mViewPdfRemote;
	
	public ProxyPdfView() {
		mViewPdfLocal = new ViewLocalPdf();
		mViewPdfRemote = new ViewRemotePdf();
	}
	
	@Override
	public void load(Activity activity, Arquivo arquivo) {
		
		if ( activity == null || arquivo == null ) 
			return;
		
		try {
			DAOFactory.instance( activity ).arquivoDAO().refresh( arquivo );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel fazer o refresh do arquivo " + arquivo.getNome() );
		}
		
		if ( arquivo.getFlagPendenteProc() ) {
			mViewPdfRemote.load(activity, arquivo);
		} else {
			mViewPdfLocal.load(activity, arquivo);
		}
		
	}

}
