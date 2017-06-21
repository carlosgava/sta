package br.com.santaconstacia.sta.tasks.interfaces;

import br.com.santaconstacia.sta.database.bean.Arquivo;

public interface DownloadCallback {

	public void onTransferenciaCompletada( int type );
	
	public void onDownloadArquivoError( int type, Arquivo arquivo );
	
	public void onDownloadArquivoCompletado( int type, Arquivo arquivo );
	
}
