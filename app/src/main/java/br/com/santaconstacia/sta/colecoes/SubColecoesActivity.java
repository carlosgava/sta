package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.colecoes.ColecaoGrupoFragment.OnSelectionListener;
import br.com.santaconstacia.sta.database.bean.Colecao;
import br.com.santaconstacia.sta.database.dao.DAOFactory;

public class SubColecoesActivity extends BaseActivity 
{

	public static final String CATEGORIA = "CATEGORIA";
	public static final String CATEGORIA_ID = "CATEGORIA_ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sub_colecoes);
		
		int idCategoria = getIntent().getExtras().getInt(CATEGORIA_ID);

		try {
			Colecao colecao = DAOFactory.instance( this ).colecaoDAO().queryForId( idCategoria );
			DAOFactory.instance( this ).colecaoDAO().refresh( colecao );
		    getFragmentManager()
	    	.beginTransaction()
	    	.add(R.id.colecoes, buildColecao(colecao))
	    	.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private Fragment buildColecao( Colecao colecaoPai ) throws SQLException {
		QueryBuilder<Colecao, Integer> qbColecao = DAOFactory.instance( this ).colecaoDAO().queryBuilder();
		qbColecao.where().eq("colecaoPai_id", colecaoPai.getId() ).and().eq("isAllowed", true);
		List<Colecao> colecoes = qbColecao.query();
		
		OnSelectionListener onSelectionListener = new OnSelectionListener() {
			
			@Override
			public void onSelect(String nome){
			}

			@Override
			public void onSelect(int item) {
				Intent intent = new Intent(SubColecoesActivity.this, ColecaoActivity.class);
				intent.putExtra(ColecaoActivity.CATEGORIA_ID, item);
		        startActivity(intent);			
			}
		};		
		
		if ( colecoes != null && colecoes.size() > 0 ) {
			int[] catsId = new int[colecoes.size()];
			String[] cats = new String[colecoes.size()];
			
			int i=0;
			for( Colecao colecao : colecoes ) {
				cats[i] = colecao.getName();
				catsId[i] = colecao.getId();
				i++;
			}
			
			Bundle arguments = new Bundle();
			arguments.putString(ColecaoGrupoFragment.TITLE, colecaoPai.getName());
			arguments.putStringArray(ColecaoGrupoFragment.ITEMS, cats);
			arguments.putIntArray(ColecaoGrupoFragment.ITEMS_ID, catsId);
	
			ColecaoGrupoFragment colecao = new ColecaoGrupoFragment();
			colecao.setArguments(arguments);
			colecao.setOnSelectionListener(onSelectionListener);
			
			return colecao;
		}
		
		return null;
	}
	
	private Fragment buildColecao(String nomeCategoria){
//		List<Categoria3> categorias = ServiceFactory
//				.getColecaoService(this, dbHelper).findCategorias3(nomeCategoria);
//		
//		OnSelectionListener onSelectionListener = new OnSelectionListener() {
//			
//			@Override
//			public void onSelect(String nome){
//				Intent intent = new Intent(SubColecoesActivity.this, ColecaoActivity.class);
//				intent.putExtra(ColecaoActivity.CATEGORIA, nome);
//		        startActivity(intent);			
//			}
//		};		
//
//		String[] cats = new String[categorias.size()];
//		
//		int i = 0;
//		for (Categoria3 categoria3 : categorias)
//			cats[i++]=categoria3.nome;
//		
//		Bundle arguments = new Bundle();
//		arguments.putString(ColecaoGrupoFragment.TITLE, nomeCategoria);
//		arguments.putStringArray(ColecaoGrupoFragment.ITEMS, cats);
//
//		ColecaoGrupoFragment colecao = new ColecaoGrupoFragment();
//		colecao.setArguments(arguments);
//		colecao.setOnSelectionListener(onSelectionListener);
//		
//		return colecao;
		return null;
	}
}