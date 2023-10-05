package lol.skript.addondocdata;

import com.google.gson.Gson;
import lol.skript.addondocdata.syntax.DocSyntaxInfo;

import java.util.List;

public class JsonExporter {
    private static final Gson gson = new Gson();
    public static String convert(List<DocSyntaxInfo> syntax) {
        return gson.toJson(gson.toJsonTree(syntax));
    }
}
