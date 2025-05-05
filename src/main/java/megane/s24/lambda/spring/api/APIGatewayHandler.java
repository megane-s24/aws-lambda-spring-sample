package megane.s24.lambda.spring.api;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import megane.s24.lambda.spring.Application;

/**
 * AWS Lambda 用のエントリポイントハンドラークラスです。
 * <p>
 * このクラスは {@code RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>} を実装し、
 * API Gateway からのリクエストを受け取り、Spring Boot アプリケーションコンテキストを介して処理を委譲します。
 * </p>
 *
 * <p>
 * 初回のリクエスト時に Spring Boot アプリケーションを起動し、 {@link APIGatewayFunction}
 * に処理を委任します。以降のリクエストでは既に起動済みのコンテキストを利用します。
 * </p>
 */
public class APIGatewayHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private final ConfigurableApplicationContext context;

  private final APIGatewayFunction function;

  public APIGatewayHandler() {
    // 実行時に Spring Boot を起動してコンテキストを使用できる状態にする
    context = SpringApplication.run(Application.class);
    function = context.getBean(APIGatewayFunction.class);
  }

  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      APIGatewayProxyRequestEvent input,
      Context context) {
    try {
      return function.apply(input);
    } catch (Throwable e) {
      e.printStackTrace();

      APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
      response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.setBody("""
          {
            "error": "Internal Server Error"
          }
          """);
      return response;
    }
  }

}
