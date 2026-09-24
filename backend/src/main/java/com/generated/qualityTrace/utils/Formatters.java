package com.generated.qualityTrace.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Formatters {
  private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  public static String audit(String type, long id) { return type + "#" + id; }

  /** 召回排查单号，如 RC-0001 */
  public static String recallNo(long id) { return "RC-" + String.format("%04d", id); }

  /** 处置记录单号，如 RD-0001 */
  public static String dispositionNo(long id) { return "RD-" + String.format("%04d", id); }

  /** 当前时间，统一写入 createdAt/disposedAt 等文本字段 */
  public static String now() { return LocalDateTime.now().format(TS); }
}
