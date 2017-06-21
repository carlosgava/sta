package br.com.santaconstacia.sta.dialogs;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class EmailFavoritosFragmentDialog extends DialogFragment {
	
	public static final String TAG = EmailFavoritosFragmentDialog.class.getSimpleName();
	public static final int CONTACT_PICKER_RESULT = 1001;	
	public static final int EMAIL_SEND_FAVORITOS_RESULT = 1002;
	public static final int EMAIL_SEND_FAVORITOS_FICHA_TECNICA = 1003;
	
	private Button mButtonCancelar = null;
	private Button mButtonContinuar = null;
	private ImageView mImageView = null;
	private EditText mEditTextContatos = null;
	private EditText mEditTextTexto = null;
	
	private OnClickListener mOnClickListenerCancelar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			EmailFavoritosFragmentDialog.this.dismiss();
		}
		
	};

	private void sendEmailFichaTecnica() throws SQLException {
		ArrayList<Uri> uris = new ArrayList<Uri>();
		
		Configuracao config = null;
		config = DAOFactory.instance( this.getActivity() ).configuracaoDAO().find();
		
		String corpoEmail = "";

		if (config != null && config.templateEmail != null) {
			corpoEmail = config.templateEmail;
		}
		
		List<FavoritoArquivo> favoritos = null;
		
		favoritos = DAOFactory.instance(this.getActivity()).favoritoArquivoDAO()
				.queryForEq("favorito_id", config.favorito.getId());

		if ( !favoritos.isEmpty() ) {
			
			corpoEmail = corpoEmail.replace("$PERSONALIZADO", mEditTextTexto.getText().toString() );
			String currentDirectory = "";
			
			for (FavoritoArquivo favoritoArquivo : favoritos) {
				Arquivo arquivo =  favoritoArquivo.getArquivo();
				DAOFactory.instance( this.getActivity() ).arquivoDAO().refresh( arquivo );

				String fileName = arquivo.getUrl().replace('\\', '/');
				fileName = fileName.substring( 0, fileName.lastIndexOf("/") );

				if ( fileName.equals( currentDirectory ) ) {
					continue;
				}
			
				File directory = AndroidUtils.getRepositorioArquivos( this.getActivity() );
				File file = new File( directory, fileName );
				
				System.out.println("directory=" + file.getAbsolutePath() );
				
				File[] files = file.listFiles( new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith("pdf");
					}
					
				} );
				
				if ( files.length == 0 ) {
					this.dismiss();
					return;
				}

				for (File f : files) {
					uris.add( Uri.fromFile(f));
				}
				
				currentDirectory = fileName;			
			}
			
			String[] emails = mEditTextContatos.getText().toString().split(",");
			
			Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, emails);
			i.putExtra(Intent.EXTRA_SUBJECT, this
					.getString(R.string.assunto_email));
			i.putExtra(Intent.EXTRA_TEXT, corpoEmail);
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			try {
				startActivityForResult(Intent.createChooser(i,
						this.getString(R.string.enviar_email)),  EMAIL_SEND_FAVORITOS_FICHA_TECNICA);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(
						this.getActivity(), this.getString(R.string.sem_aplicativo_email),
						Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
	
	private void sendEmailFavoritos() throws SQLException {
		
		ArrayList<Uri> uris = new ArrayList<Uri>();
		
		Configuracao config = null;
		config = DAOFactory.instance( this.getActivity() ).configuracaoDAO().find();
		
		String corpoEmail = "";

		if (config != null && config.templateEmail != null) {
			corpoEmail = config.templateEmail;
		}
		
		List<FavoritoArquivo> favoritos = null;
		
		favoritos = DAOFactory.instance(this.getActivity()).favoritoArquivoDAO()
				.queryForEq("favorito_id", config.favorito.getId());

		if ( !favoritos.isEmpty() ) {
			
			corpoEmail = corpoEmail.replace("$PERSONALIZADO", mEditTextTexto.getText().toString() );

			StringBuilder sb = new StringBuilder("");
			for (FavoritoArquivo favoritoArquivo : favoritos) {
				Arquivo arquivo =  favoritoArquivo.getArquivo();
				DAOFactory.instance( this.getActivity() ).arquivoDAO().refresh( arquivo );
				sb.append("\n\nImagem: ");
				sb.append( arquivo.getNome() );
				sb.append("(");
				
				String fileName = arquivo.getUrl().replace('\\', '/');
				String[] dirs = fileName.split("/");
				if ( dirs.length > 0 ) {
					sb.append( dirs[2] );
					sb.append( " - " );
					sb.append( dirs[3] );
					sb.append( " - " );
					sb.append( dirs[4] );
				}
				
				sb.append( ")\n" );
				
				sb.append( "Quantidade: " );
				sb.append( favoritoArquivo.getQtde() );
				sb.append( "\n" );
				
				corpoEmail += sb.toString();
				
				File directory = AndroidUtils.getRepositorioArquivos( this.getActivity() );
				File file = new File( directory, fileName );
				
				uris.add(Uri.fromFile(file));
			}
			
			String[] emails = mEditTextContatos.getText().toString().split(",");
			
			Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, emails);
			i.putExtra(Intent.EXTRA_SUBJECT, this
					.getString(R.string.assunto_email));
			i.putExtra(Intent.EXTRA_TEXT, corpoEmail);
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			try {
				startActivityForResult(Intent.createChooser(i,
						this.getString(R.string.enviar_email)),  EMAIL_SEND_FAVORITOS_RESULT);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(
						this.getActivity(), this.getString(R.string.sem_aplicativo_email),
						Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
	
	private OnClickListener mOnClickListenerContinuar = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if ( !AndroidUtils.validateEmails( mEditTextContatos.getText().toString() ) ) {
				MessageAlertDialogFragment messageDialog = new MessageAlertDialogFragment();
				messageDialog.setMensagemTexto("<b>Aviso</b>\nE-mail inválido!");
				messageDialog.show( getFragmentManager(), MessageAlertDialogFragment.TAG );
			}
	
			try {
				sendEmailFavoritos();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
	};
	
	private OnClickListener mOnClickListenerAddContato = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		}
		
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == EMAIL_SEND_FAVORITOS_RESULT ) {
			try {
				sendEmailFichaTecnica();
			} catch (SQLException e) {
			}
		}
		else if ( requestCode == EMAIL_SEND_FAVORITOS_FICHA_TECNICA ) {
			this.dismiss();
		}
	};
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View dialog = inflater
				.inflate(R.layout.fragment_favoritos_email, null);
		
		mButtonCancelar = (Button)dialog.findViewById(R.id.id_fragment_favoritos_email_button_cancelar);
		mButtonContinuar = (Button)dialog.findViewById(R.id.id_fragment_favoritos_email_button_continuar);
		mImageView = (ImageView)dialog.findViewById(R.id.id_favoritos_email_add);
		mEditTextContatos = (EditText)dialog.findViewById(R.id.id_fragment_favoritos_email_contatos);
		mEditTextTexto = (EditText)dialog.findViewById(R.id.id_fragment_favoritos_email_texto);
		
		mButtonCancelar.setOnClickListener( mOnClickListenerCancelar );
		mButtonContinuar.setOnClickListener( mOnClickListenerContinuar );
		//mImageView.setOnClickListener( mOnClickListenerAddContato );
		
		builder.setView(dialog);
		AlertDialog alertDialog = builder.create();
		alertDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
		alertDialog.setCanceledOnTouchOutside( false );
		
		return alertDialog;
	}

}
