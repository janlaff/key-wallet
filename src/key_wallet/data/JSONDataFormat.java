package key_wallet.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;

public class JSONDataFormat implements DataFormat {
    @Override
    public Data decode(byte[] encodedData) throws DataFormatException {
        Data data = new Data();

        String strBytes = new String(encodedData, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();

        try {
            JSONObject root = (JSONObject)parser.parse(strBytes);
            JSONArray credentialArray = (JSONArray)root.get("credentials");

            for (Object obj : credentialArray) {
                JSONObject credential = (JSONObject)obj;
                data.credentials.add(new Credential(
                        (String)credential.get("name"),
                        (String)credential.get("login"),
                        (String)credential.get("email"),
                        (String)credential.get("password"),
                        (String)credential.get("website"),
                        (String)credential.get("category")
                ));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public byte[] encode(Data data) {
        JSONObject root = new JSONObject();
        JSONArray credentialArray = new JSONArray();

        for (Credential c : data.credentials) {
            JSONObject credential = new JSONObject();
            credential.put("name", c.name);
            credential.put("login", c.login);
            credential.put("email", c.email);
            credential.put("password", c.password);
            credential.put("website", c.website);
            credential.put("category", c.category);
            credentialArray.add(credential);
        }

        root.put("credentials", credentialArray);
        return root.toJSONString().getBytes(StandardCharsets.UTF_8);
    }
}
