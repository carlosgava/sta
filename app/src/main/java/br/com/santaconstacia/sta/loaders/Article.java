package br.com.santaconstacia.sta.loaders;

import java.util.Date;
import java.util.List;

public class Article {
	public String code, name;
	public Date expirationDate;
	public TechInfo techInfo;
	public boolean wasRemoved, isColorTable;
	public List<Print> prints;
	public Article colors;
	
	public boolean hasToRemove(){
		return wasRemoved;
	}
}
