package com.fan

import com.fission.source.httpclient.ApiLibrary
import com.fission.source.httpclient.FanRequest
import com.fission.source.mysql.MySqlTest
import com.fission.source.source.WriteRead
import com.fission.source.utils.Log
import com.fission.source.utils.Regex
import com.fission.source.utils.Save
import net.sf.json.JSONObject

class Weather extends ApiLibrary {
    static def gap = 150
    static def name = "city"

    static void main(String[] args) {

        DEFAULT_CHARSET = GBK;
        def str = WriteRead.readTextByString(LONG_Path + name + ".log")
        def all = Regex.regexAll(str, "\\d{5}")
        Set<String> set = new HashSet<>(all)
        Save.saveStringList(set,"citycode.log")
//        citys.each { city ->
////            getCityAll(city)
//            getCityAll(changeStringToInt(city))
//        }
//                def city = 71321;
//        getMonth(city, 2018, 9)
//        getCityAll(city)
        testOver();
    }

/**
 * 获取城市2011-2018年数据
 * @param cityId
 */
    static getCityAll(int cityId) {
        for (int j in 2011..2018) {
            getCityYear(cityId, j)
        }
    }

/**
 * 获取当年的数据
 * @param cityId
 * @param year
 */
    static getCityYear(int cityId, int year) {
        for (int i in 1..12) {
            if (year == 2018 && i > 9) continue
            try {
                getMonth(cityId, year, i)
                Log.log(name + "s", PART + cityId + PART + year + PART + i)
            } catch (Exception e) {
                Log.log(name + "error", PART + cityId + PART + year + PART + i)
            }
            sleep(gap + getRandomInt(gap))
        }
    }

/**
 * 获取某个城市某一年某一月的数据
 * @param cityId
 * @param year
 * @param month
 */
    static getMonth(int cityId, int year, int month) {
        def yyyymm;
        def uri;
        if (year > 2016) {
            yyyymm = year * 100 + month
            uri = "http://tianqi.***.com/t/wea_history/js/" + yyyymm + "/" + cityId + "_" + yyyymm + ".js"
        } else {
            yyyymm = year + EMPTY + month
            uri = "http://tianqi.***.com/t/wea_history/js/" + cityId + "_" + yyyymm + ".js"
        }
        output(uri)
        def response = FanRequest.isGet()
                .setUri(uri)
                .getResponse()
                .getString("content")
                .substring(16)
                .replace(";", EMPTY)
        def weather = JSONObject.fromObject(response)
        def city = weather.getString("city")
        def array = weather.getJSONArray("tqInfo")
        output(array.size())
        for (int i in 0..array.size() - 1) {
            JSONObject info = array.get(i)
            if (!info.containsKey("ymd")) continue
            def date = info.getString("ymd")
            def low = info.getString("bWendu").replace("℃", EMPTY)
            def high = info.getString("yWendu").replace("℃", EMPTY)
            def wea = info.getString("tianqi")
            def wind = info.getString("fengxiang")
            def fengli = info.getString("fengli")
            def aqi = TEST_ERROR_CODE, aqiInfo = EMPTY, aqiLevel = TEST_ERROR_CODE;
            if (info.containsKey("aqi")) {
                aqi = info.getInt("aqi")
                aqiInfo = info.getString("aqiInfo")
                aqiLevel = info.getInt("aqiLevel")
            }
            String sql = "INSERT INTO weather (city,low,high,date,wind,windsize,weather,aqi,aqilevel,aqiinfo) VALUES (\"%s\",%d,%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,\"%s\");"
            sql = String.format(sql, city, changeStringToInt(low), changeStringToInt(high), date, wind, fengli, wea, aqi, aqiLevel, aqiInfo)
//            output(sql)
            MySqlTest.sendWork(sql)
        }
    }
}