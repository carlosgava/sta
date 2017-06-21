package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Distribuicao;

import com.j256.ormlite.support.ConnectionSource;


public class DistribuicaoDAO extends BaseDAO<Distribuicao, Integer>  {

	public DistribuicaoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Distribuicao.class);
	}

}
