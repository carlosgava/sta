package br.com.santaconstacia.sta.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Comunicacao;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.utils.IOUtils;

public class CanaisComunicacaoDialogFragment extends DialogFragment {
	public static final String CANAIS_COMUNICACAO_DIALOG_TAG = "CANAIS_COMUNICACAO_DIALOG";
	
	private List<Comunicacao> mComunicacoes = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View dialog = inflater.inflate(R.layout.fragment_canais_comunicacao, null);
		builder.setView(dialog);		
		builder.setNegativeButton(R.string.fechar, null);
		builder.setTitle(R.string.canais_comunicacao);
		
		final ListView list = (ListView) dialog.findViewById(android.R.id.list);
		
		try {
			mComunicacoes = DAOFactory.instance( getActivity() ).comunicacaoDAO().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if( mComunicacoes != null && mComunicacoes.size() > 0 ){
			String[] com = new String[mComunicacoes.size()];
			
			for (int i=0; i< com.length; i++)
				com[i]=mComunicacoes.get(i).getNome();
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
					R.layout.fragment_canal_comunicacao, com);
			
			list.setAdapter(adapter);
			
			list.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Comunicacao comunicacao = mComunicacoes.get(position);
					String[] arquivos = new String[comunicacao.getArquivos().size()];
					final List<Arquivo> arquivosList = new ArrayList<Arquivo>(comunicacao.getArquivos());
					
					int i=0;
					for (Arquivo arquivo : comunicacao.getArquivos())
						arquivos[i++]=arquivo.getNome();
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
							R.layout.fragment_canal_comunicacao, arquivos);
					
					list.setAdapter(adapter);
							
					list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							try {
								IOUtils.abrir(getActivity(), arquivosList.get(position), "application/pdf");
							} catch ( RuntimeException ex ) {
								Log.w(CANAIS_COMUNICACAO_DIALOG_TAG, "FALHA: Arquivo nao encontrado, talvez precise sincronizar!!!");;
							}
						}
					});
				}
			});
		}
		
		return builder.create();
	}
}