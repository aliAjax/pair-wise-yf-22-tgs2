package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.BusinessException;
import com.generated.qualityTrace.types.RecallDispositionPayload;

/** 处置入参校验：报废/让步接收字段规则、审批人与原因必填校验。 */
public final class RecallDispositionValidator {

  private RecallDispositionValidator() {}

  public static void validate(RecallDispositionPayload payload) {
    if (payload == null || isBlank(payload.batchNo())) {
      throw new BusinessException(
          ErrorCodes.BATCH_NO_REQUIRED, ErrorMessages.BATCH_NO_REQUIRED);
    }
    if (isBlank(payload.handlerId())) {
      throw new BusinessException(
          ErrorCodes.HANDLER_REQUIRED, ErrorMessages.HANDLER_REQUIRED);
    }
    if (!DispositionAction.SCRAP.name().equals(payload.action())
        && !DispositionAction.CONCESSION_ACCEPT.name().equals(payload.action())) {
      throw new BusinessException(
          ErrorCodes.INVALID_DISPOSITION_ACTION,
          String.format(ErrorMessages.INVALID_DISPOSITION_ACTION, payload.action()));
    }
    if (DispositionAction.CONCESSION_ACCEPT.name().equals(payload.action())) {
      if (isBlank(payload.approverId())) {
        throw new BusinessException(
            ErrorCodes.CONCESSION_APPROVER_REQUIRED,
            ErrorMessages.CONCESSION_APPROVER_REQUIRED);
      }
      if (isBlank(payload.concessionReason())) {
        throw new BusinessException(
            ErrorCodes.CONCESSION_REASON_REQUIRED,
            ErrorMessages.CONCESSION_REASON_REQUIRED);
      }
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
