package com.example.pictureocrv1.utils;

import com.example.pictureocrv1.dto.OutputDTO;
import org.apache.poi.util.StringUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdCardOcrUtils {
    private IdCardOcrUtils() {
    }

    public static List<OutputDTO> getStringStringMap(byte[] bytes) {
        try {
            List<Map> resMap = getResult(bytes);

            String text;
            StringBuilder result = new StringBuilder();
            for (Map map : resMap) {
                text = map.get("text").toString().trim().replace(" ", "");
                result.append(text);
            }
            String appendText = result.toString().trim();

            return getDetailInfo(appendText);
        } catch (RestClientException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static List<Map> getResult(byte[] bytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("images", ImageToBase64(bytes));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        Map json = restTemplate.postForEntity("http://127.0.0.1:8868/predict/ocr_system", request, Map.class).getBody();
        List<List<Map>> jsons = (List<List<Map>>) json.get("results");
        if (!jsons.isEmpty()) {
            return jsons.get(0);
        }
        return new ArrayList<>();
    }

    /**
     * 获取身份证姓名
     *
     * @param maps 识别的结果集合
     * @return 姓名
     */
    private static String predictName(List<Map> maps) {
        String name = "";
        for (Map map : maps) {
            String str = map.get("text").toString().trim().replace(" ", "");
            if (str.contains("姓名") || str.contains("名")) {
                String pattern = ".*名[\\u4e00-\\u9fa5]{1,4}";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(str);
                if (m.matches()) {
                    name = str.substring(str.indexOf("名") + 1);
                }
            }
        }
        return name;
    }

    /**
     * 为了防止第一次得到的名字为空，以后是遇到什么情况就解决什么情况就行了
     *
     * @param result panddleOCR扫描得到的结果拼接：
     *               如：姓名韦小宝性别男民族汉出生1654年12月20日住址北京市东城区景山前街4号紫禁城敬事房公民身份证号码11204416541220243X
     * @return
     */
    private static String fullName(String result) {
        String name = "";
        if (result.contains("性") || result.contains("性别")) {
            String str = result.substring(0, result.lastIndexOf("性"));
            String pattern = ".*名[\\u4e00-\\u9fa5]{1,4}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            if (m.matches()) {
                name = str.substring(str.indexOf("名") + 1);
            }
        }
        return name;
    }

    /**
     * 获取民族
     *
     * @param maps 识别的结果集合
     * @return 民族信息
     */
    private static String national(List<Map> maps) {
        String nation = "";
        for (Map map : maps) {
            String str = map.get("text").toString();
            String pattern = ".*民族[\u4e00-\u9fa5]{1,4}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            if (m.matches()) {
                nation = str.substring(str.indexOf("族") + 1);
            }
        }
        return nation;
    }

    /**
     * 获取身份证地址
     *
     * @param maps 识别的结果集合
     * @return 身份证地址信息
     */
    private static String address(List<Map> maps) {
        String address;
        StringBuilder addressJoin = new StringBuilder();
        for (Map map : maps) {
            String str = map.get("text").toString().trim().replace(" ", "");
            if (str.contains("住址") || str.contains("址") || str.contains("省") || str.contains("市") || str.contains("县") || str.contains("街") || str.contains("乡") || str.contains("村") || str.contains("镇") || str.contains("区") || str.contains("城") || str.contains("组") || str.contains("号") || str.contains("幢") || str.contains("室")) {
                addressJoin.append(str);
            }
        }
        String s = addressJoin.toString();
        if (s.contains("省") || s.contains("县") || s.contains("住址") || s.contains("址") || s.contains("公民身份证")) {
            // 通过这里的截取可以知道，即使是名字中有上述的那些字段，也不要紧，因为这个ocr识别是一行一行来的，所以名字的会在地址这两个字
            // 前面，除非是名字中也有地址的”地“或者”址“字，这个还可以使用lastIndexOf()来从后往左找，也可以在一定程度上避免这个。
            // 具体看后面的截图，就知道了
            address = s.substring(s.indexOf("址") + 1, s.indexOf("公民身份证"));
        } else {
            address = s;
        }
        return address;
    }

    /**
     * 获取身份证号
     *
     * @param maps ocr识别的内容列表
     * @return 身份证号码
     */
    private static String cardNumber(List<Map> maps) {
        String cardNumber = "";
        for (Map map : maps) {
            String str = map.get("text").toString().trim().replace(" ", "");
            // 之里注意了，这里的双斜杆，是因为这里是java，\会转义，所以使用双鞋干\\，去掉试一试就知道了
            String pattern = "\\d{17}[\\d|x|X]|\\d{15}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            if (m.matches()) {
                cardNumber = str;
            }
        }
        return cardNumber;
    }

    /**
     * 二代身份证18位
     * 这里之所以这样做，是因为如果直接从里面截取，也可以，但是从打印的内容中，有时候
     * 性别性别男，是在同一行，有些照片是
     * 性
     * 别
     * 男
     * 等，如果单纯是使用字符串的str.contains("男") ==》 然后返回性别男，
     * str.contains("女") ==> 然后返回性别女
     * 这个万姓名中有男字，地址中有男字，等。而这个人的性别是女。这是可能会按照识别顺序
     * 排序之后，识别的是地址的男字，所以这里直接从身份证倒数第二位的奇偶性判断男女更加准确一点
     * 从身份证号码中提取性别
     *
     * @param cardNumber 身份证号码，二代身份证18位
     * @return 性别
     */
    private static String sex(String cardNumber) {
        if (cardNumber.isEmpty()) {
            return "未知";
        }
        // 取倒身份证倒数第二位的数字的奇偶性判断性别，二代身份证18位
        String substring = cardNumber.substring(cardNumber.length() - 2, cardNumber.length() - 1);
        int parseInt = Integer.parseInt(substring);
        if (parseInt % 2 == 0) {
            return "女";
        }
        return "男";
    }

    /**
     * 从身份证中获取出生信息
     *
     * @param cardNumber 二代身份证，18位
     * @return 出生日期
     */
    private static String birthday(String cardNumber) {
        if (cardNumber.isEmpty()) {
            return "未知";
        }
        String date = cardNumber.substring(6, 14);
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return year + "年" + month + "月" + day + "日";
    }

    /**
     * 获取图片的base64位
     *
     * @param data 图片变成byte数组
     * @return 图片的base64为内容
     */
    private static String ImageToBase64(byte[] data) {
        // 直接调用JDK内置的方法
        return Base64.getEncoder().encodeToString(data);
    }


    /**
     * 获取身份证反面信息的签发机关
     *
     * @param maps ocr识别的内容列表
     * @return 身份证反面的签发机关
     */
    private static String qianFaJiGuan(List<Map> maps) {
        String qianFaJiGuan = "";
        for (Map map : maps) {
            String str = map.get("text").toString().trim().replace(" ", "");
            if (str.contains("公安局")) {
                // 为什么要有这一步，是因为，有时候身份证的签发机关（这四个字）和XXX公安局，是在一起并且是同一行的，
                // 如图片比较正的时候，识别得到的结果是：签发机关XXX公安局，
                // 如果图片是歪的，识别到的结果，签发机关和XXX公安局不在用一行的
                // 具体那一张稍微正一点的图片和一张歪一点的图片，debugger，这里看一下就知道了
                if (str.contains("签发机关")) {
                    // String为引用类型
                    str = str.replace("签发机关", "");
                }
                String pattern = ".*公安局";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(str);
                if (m.matches()) {
                    qianFaJiGuan = str;
                }
            }
        }
        return qianFaJiGuan;
    }

    /**
     * 身份证反面有效期识别
     *
     * @param maps ocr识别的内容列表
     * @return 身份证的有效期
     */
    private static String youXiaoQiXian(List<Map> maps) {
        String youXiaoQiXian = "";
        for (Map map : maps) {
            String str = map.get("text").toString().trim().replace(" ", "");
            // 为什么要有这一步，是因为，有时候身份证的有效期限（这四个字）和日期是在一起并且是同一行的，
            // 如图片比较正的时候，识别得到的结果是：效期期限2016.02.01-2026.02.01
            // 如果图片是歪的，识别到的结果，有效期限和日期不在用一行的
            // 具体那一张稍微正一点的图片和一张歪一点的图片，debugger，这里看一下就知道了
            if (str.contains("有效期限")) {
                str = str.replace("有效期限", "");
            }
            String pattern = "\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}-\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            if (m.matches()) {
                youXiaoQiXian = str;
            }
        }
        return youXiaoQiXian;
    }

    public static void main(String[] args) {
        String[] testAll = new String[]{"级建造师受人力资源和社会保障部住房和城乡建设部委托，本证书由四川省人力资源和社会保障厅四川省住房和城乡姓　名：尹航建设厅批准颁发，它表明持证人通过四证件号码：542622199407300216川省统一组织的考试，取得二级建造师性别：男执业资格出生年月：1994年07月专业：市政公用工程力资源和水批准日期：2019年05月26日A★★管理号：201905988510008503专业技术人员资格专业技术人员资格选书专用套人力资源和社会保障厅住房和城乡建设厅",
                "使用有效期：2022年05月10日2024年09月17日中华人民共和国二级建造师注册证书姓名：尹航性别：男出生日期：1994-07-30注册编号：川2512019202101082聘用企业：四川省蜀通建设集团有限责任公司注册专业：市政公用工程（有效期：2021-09-18至2024-09-17)四房和省建住房源城乡建设厅个人签名：王签发日期2021年9月18日请登录“四川建设发布”微信公众号扫一扫查询签名日期：/20..10",
                "建筑施工企业项目负责人安全生产考核合格证书编号：川建安B（2021）1052428姓名：尹航性别：男出生年月：1994年07月30日企业名称：四川省蜀通建设集团有限责任公司职务：项目负责人（项目经理）初次领证日期：2021年04月24日有效期：2023年08月17日至2024年04月23日省建发证机关：彩建设厅发证日期：200年8月中华人民共和国住房和城乡建设部监制",
                "姓名尹航性别男民族汉出生1994年7月30日住址成都市天府新区华阳天府大道南段846号附12号公民身份号码542622199407300216",
                "中华人民共和国居民身份证签发机关成都市公安局天府新区分局有效期限2018.10.23-2028.10.23",
                "普通高等学校毕业证书学生尹航性别男，一九九四年七月三十日生，于二〇一二年九月至二O一七年六月在本校给排水科学与工程专业四年制本科学习，修完教学计划规定的全部课程，成绩合格，准予毕业。康实学校武汉大学校长：印贤二0一七年六月三十日证书编号：104861201705004788中华人民共和国教育部学历证书查询网址：http://www.chsi.com.cn武汉大学监制"};

        for (String s : testAll) {
            getDetailInfo(s);
        }
    }

    public static List<OutputDTO> getDetailInfo(String s) {
        LocalDate todayDate = LocalDate.now();
        List<OutputDTO> outputList = new ArrayList<>();

        List<String> res = dateVerification(s);
        if (res.isEmpty()) {
            outputList.add(new OutputDTO("无需识别", false, false));
            return outputList;
        }

        res.forEach(date -> {
            int[] dateSplit = validDate(date);
            if (dateSplit.length != 6) {
                outputList.add(new OutputDTO("识别出错", true, true));
                return;
            }
            LocalDate startDate = LocalDate.of(dateSplit[0], dateSplit[1], dateSplit[2]);
            LocalDate endDate = LocalDate.of(dateSplit[3], dateSplit[4], dateSplit[5]);

            List<String> resDetailInfo = new ArrayList<>();
            resDetailInfo.add("证书有效期：" + startDate + "至" + endDate);

            long utilDay = todayDate.until(endDate, ChronoUnit.DAYS);
            if (utilDay <= 0) {
                resDetailInfo.add("证书已到期。");
                outputList.add(new OutputDTO(String.join(" ", resDetailInfo), true, true));
            } else {
                resDetailInfo.add("证书有效，剩余有效期：");
                Period period = Period.between(todayDate, endDate);
                if (period.getYears() > 0) {
                    resDetailInfo.add(period.getYears() + "年");
                }
                if (period.getMonths() > 0) {
                    resDetailInfo.add(period.getMonths() + "个月");
                }
                if (period.getDays() > 0) {
                    resDetailInfo.add(period.getDays() + "天");
                }
            }
            outputList.add(new OutputDTO(String.join(" ", resDetailInfo), false, true));
        });

        return outputList;
    }

    private static int[] validDate(String date) {
        String[] dateSplit = date.split(" ");
        if (dateSplit.length == 6) {
            return Arrays.stream(dateSplit).mapToInt(Integer::parseInt).toArray();
        }
        List<String> res = new ArrayList<>();
        for (String s : dateSplit) {
            int strLen = s.length();
            if (strLen == 2) {
                res.add(s);
            } else if (strLen == 4) {
                res.add(s);
            } else if (strLen == 5) {
                res.add(s.substring(0, 4));
                res.add(s.substring(4, 5));
            } else if (strLen == 6) {
                res.add(s.substring(0, 4));
                res.add(s.substring(4, 6));
            }
        }

        return res.stream().mapToInt(Integer::parseInt).toArray();
    }

    public static List<String> dateVerification(String appendText) {
        List<String> resStrList = new ArrayList<>(10);

        String allChar = "(\\u4e00-\\u9fa5|.)";
        String oneDate = "20\\d{2}" + allChar + "\\d{1,2}" + allChar + "\\d{1,2}";
        String regex = oneDate + allChar + "{1,2}" + oneDate;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(appendText);

        while (matcher.find()) {
            resStrList.add(matcher.group().replaceAll("\\D+", " "));
        }

        return resStrList;
    }

}
