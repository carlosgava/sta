package br.com.santaconstacia.sta.imagem;

import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import android.view.View;

public interface ImageViewInterface {

	void setDownloadCallback( DownloadCallback callback );
	
	void loadImage( View view, int height, Arquivo arquivo );
	void loadImage( View view, int height, int width, Arquivo arquivo );
	
}
