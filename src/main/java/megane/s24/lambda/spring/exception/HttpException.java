package megane.s24.lambda.spring.exception;

import org.springframework.http.HttpStatus;

public abstract class HttpException extends RuntimeException {

  private final HttpStatus status;

  private final String body;

  public HttpException(Throwable cause, HttpStatus status, String body) {
    super(cause);
    this.status = status;
    this.body = body;
  }

  public HttpException(HttpStatus status, String body) {
    this(null, status, body);
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

}
