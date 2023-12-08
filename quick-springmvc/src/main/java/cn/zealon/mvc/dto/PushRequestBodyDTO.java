package cn.zealon.mvc.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by hand on 2017/4/7.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PushRequestBodyDTO implements Serializable {
    private String companyOID;
    private String message;
    private String timestamp;
    private String nonce;
    private String signature;
    private String apiCode;
    private String apiVersion;
    private Long tenantId;
    private String pushType;
    private String encryptAlg;
    private UUID dataId;
}
