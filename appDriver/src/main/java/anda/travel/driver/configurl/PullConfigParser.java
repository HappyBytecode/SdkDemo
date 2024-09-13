package anda.travel.driver.configurl;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class PullConfigParser implements ConfigParser {

    @Override
    public MyConfig parse(InputStream is) throws Exception {
        MyConfig myConfig = null;

        XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("config")) {
                        myConfig = new MyConfig();
                    } else if (parser.getName().equals("about")) {
                        eventType = parser.next();
                        myConfig.setAbout(parser.getText());
                    } else if (parser.getName().equals("cancelRule")) {
                        eventType = parser.next();
                        myConfig.setCancelRule(parser.getText());
                    } else if (parser.getName().equals("noneOrder")) {
                        eventType = parser.next();
                        myConfig.setNoneOrder(parser.getText());
                    } else if (parser.getName().equals("priceRules")) {
                        eventType = parser.next();
                        myConfig.setPriceRules(parser.getText());
                    } else if (parser.getName().equals("withdrawRule")) {
                        eventType = parser.next();
                        myConfig.setWithdrawRule(parser.getText());
                    } else if (parser.getName().equals("shareTrips")) {
                        eventType = parser.next();
                        myConfig.setShareTrips(parser.getText());
                    } else if (parser.getName().equals("userAgreement")) {
                        eventType = parser.next();
                        myConfig.setUserAgreement(parser.getText());
                    } else if (parser.getName().equals("taxiPriceRules")) {
                        eventType = parser.next();
                        myConfig.setTaxiPriceRules(parser.getText());
                    } else if (parser.getName().equals("apply")) {
                        eventType = parser.next();
                        myConfig.setApply(parser.getText());
                    } else if (parser.getName().equals("NaviStrategy")) {
                        eventType = parser.next();
                        myConfig.setNaviStrategy(parser.getText());
                    } else if (parser.getName().equals("solution")) {
                        eventType = parser.next();
                        myConfig.setSolution(parser.getText());
                    } else if (parser.getName().equals("wxAppid")) {
                        eventType = parser.next();
                        myConfig.setWxAppid(parser.getText());
                    } else if (parser.getName().equals("pointRate")) {
                        eventType = parser.next();
                        myConfig.setPointRate(parser.getText());
                    } else if (parser.getName().equals("applyIsOpen")) {
                        eventType = parser.next();
                        myConfig.setApplyIsOpen(parser.getText());
                    } else if (parser.getName().equals("driverPrivacyProtocol")) {
                        eventType = parser.next();
                        myConfig.setDriverPrivacyProtocol(parser.getText());
                    } else if (parser.getName().equals("application")) {
                        eventType = parser.next();
                        myConfig.setApplication(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        return myConfig;
    }

    @Override
    public String serialize(MyConfig myConfig) throws Exception {
        return null;
    }

}