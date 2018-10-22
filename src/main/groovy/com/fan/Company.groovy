package com.fan

import com.fantest.httpclient.FanLibrary
import com.fantest.mysql.MySqlTest
import com.fantest.utils.Regex
import net.sf.json.JSONObject

class Company extends FanLibrary {
    static void main(String[] args) {
        for (def i in 715..1060) {
            getPage(i)
//                getInfo("/eportal/ui?pageId=307900&t=toDetail&ZSBH=D311056737")
        }
        testOver()
    }

    static getPage(int page) {
        def url = "http://www.***.gov.cn/eportal/ui?pageId=307900"
        def params = new JSONObject()
        params.put("filter_LIKE_QYMC", EMPTY)
        params.put("filter_LIKE_YYZZZCH", EMPTY)
        params.put("filter_LIKE_ZSBH", EMPTY)
        params.put("filter_LIKE_XXDZ", EMPTY)
        params.put("currentPage", page)
        params.put("pageSize", 15)
        params.put("OrderByField", EMPTY)
        params.put("OrderByDesc", EMPTY)
        def response = getHttpResponse(getHttpPost(url, params))
        def s = response.getString("content")
        def all = Regex.regexAll(s, "<td s.*?浏览")
        for (int i = 1; i < all.size(); i++) {
            def get = all.get(i)
            def regex = Regex.getRegex(get, "href=\".*?\"").replace("amp;", EMPTY)
            getInfo(regex)
            sleep(3)
        }
        return response;
    }

    static getInfo(String url) {
        try {
            url = "http://www.***.gov.cn" + url;
            def response = getHttpResponse(getHttpGet(url))
            def content = response.getString("content")
            def all = Regex.regexAll(content, "<td class=\"label\".*?\n.*\n.*\n.*\n.*\n.*")
            def name = all.get(0).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def adress = all.get(1).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def money = all.get(2).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def sid = all.get(3).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def type = all.get(4).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def man = all.get(5).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def paper = all.get(6).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def level = all.get(7).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def gov = all.get(8).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def time = all.get(9).replaceAll("<.*?>", EMPTY).replaceAll("(\n| )", EMPTY).split("：")[1]
            def start = time.split("~")[0]
            def end = time.split("~")[1]
            String sql = "INSERT INTO company (name,adress,money,sid,type,man,paper,level,gov,start,end) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\");"
            sql = String.format(sql, name, adress, money, sid, type, man, paper, level, gov, start, end)
            output(sql)
            MySqlTest.sendWork(sql)
        }
        catch (Exception e) {
            output(e)
        }
    }
}
