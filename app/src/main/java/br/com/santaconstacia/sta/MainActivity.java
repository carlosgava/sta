package br.com.santaconstacia.sta;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import br.com.santaconstacia.sta.colecoes.ColecoesActivity;
import br.com.santaconstacia.sta.database.dao.ApresentacaoDAO;
import br.com.santaconstacia.sta.database.dao.ArquivoDAO;
import br.com.santaconstacia.sta.database.dao.ArtigoDAO;
import br.com.santaconstacia.sta.database.dao.ColecaoDAO;
import br.com.santaconstacia.sta.database.dao.CompartilharDAO;
import br.com.santaconstacia.sta.database.dao.ComunicacaoDAO;
import br.com.santaconstacia.sta.database.dao.ConfiguracaoDAO;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.database.dao.DistribuicaoDAO;
import br.com.santaconstacia.sta.database.dao.ImagemDAO;
import br.com.santaconstacia.sta.database.dao.InformacaoTecnicaDAO;
import br.com.santaconstacia.sta.database.dao.MascaraDAO;
import br.com.santaconstacia.sta.dialogs.AtualizacoesDialogFragment;
import br.com.santaconstacia.sta.dialogs.AutenticarDialog;
import br.com.santaconstacia.sta.dialogs.CanaisComunicacaoDialogFragment;
import br.com.santaconstacia.sta.favoritos.FavoritosActivity;
import br.com.santaconstacia.sta.tasks.interfaces.ResultadoAutenticacaoCallback;

public class MainActivity extends Activity implements ResultadoAutenticacaoCallback {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private AutenticarDialog mDialogAutenticar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inicializa();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void inicializa() {
		DAOFactory.instance( this );
		mDialogAutenticar = new AutenticarDialog(this, this);
	}
	
	public void limparBancoDadosTemporariamente() {
		try {
			ColecaoDAO daoColecao = DAOFactory.instance(this).colecaoDAO();
			ArquivoDAO daoArquivo = DAOFactory.instance(this).arquivoDAO();
			MascaraDAO daoMascara = DAOFactory.instance(this).mascaraDAO();
			ImagemDAO daoImagem = DAOFactory.instance(this).imagemDAO();
			ArtigoDAO daoArtigo = DAOFactory.instance(this).artigoDAO();
			ApresentacaoDAO daoApresentacao = DAOFactory.instance(this).apresentacaoDAO();
			InformacaoTecnicaDAO daoInfoTech = DAOFactory.instance(this).informacaoTecnicaDAO();
			ComunicacaoDAO daoComunicacao = DAOFactory.instance(this).comunicacaoDAO();
			DistribuicaoDAO daoDistribuicao = DAOFactory.instance(this).distribuicaoDAO();
			ConfiguracaoDAO daoConfiguracao = DAOFactory.instance(this).configuracaoDAO();
			CompartilharDAO daoCompartilhar = DAOFactory.instance(this).compartilharDAO();

			daoCompartilhar.clearAll();
			daoMascara.clearAll();
			daoComunicacao.clearAll();
			daoDistribuicao.clearAll();
			daoImagem.clearAll();
			daoInfoTech.clearAll();
			daoArtigo.clearAll();
			daoApresentacao.clearAll();
			daoArquivo.clearAll();
			daoColecao.clearAll();
			daoConfiguracao.clearAll();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onColecoesClick(View view){
		startActivity( ColecoesActivity.class );
	}
	
	public void onFavoritosClick(View view){
		//limparBancoDadosTemporariamente();
		//mDialogAutenticar.show();
		startActivity( FavoritosActivity.class );
		
	}
	
	public void onCompartilhamentoClick(View view){
		startActivity( CompartilhamentoActivity.class );
	}

	public void onAtualizacoesClick(View view){
		//onResultadoAutenticacao( Constantes.OK );
		mDialogAutenticar.show();
	}

	public void onCanaisComunicacoesClick(View view){
		CanaisComunicacaoDialogFragment canais = new CanaisComunicacaoDialogFragment();
		canais.show(getFragmentManager(), AtualizacoesDialogFragment.TAG);
	}
	
	public void onWebSiteClick(View view){
		startActivity( WebSiteActivity.class );
	}
	
	private void startActivity(Class<? extends Activity> clazz){
		Intent intent = new Intent(this, clazz);
        startActivity(intent);
	}

	@Override
	public void onResultadoAutenticacao(String resultado) {
		
		Log.d(TAG, "Resultado => " + resultado);
		
		if ( resultado.equals( Constantes.OK ) ) {
			AtualizacoesDialogFragment atualizacaoDialogFragment = new AtualizacoesDialogFragment();
			atualizacaoDialogFragment.show(getFragmentManager(), "DOWNLOAD");
		}
		
	}
	
}
