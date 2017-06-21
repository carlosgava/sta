package br.com.santaconstacia.sta.database.bean;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Artigo {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String name;
	
	@DatabaseField
	private String code;
	
	@DatabaseField
	private Date expirationDate;
	
	@DatabaseField
	private Boolean isColorTable;
	
	@DatabaseField
	private Boolean wasRemoved;

	@DatabaseField
	private Boolean flagPendenteProc = true; 
	
	@DatabaseField(canBeNull = false, foreign = true)
	public Colecao colecao;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getIsColorTable() {
		return isColorTable;
	}

	public void setIsColorTable(Boolean isColorTable) {
		this.isColorTable = isColorTable;
	}

	public Boolean getWasRemoved() {
		return wasRemoved;
	}

	public void setWasRemoved(Boolean wasRemoved) {
		this.wasRemoved = wasRemoved;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public void setColecao(Colecao colecao) {
		this.colecao = colecao;
	}
	
	public Boolean getflagPendenteProc() {
		return flagPendenteProc;
	}
	
	public void setFlagPendenteProc( Boolean value ) {
		flagPendenteProc = value;
	}
	
	@Override
	public boolean equals(Object artigo) {
		if ( artigo == null )
			return false;
		return this.name.equals( ((Artigo)artigo).getName() );
	}
}
