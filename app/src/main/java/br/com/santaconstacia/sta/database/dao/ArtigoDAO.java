package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;
import br.com.santaconstacia.sta.database.bean.Artigo;
import com.j256.ormlite.support.ConnectionSource;

public class ArtigoDAO extends BaseDAO<Artigo, Integer>  {

	public ArtigoDAO(ConnectionSource connectionSource ) throws SQLException {
		super(connectionSource, Artigo.class);
	}

}
