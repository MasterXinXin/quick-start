package cn.zealon.mvc.util;

import cn.zealon.mvc.exception.AesException;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Ray Ma on 2017/4/6.
 */
public class MsgCryptUtil {
    static Charset CHARSET = Charset.forName("utf-8");
    byte[] aesKey;
    String token ;
    String companyId;


    public MsgCryptUtil(String token, String encodingAesKey, String companyId) throws AesException {
        if (encodingAesKey.length() != 22 && encodingAesKey.length() != 43) {
            throw new AesException(AesException.IllegalAesKey);
        }
        this.token = token ;
        this.companyId = companyId ;
        aesKey = Base64.decodeBase64(encodingAesKey+"=");
    }


    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }



    /**
     * 对密文进行解密.
     * @param  signature 证书
     * @param timestamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    public String decryptMSg(String signature,String timestamp,String nonce,String postData) throws AesException {
        byte[] original;
        // 验证安全签名
        String msgSignature = SHA1.getSHA1(token, timestamp, nonce, postData);
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);
            byte[] encrypted = Base64.decodeBase64(postData);
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.DecryptAESError);
        }
        String decryptMsg, _companyId;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 分离16位随机字符串,网络字节序和companyId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = recoverNetworkBytesOrder(networkOrder);
            decryptMsg = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            _companyId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
                CHARSET);
        } catch (Exception e) {
            throw new AesException(AesException.IllegalBuffer);
        }
        // 判断companyId是否正确
        if (!_companyId.equals(companyId)) {
            throw new AesException(AesException.ValidateCompanyIdError);
        }
        return decryptMsg;
    }



}
