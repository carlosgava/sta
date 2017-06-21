package br.com.santaconstacia.sta.negocio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint.Join;
import br.com.santaconstacia.sta.database.bean.Arquivo;
import br.com.santaconstacia.sta.database.bean.Artigo;
import br.com.santaconstacia.sta.database.bean.Colecao;
import br.com.santaconstacia.sta.database.bean.Distribuicao;
import br.com.santaconstacia.sta.database.bean.Imagem;
import br.com.santaconstacia.sta.database.bean.InformacaoTecnica;
import br.com.santaconstacia.sta.database.dao.ArquivoDAO;
import br.com.santaconstacia.sta.database.dao.ArtigoDAO;
import br.com.santaconstacia.sta.database.dao.ColecaoDAO;
import br.com.santaconstacia.sta.database.dao.DAOFactory;
import br.com.santaconstacia.sta.database.dao.DistribuicaoDAO;
import br.com.santaconstacia.sta.database.dao.ImagemDAO;
import br.com.santaconstacia.sta.database.dao.InformacaoTecnicaDAO;

import com.j256.ormlite.stmt.QueryBuilder;

public class ArtigoManager {

	private Context mContext = null;
	private List<Artigo> mArtigos = null;
	private List<Arquivo> mTodosArquivosNaoBaixados;
	
	public ArtigoManager(Context context) {
		mContext = context;
		mTodosArquivosNaoBaixados = new ArrayList<Arquivo>();
	}

	public void carregarTodosArtigosPendenteProc() throws SQLException {
		ColecaoDAO daoColecao = DAOFactory.instance(mContext).colecaoDAO();
		ArtigoDAO daoArtigo = DAOFactory.instance(mContext).artigoDAO();

		QueryBuilder<Colecao, Integer> qbColecaoPai = daoColecao.queryBuilder();
		qbColecaoPai.where().eq("isAllowed", true).and().isNull("colecaoPai_id");
		List<Colecao> colecoesPai = qbColecaoPai.query();
		
		QueryBuilder<Colecao, Integer> qbSubColecao = daoColecao.queryBuilder();
		qbSubColecao.where().eq("isAllowed", true).and().in("colecaoPai_id", colecoesPai);
		List<Colecao> subColecoes = qbSubColecao.query();
		
		QueryBuilder<Colecao, Integer> qbSubColecaoArtigo = daoColecao.queryBuilder();
		qbSubColecao.where().eq("isAllowed", true).and().in("colecaoPai_id", subColecoes);
		
		QueryBuilder<Artigo, Integer> qbArtigo = daoArtigo.queryBuilder();
		qbArtigo.where().eq("wasRemoved", false).and().eq("flagPendenteProc", true);
		qbArtigo.join( qbSubColecao );
		
		mArtigos = qbArtigo.query();
	}
	
	public void carregarArquivosNaoBaixados( Artigo artigo ) throws SQLException {
		mTodosArquivosNaoBaixados = new ArrayList<Arquivo>();
		
		carregarArquivosImagens(artigo);
		carregarArquivosImagensDistribuicao(artigo);
		carregarArquivosInformacoesTecnicas(artigo);
	}
	
	public void carregarArquivosImagens( Artigo artigo ) throws SQLException {
		DAOFactory daoFactory = DAOFactory.instance(mContext);
		ArquivoDAO daoArquivo = daoFactory.arquivoDAO();
		ImagemDAO daoImagem = daoFactory.imagemDAO();
		
		QueryBuilder<Arquivo, Integer> qbArquivo = daoArquivo
				.queryBuilder();
		QueryBuilder<Imagem, Integer> qbImagens = daoImagem
				.queryBuilder();
		
		qbImagens.where().eq("artigo_id", artigo.getId() );
		
		qbArquivo.where().eq("flagPendenteProc", true).and().eq("flagDeletar", false);
		qbArquivo.join( qbImagens );

		List<Arquivo> arquivos = qbArquivo.query();
		if ( arquivos != null && arquivos.size() > 0 ) {
			mTodosArquivosNaoBaixados.addAll( arquivos );
		}
	}
	
	public void carregarArquivosImagensDistribuicao( Artigo artigo ) throws SQLException {
		DAOFactory daoFactory = DAOFactory.instance(mContext);
		ArquivoDAO daoArquivo = daoFactory.arquivoDAO();
		DistribuicaoDAO daoImagem = daoFactory.distribuicaoDAO();
		
		QueryBuilder<Arquivo, Integer> qbArquivo = daoArquivo
				.queryBuilder();
		QueryBuilder<Distribuicao, Integer> qbImagens = daoImagem
				.queryBuilder();
		
		qbImagens.where().eq("artigo_id", artigo.getId() );
		
		qbArquivo.where().eq("flagPendenteProc", true).and().eq("flagDeletar", false);
		qbArquivo.join( qbImagens );

		List<Arquivo> arquivos = qbArquivo.query();
		if ( arquivos != null && arquivos.size() > 0 ) {
			mTodosArquivosNaoBaixados.addAll( arquivos );
		}
	}
	
	public void carregarArquivosInformacoesTecnicas( Artigo artigo ) throws SQLException {
		DAOFactory daoFactory = DAOFactory.instance(mContext);
		ArquivoDAO daoArquivo = daoFactory.arquivoDAO();
		InformacaoTecnicaDAO daoInformacaoTecnica = daoFactory.informacaoTecnicaDAO();
		ArtigoDAO daoArtigo = daoFactory.artigoDAO();
		
		QueryBuilder<Arquivo, Integer> qbArquivo = daoArquivo
				.queryBuilder();
		QueryBuilder<InformacaoTecnica, Integer> qbInfoTech = daoInformacaoTecnica
				.queryBuilder();

		qbInfoTech.where().eq("artigo_id", artigo.getId() );
		
		qbArquivo.where().eq("flagPendenteProc", true).and().eq("flagDeletar", false);
		qbArquivo.join( qbInfoTech );

		List<Arquivo> arquivos = qbArquivo.query();
		if ( arquivos != null && arquivos.size() > 0 ) {
			mTodosArquivosNaoBaixados.addAll( arquivos );
		}
	}
	
	public List<Artigo> getArtigosPendenteProc() {
		return mArtigos;
	}
	
	public Arquivo getArquivoPendenteParaBaixar( Artigo artigo ) {
		for (Arquivo arquivo : mTodosArquivosNaoBaixados) {
			if ( arquivo.getFlagDeletar() == false && arquivo.getFlagPendenteProc() == true ) {
				return arquivo;
			}
		}
		return null;
	}
	
	public List<Arquivo> getTodosArquivosParaBaixar() {
		return mTodosArquivosNaoBaixados;
	}

	public void removerArquivoBaixado( Arquivo arquivoBaixado ) {
		mTodosArquivosNaoBaixados.remove(arquivoBaixado);
	}
}
