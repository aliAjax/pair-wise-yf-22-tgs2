package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.exceptions.BusinessException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常处理：service 已包装业务异常，这里只做统一出参，不吞掉异常语义。 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
    log.warn("business exception code={} message={}", ex.getCode(), ex.getMessage());
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("code", ex.getCode());
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
    log.warn("illegal argument: {}", ex.getMessage());
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("code", ErrorCodes.MATERIAL_LOT_REQUIRED);
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
    log.error("unexpected exception", ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("code", "INTERNAL_ERROR");
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
