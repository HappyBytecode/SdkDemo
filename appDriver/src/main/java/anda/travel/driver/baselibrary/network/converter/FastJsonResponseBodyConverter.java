package anda.travel.driver.baselibrary.network.converter;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.Type;

import anda.travel.driver.baselibrary.network.RequestBean;
import anda.travel.driver.baselibrary.network.RequestError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Type type;

    FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String string = value.string();
        Timber.e(string); //打印日志
        RequestBean bean = JSON.parseObject(string, RequestBean.class);
        if (bean.getReturnCode() == 10000) {
            if (type == String.class)
                return (T) bean.getData();
            return JSON.parseObject(bean.getData(), type);
        }
        throw new RequestError(bean);
    }
}
