/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.salescoach.practical;

import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@RestController
@RequestMapping("/api/practical")
public class PracticalController {
  private final PracticalService service;
  private final String configuredToken;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public PracticalController(
      PracticalService service,
      @org.springframework.beans.factory.annotation.Value(
              "${app.practical-token:${ZHUATECH_PRACTICAL_TOKEN:}}")
          String token) {
    this.service = service;
    this.configuredToken = token;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private int access(String authorization) {
    if (configuredToken == null || !configuredToken.matches("[!-~]{32,256}")) return 503;
    if (authorization == null || !authorization.startsWith("Bearer ")) return 401;
    byte[] supplied = authorization.substring(7).getBytes(java.nio.charset.StandardCharsets.UTF_8),
        expected = configuredToken.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    return java.security.MessageDigest.isEqual(expected, supplied) ? 200 : 401;
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @PostMapping("/evidence-based-conversation-scorecard")
  public ResponseEntity<Map<String, Object>> compute(
      @RequestBody String raw,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    int status = access(authorization);
    if (status != 200)
      return ResponseEntity.status(status)
          .body(
              Map.of(
                  "success",
                  false,
                  "message",
                  status == 503 ? "专项接口未启用，请在服务端配置独立访问令牌" : "访问令牌缺失或无效"));
    try {
      return ResponseEntity.ok(
          Map.of(
              "success",
              true,
              "message",
              "处理完成；未变更业务记录",
              "data",
              PracticalInput.jsonSafe(service.execute(PracticalInput.parseJson(raw)))));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
    }
  }
}
