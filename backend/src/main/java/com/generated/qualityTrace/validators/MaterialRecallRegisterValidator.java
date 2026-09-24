package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.MaterialRecallRegisterPayload;

/** 排查登记入参校验：不合格材料批号、原因、登记人均必填 */
public final class MaterialRecallRegisterValidator {
  private MaterialRecallRegisterValidator() {}

  public static void validate(MaterialRecallRegisterPayload payload) {
    if (payload == null
        || isBlank(payload.materialLotNo())
        || isBlank(payload.reason())
        || isBlank(payload.createdBy())) {
      throw new BizException(ErrorCodes.VALIDATION_FAILED, ErrorMessages.VALIDATION_FAILED);
    }
  }

  private static boolean isBlank(String value) { return value == null || value.isBlank(); }
}
