package megane.s24.lambda.spring.routing;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import megane.s24.lambda.spring.handler.GetHello;
import megane.s24.lambda.spring.handler.PostHello;
import megane.s24.lambda.spring.handler.RequestHandler;
import megane.s24.lambda.spring.util.PathNormalizer;

/**
 * アプリケーション内で使用されるルーティング情報の管理コンポーネントです。
 *
 * <p>
 * 各エンドポイントのパス、HTTP メソッド、処理する {@link RequestHandler} のクラスを紐付ける {@link RouteMap} を保持します。
 * このクラスは、すべてのルート定義を一元的に構成し、他のコンポーネントからルート情報を取得できるようにします。
 * </p>
 */
@Component
public class RouteMapping {

  private final List<RouteMap> routeMapList;

  private final PathNormalizer normalizer;

  /**
   * ルーティング定義を構成します。
   *
   * <p>
   * 本メソッドはアプリケーション内で許可されるすべてのパス・メソッド・ハンドラーの組み合わせを定義し、 不変なリストとして返します。定義された情報は {@link RouteResolver}
   * によるルーティング解決に使用されます。
   * </p>
   *
   * @return 登録された {@link RouteMap} の不変リスト
   */
  public List<RouteMap> configure() {
    List<RouteMap> list = new ArrayList<>();
    list.add(map("/hello/{id}", GET, GetHello.class));
    list.add(map("/hello/{id}", POST, PostHello.class));
    return Collections.unmodifiableList(list);
  }

  public RouteMapping(PathNormalizer normalizer) {
    this.normalizer = normalizer;
    this.routeMapList = configure();
  }

  public List<RouteMap> getRouteMappings() {
    return routeMapList;
  }

  private RouteMap map(
      String path,
      HttpMethod method,
      Class<? extends RequestHandler<?>> requestHandler) {
    return new RouteMap(normalizer.normalize(path), method, requestHandler);
  }

  public static record RouteMap(
      String path,
      HttpMethod method,
      Class<? extends RequestHandler<?>> requestHandler) {

    public RouteMap(
        String path,
        HttpMethod method,
        Class<? extends RequestHandler<?>> requestHandler) {
      this.path = path;
      this.method = method;
      this.requestHandler = requestHandler;
    }
  }
}
