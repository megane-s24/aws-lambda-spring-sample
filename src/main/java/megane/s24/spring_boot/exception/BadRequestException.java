package megane.s24.spring_boot.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpException {

  public BadRequestException(Throwable cause) {
    super(cause, HttpStatus.BAD_REQUEST,
        """
            {
              "error": "BAD REQUEST"
            }
                """);
  }

  public BadRequestException() {
    this(null);
  }
}
