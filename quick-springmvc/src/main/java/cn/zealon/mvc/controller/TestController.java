package cn.zealon.mvc.controller;

import cn.zealon.mvc.dto.CallBackBaseDTO;
import cn.zealon.mvc.dto.PushRequestBodyDTO;
import cn.zealon.mvc.dto.PushResponseBodyDTO;
import cn.zealon.mvc.util.MsgCryptUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @Description:
 * @ClassName: TestController
 * @Author: xin.zhou
 * @Date: 2023/7/28 20:03
 * @Version: 1.0
 */
@Slf4j
@RestController
public class TestController {
    @PostMapping("/api/v1/callback")
    public PushResponseBodyDTO saveEmailEvidenceChainHistoryAuditInfo(@RequestBody PushRequestBodyDTO pushRequestBodyDTO) {
        MsgCryptUtil msgCryptUtil = new MsgCryptUtil("123456","SOltpcULU3IglUWt1LWZvVXJlsF0gwWIwCznRE5FjKK", pushRequestBodyDTO.getTenantId().toString());
        String params = msgCryptUtil.decryptMSg(pushRequestBodyDTO.getSignature(),
                pushRequestBodyDTO.getTimestamp(),
                pushRequestBodyDTO.getNonce(),
                pushRequestBodyDTO.getMessage());
        log.info("params : {}",params);
        CallBackBaseDTO callBackBaseDTO = JSONObject.parseObject(params,CallBackBaseDTO.class);
        return PushResponseBodyDTO.builder()
                .code(PushResponseBodyDTO.CustomizedApiConstant.RETURN_CODE_SUCCESS)
                .message(callBackBaseDTO.getTenantId().toString())
                .body(params)
                .build();
    }


    public static void main(String[] args) {
        String imagePath = "C:\\Users\\hly\\Desktop\\test_00.png"; // 替换为实际的图像路径

        File imageFile = new File(imagePath);

        ITesseract tesseract = new Tesseract();

        try {
            String result = tesseract.doOCR(imageFile);
            System.out.println("OCR Result:\n" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
