package br.com.santaconstacia.sta.pdf;

import android.app.Activity;
import br.com.santaconstacia.sta.database.bean.Arquivo;

public interface PdfViewInterface {

	void load( Activity activity, Arquivo arquivo );
	
}
