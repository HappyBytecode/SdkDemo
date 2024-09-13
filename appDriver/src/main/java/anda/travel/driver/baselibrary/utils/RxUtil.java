package anda.travel.driver.baselibrary.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxJava 工具类
 */
public final class RxUtil {
    private static Observable.Transformer<Object, Object> sApplyTransformer =
            observable -> observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    private static Observable.Transformer<Object, Object> sSaveTransformer =
            observable -> observable.observeOn(Schedulers.io());

    /**
     * 在 IO 线程执行数据操作，UI线程执行订阅操作
     * <p>
     * 效果等同于：
     * <p>
     * Observable.just().subscribeOn(Schedules.io()).observableOn(AndroidSchedulers.mainThread()).subscribe();
     * <p>
     * 使用方式：
     * <p>
     * Observable.just().compose(RxUtils.applySchedulers()).subscribe();
     *
     * @return
     */
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) sApplyTransformer;
    }

    /**
     * 在 IO 线程执行订阅操作
     * <p>
     * 效果等同于：
     * <p>
     * Observable.just().observableOn(Schedules.io()).subscribe();
     * <p>
     * 使用方式：
     * <p>
     * Observable.just().compose(RxUtils.saveSchedulers()).subscribe();
     */
    public static <T> Observable.Transformer<T, T> saveSchedulers() {
        return (Observable.Transformer<T, T>) sSaveTransformer;
    }

    /**
     * @return Schedulers.io();
     */
    public static Scheduler ioThread() {
        return Schedulers.io();
    }

    /**
     * @return AndroidSchedulers.mainThread();
     */
    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    /**
     * 文件下载转换工具，如果文件不存在，会创建文件；已存在，则被替换
     * <p>
     * 定义 retrofit 接口<br>
     * (@)Streaming (@)GET("https://github.com/binwin20/my.apk")<br>
     * Observable&lt;ResponseBody&gt; downloadFile();
     * <p>
     * 使用方式<br>
     * Api.downloadFile()<br>
     * .flatMap(RxUtils.downloadFlapMap(file))<br>
     * .compose(RxUtils.applySchedulers())<br>
     * .subscribe(progress -> showProgress(progress));
     *
     * @param saveFile 保存文件的地址
     * @return
     */
    public static Func1<? super ResponseBody, ? extends Observable<Integer>> downloadFlapMap(File saveFile) {
        return responseBody -> Observable.create((Observable.OnSubscribe<Integer>) subscriber ->
                writeResponseBodyToDisk(responseBody, saveFile, subscriber))
                .compose(RxUtil.applySchedulers());  // 必须加上这个，否则报错 rx.exceptions.MissingBackpressureException
    }

    private static void writeResponseBodyToDisk(ResponseBody body, File file, Subscriber<? super Integer> subscriber) {
        Throwable throwable = null;
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            long lastTime = System.currentTimeMillis();

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    if (System.currentTimeMillis() - lastTime > 200) {
                        lastTime = System.currentTimeMillis();
                        subscriber.onNext((int) (fileSizeDownloaded * 100 / fileSize));
                    }
                }
                outputStream.flush();
            } catch (IOException e) {
                throwable = e;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            throwable = e;
        } finally {
            if (throwable == null) {
                subscriber.onCompleted();
            } else {
                subscriber.onError(throwable);
            }
        }
    }
}
