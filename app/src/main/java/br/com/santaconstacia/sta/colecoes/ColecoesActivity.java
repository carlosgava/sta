package br.com.santaconstacia.sta.colecoes;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import br.com.santaconstacia.sta.R;
import br.com.santaconstacia.sta.base.BaseActivity;
import br.com.santaconstacia.sta.colecoes.ColecaoGrupoFragment.OnSelectionListener;
import br.com.santaconstacia.sta.database.bean.Colecao;
import br.com.santaconstacia.sta.database.dao.DAOFactory;

public class ColecoesActivity extends BaseActivity {

	private static final String TAG = ColecoesActivity.class.getSimpleName();
	
	private LinearLayout mLinearLayout = null;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_colecoes);
		inicializar();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void inicializar( ) {
		mLinearLayout = (LinearLayout) findViewById(R.id.colecoes_container);
		carregarColecoes();
	}
	
	private void carregarColecoes( ) {
		try {
			QueryBuilder<Colecao, Integer> qbColecao = DAOFactory.instance( this ).colecaoDAO().queryBuilder();
			qbColecao.where().eq("isAllowed", true).and().isNull("colecaoPai_id");
			List<Colecao> colecoes = qbColecao.query();
			buildColecoes( colecoes );
		} catch (SQLException e) {
			Log.w(TAG, "FALHA: Nao foi possivel carregar as colecoes");
		}
	}
	
	private Fragment buildColecoes( List<Colecao> colecoes ) {
	    int id = 1;
	    FragmentTransaction ft = getFragmentManager().beginTransaction();

	    
	    for (Colecao colecao : colecoes) {
	    	Fragment frag = buildColecao(colecao);
	    	
	    	if(frag == null)
	    		continue;
	    	
	    	View item = getLayoutInflater().inflate(R.layout.fragment_colecoes_item_1, null);
	    	item.setId(id);
		    mLinearLayout.addView(item);
	    	ft.add(id++, frag);
		}
	    
	    ft.commit();
	    
		return null;
	}
	
	private Fragment buildColecao( Colecao colecao ) {
		ColecaoGrupoFragment fragment = buildColecaoGrupoFragment( colecao );

		if ( fragment != null ) {
			OnSelectionListener onSelectionListener = new OnSelectionListener() {			
				@Override
				public void onSelect(String nome) {
				}

				@Override
				public void onSelect(int item) {
					Intent intent = new Intent(ColecoesActivity.this, SubColecoesActivity.class);
					intent.putExtra(SubColecoesActivity.CATEGORIA_ID, item);
			        startActivity(intent);			
				}
			};		
			
			fragment.setOnSelectionListener(onSelectionListener);
		}
		
		return fragment;
	}
	
	private ColecaoGrupoFragment buildColecaoGrupoFragment( Colecao colecao ) {
		try {
			QueryBuilder<Colecao, Integer> qbColecao = DAOFactory.instance( this ).colecaoDAO().queryBuilder();
			qbColecao.where().eq("isAllowed", true).and().eq("colecaoPai_id", colecao.getId());
			
			List<Colecao> colecoes = qbColecao.query();
			if( colecoes != null && colecoes.size() == 0 )
				return null;
			
			String[] cats = new String[ colecoes.size() ];
			int[] catsId = new int[ colecoes.size() ];
			
			int i = 0;
			for (Colecao col : colecoes) {
				cats[i]=col.getName();
				catsId[i]=col.getId();
				i++;
			}
			
			Bundle arguments = new Bundle();
			arguments.putString(ColecaoGrupoFragment.TITLE, colecao.getName());
			arguments.putStringArray(ColecaoGrupoFragment.ITEMS, cats);
			arguments.putIntArray(ColecaoGrupoFragment.ITEMS_ID, catsId);
			
			ColecaoGrupoFragment cgf = new ColecaoGrupoFragment();
			cgf.setArguments(arguments);
			return cgf;
			
		} catch (SQLException e) {
			Log.w(TAG, "FALHA: Nao foi possivel carregar as colecoes");
		}
		
		return null;
	}
	
}
