package br.com.santaconstacia.sta.database.bean;

import com.j256.ormlite.field.DatabaseField;

public class FavoritoArquivo {

	public static final String ID_FIELD_NAME = "id";
	public static final String ID_ARQUIVO_FIELD_NAME = "arquivo_id";
	public static final String ID_FAVORITO_FIELD_NAME = "favorito_id";
	public static final String QTDE_FIELD_NAME = "qtde";
	
	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private Integer id;
	
	@DatabaseField(canBeNull = false, foreign = true, columnName = ID_FAVORITO_FIELD_NAME)
	private Favorito favorito;
	
	@DatabaseField(canBeNull = false, foreign = true, columnName = ID_ARQUIVO_FIELD_NAME)
	private Arquivo arquivo;
	
	@DatabaseField( columnName = QTDE_FIELD_NAME )
	private Integer qtde;
	
	// Para o OrmLite
	public FavoritoArquivo() {
	}
	
	public FavoritoArquivo( Favorito favorito, Arquivo arquivo ) {
		this.favorito = favorito;
		this.arquivo = arquivo;
	}
	
	public Arquivo getArquivo() {
		return arquivo;
	}
	
	public Favorito getFavorito() {
		return favorito;
	}
	
	public Integer getQtde() {
		return qtde;
	}
	
	public void setQtde( Integer qtde ) {
		this.qtde = qtde;
	}
	
}
