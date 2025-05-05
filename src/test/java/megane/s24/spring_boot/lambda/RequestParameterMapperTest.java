package megane.s24.spring_boot.lambda;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import megane.s24.spring_boot.lambda.PathNormalizer;
import megane.s24.spring_boot.lambda.RequestParameterMapper;

class RequestParameterMapperTest {

  RequestParameterMapper mapper = new RequestParameterMapper(
      new ObjectMapper(),
      new PathNormalizer());

  @Test
  void test() throws Throwable {
    Hoge result = mapper.map(
        "/test/{fieldA}/{fieldB}/hoge",
        "/test/valueA1/valueB1/hoge",
        Map.of("fieldB", "valueB2", "fieldC", "valueC2", "fieldD", "valueD2"),
        """
              {"fieldD":"valueD3","fieldE":"valueE3"}
            """,
        Hoge.class);
    assertThat(result.fieldA).isEqualTo("valueA1");
    assertThat(result.fieldB).isEqualTo("valueB1");
    assertThat(result.fieldC).isEqualTo("valueC2");
    assertThat(result.fieldD).isEqualTo("valueD2");
    assertThat(result.fieldE).isEqualTo("valueE3");
  }

  public static class Hoge {
    public String fieldA;
    public String fieldB;
    public String fieldC;
    public String fieldD;
    public String fieldE;
  }

}
