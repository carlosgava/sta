package br.com.santaconstacia.sta.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.database.bean.Apresentacao;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Comunicacao;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.database.dao.ApresentacaoDAO;
import br.com.santaconstacia.sta.database.dao.ArquivoDAO;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.database.dao.MascaraDAO;
import br.com.santaconstacia.sta.download.DownloadManager;
import br.com.santaconstacia.sta.negocio.ArtigoManager;
import br.com.santaconstacia.sta.tasks.interfaces.DownloadCallback;

import com.j256.ormlite.stmt.QueryBuilder;

public class AtualizacoesDialogFragment extends DialogFragment implements DownloadCallback {

	public static final String TAG = AtualizacoesDialogFragment.class
			.getSimpleName();

	private View mViewDialog = null;
	private Button mButtonFechar = null;
	private Button mButtonAtualizar = null;
	private TextView mTextViewMascaras = null;
	private TextView mTextViewApresentacoes = null;
	private ProgressBar mProgressBar = null;
	private List<Artigo> mArtigos = null;
	private List<Mascara> mMascaras = null;
	private List<Arquivo> mArquivosComunicacoes = null;
	private List<Apresentacao> mApresentacoes = null;
	private ArrayAdapter<String> mAdapterArtigos = null;
	private ArtigoManager mArtigoManager= null;
	private Artigo mArtigo = null;
	private DownloadManager mDownloadManager = null;

	private View.OnClickListener mListenerButtonFechar = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mDownloadManager.stop();
			AtualizacoesDialogFragment.this.getDialog().cancel();
		}
	};
	
	private View.OnClickListener mListenerButtonAtualizarTudo = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mButtonAtualizar.setEnabled(false);
			baixarArquivos();
		}
		
	};
	
	private void baixarArquivos() {
		
		if ( mArquivosComunicacoes != null && mArquivosComunicacoes.size() > 0 ) {
			baixarComunicacoes();
		}
		else if ( mMascaras != null && mMascaras.size() > 0 ) {
			baixarMascara();
		}
		else if ( mApresentacoes != null && mApresentacoes.size() > 0 ) {
			baixarApresentacao();
		}
		else if ( mArtigos != null && mArtigos.size() > 0 ) {
			baixarArtigos();
		}
		
	}
	
	private void baixarComunicacoes() {
		if ( mArquivosComunicacoes != null &&  mArquivosComunicacoes.size() > 0 ) {
			mProgressBar.setMax( mArquivosComunicacoes.size() );
			mProgressBar.setProgress( 0 );
			mDownloadManager.putFiles(Constantes.TIPO_COMUNICACAO, this, mArquivosComunicacoes);
			new Thread( mDownloadManager ).start();
		}
	}
	
	private void baixarMascara() {
		
		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		for( int i=0; i < mMascaras.size(); i++ ) {
			arquivos.add( mMascaras.get(i).arquivo );
		}
		
		mProgressBar.setMax( arquivos.size() );
		mProgressBar.setProgress( 0 );
		mDownloadManager.putFiles(Constantes.TIPO_MASCARA, this, arquivos);
		new Thread( mDownloadManager ).start();
		
	}
	
	private void baixarApresentacao() {
		
		List<Arquivo> arquivos = new ArrayList<Arquivo>();
		for( int i=0; i < mApresentacoes.size(); i++ ) {
			arquivos.add( mApresentacoes.get(i).arquivoImagem );
		}
		
		mProgressBar.setMax( arquivos.size() );
		mProgressBar.setProgress( 0 );
		mDownloadManager.putFiles(Constantes.TIPO_APRESENTACAO, this, arquivos);
		new Thread( mDownloadManager ).start();
		
	}
	
	private Artigo getArtigo() {
		Artigo artigo = null;
		
		if ( mArtigos == null || mArtigos.size() == 0 ) {
			return null;
		}
			
		artigo = (Artigo) mArtigos.get(0);
		return artigo;
	}
	
	private void baixarArtigos() {
	
		mArtigo = getArtigo();
		if ( mArtigo == null ) {
			mDownloadManager.stop();
			AtualizacoesDialogFragment.this.getDialog().cancel();
			return;
		}
		
		try {
			System.out.println("Refresh Artigo");
			DAOFactory.instance( this.getActivity() ).artigoDAO().refresh( mArtigo );
			System.out.println("Carregando arquivos nao baixados");
			mArtigoManager.carregarArquivosNaoBaixados( mArtigo );
			List<Arquivo> arquivos = mArtigoManager.getTodosArquivosParaBaixar();
			System.out.println("Get total arquivos => " + arquivos.size());
			
			mProgressBar.setMax( arquivos.size() );
			mProgressBar.setProgress( 0 );
			
			mDownloadManager.putFiles(Constantes.TIPO_ARTIGO, this, arquivos);
			new Thread( mDownloadManager ).start();
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Nao foi possivel atualizar os dados de arquivos dos artigos");
			return;
		} catch ( RuntimeException ex ) {
			Log.e(TAG, "FALHA GERAL: Nao foi possivel atualizar os dados de arquivos dos artigos");
			return;
		}
			
	}
	
	public void carregarDadosComunicacoes() {
		
		try {
			mArquivosComunicacoes = new ArrayList<Arquivo>();
			List<Comunicacao> comunicacoes = DAOFactory.instance( getActivity( ) ).comunicacaoDAO().queryForAll();
			if ( comunicacoes != null && comunicacoes.size() > 0 ) {
				for (Comunicacao comunicacao : comunicacoes) {
					DAOFactory.instance( getActivity() ).comunicacaoDAO().refresh( comunicacao );
					Collection<Arquivo> arquivos = comunicacao.getArquivos();
					for (Arquivo arquivo : arquivos) {
						if ( arquivo.getFlagDeletar() == false && arquivo.getFlagPendenteProc() == true ) {
							mArquivosComunicacoes.add( arquivo );
						}
					}
				}
			}
		} catch (SQLException e) {
			Log.w(TAG, "FALHA: Nao foi possivel carregar os dados de comunicacoes");
		}
	}
	
	public void carregarDadosMascaras() throws SQLException {
		DAOFactory daoFactory = DAOFactory.instance(getActivity());
		
		if ( daoFactory != null ) {
			ArquivoDAO daoArquivo = daoFactory.arquivoDAO();
			MascaraDAO daoMascara = daoFactory.mascaraDAO();
	
				QueryBuilder<Arquivo, Integer> qbArquivo = daoArquivo
						.queryBuilder();
				QueryBuilder<Mascara, Integer> qbMascara = daoMascara
						.queryBuilder();
				qbArquivo.where().eq("flagPendenteProc",
						Boolean.valueOf(true));
				qbMascara.join(qbArquivo);
				mMascaras = qbMascara.query();
		}
	}
	
	public void carregarDadosApresentacoes() throws SQLException {
		DAOFactory daoFactory = DAOFactory.instance(getActivity());
		
		if ( daoFactory != null ) {
			ArquivoDAO daoArquivo = daoFactory.arquivoDAO();
			ApresentacaoDAO daoApresentacao = daoFactory.apresentacaoDAO();
			
			QueryBuilder<Arquivo, Integer> qbArquivo = daoArquivo
					.queryBuilder();
			QueryBuilder<Apresentacao, Integer> qbApresentacao = daoApresentacao
					.queryBuilder();
			qbArquivo.where().eq("flagPendenteProc", Boolean.valueOf(true))
					.and().eq("flagDeletar", Boolean.valueOf(false));
			qbApresentacao.join(qbArquivo);
			mApresentacoes = qbApresentacao.query();
		}
	}
	
	public void carregarDadosArtigos() throws SQLException {
		mArtigos = mArtigoManager.getArtigosPendenteProc();
	}
	
	public void popularListaArtigos() {
		if ( mArtigos == null )
			return;
		
		for( int i=0; i < mArtigos.size(); i++ ) {
			Artigo artigo = (Artigo) mArtigos.get(i);
			mAdapterArtigos.add(artigo.getName() + "(" + artigo.getCode() + ")\n"); 
		}
	}
	
	public void carregarDadosIniciais() {
		DAOFactory daoFactory = DAOFactory.instance(getActivity());

		if (daoFactory != null) {
			try {
				carregarDadosComunicacoes();
				carregarDadosMascaras();
				carregarDadosApresentacoes();
				carregarDadosArtigos();
				popularListaArtigos();
			} catch (SQLException e) {
				Log.e(TAG,
						"FALHA para instanciar o arquivoDAO, erro => "
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void inicializa(View dialog, long quantidade, int idCampo,
			int nenhumaMsg, int singularMsg, int pluralMsg) {
		String msg = null;

		if (quantidade == 0)
			msg = this.getString(nenhumaMsg);
		else if (quantidade == 1)
			msg = this.getString(singularMsg);
		else
			msg = this.getString(pluralMsg, quantidade);

		TextView textView = (TextView) dialog.findViewById(idCampo);
		textView.setText(msg);
	}

	public void atualizarPainelMascaras() {
		getActivity().runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				inicializa(mViewDialog, mMascaras == null ? 0 : mMascaras.size(),
						R.id.mascaras, R.string.nenhuma_mascara_para_atualizar,
						R.string.mascara_para_atualizar,
						R.string.mascaras_para_atualizar);
			}
			
		});
	}
	
	public void atualizarPainelApresentacoes() {
		getActivity().runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				inicializa(mViewDialog,
						mApresentacoes == null ? 0 : mApresentacoes.size(),
						R.id.apresentacoes,
						R.string.nenhuma_apresentacao_para_atualizar,
						R.string.apresentacao_para_atualizar,
						R.string.apresentacoes_para_atualizar);
			}
			
		});
	}
	
	public void atualizarPainelArtigos() {
		getActivity().runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				inicializa(mViewDialog,
						mArtigos == null ? 0 : mArtigos.size(),
						R.id.faltam_artigos,
						R.string.nenhum_artigo,
						R.string.falta_artigo,
						R.string.faltam_artigos);
			}
			
		});
	}

	public void atualizarPaineis() {
		atualizarPainelMascaras();
		atualizarPainelApresentacoes();
		atualizarPainelArtigos();
	}

	public void inicializar() throws SQLException {
		mTextViewMascaras = (TextView) mViewDialog.findViewById(R.id.mascaras);
		mTextViewApresentacoes = (TextView) mViewDialog
				.findViewById(R.id.apresentacoes);
		
		mAdapterArtigos = new ArrayAdapter<String>(getActivity(),
				R.layout.fragment_atualizacoes_artigo, R.id.artigo_item,
				new ArrayList<String>());
		
		ListView list = (ListView) mViewDialog.findViewById(android.R.id.list);
		list.setAdapter(mAdapterArtigos);
		
		mButtonFechar = (Button) mViewDialog.findViewById(R.id.fecharButton);
		mButtonAtualizar = (Button) mViewDialog.findViewById(R.id.atualizarTudoButton);
		
		mButtonFechar.setOnClickListener( mListenerButtonFechar );
		mButtonAtualizar.setOnClickListener( mListenerButtonAtualizarTudo );
		
		mArtigoManager = new ArtigoManager( getActivity() );
		mArtigoManager.carregarTodosArtigosPendenteProc();
		
		mDownloadManager = new DownloadManager( getActivity() );
		mProgressBar = (ProgressBar) mViewDialog.findViewById(R.id.progress_bar);
		
		carregarDadosIniciais();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mViewDialog = inflater.inflate(R.layout.fragment_atualizacoes, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(mViewDialog);
		builder.setTitle(R.string.atualizacoes);
		try {
			inicializar();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		atualizarPaineis();
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		return dialog;
	}

	private void atualizarAdapterArtigo() {
		getActivity().runOnUiThread( new Runnable() {
			
			@Override
			public void run() {
				String label = mAdapterArtigos.getItem(0);
				mAdapterArtigos.remove(label);
			}
			
		});
	}
	
	@Override
	public void onTransferenciaCompletada(int type) {
		if ( type == Constantes.TIPO_COMUNICACAO ) {
			mArquivosComunicacoes.clear();
			baixarMascara();
		}
		else if ( type == Constantes.TIPO_MASCARA ) {
			mMascaras.clear();
			baixarApresentacao();
		}
		else if ( type == Constantes.TIPO_APRESENTACAO ) {
			mApresentacoes.clear();
			baixarArtigos();
		}
		else if ( type == Constantes.TIPO_ARTIGO ) {
			
			try {
				
				if ( mDownloadManager.getCountTransferErrors() == 0 ) {
					mArtigo.setFlagPendenteProc( false );
					DAOFactory.instance( getActivity() ).artigoDAO().update( mArtigo );
				}
				
				mArtigos.remove( mArtigo );
				atualizarAdapterArtigo();
				atualizarPainelArtigos();
				baixarArtigos();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void atualizarArquivo( int type, Arquivo arquivo ) {
		arquivo.setFlagPendenteProc(false);
		mProgressBar.setProgress( mProgressBar.getProgress() + 1 );
		try {
			DAOFactory.instance(this.getActivity()).arquivoDAO().update(arquivo);
			
			if ( type == Constantes.TIPO_MASCARA ) {
				atualizarPainelMascaras();
			}
			else if ( type == Constantes.TIPO_APRESENTACAO ) {
				atualizarPainelApresentacoes();
			}
			
		} catch (SQLException e) {
			Log.w(TAG, "FALHA: Nao foi possivel atualizar o flag de processamento pendente do arquivo => " + arquivo.getUrl() );
		}
	}
	
	@Override
	public void onDownloadArquivoError(int type, Arquivo arquivo) {
		
		if ( type == Constantes.TIPO_MASCARA )
			mMascaras.remove(0);
		else if ( type == Constantes.TIPO_APRESENTACAO )
			mApresentacoes.remove(0);
		else if ( type == Constantes.TIPO_COMUNICACAO )
			mArquivosComunicacoes.remove( 0 );
		
		atualizarArquivo(type, arquivo);
		Log.w(TAG, "FALHA: Nao foi possivel fazer o download da url => " + arquivo.getUrl() );
	}

	@Override
	public void onDownloadArquivoCompletado(int type, Arquivo arquivo) {
		
		if ( type == Constantes.TIPO_MASCARA )
			mMascaras.remove(0);
		else if ( type == Constantes.TIPO_APRESENTACAO )
			mApresentacoes.remove(0);
		else if ( type == Constantes.TIPO_COMUNICACAO )
			mArquivosComunicacoes.remove( 0 );
		
		atualizarArquivo(type, arquivo);
		Log.d(TAG, "Arquivo baixado da url => " + arquivo.getUrl() );
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

}
