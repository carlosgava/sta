package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import android.content.Context;
import android.util.Log;
import br.com.santaconstacia.sta.database.jpa.DatabaseHelper;

public class DAOFactory {

	private static final String TAG = DAOFactory.class.getSimpleName();
	
	private static DAOFactory mInstance = null;
	
	private DatabaseHelper mDatabaseHelper = null;
	private ArquivoDAO mArquivoDAO = null;
	private MascaraDAO mMascaraDAO = null;
	private ComunicacaoDAO mComunicacaoDAO = null;
	private ConfiguracaoDAO mConfiguracaoDAO = null;
	private ColecaoDAO mColecaoDAO = null;
	private ArtigoDAO mArtigoDAO = null;
	private ImagemDAO mImagemDAO = null;
	private InformacaoTecnicaDAO mInformacaoTecnicaDAO = null;
	private ApresentacaoDAO mApresentacaoDAO = null;
	private DistribuicaoDAO mDistribuicaoDAO = null;
	private CompartilharDAO mCompartilharDAO = null;
	private FavoritoDAO mFavoritoDAO = null;
	private FavoritoArquivoDAO mFavoritoArquivoDAO = null;
	
	protected DAOFactory( DatabaseHelper databaseHelper ) {
		mDatabaseHelper = databaseHelper;
	}

	public static DAOFactory instance( Context context ) {
		
		if ( mInstance == null ) {
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			mInstance = new DAOFactory(dbHelper);
		}
		
		return mInstance;
	}
	
	public synchronized ArquivoDAO arquivoDAO() throws SQLException {
		
		if ( mArquivoDAO == null ) {
			mArquivoDAO = new ArquivoDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mArquivoDAO;
	}
	
	public synchronized MascaraDAO mascaraDAO() throws SQLException {
		
		if ( mMascaraDAO == null ) {
			mMascaraDAO = new MascaraDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mMascaraDAO;
	}
	
	public synchronized ComunicacaoDAO comunicacaoDAO() throws SQLException {
		
		if ( mComunicacaoDAO == null ) {
			mComunicacaoDAO = new ComunicacaoDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mComunicacaoDAO;
	}
	
	public synchronized ConfiguracaoDAO configuracaoDAO() throws SQLException {
		
		if ( mConfiguracaoDAO == null ) {
			mConfiguracaoDAO = new ConfiguracaoDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mConfiguracaoDAO;
	}
	
	public synchronized ColecaoDAO colecaoDAO() throws SQLException {
		
		if ( mColecaoDAO == null ) {
			mColecaoDAO = new ColecaoDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mColecaoDAO;
	}
	
	public synchronized ArtigoDAO artigoDAO() throws SQLException {
		
		if ( mArtigoDAO == null ) {
			mArtigoDAO = new ArtigoDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mArtigoDAO;
	}
	
	public synchronized ImagemDAO imagemDAO() throws SQLException {
		
		if ( mImagemDAO == null ) {
			mImagemDAO = new ImagemDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mImagemDAO;
	}
	
	public synchronized InformacaoTecnicaDAO informacaoTecnicaDAO() throws SQLException {
		
		if ( mInformacaoTecnicaDAO == null ) {
			mInformacaoTecnicaDAO = new InformacaoTecnicaDAO( mDatabaseHelper.getConnectionSource() );
		}
		
		return mInformacaoTecnicaDAO;
	}
	
	public synchronized ApresentacaoDAO apresentacaoDAO() throws SQLException {
		
		if ( mApresentacaoDAO == null ) {
			mApresentacaoDAO = new ApresentacaoDAO( mDatabaseHelper.getConnectionSource() );
		}
	
		return mApresentacaoDAO;
	}
	
	public synchronized DistribuicaoDAO distribuicaoDAO() throws SQLException {
		
		if ( mDistribuicaoDAO == null ) {
			mDistribuicaoDAO = new DistribuicaoDAO( mDatabaseHelper.getConnectionSource() );
		}
	
		return mDistribuicaoDAO;
	}
	
	public synchronized CompartilharDAO compartilharDAO() throws SQLException {
		
		if ( mCompartilharDAO == null ) {
			mCompartilharDAO = new CompartilharDAO( mDatabaseHelper.getConnectionSource() );
		}
	
		return mCompartilharDAO;
	}
	
	public synchronized FavoritoDAO favoritoDAO() throws SQLException {
		
		if ( mFavoritoDAO == null ) {
			mFavoritoDAO = new FavoritoDAO( mDatabaseHelper.getConnectionSource() );
		}
	
		return mFavoritoDAO;
	}
	
	public synchronized FavoritoArquivoDAO favoritoArquivoDAO() throws SQLException {
		
		if ( mFavoritoArquivoDAO == null ) {
			mFavoritoArquivoDAO = new FavoritoArquivoDAO( mDatabaseHelper.getConnectionSource() );
		}
	
		return mFavoritoArquivoDAO;
	}
	
	public synchronized void enableAutoCommit() {
		try {
			mDatabaseHelper.getConnectionSource().getReadWriteConnection().setAutoCommit( true );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Habilitar o auto commit, erro => " + e.getMessage() );
		}
	}
	
	public synchronized void disableAutoCommit() {
		try {
			mDatabaseHelper.getConnectionSource().getReadWriteConnection().setAutoCommit( false );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Desabilitar o auto commit, erro => " + e.getMessage() );
		}
	}
	
	public synchronized void commit() {
		try {
			mDatabaseHelper.getConnectionSource().getReadWriteConnection().commit( null );
		} catch (SQLException e) {
			Log.e(TAG, "FALHA: Efetuar o commit, erro => " + e.getMessage() );
		}
	}
}
