package megane.s24.lambda.spring;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import megane.s24.lambda.spring.api.APIGatewayHandler;

public class Main {

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  public static void main(String[] args) throws UnsupportedEncodingException {
    System.setOut(new PrintStream(System.out, true, "UTF-8"));

    var handler = new APIGatewayHandler();
    var scanner = new Scanner(System.in);

    while (scanner.hasNextLine()) {
      var line = scanner.nextLine();
      try {
        var request = objectMapper.readValue(line, APIGatewayProxyRequestEvent.class);
        var response = handler.handleRequest(request, null);
        System.out
            .println("レスポンス:" + objectMapper.writeValueAsString(response));
      } catch (JsonProcessingException e) {
        System.err.println("Invalid JSON: " + e.getMessage());
      } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
      }
    }

    scanner.close();
  }

}
