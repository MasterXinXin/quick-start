package cn.zealon.bean;


/**
 * @auther: Zealon
 * @Date: 2018-10-16 16:12
 */

public class BizService {

    public BizService(){
        System.out.println("ִ�� BizService() ���췽��.");
    }

    public void ini(){
        System.out.println("init");
    }

    public String sayHello(String words){
        String str = "you say:"+words;
        System.out.println(str);
        return str;
    }
}
