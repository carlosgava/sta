package br.com.santaconstacia.sta.loaders;

import java.util.List;

public class Comm {

	private String name;
	public List<Downloadable> files	;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Downloadable> getFiles() {
		return files;
	}
	public void setFiles(List<Downloadable> files) {
		this.files = files;
	}
	
}
