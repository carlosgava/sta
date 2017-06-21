package br.com.santaconstacia.sta.utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonDateAdapter implements JsonSerializer<Date>,
		JsonDeserializer<Date> {
	
	private SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy", new Locale("pt", "BR"));

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(format.format(src));
	}

	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			return format.parse(json.getAsString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
