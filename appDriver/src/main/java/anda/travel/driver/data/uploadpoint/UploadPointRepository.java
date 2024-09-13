package anda.travel.driver.data.uploadpoint;

import android.text.TextUtils;

import org.litepal.crud.DataSupport;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.data.entity.UploadPointEntity;
import anda.travel.driver.socket.message.UploadLocationMessage;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class UploadPointRepository implements UploadPointSource {

    @Inject
    public UploadPointRepository() {

    }

    @Override
    public void insertPoint(UploadLocationMessage message) {
        if (message == null) return;
        String locationUUid = message.getLocationUuid();
        if (TextUtils.isEmpty(locationUUid)) return;

        Observable.concat(Observable.just(locationUUid)
                .map(point -> DataSupport.where("locationUuid = ?", locationUUid).find(UploadPointEntity.class))
                .map(uploadPointEntities -> {
                    if (uploadPointEntities != null && uploadPointEntities.size() > 0) {
                        UploadPointEntity uploadPointEntity = uploadPointEntities.get(0);
                        return uploadPointEntity.delete();
                    } else {
                        return 0;
                    }
                }), Observable.just(message)
                .map(UploadPointEntity::upload2Entity
                )
                .map(point -> {
                    boolean res = point.save();
                    return res ? 1 : 0;
                }))
                .compose(RxUtil.applySchedulers())
                .subscribe(isSave ->
                                Timber.e(MessageFormat.format("(#UploadPointRepository #insertPoint) isSave = {0}", isSave))
                        , ex -> {
                            Timber.e("(#UploadPointRepository #insertPoint) 出现异常！");
                            ex.printStackTrace();
                        }
                );
    }

    /**
     * 网络正常的时候插入数据
     *
     * @param entity
     */
    @Override
    public void insertPoint(UploadPointEntity entity) {
        if (entity == null) return;
        String locationUUid = entity.getLocationUuid();
        if (TextUtils.isEmpty(locationUUid)) return;
        entity.setIsUpload(1); ////////////直接设置为1
        Observable.concat(Observable.just(locationUUid)
                .map(point -> DataSupport.where("locationUuid = ?", locationUUid).find(UploadPointEntity.class))
                .map(uploadPointEntities -> {
                    if (uploadPointEntities != null && uploadPointEntities.size() > 0) {
                        UploadPointEntity uploadPointEntity = uploadPointEntities.get(0);
                        return uploadPointEntity.delete();
                    } else {
                        return 0;
                    }
                }), Observable.just(entity)
                .map(point -> {
                    boolean res = point.save();
                    return res ? 1 : 0;
                }))
                .compose(RxUtil.applySchedulers())
                .subscribe(isSave ->
                                Timber.e(MessageFormat.format("(#UploadPointRepository #insertPoint) isSave = {0}", isSave))
                        , ex -> {
                            Timber.e("(#UploadPointRepository #insertPoint) 出现异常！");
                            ex.printStackTrace();
                        }
                );
    }

    /////查询出该订单下未使用
    @Override
    public List<UploadPointEntity> queryNoUploadPoints(String orderid) {
        if (TextUtils.isEmpty(orderid)) return null;
        List<UploadPointEntity> uploads = new ArrayList<>();
        uploads.addAll(DataSupport.where("orderUuid = ? and isUpload = ?", orderid, "0").find(UploadPointEntity.class));
        return uploads;
    }

    //////更新订单下对应locationuuid的isUpload状态为1
    @Override
    public void updatePoint(String orderid, String locationUuid) {
        if (TextUtils.isEmpty(locationUuid)) return;
        if (TextUtils.isEmpty(orderid)) return;
        Observable.just(orderid)
                .map(o -> DataSupport.where("orderUuid = ? and locationUuid = ?", o, locationUuid).find(UploadPointEntity.class))
                .map(uploadPointEntities -> {
                    if (uploadPointEntities != null && uploadPointEntities.size() > 0) {
                        UploadPointEntity entity = uploadPointEntities.get(0);
                        entity.setIsUpload(1);
                        return entity.save();
                    }
                    return false;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(result -> Timber.e(MessageFormat.format("(#UploadPointRepository #updatePoint){0}", result))
                        , ex -> {
                            ex.printStackTrace();
                            Timber.e("(#UploadPointRepository #updatePoint) 出现异常！");
                        }
                );
    }

    @Override
    public void deleteAllPoints() {
        Observable.just("")
                .map(uuid -> DataSupport.deleteAll(UploadPointEntity.class)) //先删除相同订单编号的索引
                .compose(RxUtil.applySchedulers())
                .subscribe(count -> {
                    Timber.e(MessageFormat.format("(#UploadPointRepository #delete){0}", count)); //打印是否保存成功
                }, ex -> Timber.e("(#UploadPointRepository #delete) 出现异常！"));
    }

    @Override
    public UploadPointEntity getLatelyPoint(String orderid) {
        if (TextUtils.isEmpty(orderid)) return null;
        return DataSupport.where("orderUuid = ?", orderid).findLast(UploadPointEntity.class);
    }

}
