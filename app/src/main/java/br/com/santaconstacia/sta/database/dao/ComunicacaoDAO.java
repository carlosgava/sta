package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Comunicacao;

import com.j256.ormlite.support.ConnectionSource;


public class ComunicacaoDAO extends BaseDAO<Comunicacao, Integer>  {

	public ComunicacaoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Comunicacao.class);
	}

}
