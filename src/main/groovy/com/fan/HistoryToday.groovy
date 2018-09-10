package com.fan

import com.fission.source.httpclient.ApiLibrary
import com.fission.source.httpclient.FanRequest
import com.fission.source.mysql.MySqlTest
import com.fission.source.utils.Regex
import net.sf.json.JSONObject

class HistoryToday extends ApiLibrary {
    static void main(String[] args) {
        DEFAULT_CHARSET = GBK;

        for (int i in 1..12) {
            for (int j in 1..31) {
                if (i == 2 && (j == 30 || j == 31)) continue
                if ((i in [4, 6, 9, 11]) && j == 31) continue
                def month = i > 9 ? i + EMPTY : "0" + i;
                def day = j > 9 ? j + EMPTY : "0" + j;
                def date = month + "-" + day
                getInfo(date)
            }
        }

        testOver()
    }

    static getInfo(String date) {
        def url = "http://tools.***.com/his/" + date.replace("-", EMPTY) + "_c.js"
        def all = FanRequest.isGet()
                .setUri(url)
                .getResponse()
                .getString("content")
                .substring(8)
                .replace(";", EMPTY)
                .replaceAll("\t", EMPTY)
                .replaceAll("(&nbsp)+", EMPTY)
                .replaceAll("\\t", EMPTY)
                .replace("##", EMPTY)
                .replaceAll(SPACE_1, EMPTY)
        def json = JSONObject.fromObject(all)
        def keys = json.keySet()
        keys.each { key ->
            def s = json.get(key).toString()
            def all1 = Regex.regexAll(s, "\\{\"title.+?\\}")
            for (int i in 0..all1.size() - 1) {
                def info = all1.get(i)
                def inf = JSONObject.fromObject(info.toString())
                def title = inf.getString("title")
                def keyword = inf.getString("keyword")
                def content = inf.getString("content")
                def alt = inf.getString("alt")
                String sql = "INSERT INTO today_histroy (date,title,keyword,content,alt) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\");"
                sql = String.format(sql, key + "-" + date, title, keyword, content.replace("　　", EMPTY), alt)
//                output(sql)
                MySqlTest.sendWork(sql)
            }
        }
    }
}
