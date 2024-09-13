package anda.travel.driver.data.uploadpoint;

import java.util.List;

import anda.travel.driver.data.entity.UploadPointEntity;
import anda.travel.driver.socket.message.UploadLocationMessage;

interface UploadPointSource {

    void insertPoint(UploadLocationMessage message); //插入一个点

    void insertPoint(UploadPointEntity entity);

    List<UploadPointEntity> queryNoUploadPoints(String orderid);  ///////查询表中未上传

    void updatePoint(String orderid, String locationUuid);  /////上传成功后更新数据状态

    void deleteAllPoints(); ////删除数据库的所有点

    UploadPointEntity getLatelyPoint(String orderid);/////////获取最近点

}
