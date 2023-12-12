package cn.zealon.util.opencv.wechat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 定位二维码位置
 * @ClassName: QRCodeFinder
 * @Author: xin.zhou
 * @Date: 2023/12/11 20:00
 * @Version: 1.0
 */
@Slf4j
public class QRCodeFinder {
    /**
     * 二维码宽度
     */
    public static volatile int QRCODE_WIDTH = 220;
    /**
     * 二维码定位轮廓距离中心的大小
     */
    public static volatile int QRCODE_LOCATION_CENTER_WIDTH = 26;

    /**
     * 查找图片中二维码轮廓并给出矩形坐标点
     *
     * @param srcImage
     * @return List<Point>
     * @author xin.zhou
     * @date 2023/12/12 10:28
     */
    public static List<Point[]> findQRCodePoint(@NonNull Mat srcImage){
        //建立灰度图像存储空间
        Mat gray = new Mat(srcImage.rows(), srcImage.cols(), CvType.CV_8UC1);
        //彩色图像灰度化
        Imgproc.cvtColor(srcImage, gray, Imgproc.COLOR_RGB2GRAY);
        //高斯模糊
        Mat gauss = gray.clone();
        Imgproc.GaussianBlur(gray, gauss, new Size(new Point(5, 5)), 0);
        // 函数检测边缘
        Mat canny = gauss.clone();
        Imgproc.Canny(gauss, canny, 100, 200);

        //找到轮廓
        Mat hierarchy = canny.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Integer> ints = new ArrayList<>();
        List<MatOfPoint> points = new ArrayList<MatOfPoint>();
        //从轮廓的拓扑结构信息中得到有5层以上嵌套的轮廓
        for (int i = 0; i < contours.size(); i++) {
            int k = i;
            int c = 0;
            while (hierarchy.get(0, k)[2] != -1) {
                k = (int) hierarchy.get(0, k)[2];
                c = c + 1;
                if (c >= 5) {
                    ints.add(i);
                    points.add(contours.get(i));
                }
            }
        }
        log.debug("找到{}个标志轮廓!", ints.size());

        return convertPoints(points, srcImage);
    }

    /**
     * 根据二维码定位标志轮廓画大致的二维码区域
     *
     * 二维码轮廓A、B、C固定在左上、左下、右上
     * A    C
     * B
     *
     * @param points 定位标志轮廓
     * @param srcImage 原文件
     * @return Point
     * @author xin.zhou
     * @date 2023/12/11 20:14
     */
    private static List<Point[]> convertPoints(List<MatOfPoint> points, @NonNull Mat srcImage) {
        List<Point[]> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(points)){
            return result;
        }
        if(points.size() == 3){
            //3点可以确认矩形
            result.add(convertPointsThree(points));
        }
        if(points.size() == 2){
            //两点可能为AB、AC、BC
            Point first = centerCal(points.get(0));
            Point second = centerCal(points.get(1));
            if(calculatePointDistance(first, second) > 1.5 * QRCODE_WIDTH){
                //如果两点距离测算大于二维码1.5倍，则认为两个点太远了算两个单点
                //根据勾股定理，二维码最长的斜线应该是等腰直角三角形的斜线，即为二维码宽度*根号2(约等于1.41....)，按1.5来算
                result.addAll(convertPointsOne(first, srcImage.width(), srcImage.height()));
                result.addAll(convertPointsOne(second, srcImage.width(), srcImage.height()));
            }else{
                result.add(convertPointsTwo(first, second));
            }
        }
        if(points.size() == 1){
            //只确定一点的情况，需要按默认二维码大小截取3部分图像
            Point onePoint = centerCal(points.get(0));
            result.addAll(convertPointsOne(onePoint, srcImage.width(), srcImage.height()));
        }
        return result;
    }

    /**
     * 计算平面空间直角坐标系中两点的距离
     * A(1,2)  B(4,6)  距离 = 开平方根((4-1)平方 + (6-2)平方) = 5
     * @param first
     * @param second
     * @return int
     * @author xin.zhou
     * @date 2023/12/12 14:23
     */
    public static double calculatePointDistance(@NonNull Point first, @NonNull Point second){
        return Math.sqrt(Math.pow(Math.abs(first.x - second.x), 2) + Math.pow(Math.abs(first.y - second.y), 2));
    }

    /**
     * 取矩阵点图的中心点
     *
     * @param matOfPoint
     * @return Point
     * @author xin.zhou
     * @date 2023/12/11 20:13
     */
    private static Point centerCal(MatOfPoint matOfPoint) {
        double centerx = 0, centery = 0;
        MatOfPoint2f mat2f = new MatOfPoint2f(matOfPoint.toArray());
        RotatedRect rect = Imgproc.minAreaRect(mat2f);
        Point vertices[] = new Point[4];
        rect.points(vertices);
        centerx = ((vertices[0].x + vertices[1].x) / 2 + (vertices[2].x + vertices[3].x) / 2) / 2;
        centery = ((vertices[0].y + vertices[1].y) / 2 + (vertices[2].y + vertices[3].y) / 2) / 2;
        Point point = new Point(centerx, centery);
        return point;
    }

    /**
     * 三个定位轮廓
     *
     * @param points
     * @return
     */
    private static Point[] convertPointsThree(List<MatOfPoint> points) {
        Point[] points1 = points.get(0).toArray();
        Point[] points2 = points.get(1).toArray();
        Point[] points3 = points.get(2).toArray();

        Point[] point = new Point[points1.length + points2.length + points3.length];
        System.arraycopy(points1, 0, point, 0, points1.length);
        System.arraycopy(points2, 0, point, points1.length, points2.length);
        System.arraycopy(points3, 0, point, points1.length + points2.length, points3.length);
        return point;
    }

    /**
     * 两个定位轮廓
     *
     * @return Point
     * @author xin.zhou
     * @date 2023/12/11 20:35
     */
    private static Point[] convertPointsTwo(@NonNull Point first, @NonNull Point second) {
        List<Point> result = new ArrayList<>(3);

        int firstX = (int)first.x;
        int firstY = (int)first.y;
        int secondX = (int)second.x;
        int secondY = (int)second.y;
        int xDiff = Math.abs(firstX - secondX);
        int yDiff = Math.abs(firstY - secondY);
        //误差不能超过20
        if(xDiff == 0 || xDiff < 20){
            //A B 第三点Cx= Ax + Ay - By  Cy = Ay
            Point A = new Point(firstX - QRCODE_LOCATION_CENTER_WIDTH, Math.min(firstY, secondY) - QRCODE_LOCATION_CENTER_WIDTH);
            Point B = new Point(firstX - QRCODE_LOCATION_CENTER_WIDTH, Math.max(firstY, secondY) + QRCODE_LOCATION_CENTER_WIDTH);
            result.add(A);
            result.add(B);
            result.add(new Point(A.x + yDiff + 2 * QRCODE_LOCATION_CENTER_WIDTH, A.y));
        }else if(yDiff == 0 || yDiff < 20){
            //A C 第三点Bx= Ax  By = Ay + Ax - Cx
            Point A = new Point(Math.min(firstX, secondX) - QRCODE_LOCATION_CENTER_WIDTH, firstY - QRCODE_LOCATION_CENTER_WIDTH);
            Point C = new Point(Math.max(firstX, secondX) + QRCODE_LOCATION_CENTER_WIDTH, firstY - QRCODE_LOCATION_CENTER_WIDTH);
            result.add(A);
            result.add(C);
            result.add(new Point(A.x, A.y + xDiff + 2 * QRCODE_LOCATION_CENTER_WIDTH));
        }else{
            //B C 第三点Ax= Bx  Ay = Cy
            Point B = new Point(Math.min(firstX, secondX) - QRCODE_LOCATION_CENTER_WIDTH, Math.max(firstY, secondY) + QRCODE_LOCATION_CENTER_WIDTH);
            Point C = new Point(Math.max(firstX, secondX) + QRCODE_LOCATION_CENTER_WIDTH, Math.min(firstY, secondY) - QRCODE_LOCATION_CENTER_WIDTH);
            result.add(B);
            result.add(C);
            result.add(new Point(B.x, C.y));
        }
        return result.toArray(new Point[3]);
    }

    /**
     * 一个定位轮廓  可能有最多3种框选位置
     *
     * @param onePoint 定位轮廓
     * @param width 原图宽
     * @param height 原图高
     * @return List<Point>
     * @author xin.zhou
     * @date 2023/12/12 9:54
     */
    private static List<Point[]> convertPointsOne(Point onePoint, int width, int height) {
        List<Point[]> result = new ArrayList<>();
        int x = (int)onePoint.x;
        int y = (int)onePoint.y;
        //如果是A点 截取右下方
        int aX = x - QRCODE_LOCATION_CENTER_WIDTH;
        int aY = y - QRCODE_LOCATION_CENTER_WIDTH;
        if(aX + QRCODE_WIDTH <= width && aY + QRCODE_WIDTH <= height){
            Point[] A = new Point[3];
            A[0] = new Point(aX, aY);
            A[1] = new Point(aX, aY + QRCODE_WIDTH);
            A[2] = new Point(aX + QRCODE_WIDTH, aY);
            result.add(A);
        }
        //如果是B点 截取右上方
        int bX = x - QRCODE_LOCATION_CENTER_WIDTH;
        int bY = y + QRCODE_LOCATION_CENTER_WIDTH;
        if(bX + QRCODE_WIDTH <= width && bY - QRCODE_WIDTH >= 0){
            Point[] B = new Point[3];
            B[0] = new Point(bX, bY - QRCODE_WIDTH);
            B[1] = new Point(bX, bY);
            B[2] = new Point(bX + QRCODE_WIDTH, bY - QRCODE_WIDTH);
            result.add(B);
        }
        //如果是C点 截取左下方
        int cX = x + QRCODE_LOCATION_CENTER_WIDTH;
        int cY = y - QRCODE_LOCATION_CENTER_WIDTH;
        if(cX - QRCODE_WIDTH >= 0 && cY + QRCODE_WIDTH <= height){
            Point[] C = new Point[3];
            C[0] = new Point(cX - QRCODE_WIDTH, cY);
            C[1] = new Point(cX - QRCODE_WIDTH, cY + QRCODE_WIDTH);
            C[2] = new Point(cX, cY);
            result.add(C);
        }
        return result;
    }
}
