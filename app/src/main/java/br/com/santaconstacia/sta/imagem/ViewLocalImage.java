package br.com.santaconstacia.sta.imagem;

import java.io.File;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class ViewLocalImage implements ImageViewInterface {

	private DownloadCallback mCallback = null;
	
	@Override
	public void loadImage(View view, int height, Arquivo arquivo) {
		
		File directory = AndroidUtils.getRepositorioArquivos(view.getContext());
		File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
		
		if ( !arquivoImagem.exists() ) {
			return;
		}
		
		BitmapDrawable drawable = AndroidUtils.toDrawable(view.getContext(),
				arquivoImagem.getAbsolutePath(), height);
		
		if(view instanceof ImageView){
			ImageView imgView = (ImageView) view;
			imgView.setImageDrawable(drawable);
		}else
			view.setBackgroundDrawable(drawable);
	}

	@Override
	public void loadImage(View view, int height, int width, Arquivo arquivo) {
		loadImage( view, height, arquivo );
	}

	@Override
	public void setDownloadCallback(DownloadCallback callback) {
		mCallback = callback;
	}

}
