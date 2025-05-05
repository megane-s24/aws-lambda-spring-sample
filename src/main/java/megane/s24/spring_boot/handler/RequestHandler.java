package megane.s24.spring_boot.handler;

/**
 * リクエストを処理するハンドラ
 */
public interface RequestHandler<Request> {

  default public String getMethodName() {
    return "handle";
  }

  Object handle(Request request);

}
