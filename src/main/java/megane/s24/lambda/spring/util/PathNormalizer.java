package megane.s24.lambda.spring.util;

import org.springframework.stereotype.Component;

@Component
public class PathNormalizer {

  public String normalize(final String path) {
    return "/" + path.replaceAll("^/|/$", "");
  }

}
