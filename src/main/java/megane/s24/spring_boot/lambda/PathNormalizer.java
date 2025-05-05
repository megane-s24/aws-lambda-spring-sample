package megane.s24.spring_boot.lambda;

import org.springframework.stereotype.Component;

@Component
public class PathNormalizer {

  public String normalize(final String path) {
    return "/" + path.replaceAll("^/|/$", "");
  }

}
