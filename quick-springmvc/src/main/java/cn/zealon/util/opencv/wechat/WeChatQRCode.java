package cn.zealon.util.opencv.wechat;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@Slf4j
public final class WeChatQRCode {
    /**
     * 系统临时文件夹位置  user/local/temp/xxxx/
     */
    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");

    private static org.opencv.wechat_qrcode.WeChatQRCode sWeChatQRCode;

    private WeChatQRCode(){
        throw new AssertionError();
    }

    /**
     * 初始化 WeChatQRCode
     */
    public static void init() throws Exception{
        init("/452/models");
    }

    /**
     * 初始化 WeChatQRCode
     * @param modelDirPath WeChatQRCode 相关模型文件所在的文件夹
     * @throws Exception
     */
    public static void init(String modelDirPath) throws Exception{
        //初始化 WeChatQRCode
        initWeChatQRCode(modelDirPath);
    }


    /**
     * 初始化 WeChatQRCode
     * @throws Exception
     */
    private static void initWeChatQRCode(String modelDirPath) throws Exception{
        //WeChatQRCode 相关的模型文件
        String detectPath = modelDirPath + "/detect.prototxt";
        File detect = new File(TEMP_FILE_PATH + "452/detect.prototxt");
        String detectModelPath = modelDirPath + "/detect.caffemodel";
        File detectModel = new File(TEMP_FILE_PATH + "452/detect.caffemodel");
        String resolutionPath = modelDirPath + "/sr.prototxt";
        File resolution = new File(TEMP_FILE_PATH + "452/sr.prototxt");
        String resolutionModelPath = modelDirPath + "/sr.caffemodel";
        File resolutionModel = new File(TEMP_FILE_PATH + "452/sr.caffemodel");
        try{
            if(!detect.exists()){
                FileUtils.copyInputStreamToFile(OpenCV.class.getResourceAsStream(detectPath), detect);
            }
            if(!detectModel.exists()){
                FileUtils.copyInputStreamToFile(OpenCV.class.getResourceAsStream(detectModelPath), detectModel);
            }
            if(!resolution.exists()){
                FileUtils.copyInputStreamToFile(OpenCV.class.getResourceAsStream(resolutionPath), resolution);
            }
            if(!resolutionModel.exists()){
                FileUtils.copyInputStreamToFile(OpenCV.class.getResourceAsStream(resolutionModelPath), resolutionModel);
            }
            //实例化 WeChatQRCode
            sWeChatQRCode = new org.opencv.wechat_qrcode.WeChatQRCode(
                    detect.getAbsolutePath(),
                    detectModel.getAbsolutePath(),
                    resolution.getAbsolutePath(),
                    resolutionModel.getAbsolutePath());
            log.info("Initialization WeChatQRCode.");
        }catch (Exception e){
            log.error("Load failed. error is : {}", e.getMessage());
        }finally {
            System.out.println("delete file");
            FileUtils.deleteQuietly(detect);
            FileUtils.deleteQuietly(detectModel);
            FileUtils.deleteQuietly(resolution);
            FileUtils.deleteQuietly(resolutionModel);
        }
    }


    /**
     * Both detects and decodes QR code.
     * To simplify the usage, there is a only API: detectAndDecode
     *
     * @param filename
     * @return list of decoded string.
     */
    public static List<String> detectAndDecode(String filename){
        return detectAndDecode(Imgcodecs.imread(filename));
    }

    /**
     * Both detects and decodes QR code.
     * To simplify the usage, there is a only API: detectAndDecode
     *
     * @param img supports grayscale or color (BGR) image.
     * empty if not found.
     * @return list of decoded string.
     */
    public static List<String> detectAndDecode(Mat img){
        return sWeChatQRCode.detectAndDecode(img);
    }

    /**
     * Both detects and decodes QR code.
     * To simplify the usage, there is a only API: detectAndDecode
     *
     * @param img supports grayscale or color (BGR) image.
     * @param points optional output array of vertices of the found QR code quadrangle. Will be
     * empty if not found.
     * @return list of decoded string.
     */
    public static List<String> detectAndDecode(Mat img, List<Mat> points){
        return sWeChatQRCode.detectAndDecode(img,points);
    }

}