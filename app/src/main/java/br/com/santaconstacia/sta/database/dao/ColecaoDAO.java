package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;
import br.com.santaconstacia.sta.database.bean.Colecao;
import com.j256.ormlite.support.ConnectionSource;

public class ColecaoDAO extends BaseDAO<Colecao, Integer>  {

	public ColecaoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Colecao.class);
	}

}
