/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.salescoach.practical;

import static org.junit.jupiter.api.Assertions.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class PracticalServiceTest {
  private final PracticalService service = new PracticalService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private Map<String, Object> sample() {
    return Map.<String, Object>ofEntries(
        Map.entry(
            "criteria",
            List.of(
                Map.<String, Object>ofEntries(
                    Map.entry("id", "DISCOVERY"),
                    Map.entry("weight", 60),
                    Map.entry("phrases", List.of("需求"))),
                Map.<String, Object>ofEntries(
                    Map.entry("id", "NEXT_STEP"),
                    Map.entry("weight", 40),
                    Map.entry("phrases", List.of("下次会议"))))),
        Map.entry(
            "turns",
            List.of(
                Map.<String, Object>ofEntries(
                    Map.entry("id", "T1"),
                    Map.entry("speaker", "SELLER"),
                    Map.entry("text", "请介绍目前需求。")),
                Map.<String, Object>ofEntries(
                    Map.entry("id", "T2"),
                    Map.entry("speaker", "BUYER"),
                    Map.entry("text", "下次会议再讨论。")))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void computesActualFeatureResult() {
    var result = service.execute(sample());
    assertEquals(new BigDecimal("60.000000"), result.get("scorePercent"));
    assertEquals(
        List.of("T1"),
        ((Map<?, ?>) ((List<?>) result.get("criteria")).get(0)).get("evidenceTurns"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsFeatureBoundary() {
    var invalid = new HashMap<>(sample());
    invalid.put("criteria", List.of());
    assertThrows(IllegalArgumentException.class, () -> service.execute(invalid));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void featureBoundary1() {
    var r = new HashMap<>(sample());
    r.put("turns", List.of(Map.of("id", "B", "speaker", "BUYER", "text", "需求和下次会议")));
    assertEquals(new BigDecimal("0.000000"), service.execute(r).get("scorePercent"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void featureBoundary2() {
    var r = new HashMap<>(sample());
    r.put("criteria", List.of(Map.of("id", "X", "weight", 0, "phrases", List.of("需求"))));
    assertEquals(false, service.execute(r).get("scoreDefined"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void featureBoundary3() {
    var r = new HashMap<>(sample());
    var c = ((List<?>) r.get("criteria")).get(0);
    r.put("criteria", List.of(c, c));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void featureBoundary4() {
    var r = new HashMap<>(sample());
    r.put(
        "turns",
        List.of(
            Map.of("id", "S", "speaker", "SELLER", "text", "😀"),
            Map.of("id", "B", "speaker", "BUYER", "text", "甲")));
    assertEquals(new BigDecimal("5E+1"), service.execute(r).get("buyerTextSharePercent"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void deterministicWithoutMutatingInput() {
    var input = sample();
    String before = input.toString();
    assertEquals(service.execute(input), service.execute(input));
    assertEquals(before, input.toString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(null));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMissingFields() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(Map.of()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnknownFields() {
    var input = new HashMap<>(sample());
    input.put("__unknown", true);
    assertThrows(IllegalArgumentException.class, () -> service.execute(input));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsOversizedText() {
    assertThrows(
        IllegalArgumentException.class, () -> service.execute(Map.of("text", "x".repeat(64001))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsDeepInput() {
    Map<String, Object> input = Map.of("v", 1);
    for (int i = 0; i < 14; i++) input = Map.of("v", input);
    final var value = input;
    assertThrows(IllegalArgumentException.class, () -> service.execute(value));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsExtremeNumberScale() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PracticalInput.decimal(Map.of("v", new BigDecimal("1e-1000000")), "v", 0, 1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            PracticalInput.decimal(
                Map.of("v", new BigDecimal("0.1234567890123456789012345678901")), "v", 0, 1));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnsupportedType() {
    assertThrows(
        IllegalArgumentException.class, () -> PracticalInput.validate(Map.of("v", new Object())));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsOversizedArray() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PracticalInput.validate(Map.of("v", Collections.nCopies(501, 1))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void disabledWithoutConfiguredToken() {
    assertEquals(
        503, new PracticalController(service, "").compute("{}", null).getStatusCode().value());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void shortConfigurationCannotEnableAccess() {
    assertEquals(
        503,
        new PracticalController(service, "short")
            .compute("{}", "Bearer short")
            .getStatusCode()
            .value());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsNormalizedBlank() {
    assertThrows(IllegalArgumentException.class, () -> PracticalInput.normalize("\u00a0"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void parserPreservesDecimalPrecision() {
    var parsed = PracticalInput.parseJson("{\"v\":0.123456789012}");
    assertEquals(new BigDecimal("0.123456789012"), parsed.get("v"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void parserRejectsDuplicateFields() {
    assertThrows(
        IllegalArgumentException.class, () -> PracticalInput.parseJson("{\"v\":1,\"v\":2}"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void decimalResponsesAreLosslessStrings() {
    var safe =
        (Map<?, ?>)
            PracticalInput.jsonSafe(Map.of("amount", new BigDecimal("9007199254740993.01")));
    assertEquals("9007199254740993.01", safe.get("amount"));
  }
}
