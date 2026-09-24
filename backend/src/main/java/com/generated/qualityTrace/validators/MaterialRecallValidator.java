package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.BusinessException;
import com.generated.qualityTrace.types.MaterialRecallInvestigatePayload;

/** 排查判定入参校验：只校验材料批号等基础字段，业务判定在 service。 */
public final class MaterialRecallValidator {

  private MaterialRecallValidator() {}

  public static void validateInvestigate(MaterialRecallInvestigatePayload payload) {
    if (payload == null || isBlank(payload.materialLotNo())) {
      throw new BusinessException(
          ErrorCodes.MATERIAL_LOT_REQUIRED, ErrorMessages.MATERIAL_LOT_REQUIRED);
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
