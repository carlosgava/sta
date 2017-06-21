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
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.dialogs.FavoritoDialogFragment.OnFavoritoInformado;


public class AdicionaFavoritoFragmentDialog extends DialogFragment {
	
	private EditText nomeCliente;
	private Favorito mFavorito;
	private OnFavoritoInformado onFavoritoInformadoListener;
	
	public static final String TAG = AdicionaFavoritoFragmentDialog.class.getSimpleName(); 

	public AdicionaFavoritoFragmentDialog() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View dialog = inflater
				.inflate(R.layout.fragment_favoritos_adiciona, null);
		builder.setView(dialog);
		nomeCliente = (EditText) dialog.findViewById(R.id.nome);
		if ( mFavorito != null ) {
			nomeCliente.setText(mFavorito.getNome());
		}

		OnClickListener adicionarListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String nome = nomeCliente.getText().toString();
				
				if(nome != null && (nome=nome.trim()).length() != 0){
					if ( onFavoritoInformadoListener != null ) {
						if ( mFavorito == null ) {
							mFavorito = new Favorito( nome );
						} else {
							mFavorito.setNome( nome );
						}
						onFavoritoInformadoListener.onSave( mFavorito );
					}
				}
			}
		};

		builder.setPositiveButton(R.string.adicionar, adicionarListener);
		builder.setNegativeButton(R.string.fechar, null);
		builder.setTitle(R.string.adicionar_favorito);

		return builder.create();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public OnFavoritoInformado getOnFavoritoInformadoListener() {
		return onFavoritoInformadoListener;
	}

	public void setOnFavoritoInformadoListener(
			OnFavoritoInformado onFavoritoInformadoListener) {
		this.onFavoritoInformadoListener = onFavoritoInformadoListener;
	}

	public Favorito getFavorito() {
		return mFavorito;
	}

	public void setFavorito(Favorito favorito) {
		mFavorito = favorito;
	}
	
	

}