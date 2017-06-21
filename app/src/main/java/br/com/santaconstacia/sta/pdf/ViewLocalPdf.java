package br.com.santaconstacia.sta.pdf;

import android.app.Activity;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.utils.IOUtils;

public class ViewLocalPdf implements PdfViewInterface {

	@Override
	public void load(Activity activity, Arquivo arquivo) {
		IOUtils.abrir(activity, arquivo, "application/pdf" );
	}

}
