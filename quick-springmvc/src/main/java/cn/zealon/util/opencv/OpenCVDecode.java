package cn.zealon.util.opencv;

import cn.zealon.util.opencv.wechat.OpenCV;
import cn.zealon.util.opencv.wechat.QRCodeFinder;
import cn.zealon.util.opencv.wechat.WeChatQRCode;
import org.apache.commons.collections.CollectionUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.UUID;

/**
 * @Description: 二维码解析
 * @ClassName: OpenCVDecode
 * @Author: xin.zhou
 * @Date: 2023/12/11 19:54
 * @Version: 1.0
 */
public class OpenCVDecode {
    static {
        try {
            OpenCV.init();
            //初始化 WeChatQRCode
            WeChatQRCode.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> decode(String srcImgPath){
        Mat img = Imgcodecs.imread(srcImgPath);
        //检测并识别二维码 （同一张图片可能有多个二维码）
        List<String> results = WeChatQRCode.detectAndDecode(img);
        if(CollectionUtils.isEmpty(results)){
            List<Point[]> qrCodeAreas = QRCodeFinder.findQRCodePoint(img);
            for(Point[] qrCodeArea : qrCodeAreas){
                //轮廓转换成最小矩形包围盒
                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(qrCodeArea));
                //截取出二维码
                Rect qrRect = rotatedRect.boundingRect();
                Mat qrCodeImg = new Mat(img, qrRect);
                List<String> rectResults = WeChatQRCode.detectAndDecode(qrCodeImg);
                //保存图像
                Imgcodecs.imwrite("C:\\Users\\hly\\Desktop\\QRCODE\\test\\"+ UUID.randomUUID().toString()+".jpg", qrCodeImg);
                if(CollectionUtils.isNotEmpty(rectResults)){
                    return rectResults;
                }
            }
        }
        return results;
    }

    public static void print(String path){
        List<String> results = decode(path);
        if(results != null){
            System.out.println("results:" + results);
        }
    }

    public static void main(String[] args) throws Exception{
        print("C:\\Users\\hly\\Desktop\\QRCODE\\20231207-162726.jpg");
        print("C:\\Users\\hly\\Desktop\\QRCODE\\20231207-162817.jpg");
        print("C:\\Users\\hly\\Desktop\\QRCODE\\20231211-145619.jpg");
        print("C:\\Users\\hly\\Desktop\\QRCODE\\20231211-155732.jpg");
        print("C:\\Users\\hly\\Desktop\\QRCODE\\20231211-155818.jpg");
    }
}
