package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Colecao {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String name;
	
	@DatabaseField
	private Boolean isAllowed;
	
	@DatabaseField(canBeNull = true, foreign = true)
	public Colecao colecaoPai;
	
	protected Boolean mFlagDeletar = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	
	public void setColecaoPai( Colecao colecao ) {
		this.colecaoPai = colecao;
	}
	
	public Colecao getColecaoPai() {
		return this.colecaoPai;
	}
	
	public void setFlagDeletar( boolean flag ) {
		mFlagDeletar = flag;
	}
	
	public boolean isDeletar() {
		return ( mFlagDeletar == true );
	}
	
}
