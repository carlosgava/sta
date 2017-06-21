package br.com.santaconstacia.sta.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import br.com.santaconstacia.sta.Constantes;
import br.com.santaconstacia.sta.database.bean.Apresentacao;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Colecao;
import br.com.santaconstacia.sta.database.bean.Distribuicao;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.loaders.Article;
import br.com.santaconstacia.sta.loaders.Distribution;
import br.com.santaconstacia.sta.loaders.Downloadable;
import br.com.santaconstacia.sta.loaders.Print;
import br.com.santaconstacia.sta.loaders.TechInfo;
import br.com.santaconstacia.sta.tasks.interfaces.ProcessarJsonCallback;
import br.com.santaconstacia.sta.utils.AndroidUtils;
import br.com.santaconstacia.sta.utils.JSONUtils;

import com.google.gson.stream.JsonReader;
import com.j256.ormlite.stmt.QueryBuilder;

public class ParserJsonTask extends AsyncTask<Void, Long, String> {

	private static final String TAG = ParserJsonTask.class.getSimpleName();
	
	private static final String TIMESTAMP = "timestamp";
	private static final String COLECOES = "colecoes";
	private static final String CHILDREN = "children";
	private static final String NAME = "name";
	private static final String ARTICLES = "articles";
	private static final String IS_ALLOWED = "isAllowed";
	private static final String CATEGORY = "Category";
	private static final String TYPE = "type";
	private static final String CODE = "code";
	private static final String EXPIRATION_DATE = "expirationDate";
	private static final String PRINTS = "prints";
	private static final String DOWNLOAD_URL = "downloadURL";
	private static final String WAS_REMOVED = "wasRemoved";
	private static final String FILENAME = "fileName";
	private static final String DISTRIBUTION = "distribution";
	private static final String TECHINFO = "TechInfo";
	private static final String IS_COLORTABLE = "isColorTable";
	private static final String PRINT = "Print";
	private static final String COLORS = "colors";
	private static final String PARADE = "parade";
	private static final String ARTICLE = "Article";
	
	int mRow = 0;
	int mNumeroArtigosDescartadosWasRemoved = 0;
	int mNumeroArtigosDescartadosExpirationDate = 0;

	private String mLastRecordLog = "";

	private Context mContext = null;
	private ProcessarJsonCallback mCallback = null;

	public ParserJsonTask(Context context, ProcessarJsonCallback callback ) {
		mContext = context;
		mCallback = callback;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		String timeStamp = "";
		String resultado = Constantes.ERRO;
		
		JsonReader jReader = null;
		ZipInputStream zipInputStream = null;
		
		try {
			System.out.println("Inicio!!!");
			File file = new File(AndroidUtils.getRepositorioArquivos(mContext),
					"json.zip");
			
			zipInputStream = new ZipInputStream(
					new FileInputStream(file));
			
			if (zipInputStream.getNextEntry() == null) {
				mCallback.onProcessoEncerrado( Constantes.OK );
				return Constantes.OK;
			}
			
			HashMap<String, Object> tags = null;
			
			jReader = new JsonReader(new InputStreamReader(zipInputStream));
			
			jReader.beginObject();

			System.out.println("Inicio !!!");
			
			while( jReader.hasNext() ) {
			
				String tagName = jReader.nextName();
				
				if ( TIMESTAMP.equals(tagName) ) {
					timeStamp = jReader.nextString();
				}
				else if (COLECOES.equals(tagName) ){
					handler_colecoes( jReader, null );
				}
			}
			
			jReader.endObject();

			saveTimeStamp( timeStamp );
			
			resultado = Constantes.OK;
			
		} catch (FileNotFoundException e) {
			resultado += " - " + Constantes.ERRO_ARQUIVO_NAO_ENCONTRADO;
		} catch (IOException e) {
			resultado += " - " + Constantes.ERRO_ARQUIVO_INVALIDO;
		} catch (SQLException e) {
			e.printStackTrace();
			resultado += " - " + Constantes.ERRO_SQL;
		} catch ( RuntimeException e ) {	
			resultado += " - Generico";
		} finally {
				try {
					if ( jReader != null ) {
						jReader.close();
					}
					if ( zipInputStream != null ) {
						zipInputStream.close();
					}
				} catch (IOException e) {
				}
		}
			
		mCallback.onProcessoEncerrado( resultado );
		
		return resultado;
	}
	
	public void saveTimeStamp( String value ) throws SQLException {
		
		System.out.println(TIMESTAMP + " = " + value );
		
		Date date;
		try {
			date = AndroidUtils.DATE_FORMAT.parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		DAOFactory.instance( mContext ).configuracaoDAO().salvaTimestampAtualizacao(date);
		
	}
		
	public Arquivo save_arquivo_imagem(Downloadable downloadable, int tipoArquivo ) throws SQLException {
		
		Arquivo arquivo = null;
		
		if ( downloadable != null ) {
			try {
			arquivo = new Arquivo();
			arquivo.setNome( downloadable.fileName );
			arquivo.setTipoTransf( Arquivo.TIPO_TRANSFERENCIA.DOWNLOAD );
			arquivo.setUrl( downloadable.downloadURL );
			arquivo.setFlagDeletar( downloadable.wasRemoved );
			arquivo.setTipoArquivo( tipoArquivo );
			DAOFactory.instance( mContext ).arquivoDAO().create( arquivo );
			}
			catch( SQLException ex ) {
				throw new SQLException(ex);
			} 
			catch( RuntimeException ex ) {
				ex.printStackTrace();
			}
		}
		
		return arquivo;
	}
	
	public void save_imagem( Artigo artigo, Print print ) throws SQLException {
		
		Imagem imagem = new Imagem();
		imagem.setArquivoImagem( save_arquivo_imagem( print, Arquivo.TIPO_ARQUIVO.IMAGEM ) );
		imagem.setName( print.downloadURL.replace('\\', '/') );
		imagem.setArtigo(artigo);
		DAOFactory.instance(mContext).imagemDAO().create( imagem );
		
	}
	
	public void save_distribuicao( Artigo artigo, Distribution distribution ) throws SQLException {
		
		if ( distribution == null )
			return;
		
		Distribuicao dist = new Distribuicao();
		dist.setArquivoImagem( save_arquivo_imagem( distribution, Arquivo.TIPO_ARQUIVO.DISTRIBUICAO ) );
		dist.setName( distribution.fileName );
		dist.setArtigo(artigo);
		DAOFactory.instance(mContext).distribuicaoDAO().create( dist );
		
	}

	public Imagem loadImagem( Artigo artigo, Print print ) throws SQLException {
		QueryBuilder<Imagem, Integer> qbImagem = DAOFactory.instance( mContext ).imagemDAO().queryBuilder();
		qbImagem.where().eq("artigo_id", artigo.getId()).and().eq("name", print.fileName);
		Imagem imagem = qbImagem.queryForFirst();
		return imagem;
	}
	
	public Distribuicao loadImagemDistribuicao( Artigo artigo, Distribution distribution ) throws SQLException {
		QueryBuilder<Distribuicao, Integer> qbImagem = DAOFactory.instance( mContext ).distribuicaoDAO().queryBuilder();
		qbImagem.where().eq("artigo_id", artigo.getId()).and().eq("name", distribution.fileName);
		Distribuicao imagem = qbImagem.queryForFirst();
		return imagem;
	}
	
	public void save_prints( Artigo artigo, List<Print> prints ) throws SQLException {
		
		if ( prints != null && prints.size() > 0 ) {
			for (Print print : prints) {
				try
				{
					if ( print.wasRemoved ) {
						Imagem imagem = loadImagem( artigo, print );
						if ( imagem != null ) {
							List<Imagem> imagens = new ArrayList<Imagem>();
							imagens.add( imagem );
							removeImagens(imagens);
							
							if ( print.distribution != null ) {
								Distribuicao distribuicao = loadImagemDistribuicao(artigo, print.distribution);
								if ( distribuicao != null ) {
									List<Distribuicao> dist = new ArrayList<Distribuicao>();
									removeImagensDistribuicao( dist );
								}
							}
						}
					} else {
						save_imagem(artigo, print);
						save_distribuicao(artigo, print.distribution);
					}
					
				} catch( SQLException ex ) {
					throw new SQLException(ex);
				} catch( RuntimeException ex ) {
					throw new RuntimeException(ex);
				}
			}
		}
		
	}
	
	public void save_tech_info( Artigo artigo, TechInfo loader ) throws SQLException {
		
		InformacaoTecnica infoTech = null;
		
		if ( loader != null ) {
			try 
			{
				QueryBuilder<InformacaoTecnica, Integer> qbInfoTech = DAOFactory.instance( mContext ).informacaoTecnicaDAO().queryBuilder();
				qbInfoTech.where().eq("artigo_id", artigo.getId()).and().eq("name", loader.fileName);
				infoTech = qbInfoTech.queryForFirst();
				
				if ( infoTech == null ) {
					infoTech = new InformacaoTecnica();
					infoTech.setArquivoImagem( save_arquivo_imagem( loader, Arquivo.TIPO_ARQUIVO.INFORMACAO_TECNICA ) );
					infoTech.setArtigo(artigo);
					infoTech.setName( loader.downloadURL.replace('\\', '/') );
					DAOFactory.instance( mContext ).informacaoTecnicaDAO().create( infoTech );
				} 
				else {
					if ( loader.wasRemoved ) {
						List<InformacaoTecnica> infoTechs = new ArrayList<InformacaoTecnica>();
						infoTechs.add(infoTech);
						removeTechInfo(infoTechs);
					}
				}
				
				
			} catch( SQLException ex ) {
				throw new SQLException(ex);
			}
		}
	}
	
	public void handler_parade( JsonReader reader, Colecao colecao ) throws SQLException, IOException {
		
		Arquivo arquivoImagem = new Arquivo();
		reader.beginObject();

		while( reader.hasNext() ) {
			String tagname = reader.nextName();
			
			if ( DOWNLOAD_URL.equals(tagname) ) {
				arquivoImagem.setUrl( reader.nextString() );
			}
			else if ( FILENAME.equals(tagname) ) {
				arquivoImagem.setNome( reader.nextString() );
			}
			else if ( WAS_REMOVED.equals(tagname) ) {
				arquivoImagem.setFlagDeletar( reader.nextBoolean() );
			}
			else {
				reader.skipValue();
			}
		}
		
		reader.endObject();
		
		QueryBuilder<Apresentacao, Integer> qbApresentacao = DAOFactory.instance( mContext ).apresentacaoDAO().queryBuilder();
		qbApresentacao.where().eq("colecao_id", colecao.getId()).and().eq("name", arquivoImagem.getNome());
		Apresentacao apresentacao = qbApresentacao.queryForFirst();
		
		if ( arquivoImagem.getFlagDeletar() ) {
			if ( apresentacao != null ) {
				try {
					removeArquivo( apresentacao.getArquivoImagem() );
				} catch( RuntimeException ex ) { }
				DAOFactory.instance( mContext ).apresentacaoDAO().deleteById( apresentacao.getId() );
			}
			
			return;
		}
		
		if ( apresentacao == null ) {
			arquivoImagem.setTipoTransf( Arquivo.TIPO_TRANSFERENCIA.DOWNLOAD );
			arquivoImagem.setTipoArquivo( Arquivo.TIPO_ARQUIVO.APRESENTACAO );
			DAOFactory.instance( mContext ).arquivoDAO().create( arquivoImagem );
			
			apresentacao = new Apresentacao();
			apresentacao.setArquivoImagem( arquivoImagem );
			apresentacao.setName( arquivoImagem.getNome() );
			apresentacao.setColecao( colecao );
			DAOFactory.instance( mContext ).apresentacaoDAO().create( apresentacao );
		}
		
	}
	
	public void removeArquivo( Arquivo arquivo ) {
		try {
			
			DAOFactory.instance( mContext ).arquivoDAO().refresh( arquivo );
			
			File pathDestino = AndroidUtils.getDirectoryByUrl( mContext, arquivo.getUrl() );
			File file = new File( pathDestino, arquivo.getNome() );
			
			if ( file.delete() ) {
				Log.d(TAG, "Arquivo deletado => " + file.getAbsolutePath() );
			} else {
				Log.d(TAG, "Nao foi possivel deltar o arquivo => " + file.getAbsolutePath() );
			}
			
			DAOFactory.instance( mContext ).arquivoDAO().delete( arquivo );
			
		} catch (SQLException e) {
		}
	}
	
	public void removeArquivos( List<Arquivo> arquivos ) {
		if ( arquivos == null || arquivos.size() == 0 ) {
			return ;
		}
		
		for (Arquivo arquivo : arquivos) {
			removeArquivo( arquivo );
		}
	}
	
	public void removeImagens( List<Imagem> imagens ) throws SQLException {
		if ( imagens == null || imagens.size() == 0 ) {
			return ;
		}
		
		for (Imagem imagem : imagens) {
			removeArquivo( imagem.getArquivoImagem() );
			DAOFactory.instance( mContext ).imagemDAO().deleteById( imagem.getId() );
		}
	}
	
	public void removeImagensDistribuicao( List<Distribuicao> imagens ) throws SQLException {
		if ( imagens == null || imagens.size() == 0 ) {
			return ;
		}
		
		for (Distribuicao imagem : imagens) {
			removeArquivo( imagem.getArquivoImagem() );
			DAOFactory.instance( mContext ).distribuicaoDAO().deleteById( imagem.getId() );
		}
	}
	
	public void removeTechInfo( List<InformacaoTecnica> infoTechs ) throws SQLException {
		if ( infoTechs == null || infoTechs.size() == 0 ) {
			return ;
		}
		
		for (InformacaoTecnica infoTech : infoTechs) {
			removeArquivo( infoTech.getArquivoImagem() );
			DAOFactory.instance( mContext ).informacaoTecnicaDAO().deleteById( infoTech.getId() );
		}
	}
	
	public void removeArtigo( Artigo artigo, Article loader ) {

		try {
			List<Imagem> imagens = DAOFactory.instance( mContext ).imagemDAO().queryForEq("artigo_id", artigo.getId() );
			removeImagens( imagens );
			
			List<Distribuicao> imagensDistribuicao = DAOFactory.instance( mContext ).distribuicaoDAO().queryForEq("artigo_id", artigo.getId() );
			removeImagensDistribuicao(imagensDistribuicao );
			
			List<InformacaoTecnica> infoTechs = DAOFactory.instance( mContext ).informacaoTecnicaDAO().queryForEq("artigo_id", artigo.getId() );
			removeTechInfo( infoTechs );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deletarColecao( Colecao colecao ) {
		if ( colecao.getColecaoPai() != null ) {
			deletarColecao( colecao.getColecaoPai() );
		}
		colecao.setFlagDeletar( true );
	}
	
	public void save_article( Article loader, Colecao root ) throws SQLException {

		try 
		{
			DAOFactory.instance(mContext).disableAutoCommit();
			
			Artigo artigo = null;
			try
			{
				QueryBuilder<Artigo, Integer> qbArtigo = DAOFactory.instance( mContext ).artigoDAO().queryBuilder();
				qbArtigo.where().eq("name", loader.name).and().eq("colecao_id", root.getId() );
				artigo = qbArtigo.queryForFirst();
				
				if ( artigo == null ) {
					
					if ( loader.wasRemoved ) {
						mNumeroArtigosDescartadosWasRemoved++;
						Log.d(TAG, "Artigo(" + mNumeroArtigosDescartadosWasRemoved + ") [" + loader.name + "] foi desconsiderado por ser wasRemoved=true");
						return;
					}
					
					Date currentTimeStamp = Calendar.getInstance().getTime();
					System.out.println("currentTimeStamp => " + currentTimeStamp.toString());
					
					if ( currentTimeStamp.after( loader.expirationDate ) ) {
						mNumeroArtigosDescartadosExpirationDate++;
						Log.d(TAG, "Artigo(" + mNumeroArtigosDescartadosExpirationDate + ") ["  + loader.name + "] foi desconsiderado por ser expirateDate="+loader.expirationDate);
						return;
					}
					
					artigo = new Artigo();
					artigo.setColecao(root);
					artigo.setName( loader.name );
					artigo.setCode( loader.code );
					artigo.setExpirationDate( loader.expirationDate );
					artigo.setIsColorTable( loader.isColorTable );
					artigo.setWasRemoved( loader.wasRemoved );
					DAOFactory.instance(mContext).artigoDAO().create(artigo);
					
					save_prints( artigo, loader.prints );
					save_tech_info( artigo, loader.techInfo );
					
				} else {
					if ( loader.wasRemoved ) {
						removeArtigo(artigo, loader);
					} else {
						artigo.setExpirationDate( loader.expirationDate );
						DAOFactory.instance( mContext ).artigoDAO().update( artigo );
					}
					
				}
				
			} catch( SQLException ex ) {
				throw new SQLException(ex);
			}
			
		} finally {
			DAOFactory.instance(mContext).commit();
			DAOFactory.instance(mContext).enableAutoCommit();
		}
	}
	
	public void handler_articles( JsonReader reader, Colecao root ) throws IOException, SQLException {
		reader.beginArray();
		
		while( reader.hasNext() ) {
			Article loaderArticle = JSONUtils.newGson().fromJson(reader, Article.class);
			save_article( loaderArticle, root );
		}
		
		reader.endArray();
	}
	
	public void handler_colecao( JsonReader reader, Colecao root ) throws IOException, SQLException {
	
		Colecao colecao = new Colecao();
		colecao.colecaoPai = root;
		
		reader.beginObject();
		
		while( reader.hasNext() ) {
			mRow++;
			String tagname = "";
			try {
				tagname = reader.nextName();
			} catch( RuntimeException ex ) {
				ex.printStackTrace();
			}
			
			
			if ( TYPE.equals(tagname) ) {
				//System.out.println( "type=" + reader.nextString() );
				reader.nextString();
			}
			else if ( NAME.equals(tagname) ) {
				colecao.setName( reader.nextString() );
				if ( root == null ) {
					System.out.println( "name=" + colecao.getName() + ", colecaoPai=null");
				} else {
					System.out.println( "name=" + colecao.getName() + ", colecaoPai=" + root.getName());
				}
				
				QueryBuilder<Colecao, Integer> qbColecao = DAOFactory.instance( mContext ).colecaoDAO().queryBuilder();
				
				if ( root != null ) {
					qbColecao.where().eq("name", colecao.getName()).and().eq("colecaoPai_id", root.getId() );
				} else {
					qbColecao.where().eq("name", colecao.getName()).and().isNull("colecaoPai_id");
				}
				
				Colecao resultado = qbColecao.queryForFirst();
				if ( resultado != null ) {
					colecao = resultado;					
				}
				else {
					DAOFactory.instance( mContext ).colecaoDAO().create( colecao );
				}
			}
			else if ( CHILDREN.equals(tagname) ) {
				handler_colecoes(reader, colecao);
			}
			else if ( ARTICLES.equals(tagname) ) {
				handler_articles(reader, colecao);
			}
			else if ( PARADE.equals(tagname) ) {
				handler_parade( reader, colecao );
			}
			else if ( IS_ALLOWED.equals(tagname) ) {
				colecao.setIsAllowed( reader.nextBoolean() );
				DAOFactory.instance( mContext ).colecaoDAO().update( colecao );
			}
		}
		
		if ( !colecao.isAllowed() ) {
			System.out.println( "Colecao " + colecao.getName() + " sendo removida, isAllowed igual a false" );
			DAOFactory.instance( mContext ).colecaoDAO().delete( colecao );
		}
		
		reader.endObject();
		
		if ( root == null ) {
			System.out.println("-------------------------------------------------------------------");
		}
	}
	
	public void handler_colecoes( JsonReader reader, Colecao root ) throws IOException, SQLException {
		reader.beginArray();
		
		while( reader.hasNext() ) {
			try {
				handler_colecao(reader, root);
			} catch( RuntimeException ex ) {
				ex.printStackTrace();
			}
		}
		
		reader.endArray();
	}
	
}
