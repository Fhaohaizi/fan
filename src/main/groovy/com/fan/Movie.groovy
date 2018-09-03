package com.fan

import com.fission.source.httpclient.ApiLibrary
import com.fission.source.mysql.MySqlTest
import com.fission.source.profile.Constant
import net.sf.json.JSONObject
import org.apache.http.client.methods.HttpGet

class Movie extends ApiLibrary {
    static UTF_8 = Constant.UTF_8;

    static void main(String[] args) {
        DEFAULT_CHARSET = GB2312;
        spider(1)
        testOver()
    }

    static void spider(int pa) {
        List<String> page = getPage(pa);
        String[] abc = "http://www.dygang.net/ys/20170620/37704.htm, http://www.dygang.net/ys/20170727/38028.htm, http://www.dygang.net/ys/20170810/38113.htm, http://www.dygang.net/ys/20170703/37769.htm, http://www.dygang.net/ys/20170615/37680.htm, http://www.dygang.net/ys/20170615/37678.htm, http://www.dygang.net/ys/20170727/38027.htm, http://www.dygang.net/ys/20170802/38060.htm, http://www.dygang.net/ys/20170515/37385.htm, http://www.dygang.net/ys/20170725/38001.htm, http://www.dygang.net/ys/20170608/37614.htm, http://www.dygang.net/ys/20170802/38059.htm, http://www.dygang.net/ys/20170629/37742.htm, http://www.dygang.net/ys/20170512/37323.htm, http://www.dygang.net/ys/20170426/37219.htm, http://www.dygang.net/ys/20170727/38026.htm, http://www.dygang.net/ys/20170730/38046.htm, http://www.dygang.net/ys/20170804/38082.htm, http://www.dygang.net/ys/20170714/37848.htm, http://www.dygang.net/ys/20180819/40982.htm, http://www.dygang.net/ys/20180819/40981.htm, http://www.dygang.net/ys/20180818/40980.htm, http://www.dygang.net/ys/20180818/40979.htm, http://www.dygang.net/ys/20180818/40978.htm, http://www.dygang.net/ys/20180818/40977.htm, http://www.dygang.net/ys/20180817/40975.htm, http://www.dygang.net/ys/20180817/40974.htm".split(", ");
        List<String> list = Arrays.asList(abc);
        page.removeAll(list);
        output(page.size());
        Set<String> truelist = new HashSet<>();
        page.each { l -> truelist.add(l) };
        truelist.each { p ->
            try {
                getMovieInfo(p);
                sleep(getRandomInt(3) + 3);
            } catch (Exception e) {
                output(p);
            }
        }
    }

    static void spider(String text) {
        List<String> page = getPage(text);
        Set<String> truelist = new HashSet<>();
        page.each { l -> truelist.add(l) }
        truelist.each { p ->
            try {
                getMovieInfo(p);
                sleep(getRandomInt(3));
            } catch (Exception e) {
                output(p);
            }
        }
    }

    static List<String> getPage(int page) {
        String url = "http://www.dygang.net/ys/index_" + page + ".htm";
        if (page == 1) url = "http://www.dygang.net/ys/";
        output(url);
        HttpGet httpGet = getHttpGet(url);
        JSONObject response = getHttpResponse(httpGet);
        String content = response.getString("content");
        byte[] bytes = content.getBytes(UTF_8);
        String all = new String(bytes, UTF_8);
        List<String> list = regexAll(all, "http://www.dygang.net/ys/\\d+/\\d+.htm");
        return list;
    }

    static List<String> getPage(String page) {
        String content = page;
        byte[] bytes = content.getBytes(UTF_8);
        String all = new String(bytes, UTF_8);
        List<String> list = regexAll(all, "http://www.dygang.net/ys/\\d+/\\d+.htm");
        return list;
    }

    static boolean getMovieInfo(int day, int index) {
//        String url = "http://www.dygang.net/ys/20180819/40981.htm";
        String url = "http://www.dygang.net/ys/" + day + "/" + index + ".htm";
        getMovieInfo(url);
        return true;
    }

    static boolean getMovieInfo(String url) {
        HttpGet httpGet = getHttpGet(url);
        JSONObject response = getHttpResponse(httpGet);
        String s = response.getString("content");
        if (s.contains("您查询的内容不存在，请返回首页重新搜索")) return false;
        byte[] bytes = s.getBytes(UTF_8);
        String all = new String(bytes, UTF_8);
        String name = EMPTY, tname = EMPTY, year = EMPTY, language = EMPTY, date = EMPTY, score = EMPTY, length = EMPTY, author = EMPTY;
        if (all.contains("◎")) {
            int i = all.indexOf("◎");
            int i1 = all.indexOf("<hr");
            String info = s.substring(i, i1);
            name = getInfo(info, "片　　名　");
            tname = getInfo(info, "译　　名　");
            year = getInfo(info, "年　　代　");
            language = getInfo(info, "语　　言　");
            date = getInfo(info, "上映日期　");
            score = getInfo(info, "豆瓣评分　");
            length = getInfo(info, "片　　长　");
            author = getInfo(info, "导　　演　");
        } else {
            name = getInfo(all, "<title>");
            if (name.contains("_")) name = name.substring(0, name.indexOf("_"));
            length = getInfo(all, "片长: ");
            date = getInfo(all, "上映日期: ");
            author = getInfo(all, "导演: ");
            language = getInfo(all, "语言: ");
        }
        List<String> magnets = regexAll(all, "magnet:.+?>");
        List<String> ed2ks = regexAll(all, "ed2k:.+?>");
        if (ed2ks.size() == 0) ed2ks = regexAll(all, "ftp://.+?>");
        if (ed2ks.size() == 0) ed2ks = regexAll(all, "thunder://.+?>");
        List<String> pans = regexAll(all, "http(s)*://pan.baidu.com/.+?</td>");
        String sql = "INSERT INTO movie (name,tname,year,language,date,score,length,author,magnet,ed2k,pan) VALUES(\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\");";
        sql = String.format(sql, name, tname, year, language, date, score, length, author, magnets.toString().replace("\"", EMPTY), ed2ks.toString().replace("\"", EMPTY), pans.toString().replace("\"", EMPTY));
        if (ed2ks.size() != 0) MySqlTest.sendWork(sql);
        output(magnets.toString().length(), ed2ks.toString().length(), pans.toString().length());
        output(sql);
        return true;
    }

    static String getInfo(String text, String start) {
        String value = EMPTY;
        List<String> nameinfo = regexAll(text, start + ".+?<");
        if (nameinfo.size() > 0) value = nameinfo.get(0).replace(start, EMPTY).replace("<", EMPTY);
        return value;
    }

}
