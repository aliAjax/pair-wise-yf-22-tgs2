package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;

/** 业务异常：service 层包装后抛出，controller / 全局处理器分别兜底。 */
public class BusinessException extends RuntimeException {
  private final String code;

  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }

  public BusinessException(String message) {
    this(ErrorCodes.RBAC_DENIED, message);
  }

  public String getCode() {
    return code;
  }
}
