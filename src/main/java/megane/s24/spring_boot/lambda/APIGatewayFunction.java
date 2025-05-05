package megane.s24.spring_boot.lambda;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import megane.s24.spring_boot.exception.HttpException;
import megane.s24.spring_boot.exception.NotFoundException;
import megane.s24.spring_boot.handler.RequestHandler;

/**
 * API Gateway からのリクエストを処理する機能クラスです。
 *
 * <p>
 * このクラスは、API Gateway から受け取ったリクエストイベントに基づき、ルーティング、リクエストデシリアライズ、 ハンドラーメソッドの実行、レスポンス生成を一貫して行います。
 * </p>
 */
@Component
public class APIGatewayFunction {

  @Autowired
  RouteResolver routeResolver;
  @Autowired
  ConfigurableApplicationContext context;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  RequestParameterMapper requestParameterMapper;

  /**
   * API Gateway からのリクエストイベントを処理し、レスポンスを生成します。
   *
   * <p>
   * 入力された {@link APIGatewayProxyRequestEvent} をもとに、リクエストパスとHTTPメソッドに基づいて 適切なハンドラーをルーティングし、対応する
   * {@link RequestHandler} の処理メソッドを動的に実行します。
   * </p>
   *
   * <p>
   * 入力は以下の要素に分解され、ハンドラーの入力型（リクエスト DTO）にマッピングされます：
   * <ul>
   * <li>パスパラメータ</li>
   * <li>クエリパラメータ</li>
   * <li>リクエストボディ（JSON）</li>
   * </ul>
   * </p>
   *
   * <p>
   * 結果として得られたレスポンスオブジェクトは JSON にシリアライズされ、200 OK のレスポンスとして返されます。
   * </p>
   * 
   * @param requestEvent API Gateway から受け取ったリクエストイベント
   * @return 生成された {@link APIGatewayProxyResponseEvent}（通常はJSON形式のレスポンスボディを含む）
   * @throws NoSuchMethodException ハンドラーメソッドが見つからない場合
   * @throws IllegalAccessException リフレクション実行時のアクセス違反
   * @throws InvocationTargetException ハンドラーメソッドの実行中に例外がスローされた場合
   * @throws JsonProcessingException JSONの解析または変換に失敗した場合
   */
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent requestEvent)
      throws NoSuchMethodException, SecurityException, JsonMappingException,
      JsonProcessingException, IllegalAccessException, InvocationTargetException {
    try {
      final var routeMap =
          routeResolver.resolve(requestEvent.getPath(),
              HttpMethod.valueOf(requestEvent.getHttpMethod()));
      if (Objects.isNull(routeMap)) {
        throw new NotFoundException();
      }

      final var handlerType = routeMap.requestHandler();
      final var handler = context.getBean(handlerType);

      final var types =
          GenericTypeResolver.resolveTypeArguments(handlerType, RequestHandler.class);
      final var requestType = types[0];

      final var method = handlerType.getMethod(handler.getMethodName(), requestType);
      final var request = requestParameterMapper.map(
          routeMap.path(),
          requestEvent.getPath(),
          Optional.ofNullable(requestEvent.getQueryStringParameters())
              .orElse(Collections.emptyMap()),
          Optional.ofNullable(requestEvent.getBody()).orElse(""),
          requestType);
      final var response =
          objectMapper.writeValueAsString(method.invoke(handler, request));

      APIGatewayProxyResponseEvent event = new APIGatewayProxyResponseEvent();
      event.setStatusCode(200);
      event.setBody(response);

      return event;
    } catch (HttpException e) {
      e.printStackTrace();
      APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
      response.setStatusCode(e.getStatus().value());
      response.setBody(e.getBody());
      return response;
    }
  }

}
