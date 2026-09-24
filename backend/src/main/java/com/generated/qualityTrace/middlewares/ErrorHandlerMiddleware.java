package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.types.BizException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常出口：BizException 按错误码返回，未知异常兜底 500 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Map<String, Object>> handleBiz(BizException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("code", e.code, "message", e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnknown(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("code", "INTERNAL_ERROR", "message", String.valueOf(e.getMessage())));
  }
}
