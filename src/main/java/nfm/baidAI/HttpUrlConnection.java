package nfm.baidAI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnection {
    public static void main(String[] args) throws Exception {
        //1.得到访问地址
        URL url =new URL("http://www.baidu.com");
        //2.得到网络访问对象  强制转换
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //3.连接
        urlConnection.connect();
        //4.读取响应信息
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String line = "";
        while ((line = bufferedReader.readLine())!=null){
            System.out.println(line);
        }
    }
}
