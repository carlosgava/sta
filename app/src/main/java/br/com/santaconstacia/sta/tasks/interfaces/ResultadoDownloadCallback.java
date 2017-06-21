package br.com.santaconstacia.sta.tasks.interfaces;

public interface ResultadoDownloadCallback {

	public void onTransferCompleted( String nomeArquivo );
	
	public void onTransferError( String nomeArquivo );
	
}
