package br.com.santaconstacia.sta.database.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Jo??o Paulo Ferreira
 *
 */
public class BaseDAO<T, ID> extends RuntimeExceptionDao<T, ID>{
	
	public BaseDAO(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
		super(new BaseDaoImpl<T, ID>(connectionSource, dataClass) {
		});
	}
	
	public void create(Collection<T> ts){
		if(ts == null)
			return;
		
		for (T t : ts) 
			create(t);
	}
	
	public void createOrUpdate(Collection<T> ts){
		if(ts == null)
			return;
		
		for (T t : ts) 
			createOrUpdate(t);
	}
	
	public void createIfNotExists(Collection<T> ts){
		if(ts == null)
			return;
		
		for (T t : ts) 
			createIfNotExists(t);
	}
	
	public void clearAll() throws SQLException {
		TableUtils.clearTable( this.getConnectionSource(), this.getDataClass() );
	}
	
}
