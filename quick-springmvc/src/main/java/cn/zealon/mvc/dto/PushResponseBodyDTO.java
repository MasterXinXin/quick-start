package cn.zealon.mvc.dto;


import cn.zealon.mvc.util.MsgCryptUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by hand on 2017/4/7.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PushResponseBodyDTO implements Serializable {

    public static class CustomizedApiConstant {
        public static final String RETURN_CODE_SUCCESS = "SUCCESS";
        public static final String RETURN_CODE_ERROR = "ERROR";
        public static final String RETURN_CODE_TIMEOUT = "TIMEOUT";
        public static final String PENDING = "PENDING";
        public static final String SYNC_CALLBACK = "SYNC";
        public static final String ASYNC_CALLBACK = "ASYNC";
    }

    private String code;
    private String body;
    private String message;

    public static void main(String[] args) {
        MsgCryptUtil msgCryptUtil = new MsgCryptUtil("fangzheng","GVFZnSgxyvBfNGY1ECb0QaKZymNPiEbP22aG0Kp0eKy", Long.valueOf(1367036488438124546L).toString());
        String str =msgCryptUtil.decryptMSg("5e87004cd6be04211ae14f465c9befef8237c8d3","1694685899255","jyc4HN","UyFsOH7A28OnuPWPe3v/9tA+WihiW9K25kJliNOdNABmAZI/ebpBKyirHo9No4qyu8z/SLI/y2xLfGs/yN0AXk+y5lkKvzHSAjjuaBNV190NDgiv+Wn0akHRY8z+hv71");
        System.out.println(str);
    }
}
