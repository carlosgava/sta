package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;

import br.com.santaconstacia.sta.database.bean.Mascara;

import com.j256.ormlite.support.ConnectionSource;


public class MascaraDAO extends BaseDAO<Mascara, Integer>  {

	public MascaraDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Mascara.class);
	}

}
