package br.com.santaconstacia.sta.database.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.santaconstacia.sta.database.bean.Apresentacao;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Colecao;
import br.com.santaconstacia.sta.database.bean.Compartilhar;
import br.com.santaconstacia.sta.database.bean.Comunicacao;
import br.com.santaconstacia.sta.database.bean.Configuracao;
import br.com.santaconstacia.sta.database.bean.Distribuicao;
import br.com.santaconstacia.sta.database.bean.Favorito;
import br.com.santaconstacia.sta.database.bean.FavoritoArquivo;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;
import br.com.santaconstacia.sta.database.bean.Mascara;
import br.com.santaconstacia.sta.utils.AndroidUtils;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static String LOG_CATEGORY="DBHELPER";

	private Context context;
	private static final String DATABASE_NAME = "sta.db";
	private static final int DATABASE_VERSION = 23;
	

	public DatabaseHelper(Context context) {
		//super(context, DATABASE_NAME, null, DATABASE_VERSION);
		super(context, AndroidUtils.getDatabasePathFile( DATABASE_NAME ), null, DATABASE_VERSION );
		this.context = context;
	}

	private void createTables( ConnectionSource connectionSource ) throws SQLException {
		TableUtils.createTableIfNotExists(connectionSource, Arquivo.class);
		TableUtils.createTableIfNotExists(connectionSource, Mascara.class);
		TableUtils.createTableIfNotExists(connectionSource, Comunicacao.class);
		TableUtils.createTableIfNotExists(connectionSource, Configuracao.class);
		TableUtils.createTableIfNotExists(connectionSource, Colecao.class);
		TableUtils.createTableIfNotExists(connectionSource, Artigo.class);
		TableUtils.createTableIfNotExists(connectionSource, Imagem.class);
		TableUtils.createTableIfNotExists(connectionSource, InformacaoTecnica.class);
		TableUtils.createTableIfNotExists(connectionSource, Apresentacao.class);
		TableUtils.createTableIfNotExists(connectionSource, Distribuicao.class);
		TableUtils.createTableIfNotExists(connectionSource, Compartilhar.class);
		TableUtils.createTableIfNotExists(connectionSource, Favorito.class);
		TableUtils.createTableIfNotExists(connectionSource, FavoritoArquivo.class);
	}
	
	private void dropTables( ConnectionSource connectionSource ) throws SQLException {
		TableUtils.dropTable(connectionSource, Arquivo.class, true);
		TableUtils.dropTable(connectionSource, Mascara.class, true);
		TableUtils.dropTable(connectionSource, Comunicacao.class, true);
		TableUtils.dropTable(connectionSource, Configuracao.class, true);
		TableUtils.dropTable(connectionSource, Colecao.class, true);
		TableUtils.dropTable(connectionSource, Artigo.class, true);
		TableUtils.dropTable(connectionSource, Imagem.class, true);
		TableUtils.dropTable(connectionSource, InformacaoTecnica.class, true);
		TableUtils.dropTable(connectionSource, Apresentacao.class, true);
		TableUtils.dropTable(connectionSource, Distribuicao.class, true);
		TableUtils.dropTable(connectionSource, Compartilhar.class, true);
		TableUtils.dropTable(connectionSource, Favorito.class, true);
		TableUtils.dropTable(connectionSource, FavoritoArquivo.class, true);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		
		try {
			createTables(connectionSource);
			
			InputStream in = context.getResources().openRawResource(br.com.santaconstacia.sta.R.raw.empty_database);
			byte bytes[] = null;
			try {
				int scriptSize = in.available();
				bytes = new byte[scriptSize];
				in.read(bytes);
			} catch (NotFoundException e) {
				Log.e(LOG_CATEGORY, "Arquivo n??o encontrado: " + e.getMessage(), e);
			} catch (IOException e) {
				Log.e(LOG_CATEGORY, "Arquivo n??o encontrado: " + e.getMessage(), e);
			}
			String[] sqls = new String(bytes).split(";");
			for (String sql : sqls) {
				sqliteDatabase.execSQL(sql);
			}
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Erro ao criar o banco de dados", e);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int olderVersion,
			int newVersion) {
		try {
			dropTables(connectionSource);
			createTables(connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static DatabaseHelper getInstance(Context context){
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}
	
	public boolean existe(){
		//return context.getDatabasePath(DATABASE_NAME).exists();
		return context.getDatabasePath(AndroidUtils.getDatabasePathFile(DATABASE_NAME)).exists();
	}
	
	public static void release(){
		OpenHelperManager.releaseHelper();
	}
}