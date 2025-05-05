package megane.s24.lambda.spring.handler;

/**
 * リクエストを処理するハンドラ
 */
public interface RequestHandler<Request> {

  default public String getMethodName() {
    return "handle";
  }

  Object handle(Request request);

}
