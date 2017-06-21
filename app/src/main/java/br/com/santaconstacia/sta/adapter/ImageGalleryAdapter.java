package br.com.santaconstacia.sta.adapter;

import java.util.List;

import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.imagem.ProxyImageView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class ImageGalleryAdapter extends PagerAdapter {

	private Context mContext = null;
	private List<Imagem> mImagens = null;
	
	public ImageGalleryAdapter( Context context, List<Imagem> imagens ) {
		mContext = context;
		mImagens = imagens;
	}
	
	@Override
	public int getCount() {
		return mImagens.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ( view == (ImageView)object );
	}

	@Override
	public Object instantiateItem(View container, int position) {
		ImageView imageView = new ImageView( mContext );
		imageView.setScaleType( ImageView.ScaleType.FIT_XY );
		ProxyImageView proxyImageView = new ProxyImageView();
		proxyImageView.loadImage(imageView, 600, mImagens.get( position).getArquivoImagem() );
		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);		
	}
}
