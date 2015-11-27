package by.bsuir.httpserver.config;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Dzmitry_Dydyshka on 11/3/2015.
 */
public class Configuration {

    //The path to file with server configuration
    public static final String PATH = "src/main/resources/config.json";

    public int getPort() {

        JsonObject jsonObject = null;
        try {
            jsonObject = readConfigJsonObject();
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return jsonObject.get("port").asInt();

    }

    public String getDestination(){
        JsonObject jsonObject = null;
        try {
            jsonObject = readConfigJsonObject();
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return jsonObject.get("destination").asString();
    }

    private JsonObject readConfigJsonObject() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(PATH));
        JsonObject jsonConfig = Json.parse(new String(encoded)).asObject();

        return jsonConfig;
    }

}
