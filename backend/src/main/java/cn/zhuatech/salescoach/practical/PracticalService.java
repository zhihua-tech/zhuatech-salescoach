/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.salescoach.practical;

import static cn.zhuatech.salescoach.practical.PracticalInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class PracticalService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "criteria", "turns");
    var turns = rows(r, "turns", 1, 300);
    Set<String> ids = new HashSet<>();
    long seller = 0, buyer = 0;
    for (var t : turns) {
      keys(t, "id", "speaker", "text");
      unique(ids, id(t, "id"));
      String speaker = text(t, "speaker", 10), text = text(t, "text", 2000);
      if (!Set.of("SELLER", "BUYER").contains(speaker))
        throw new IllegalArgumentException("说话人类型错误");
      long count = text.codePoints().count();
      if (speaker.equals("SELLER")) seller += count;
      else buyer += count;
    }
    Set<String> criterionIds = new HashSet<>();
    BigDecimal possible = BigDecimal.ZERO, earned = BigDecimal.ZERO;
    List<Map<String, Object>> out = new ArrayList<>();
    for (var c : rows(r, "criteria", 1, 100)) {
      keys(c, "id", "weight", "phrases");
      String id = id(c, "id");
      unique(criterionIds, id);
      var weight = decimal(c, "weight", 0, 100);
      var phrases =
          strings(c, "phrases", 1, 20).stream().map(PracticalInput::normalize).distinct().toList();
      List<String> evidence = new ArrayList<>();
      for (var t : turns)
        if (t.get("speaker").equals("SELLER")) {
          String text = normalize(t.get("text").toString());
          if (phrases.stream().anyMatch(text::contains)) evidence.add(t.get("id").toString());
        }
      possible = possible.add(weight);
      if (!evidence.isEmpty()) earned = earned.add(weight);
      out.add(
          Map.of(
              "id",
              id,
              "weight",
              weight,
              "covered",
              !evidence.isEmpty(),
              "evidenceTurns",
              evidence));
    }
    long count = seller + buyer;
    return Map.of(
        "criteria",
        out,
        "scoreDefined",
        possible.signum() > 0,
        "scorePercent",
        possible.signum() == 0
            ? BigDecimal.ZERO
            : earned.multiply(new BigDecimal("100")).divide(possible, 6, RoundingMode.HALF_UP),
        "buyerTextSharePercent",
        count == 0 ? BigDecimal.ZERO : round(buyer * 100.0 / count),
        "employeeEvaluationApproved",
        false);
  }
}
