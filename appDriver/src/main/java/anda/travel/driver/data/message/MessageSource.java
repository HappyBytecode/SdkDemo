package anda.travel.driver.data.message;

import java.util.List;

import anda.travel.driver.data.entity.HxMessageEntity;
import rx.Observable;

public interface MessageSource {

    /**
     * 保存消息
     *
     * @param entity
     */
    Observable<Boolean> saveMessage(HxMessageEntity entity);

    /**
     * 获取未读消息
     *
     * @return
     */
    Observable<List<HxMessageEntity>> getUnreadMessage();

    /**
     * 将消息设置为"已读"
     *
     * @param entity
     */
    Observable<Integer> readMessage(HxMessageEntity entity);

    /**
     * 删除所有消息
     *
     * @return
     */
    Observable<Integer> deleteAllMessage();

    /**
     * 获取所有消息
     *
     * @return
     */
    Observable<List<HxMessageEntity>> getAllMessage();

    /**
     * 获取对应页的消息列表
     *
     * @return
     */
    Observable<List<HxMessageEntity>> getAllMessageByPage(int pageNum, int pageSize);

}
