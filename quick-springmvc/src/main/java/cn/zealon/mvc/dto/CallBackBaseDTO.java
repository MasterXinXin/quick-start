package cn.zealon.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回调请求基础类
 *
 * @author xin.zhou
 * @date 2023/7/25 14:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallBackBaseDTO {
    private Long tenantId;

    /**
     *        ("x-helios-client", "helios-wx")
     *        .or()
     *        .header("x-helios-client", "helios-dd")
     *        .or()
     *        .header("x-helios-client", "wechat")
     *        .or()
     *        .header("x-helios-client", "react web")
     *        .or()
     *        .header("x-helios-client","helios-feishu")
     *        .or()
     *        .header("x-helios-client","helios-welink")
     */
    String client;
}
