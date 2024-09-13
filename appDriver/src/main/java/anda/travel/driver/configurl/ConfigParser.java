package anda.travel.driver.configurl;

import java.io.InputStream;

interface ConfigParser {
    /**
     * 解析输入流 得到MyConfig对象集合
     *
     * @param is
     * @return
     * @throws Exception
     */
    MyConfig parse(InputStream is) throws Exception;

    /**
     * 序列化MyConfig对象集合 得到XML形式的字符串
     *
     * @param myConfig
     * @return
     * @throws Exception
     */
    String serialize(MyConfig myConfig) throws Exception;
}
