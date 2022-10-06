package nfm.baidAI;

import com.alibaba.fastjson.JSON;
import nfm.utils.Base64Util;
import nfm.utils.FileUtil;
import nfm.utils.GsonUtils;
import nfm.utils.HttpUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceMatch {
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String match() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {
            // 登录图片
            byte[] bytes1 = FileUtil.readFileByBytes("C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\user.jpg");
            // 对比的图片
            //byte[] bytes2 = FileUtil.readFileByBytes("F:\\Grade\\user\\杨颖.jpg");
            //byte[] bytes2 = ReadPicture.testQuery();
            byte[] bytes2 = FileUtil.readFileByBytes("F:\\Grade\\user\\杨颖.jpg");
            String image1 = Base64Util.encode(bytes1);
            String image2 = Base64Util.encode(bytes2);
            System.out.println(image1);
            System.out.println(image2);
            List<Map<String, Object>> images = new ArrayList<>();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("image", image1);
            map1.put("image_type", "BASE64");
            map1.put("face_type", "LIVE");
            map1.put("quality_control", "LOW");
            map1.put("liveness_control", "NONE");//第一张照片活体检测

            Map<String, Object> map2 = new HashMap<>();
            map2.put("image", image2);
            map2.put("image_type", "BASE64");
            map2.put("face_type", "LIVE");
            map2.put("quality_control", "LOW");
            map2.put("liveness_control", "NONE");//第二张照片活体检测

            images.add(map1);
            images.add(map2);

            String param = GsonUtils.toJson(images);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            //String accessToken = "24.2a129521d4efa52d947fd6ab5dfe058c.2592000.1664957816.282335-26464165";
            String accessToken = AuthService.getAuth();
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            // 调用 checkIsOnePerson 是否同一人
            result = checkIsOnePerson(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据调用百度接口返回的数据分析是否是同一个人
     * 如果方法返回值1,代表是同一人
     * 如果方法返回值0,代表不是同一个人
     * result:{"error_code":0,"error_msg":"SUCCESS","log_id":305486840896443631,"timestamp":1554089644,"cached":0,
     * "result":{"score":92.54801178,"face_list":[{"face_token":"e8040e31f97842b5e3f8cebe6b59d11a"},
     * {"face_token":"732073caf0696b2314d8fb67c31dc87a"}]}}
     *
     * @param result
     * @return
     */
    public static String checkIsOnePerson(String result) {
        try {
            // 1. 将百度接口返回的result 转化成HashMap
            HashMap baiduMap = JSON.parseObject(result, HashMap.class);
            // 2. 如果从baiduMap中获取不到result则代表失败不是同一人,返回 0
            if (baiduMap.get("result") == null) {
                return "0";
            } else {
                // 3. 获取到result
                HashMap resultMap = JSON.parseObject(String.valueOf(baiduMap.get("result")), HashMap.class);
                // 4. 从result中获取score  BigDecimal 类,用于高精度计算
                BigDecimal score = (BigDecimal) resultMap.get("score");
                // 5. 定义对比分值界限为85,如果score大于界限值lineScore则认为是同一人,否则不是同一人
                BigDecimal lineScore = new BigDecimal(85);
                // 6. BigDecimal提供的比较方法 ,原理就是 score - lineScore
                int flag = score.compareTo(lineScore);
                if (flag > 0) {
                    return "同一个人";//1代表是同一个人
                } else {
                    return "不是同一个人";//0代表不是同一人
                }
            }
        } catch (Exception e) {
        }
        return "不是同一个人";//0代表不是同一人
    }

    public static void main(String[] args) {

        System.out.println(FaceMatch.match());
    }
}