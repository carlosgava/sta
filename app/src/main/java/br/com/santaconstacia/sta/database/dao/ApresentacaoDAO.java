package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Apresentacao;

import com.j256.ormlite.support.ConnectionSource;

public class ApresentacaoDAO  extends BaseDAO<Apresentacao, Integer>  {

	public ApresentacaoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Apresentacao.class);
	}

}
