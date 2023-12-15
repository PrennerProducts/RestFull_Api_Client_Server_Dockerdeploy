package REST.client.helloworld;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseClass {
    @JsonProperty("result") // Dieses Annotation sagt Jackson, wie das Feld im JSON heißt
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
