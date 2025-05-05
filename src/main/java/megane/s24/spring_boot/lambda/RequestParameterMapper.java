package megane.s24.spring_boot.lambda;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import megane.s24.spring_boot.exception.BadRequestException;

/**
 * リクエストパラメータをマッピングするクラスです。 このクラスは、パス、クエリパラメータ、およびリクエストボディを処理して、指定されたリクエストハンドラータイプに変換します。
 * <p>
 * リクエストパラメータは、順番に優先度を持ち、最初にパスパラメータ、次にクエリパラメータ、最後にボディパラメータが使用されます。
 * </p>
 */
@Component
public class RequestParameterMapper {

  private final ObjectMapper objectMapper;
  private final PathNormalizer normalizer;

  public RequestParameterMapper(ObjectMapper objectMapper, PathNormalizer normalizer) {
    this.objectMapper = objectMapper;
    this.normalizer = normalizer;
  }

  private static final AntPathMatcher matcher = new AntPathMatcher();

  private static final TypeReference<Map<String, String>> mapTypeReference =
      new TypeReference<Map<String, String>>() {};

  /**
   * 指定されたパス、クエリパラメータ、ボディをもとに、リクエストハンドラータイプにマッピングします。
   * 
   * @param pattern リクエストのパスパターン（Antパターン）
   * @param path リクエストの実際のパス
   * @param queryParams クエリパラメータ（キーと値のペア）
   * @param jsonBody リクエストボディ（JSON形式の文字列）
   * @param requestType マッピング先のリクエストハンドラータイプ（クラス）
   * @param <T> リクエストタイプ
   * @return 指定されたリクエストハンドラータイプにマッピングされたオブジェクト
   * @throws NullPointerException 引数が null の場合にスローされます
   * @throws BadRequestException リクエストボディの解析中にエラーが発生した場合にスローされます
   */
  public <T> T map(String pattern, String path,
      Map<String, String> queryParams, String jsonBody, Class<T> requestType) {
    Objects.requireNonNull(pattern);
    Objects.requireNonNull(path);
    Objects.requireNonNull(queryParams);
    Objects.requireNonNull(requestType);

    Map<String, String> pathParams =
        Optional
            .ofNullable(
                matcher.extractUriTemplateVariables(
                    normalizer.normalize(pattern),
                    normalizer.normalize(path)))
            .orElse(Collections.emptyMap());
    Map<String, String> bodyparams =
        Optional.ofNullable(jsonBody)
            .map(
                b -> {
                  try {
                    return objectMapper.readValue(b.isBlank() ? "{}" : b, mapTypeReference);
                  } catch (JsonProcessingException e) {
                    throw new BadRequestException(e);
                  }
                })
            .orElse(Collections.emptyMap());

    Map<String, String> allParams = new HashMap<>();
    allParams.putAll(bodyparams);
    allParams.putAll(queryParams);
    allParams.putAll(pathParams);

    return objectMapper.convertValue(allParams, requestType);
  }

}
