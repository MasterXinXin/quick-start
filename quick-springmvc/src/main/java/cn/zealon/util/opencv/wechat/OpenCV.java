package cn.zealon.util.opencv.wechat;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class OpenCV {
    /**
     * 系统临时文件夹位置  user/local/temp/xxxx/
     */
    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");

    private OpenCV(){
        throw new AssertionError();
    }

    /**
     * 初始化 OpenCV
     */
    public static void init() throws Exception{
        init("quick-springmvc/lib");
    }

    /**
     * 初始化 OpenCV
     * @param libDirPath opencv_java*.dll 或 opencv_java*.so 所在文件夹
     */
    public static void init(String libDirPath) throws Exception{
        initOpenCV(libDirPath);
    }

    /**
     * 初始化 OpenCV
     * @param libDirPath opencv_java*.dll 或 opencv_java*.so 所在文件夹
     */
    private static void initOpenCV(String libDirPath) throws Exception {
        String path;
        File openCVFile = null;
        try{
            path = "/452/lib/opencv_java452.dll";
            openCVFile = new File(TEMP_FILE_PATH + "452/opencv_java452.dll");
            if(!openCVFile.exists()){
                FileUtils.copyInputStreamToFile(OpenCV.class.getResourceAsStream(path), openCVFile);
            }
            System.load(openCVFile.getAbsolutePath());

            log.info("Load success. path is : {}", openCVFile.getAbsolutePath());
        }catch (Exception e){
            log.error("Load failed. path is : {} . error is : {}", openCVFile != null ? openCVFile.getAbsolutePath() : "", e.getMessage());
        }finally {
            System.out.println("delete file");
            FileUtils.deleteQuietly(openCVFile);
        }
    }
}
