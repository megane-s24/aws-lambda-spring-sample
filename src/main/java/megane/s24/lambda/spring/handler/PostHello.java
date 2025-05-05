package megane.s24.lambda.spring.handler;

import org.springframework.stereotype.Component;
import megane.s24.lambda.spring.handler.PostHello.PostHelloRequest;

@Component
public class PostHello implements RequestHandler<PostHelloRequest> {

  @Override
  public PostHelloResponse handle(PostHelloRequest request) {
    System.out.println(request);

    PostHelloResponse response = new PostHelloResponse();
    response.message = "Hello World!!";
    return response;
  }

  public static class PostHelloRequest {
    public String id;
    public String filterA;
    public String filterB;

    @Override
    public String toString() {
      return "PostHelloRequest [id=" + id + ", filterA=" + filterA + ", filterB=" + filterB + "]";
    }

  }

  public static class PostHelloResponse {
    public String message;
  }
}
