package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.RecallAction;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.RecallDispositionPayload;

/** 处置入参校验：处置方式必须合法；让步接收必须登记审批人和原因 */
public final class RecallDispositionValidator {
  private RecallDispositionValidator() {}

  public static RecallAction validate(RecallDispositionPayload payload) {
    if (payload == null || payload.recallId() == null || payload.batchId() == null
        || isBlank(payload.action()) || isBlank(payload.disposedBy())) {
      throw new BizException(ErrorCodes.VALIDATION_FAILED, ErrorMessages.VALIDATION_FAILED);
    }
    RecallAction action;
    try {
      action = RecallAction.valueOf(payload.action());
    } catch (IllegalArgumentException e) {
      throw new BizException(ErrorCodes.VALIDATION_FAILED, ErrorMessages.VALIDATION_FAILED);
    }
    if (action == RecallAction.CONCESSION
        && (isBlank(payload.approver()) || isBlank(payload.reason()))) {
      throw new BizException(ErrorCodes.CONCESSION_APPROVAL_REQUIRED, ErrorMessages.CONCESSION_APPROVAL_REQUIRED);
    }
    return action;
  }

  private static boolean isBlank(String value) { return value == null || value.isBlank(); }
}
