package megane.s24.lambda.spring.routing;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import megane.s24.lambda.spring.exception.NotFoundException;
import megane.s24.lambda.spring.routing.RouteMapping.RouteMap;
import megane.s24.lambda.spring.util.PathNormalizer;

/**
 * リクエストの HTTP メソッドとパスに基づいて、適切なルーティング情報を解決するコンポーネントです。
 *
 * <p>
 * {@link RouteMapping} に登録されたルート定義の中から、受け取ったリクエストと一致するルートを検索します。 パスのマッチングには Spring の
 * {@code AntPathMatcher} を使用し、動的パスパラメータにも対応しています。
 * </p>
 */
@Component
public class RouteResolver {

  private final RouteMapping routeMapping;

  private final PathNormalizer normalizer;

  private static final AntPathMatcher matcher = new AntPathMatcher();

  public RouteResolver(RouteMapping routeMapping, PathNormalizer normalizer) {
    super();
    this.routeMapping = routeMapping;
    this.normalizer = normalizer;
  }

  /**
   * 指定された HTTP パスとメソッドに一致するルートを解決します。
   *
   * <p>
   * {@link RouteMapping} に定義されたルートリストを走査し、メソッドとパスが一致する最初の {@link RouteMap} を返します。 パスの比較は Ant
   * スタイルのパターンマッチングで行われ、動的なパス（例: {@code /hello/{id}}）にも対応します。
   * </p>
   *
   * @param path リクエストのパス（例：{@code /hello/123}）
   * @param method リクエストの HTTP メソッド（GET, POST など）
   * @return 一致する {@link RouteMap}、一致しない場合は {@code null}
   */
  public RouteMap resolve(final String path, final HttpMethod method) {
    return routeMapping
        .getRouteMappings().stream()
        .filter(m -> m.method().equals(method))
        .filter(m -> matcher.match(m.path(), normalizer.normalize(path)))
        .findFirst()
        .orElseThrow(() -> new NotFoundException());
  }

}
