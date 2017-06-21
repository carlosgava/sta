package br.com.santaconstacia.sta.colecoes;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.utils.AndroidUtils;

public class MascarasFragmentDialog extends DialogFragment {
	
	private ListView list;
	private MascaraAdapter adapter;
	private SeekBar mSeekBarOpacity = null;
	private ImageButton mImageButtonRemove = null;
	
	public static final String OPCOES_DIALOG_TAG = "MASCARAS_DIALOG_TAG";
	
	public static interface OnMascaraSelecionada{
		public void onSelecao(Mascara mascara);
	}
	
	public static interface OnMascaraOpacidade {
		public void onOpacityChanged( int opacity );
	}
	
	private OnMascaraSelecionada onMascaraSelecionadaListener;
	private OnMascaraOpacidade onMascaraOpacidadeListener;

    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			MascarasFragmentDialog.this.onMascaraOpacidadeListener.onOpacityChanged( progress );
		}
	};
	
	private OnClickListener mOnClickListenerImageRemove = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onMascaraSelecionadaListener.onSelecao(null);
		}
	};
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View dialog = inflater
				.inflate(R.layout.fragment_impressao_mascaras, null);
		builder.setView(dialog);
		dialog.setLayoutParams(new LayoutParams(232, LayoutParams.WRAP_CONTENT));
		
		DialogInterface.OnClickListener cancelarListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MascarasFragmentDialog.this.dismiss();
			}
		};

		mSeekBarOpacity = (SeekBar)dialog.findViewById( R.id.idSeekBarOpacity);
		mSeekBarOpacity.setOnSeekBarChangeListener( mSeekBarChangeListener );
		
		mImageButtonRemove = (ImageButton)dialog.findViewById(R.id.idButtonRemoveMascarasDistribuicao);
		mImageButtonRemove.setOnClickListener( mOnClickListenerImageRemove );
		
		builder.setNegativeButton(R.string.fechar, cancelarListener);
		builder.setTitle(R.string.mascaras);

		List<Mascara> mascaras;
		try {
			mascaras = DAOFactory.instance( getActivity() ).mascaraDAO().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return builder.create();
		}
		
		list = (ListView) dialog.findViewById(android.R.id.list);
		
		adapter = new MascaraAdapter(getActivity(), mascaras, onMascaraSelecionadaListener);

		list.setAdapter(adapter);
		
		Dialog d = builder.create();
		adapter.setDialog(d);
		
		return d;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public OnMascaraSelecionada getOnClienteSelecionadoListener() {
		return onMascaraSelecionadaListener;
	}

	public void setOnClienteSelecionadoListener(
			OnMascaraSelecionada onClienteSelecionadoListener) {
		this.onMascaraSelecionadaListener = onClienteSelecionadoListener;
	}
	
	public OnMascaraOpacidade getOnMascaraOpacidadeListener() {
		return onMascaraOpacidadeListener;
	}
	
	public void setOnCallbackOpacityListener( OnMascaraOpacidade listener ) {
		this.onMascaraOpacidadeListener = listener;
	}
}


class MascaraAdapter extends BaseAdapter {
    private Activity activity;
	private List<Mascara> mascaras;
	private MascarasFragmentDialog.OnMascaraSelecionada onMascaraSelecionadaListener;
	private Dialog dialog;
	
    public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public MascaraAdapter(Activity activity, List<Mascara> mascaras, 
    		MascarasFragmentDialog.OnMascaraSelecionada onMascaraSelecionadaListener) {
    	this.activity = activity;
        this.mascaras = mascaras;
        this.onMascaraSelecionadaListener = onMascaraSelecionadaListener;
    }

    public int getCount() {
        return this.mascaras.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
    	int side = 200;
        ImageButton button;
    	final Mascara mascara = mascaras.get(position);
    	Arquivo arquivo = mascara.getArquivo();
    	
    	LinearLayout container = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.fragment_impressao_mascaras_item, null);
    	button = (ImageButton) container.findViewById(R.id.button);
    	
    	try {
			DAOFactory.instance( this.activity ).arquivoDAO().refresh( arquivo );
		} catch (SQLException e) {
			e.printStackTrace();
			return container;
		}
    	
		File directory = AndroidUtils.getRepositorioArquivos( this.activity );
		File arquivoImagem = new File( directory, arquivo.getUrl().replace('\\', '/') );
    	
    	BitmapDrawable drawable = AndroidUtils.toDrawable(activity, 
    			arquivoImagem.getAbsolutePath(), 
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, side, activity.getResources().getDisplayMetrics()));
		
    	PointF dim = new PointF();
    	dim.x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, drawable.getBitmap().getWidth(), activity.getResources().getDisplayMetrics());
    	dim.y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, drawable.getBitmap().getHeight(), activity.getResources().getDisplayMetrics());
    	
    	dim = AndroidUtils.scaleToFit(side, side, dim.x, dim.y);
    	
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(button.getLayoutParams());
    	params.width = (int) dim.x;
    	params.height = (int)dim.y;
        button.setPadding(0, 0, 0, 0);
        button.setScaleType(ScaleType.FIT_CENTER);
        button.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
				onMascaraSelecionadaListener.onSelecao(mascara);
				dialog.cancel();
    		}
    	});
		
		button.setImageDrawable(drawable);
		button.setBackgroundColor(Color.WHITE);
		
        return container;
    }
}