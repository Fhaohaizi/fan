package com.fan

import com.fission.source.httpclient.ApiLibrary
import com.fission.source.mysql.MySqlTest
import com.fission.source.utils.DecodeEncode
import net.sf.json.JSONObject
import org.apache.http.client.methods.HttpPost

class GuDan extends ApiLibrary {
    static int uid1 = 84640, tid1 = 262744;
    static String uname = DecodeEncode.urlDecoderText("%e9%98%bf%e9%83%a8%e9%ab%98%e5%92%8c%e2%98%83%ef%b8%8f%e2%98%83%ef%b8%8f%e2%98%83%ef%b8%8f"),
                  rcontent = "贱人就是矫情", device = "SM-C7010";
    static int uid2 = 10214, tuid2 = 65541, tid2 = 262737;

    static int rid = 1084600, tuid3 = 26440, tid3 = 262496, uid3 = 10214;
    static String tname = "", text = "", uanme3 = DecodeEncode.urlDecoderText("%e9%98%bf%e9%83%a8%e9%ab%98%e5%92%8c%e2%98%83%ef%b8%8f%e2%98%83%ef%b8%8f%e2%98%83%ef%b8%8f"), device3 = "SM-C7010";

    static int tuid4 = 10214;
    static String uname4 = uname, tname4 = "重发道歉贴", tcontent4 = "本人郑重想个人资料道歉，求原谅！", device4 = "SM-C7010";

    static void main(String[] args) {
//        for (int i in [0..20]) {
////            up();
//            down();
//            sleep(200);
//        }

//		for (int i = 0; i < 50; i++) {
//			try {
//				tid2 = getRandomInt(1000) + 261377;
//				tuid2 = getUser(tid2);
//				logger.info(tid2 + SPACE_4 + tuid2);
//		reply1();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			sleep(10);
//		}
//		reply2();
//		int acb = 80100;
//		for (int i = 0; i < 100; i++) {
//			getHide(78199);
//			getHide(acb--);
//		}
//		send();

        testOver()

    }

    static void getHide(int id) {
        int i = 1;
        while (get(id, i++)) {
            sleep(1);
        }
    }

    static boolean get(int id, int page) {
        String url = "http://1.lonelymind.sinaapp.com/select";
        JSONObject params = new JSONObject();
        params.put("uid", id);
        params.put("op", "myuser");
        params.put("page", page);
        HttpPost httpPost = getHttpPost(url, params);
        JSONObject response = getHttpResponse(httpPost);
//		output(response);
        if (response.toString().length() < 150) return false;
        String content = response.getString("content").replace("\n", EMPTY).replace("\r", EMPTY);
        List<String> list = regexAll(content, "<tname>.*?</tname>");
        List<String> list1 = regexAll(content, "<tcontent>.*?</tcontent>");
        List<String> list2 = regexAll(content, "<tid>.*?</tid>");
        List<String> list3 = regexAll(content, "<kind>.*?</kind>");
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).replaceAll("</*tname>", EMPTY);
            String tid = list2.get(i).replaceAll("</*tid>", EMPTY);
            String text = list1.get(i).replaceAll("</*tcontent>", EMPTY);
            String kind = list3.get(i).replaceAll("</*kind>", EMPTY);
            if (kind.equals("-1"))
                output(tid + TAB + name + TAB + text);
        }
        return true;
    }

    static void spider(int id) {
        JSONObject params = new JSONObject();
        params.put("uid", id);
        HttpPost httpPost = getHttpPost("http://1.lonelymind.sinaapp.com/userSelect", params);
        JSONObject response = getHttpResponse(httpPost);
        output(response);
        String content = response.getString("content").replace("\n", EMPTY).replace("\r", EMPTY);
        if (content == null || content.equals("-1") || !content.startsWith("<?xml")) return;
        String uid = regexAll(content, "<uid>\\d+</uid>").get(0);
        String name = regexAll(content, "<uname>.*</uname>").get(0);
        String flower = regexAll(content, "<flower>.+</flower>").get(0);
        String cai = regexAll(content, "<niufun>.*</niufun>").get(0);
        List<String> list = regexAll(content, "<qianming>.*</qianming>");
        String desc = regexAll(content, "<qianming>.*</qianming>").get(0);
        flower = flower.replace("<flower>", EMPTY).replace("</flower>", EMPTY);
        cai = cai.replace("<niufun>", EMPTY).replace("</niufun>", EMPTY);
        uid = uid.replace("<uid>", EMPTY).replace("</uid>", EMPTY);
        name = name.replace("<uname>", EMPTY).replace("</uname>", EMPTY);
        desc = desc.replace("<qianming>", EMPTY).replace("</qianming>", EMPTY);
        String sql = String.format("INSERT INTO spider_gudan (uid,uname,flower,tread,sign)VALUES(%s,\"%s\",%s,%s,\"%s\");", uid, name, flower, cai, desc);
        MySqlTest.sendWork(sql);
    }


    static void up() {
        String url = "http://1.lonelymind.sinaapp.com/titleUp";
        JSONObject params = new JSONObject();
        params.put("uid", uid1);
        params.put("tid", tid1);
        JSONObject response = getHttpResponse(getHttpPost(url, params));
        output(response);
    }

    static void down() {
        String url = "http://1.lonelymind.sinaapp.com/titleDown";
        JSONObject params = new JSONObject();
        params.put("uid", uid1);
        params.put("tid", tid1);
        JSONObject response = getHttpResponse(getHttpPost(url, params));
        output(response);
    }

    /**
     * 回复帖子
     */
    static void reply1() {
        String url = "http://1.lonelymind.sinaapp.com/replyInsert";
        JSONObject params = new JSONObject();
        params.put("uname", uname);
        params.put("rcontent", rcontent);
        params.put("uid", uid2);
        params.put("device", device);
        params.put("tuid", tuid2);
        params.put("tid", tid2);
        JSONObject response = getHttpResponse(getHttpPost(url, params));
        output(response);
    }

    /**
     * 回复留言
     */
    static void reply2() {
        String url = "http://1.lonelymind.sinaapp.com/freplyInsert";
        JSONObject params = new JSONObject();
        params.put("rid", rid);
        params.put("uid", uid3);
        params.put("uname", uanme3);
        params.put("tname", tname);
        params.put("tid", tid3);
        params.put("text", text);
        params.put("tuid", tuid3);
        params.put("device", device3);
        HttpPost httpPost = getHttpPost(url, params);
        JSONObject response = getHttpResponse(httpPost);
        output(response);
    }

    static void send() {
        String url = "http://1.lonelymind.sinaapp.com/titleInsert";
        JSONObject params = new JSONObject();
        params.put("uname", uname4);
        params.put("kind", -1);
        params.put("tname", tname4);
        params.put("tuid", tuid4);
        params.put("tcontent", tcontent4);
        params.put("device", device4);
        HttpPost httpPost = getHttpPost(url, params);
        JSONObject response = getHttpResponse(httpPost);
        output(response);
    }

    static int getUser(int tid) {
        String url = "http://www.gudanxinshi.com/blog/" + tid + "/1";
        JSONObject response = getHttpResponse(getHttpGet(url));
        String content = response.toString();
        List<String> list = regexAll(content, "uid=\\d+");
        int id = changeStringToInt(list.get(0).substring(4));
        return id;
    }
}
