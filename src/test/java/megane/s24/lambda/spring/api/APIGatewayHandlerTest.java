package megane.s24.lambda.spring.api;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import megane.s24.lambda.spring.api.APIGatewayHandler;

class APIGatewayHandlerTest {

  @Test
  void APIGatewaHandlerが起動する() {
    var request = new APIGatewayProxyRequestEvent();
    request.setHttpMethod("GET");
    request.setPath("/hello/123");
    request.setQueryStringParameters(Map.of("filterA", "aaa", "filterB", "ccc"));
    var handler = new APIGatewayHandler();
    var response = handler.handleRequest(request, null);
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo("""
        {"message":"Hello World!!"}""");
  }

}
