package br.com.santaconstacia.sta.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.parser.ResultadoAutenticacaoParser;
import br.com.santaconstacia.sta.security.TokenSecurity;
import br.com.santaconstacia.sta.tasks.AutenticarTask;
import br.com.santaconstacia.sta.tasks.DownloadArquivoJSONTask;
import br.com.santaconstacia.sta.tasks.ParserJsonTask;
import br.com.santaconstacia.sta.tasks.interfaces.ProcessarJsonCallback;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoAutenticacaoCallback;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoDownloadCallback;

public class AutenticarDialog extends Dialog implements ResultadoAutenticacaoCallback, ProcessarJsonCallback, ResultadoDownloadCallback {

	private static final String TAG = AutenticarDialog.class.getSimpleName();
	
	private Context mContext = null;
	private ProgressBar mProgressBar = null;
	private EditText mEditTextUsuario = null;
	private EditText mEditTextSenha = null;
	private Button mButtonAutenticar = null;
	private Button mButtonCancelar = null;
	private TextView mTextViewMensagem = null;
	private LinearLayout mLayoutMensagem = null;
	private Handler mHandler = new Handler();
	private ResultadoAutenticacaoCallback mCallBack = null;
	
	private android.view.View.OnClickListener mButtonListenerAutenticar = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mProgressBar.setVisibility( View.VISIBLE );
			mLayoutMensagem.setVisibility( View.GONE );
			
			controlarWidgets( false );
			
			new AutenticarTask(
					mEditTextUsuario.getText().toString()
					, mEditTextSenha.getText().toString()
					, (ResultadoAutenticacaoCallback) AutenticarDialog.this 
					).execute();
			
		}
		
	}; 
		
	private android.view.View.OnClickListener mButtonListenerCancelar = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mProgressBar.setVisibility( View.GONE );
			mCallBack.onResultadoAutenticacao( Constantes.CANCEL );
			dismiss();
		}
		
	};
	
	private void controlarWidgets( boolean flag ) {
	
		mEditTextUsuario.setEnabled( flag );
		mEditTextSenha.setEnabled( flag );
		mButtonAutenticar.setEnabled( flag );
		mButtonCancelar.setEnabled( flag );
		
	}
	
	private void inicializar() {
		mProgressBar.setProgress(0);
		mProgressBar.setMax(100);
		mProgressBar.setVisibility( View.GONE );
		mLayoutMensagem.setVisibility(View.GONE);
		mTextViewMensagem.setVisibility( View.GONE );
		mEditTextUsuario.setText( Constantes.USUARIO_TESTE );
		mEditTextSenha.setText( Constantes.SENHA_TESTE );
		
		controlarWidgets( true );
	}
	
	public AutenticarDialog(Context context, ResultadoAutenticacaoCallback callback) {
		super(context);
		
		mContext = context;
		mCallBack = callback;
		
		this.setContentView(R.layout.dialog_autenticacao);
		this.setTitle(R.string.autenticacao);
		
		mButtonAutenticar = (Button)findViewById(R.id.login_dialog_button_autenticar);
		mButtonAutenticar.setOnClickListener( mButtonListenerAutenticar );
		mButtonAutenticar.requestFocus();

		mButtonCancelar = (Button)findViewById(R.id.login_dialog_button_cancelar);
		mButtonCancelar.setOnClickListener( mButtonListenerCancelar );
		
		mEditTextUsuario = (EditText)findViewById(R.id.login_dialog_username);
		mEditTextSenha = (EditText)findViewById(R.id.login_dialog_password);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
		mTextViewMensagem = (TextView)findViewById(R.id.dialog_autenticacao_mensagem);
		mLayoutMensagem = (LinearLayout)findViewById(R.id.dialog_autenticacao_layout_mensagem);
		
		this.setCanceledOnTouchOutside(false);
	}

	public void pararAmpulheta() {
		mProgressBar.setVisibility( View.GONE );
	}
	
	public void iniciarAmpulheta() {
		mProgressBar.setVisibility( View.VISIBLE );
	}

	@Override
	public void onResultadoAutenticacao(String resultado) {
		
		if ( resultado.contains( Constantes.ERRO ) ) {
			mProgressBar.setVisibility( View.GONE );
			
			if ( resultado.contains(Constantes.AUTENTICACAO) ) {
				mTextViewMensagem.setText( R.string.falha_autenticacao ); 
			} else {
				mTextViewMensagem.setText( R.string.falha_comunicacao );
			}
			
			mLayoutMensagem.setVisibility( View.VISIBLE );
			mTextViewMensagem.setVisibility( View.VISIBLE );
			
			controlarWidgets( true );
			return;
		}
		
		mProgressBar.setProgress(30);

		ResultadoAutenticacaoParser parser = new ResultadoAutenticacaoParser( mContext );
		parser.parser( resultado );
		
		new DownloadArquivoJSONTask(
				mContext
				, mEditTextUsuario.getText().toString()
				, mEditTextSenha.getText().toString()
				, this ).execute();
		
	}

	@Override
	protected void onStart() {
		Log.d(TAG,"onStart");
		inicializar();
	}

	private Runnable runnableErrorProcess= new Runnable() {
		
		@Override
		public void run() {
			mProgressBar.setVisibility( View.GONE );
			mTextViewMensagem.setText( R.string.falha_comunicacao );
			mLayoutMensagem.setVisibility( View.VISIBLE );
			mTextViewMensagem.setVisibility( View.VISIBLE );
			controlarWidgets( true );
		}
	};
	
	private Runnable runnableSuccessProcess = new Runnable() {
		
		@Override
		public void run() {
			mCallBack.onResultadoAutenticacao( Constantes.OK );
		}
		
	};
	
	@Override
	public void onProcessoEncerrado(String resultado) {
		
		if ( resultado.contains( Constantes.ERRO ) ) {
			mHandler.post( runnableErrorProcess);
			return;
		}
		
		TokenSecurity.instance().generateToken(
				mEditTextUsuario.getText().toString(),
				mEditTextSenha.getText().toString());
		
		mHandler.post( runnableSuccessProcess );
		
		AutenticarDialog.this.dismiss();
	}

	@Override
	public void onTransferCompleted(String nomeArquivo) {
		mProgressBar.setProgress(60);
		new ParserJsonTask(mContext, this).execute();
	}

	@Override
	public void onTransferError(String nomeArquivo) {
		onProcessoEncerrado( Constantes.ERRO );
	}
}
