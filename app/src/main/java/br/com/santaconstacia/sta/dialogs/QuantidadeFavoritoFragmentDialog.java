package br.com.santaconstacia.sta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.favoritos.FavoritosActivity.OnQuantidadeInformada;

public class QuantidadeFavoritoFragmentDialog extends DialogFragment {

	public static final String TAG = QuantidadeFavoritoFragmentDialog.class.getSimpleName();

	private EditText mEditTextQtde;
	private FavoritoArquivo mFavoritoArquivo;
	private View mDialog;
	
	private OnQuantidadeInformada mOnQuantidadeInformada;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mDialog = inflater
				.inflate(R.layout.fragment_favoritos_quantidade, null);
		builder.setView(mDialog);
		
		mEditTextQtde = (EditText) mDialog.findViewById(R.id.id_fragment_favoritos_quantidade_qtde);
		if ( mFavoritoArquivo != null && mFavoritoArquivo.getQtde() != null ) {
			mEditTextQtde.setText( String.valueOf( mFavoritoArquivo.getQtde().intValue() ) );
		}
		
		OnClickListener qtdeListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String qtde = mEditTextQtde.getText().toString();
				
				if ( qtde != null && !qtde.trim().isEmpty() ) {
					mFavoritoArquivo.setQtde( Integer.valueOf( qtde ) );
					if ( mOnQuantidadeInformada != null ) {
						mOnQuantidadeInformada.onQuantidadeInformada( mFavoritoArquivo );
					}
				}
			}
			
		};
		
		builder.setPositiveButton(R.string.adicionar, qtdeListener);
		builder.setNegativeButton(R.string.fechar, null);
		builder.setTitle(R.string.quantidade);
		
		
		return builder.create();
	}
	
	public void setFavoritoArquivo( FavoritoArquivo favoritoArquivo ) {
		mFavoritoArquivo = favoritoArquivo;
	}
	
	public void setOnQuantidadeInformada( OnQuantidadeInformada onQuantidadeInformada ) {
		mOnQuantidadeInformada = onQuantidadeInformada;
	}
	
}
