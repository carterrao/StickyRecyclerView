package com.rzc.stickyrecyclerview.demo;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rzc on 17/9/29.
 */

public class MockData {
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void queryProvinceList(final OnProvinceListResponse listener, final boolean mockSuccess) {
        new Thread() {
            @Override
            public void run() {
                final List<Province> list = new ArrayList<Province>();
                addProvinces(list);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mockSuccess) {
                            listener.onSuccess(list);
                        } else {
                            listener.onFailed();
                        }
                    }
                });
            }
        }.start();
    }

    public interface OnProvinceListResponse {
        void onFailed();
        void onSuccess(List<Province> data);
    }

    private static void addProvinces(List<Province> list) {
        list.add(new Province("北京市", "http://www.gsfcw.com/uploadImages/same/201204/634703641742660380463.jpg", "北京是中国四个直辖市之一", "北京是中国的首都"));
        list.add(new Province("上海市", "http://www.williamlong.info/google/upload/877_2.jpg", "上海是中国四个直辖市之一", "上海在中国省级行政区GPD第一"));
        list.add(new Province("天津市", "", "天津是中国四个直辖市之一", "天津与北京相邻"));
        list.add(new Province("重庆市", "xxx", "重庆是中国四个直辖市之一", "重庆是四大直辖市中面积最大的一个"));
        list.add(new Province("浙江省", "http://youimg1.c-ctrip.com/target/tg/459/262/438/cfb48634f2c44bc28a7bb5f1d4b7cb98.jpg", "浙江在中国东部沿海，与上海相邻", "浙江是中国比较发达的一个省份"));
        list.add(new Province("江苏省", "http://www.hngican.com/UploadFiles/FCK/2014-10/20141024R40042JN44.jpg", "江苏在中国东部沿海，与浙江、上海相邻", "江苏省在全国的GDP排名比较靠前"));
        list.add(new Province("广东省", "http://y2.ifengimg.com/haina/2014_01/dc4c940cd55de4e.jpg", "广东在中国南部沿海，临近香港、澳门", "广东省GDP目前排名全国第一"));
        list.add(new Province("新疆自治区", "http://a0.att.hudong.com/36/12/01300000257324122447125981168.jpg", "新疆位于中国西部，中国土地行政面积最大的一个省级行政区", "新疆是维吾尔族自治区，吐鲁番的葡萄比较有名"));
        list.add(new Province("湖北省", "http://www.qlx123.com/files/2015-9/20150929105216165182.jpg", "湖北省位于中国中部，有九省通衢之称", "湖北省是重要的交通枢纽，全国多条重要的南北-东西向车都经过"));
        list.add(new Province("湖南省", "http://images.quanjing.com/chineseview094/high/36-11097.jpg", "湖南省位于中国中南部，在湖北与广东之间", "湖南电视台比较出名，有比较出名的综艺节目"));
        list.add(new Province("西藏自治区", "http://www.en8848.com.cn/d/file/201311/af5c5a39baf958e1ed3efe05ccf42e04.jpg", "西藏位于中国西南部，是藏族自治区", "西藏是长江发源地，有世界上最高的的喜马拉雅山珠穆朗玛峰"));
        list.add(new Province("内蒙古自治区", "http://img03.sogoucdn.com/app/a/100520093/7fee2bba8ae86905-bb957ec9e220398a-323079f6ac6a61d7891ec91cb50444a8.jpg", "位于中国北方，有很多草原", ""));
        list.add(new Province("黑龙江省", "http://img5q.duitang.com/uploads/item/201412/23/20141223225307_SxUGT.jpeg", "位于中国最北部，属于北寒带气候", ""));
        list.add(new Province("福建省", "http://pic2.ooopic.com/12/21/86/35bOOOPIC42_1024.jpg", "位于中国东南沿海，与台湾隔着台湾海峡", "出名的景点有武夷山和厦门鼓浪屿等"));
        list.add(new Province("四川省", "http://img4.duitang.com/uploads/item/201304/10/20130410133051_2iv5V.jpeg", "位于中国中西部，简称川或蜀", "这里有中国独有的国宝动物大熊猫"));
    }

    public static void queryProvinceCityMap(final OnProvinceCityMapResponse listener, final boolean mockSuccess) {
        new Thread() {
            @Override
            public void run() {
                final Map<Province, List<City>> map = new HashMap<Province, List<City>>();
                final List<Province> provinces = new ArrayList<Province>();
                addProvinces(provinces);
                addBjCity(map, provinces.get(0));
                addShCity(map, provinces.get(1));
                addTjCity(map, provinces.get(2));
                addCqCity(map, provinces.get(3));
                addZjCity(map, provinces.get(4));
                addJsCity(map, provinces.get(5));
                addGdCity(map, provinces.get(6));
                addXjCity(map, provinces.get(7));
                addHbCity(map, provinces.get(8));
                addHnCity(map, provinces.get(9));
                addXzCity(map, provinces.get(10));
                addNmCity(map, provinces.get(11));
                addHljCity(map, provinces.get(12));
                addFjCity(map, provinces.get(13));
                addScCity(map, provinces.get(14));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mockSuccess) {
                            listener.onSuccess(provinces, map);
                        } else {
                            listener.onFailed();
                        }
                    }
                });
            }
        }.start();
    }

    public interface OnProvinceCityMapResponse {
        void onFailed();
        void onSuccess(List<Province> provinces, Map<Province, List<City>> map);
    }

    private static void addBjCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("朝阳区", "朝阳区属于北京", "朝阳区是中心城区"));
        bj.add(new City("海淀区", "海淀区属于北京", "海淀区是中心城区，海淀区有很多大学，包括清华大学、北京大学、北京航空航天大学等"));
        map.put(province, bj);
    }

    private static void addShCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("静安区", "静安区属于上海", "静安区是中心城区"));
        map.put(province, bj);
    }

    private static void addTjCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("和平区", "和平区属于天津", "和平区是中心城区"));
        map.put(province, bj);
    }

    private static void addCqCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("渝中区", "渝中区属于重庆", "渝中区是中心城区"));
        map.put(province, bj);
    }

    private static void addZjCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("杭州市", "杭州属于浙江省", "杭州是浙江省省会"));
        map.put(province, bj);
    }

    private static void addJsCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("南京市", "南京市属于江苏省", "南京是江苏省省会"));
        map.put(province, bj);
    }

    private static void addGdCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("广州市", "广州市属于广东省", "广州市是广东省省会"));
        map.put(province, bj);
    }

    private static void addXjCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("乌鲁木齐", "乌鲁木齐属于新疆", "乌鲁木齐是新疆首府"));
        map.put(province, bj);
    }

    private static void addHbCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("武汉市", "武汉市属于湖北省", "武汉市是湖北省省会"));
        map.put(province, bj);
    }

    private static void addHnCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("长沙市", "长沙市属于湖南省", "长沙市是湖南省省会"));
        map.put(province, bj);
    }

    private static void addXzCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("拉萨", "拉萨属于西藏", "拉萨是西藏首府"));
        map.put(province, bj);
    }

    private static void addNmCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("呼和浩特", "呼和浩特属于内蒙古", "呼和浩特是内蒙古首府"));
        map.put(province, bj);
    }

    private static void addHljCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("哈尔滨", "哈尔滨属于黑龙江省", "哈尔滨是黑龙江省省会"));
        map.put(province, bj);
    }

    private static void addFjCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("福州市", "福州市属于福建省", "福州市是福建省省会"));
        map.put(province, bj);
    }

    private static void addScCity(Map<Province, List<City>> map, Province province) {
        List<City> bj = new ArrayList<City>();
        bj.add(new City("成都市", "成都市属于四川省", "成都市是四川省省会"));
        map.put(province, bj);
    }

    public static void getMoreCityList(final String province, final OnMoreCityResponse listener, final boolean mockSuccess) {
        new Thread() {
            @Override
            public void run() {
                final List<City> list = new ArrayList<City>();
                list.add(new City(province + "测试城市1", "测试城市1第一行内容", "测试城市1第二行内容"));
                list.add(new City(province + "测试城市2", "测试城市2第一行内容", "测试城市2第二行内容"));
                list.add(new City(province + "测试城市3", "测试城市3第一行内容", "测试城市3第二行内容"));
                list.add(new City(province + "测试城市4", "测试城市4第一行内容", "测试城市4第二行内容"));
                list.add(new City(province + "测试城市5", "测试城市5第一行内容", "测试城市5第二行内容"));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mockSuccess) {
                            listener.onSuccess(list);
                        } else {
                            listener.onFailed();
                        }
                    }
                });
            }
        }.start();
    }

    public interface OnMoreCityResponse {
        void onFailed();
        void onSuccess(List<City> moreCity);
    }
}
