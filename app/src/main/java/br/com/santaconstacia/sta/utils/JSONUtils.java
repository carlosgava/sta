package br.com.santaconstacia.sta.utils;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtils {

	private static GsonBuilder builder;
	
	static{
		builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new GsonDateAdapter());
	}
	
	public static Gson newGson(){
		return builder.create();
	}
}