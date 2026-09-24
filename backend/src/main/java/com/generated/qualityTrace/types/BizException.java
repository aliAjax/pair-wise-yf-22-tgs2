package com.generated.qualityTrace.types;

/** 业务异常：service 抛出时携带错误码，controller 记录日志后由 ErrorHandlerMiddleware 统一包装响应 */
public class BizException extends RuntimeException {
  public final String code;

  public BizException(String code, String message) {
    super(message);
    this.code = code;
  }
}
