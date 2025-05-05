package megane.s24.lambda.spring.handler;

import org.springframework.stereotype.Component;
import megane.s24.lambda.spring.handler.GetHello.GetHelloRequest;

@Component
public class GetHello implements RequestHandler<GetHelloRequest> {

  @Override
  public GetHelloResponse handle(GetHelloRequest request) {
    System.out.println(request);

    GetHelloResponse response = new GetHelloResponse();
    response.message = "Hello World!!";
    return response;
  }

  public static class GetHelloRequest {
    public String id;
    public String filterA;
    public String filterB;

    @Override
    public String toString() {
      return "GetHelloRequest [id=" + id + ", filterA=" + filterA + ", filterB=" + filterB + "]";
    }

  }

  public static class GetHelloResponse {
    public String message;
  }

}
