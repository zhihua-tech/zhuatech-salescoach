/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.salescoach.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class WorkspaceService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public RunResult run(RunRequest request) {
        List<String> warnings = new ArrayList<>();
        if (!request.humanReview()) warnings.add("未启用人工复核，结果不能进入正式业务流程");
        if (request.confidenceFloor() < 70) warnings.add("置信度阈值低于建议值 70，需扩大人工抽检范围");
        if (request.context() == null || request.context().isBlank()) warnings.add("缺少补充上下文，本次仅按基础规则处理");

        List<Insight> insights = List.of(
            new Insight("事实", "主要异议为实施风险而非价格", 94),
            new Insight("关注", "客户希望三个月内看到业务效果", 82),
            new Insight("边界", "缺少现有系统接口数量信息", 76)
        );
        List<Action> actions = List.of(
            new Action("先提问确认范围和关键接口", "业务负责人", "今天"),
            new Action("用里程碑解释分阶段价值", "审核人员", "本周"),
            new Action("技术评估后再给出正式排期", "系统管理员", "复核后")
        );
        Map<String, Object> providerPayload = new LinkedHashMap<>();
        providerPayload.put("subject", request.subject());
        providerPayload.put("scenario", request.scenario());
        providerPayload.put("context", request.context());
        providerPayload.put("confidenceFloor", request.confidenceFloor());
        providerPayload.put("provider", "deepseek-compatible");
        providerPayload.put("model", "deepseek-chat");

        String status = request.humanReview() ? "REVIEW_READY" : "HUMAN_REVIEW_REQUIRED";
        return new RunResult(status, "COACH_REVIEW", "客户关注实施周期，建议先确认组织范围与上线目标，再说明分阶段交付方法；避免承诺未经评估的固定工期。", insights, actions,
            List.copyOf(warnings), providerPayload, "LOCAL_DEMO_PIPELINE", OffsetDateTime.now());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record RunRequest(
        @NotBlank String subject,
        @NotBlank String scenario,
        @Min(0) @Max(100) int confidenceFloor,
        boolean humanReview,
        @Size(max = 1200) String context
    ) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Insight(String type, String content, int confidence) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Action(String task, String ownerRole, String dueHint) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record RunResult(String status, String riskLevel, String summary, List<Insight> insights,
                            List<Action> actions, List<String> warnings, Map<String, Object> providerPayload,
                            String executionMode, OffsetDateTime generatedAt) {}
}
