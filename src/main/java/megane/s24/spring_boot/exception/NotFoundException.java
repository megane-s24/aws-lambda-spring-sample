package megane.s24.spring_boot.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpException {

  public NotFoundException() {
    super(HttpStatus.NOT_FOUND,
        """
            {
              "error": "NOT FOUND"
            }
                """);
  }
}
