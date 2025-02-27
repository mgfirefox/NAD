package by.bsuir.nad.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class GsonHelper {
    private final Gson gson;

    public GsonHelper() {
        this(1.0);
    }

    public GsonHelper(double gsonVersion) {
        this(new GsonBuilder().setVersion(gsonVersion).setExclusionStrategies(new TcpExclusionStrategy()));
    }

    public GsonHelper(GsonBuilder gsonBuilder) {
        this(gsonBuilder.create());
    }

    public GsonHelper(Gson gson) {
        this.gson = gson;
    }

    public <T> T fromJson(InputStream in, Class<T> classOfT) throws IOException {
        return fromJson(new InputStreamReader(in), classOfT);
    }

    public <T> T fromJson(Reader in, Class<T> classOfT) throws IOException {
        try {
            return gson.fromJson(gson.newJsonReader(in), classOfT);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    public <T> T fromJson(InputStream in, TypeToken<?> typeOfT) throws IOException {
        return fromJson(new InputStreamReader(in), typeOfT);
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJson(Reader in, TypeToken<?> typeOfT) throws IOException {
        try {
            return (T) gson.fromJson(gson.newJsonReader(in), typeOfT);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJson(String json, TypeToken<?> typeOfT) {
        return (T) gson.fromJson(json, typeOfT);
    }

    public void toJson(Object src, OutputStream out) throws IOException {
        toJson(src, new OutputStreamWriter(out));
    }

    public void toJson(Object src, Writer out) throws IOException {
        try {
            JsonWriter jsonWriter = new JsonWriter(out);
            gson.toJson(src, src.getClass(), jsonWriter);
            jsonWriter.flush();
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    public void toJson(Object src, TypeToken<?> typeOfSrc, OutputStream out) throws IOException {
        toJson(src, typeOfSrc, new OutputStreamWriter(out));
    }

    public void toJson(Object src, TypeToken<?> typeOfSrc, Writer out) throws IOException {
        try {
            JsonWriter jsonWriter = new JsonWriter(out);
            gson.toJson(src, typeOfSrc.getType(), jsonWriter);
            jsonWriter.flush();
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public String toJson(Object src, TypeToken<?> typeOfSrc) {
        return gson.toJson(src, typeOfSrc.getType());
    }
}
