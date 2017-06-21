package br.com.santaconstacia.sta.dialogs;

import java.util.Timer;
import java.util.TimerTask;

import br.com.santaconstacia.sta.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAlertDialogFragment extends DialogFragment {

	public static final String TAG = MessageAlertDialogFragment.class.getSimpleName();
	
	private TimerTask mTask = null;
	private View mViewDialog = null;
	private TextView mTextView = null;
	private ImageView mImageView = null;
	private String mMensagemTexto = null;
	
	private Timer mTimerAtual = new Timer();
	private final Handler mHandler = new Handler();
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "MessageAlertDialog > onCreateDialog");
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		mViewDialog = inflater.inflate(R.layout.dialog_message_alert, null);
		
		mTextView = (TextView) mViewDialog.findViewById(R.id.id_dialog_message_alert_textview);
		mTextView.setText( mMensagemTexto );
		
		mImageView = (ImageView) mViewDialog.findViewById(R.id.id_dialog_message_alert_imagem);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView( mViewDialog );
		return builder.create();
	}
	
	public void setMensagemTexto( String mensagem ) {
		mMensagemTexto = mensagem ;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d( TAG, "MessageAlertDialog > onStart" );
		ativaTimer();
	}
	
	private void ativaTimer() {
		mTask = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.post( new Runnable( ) {
					
					@Override
					public void run() {
						MessageAlertDialogFragment.this.dismiss();
					}
					
				});
			}
			
		};
		
		mTimerAtual.schedule(mTask, 3000);
		
	}
	
}
