package anda.travel.driver.data.message;

import android.text.TextUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.data.user.UserRepository;
import rx.Observable;

/**
 * 功能描述：系统消息管理者
 */
@Singleton
public class MessageRepository implements MessageSource {

    private final UserRepository mUserRepository; //用于获取登录的用户uuid

    @Inject
    public MessageRepository(UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    @Override
    public Observable<Boolean> saveMessage(HxMessageEntity entity) {
        return Observable.just(entity)
                .map(msgEntity -> {
                    List<HxMessageEntity> list = DataSupport
                            .where("uuid = ?", entity.getUuid())
                            .find(HxMessageEntity.class);
                    if (list != null && list.size() > 0) return false; //相同id的消息，说明是同一条，不保存

                    msgEntity.setClientUuid(mUserRepository.getUserUuid()); //设置用户uuid
                    msgEntity.setStatus(HxMessageEntity.ReadStatus.UNREAD); //设置为"未读"
                    return msgEntity.save(); //保存消息，返回保存结果 true或false
                });
    }

    @Override
    public Observable<List<HxMessageEntity>> getUnreadMessage() {
        return Observable.just(DataSupport
                .where("status = ? and clientUuid = ?", String.valueOf(HxMessageEntity.ReadStatus.UNREAD), mUserRepository.getUserUuid()) //查找未读消息
                .order("pushTime desc") //根据"推送时间"降序排列
                .find(HxMessageEntity.class));
    }

    @Override
    public Observable<Integer> readMessage(HxMessageEntity entity) {
        return Observable.just(entity)
                .map(msgEntity -> {
                    msgEntity.setStatus(HxMessageEntity.ReadStatus.READED); //设置为"已读"

                    if (TextUtils.isEmpty(msgEntity.getUuid()))
                        return msgEntity.save() ? 1 : 0; //为处理旧数据，uuid为空的情况，添加该处理

                    return msgEntity.updateAll("uuid = ?", msgEntity.getUuid()); //更新消息，返回"更新的消息数量"
                });
    }

    @Override
    public Observable<Integer> deleteAllMessage() {
        return Observable.just(DataSupport.deleteAll(HxMessageEntity.class, "clientUuid = ?", mUserRepository.getUserUuid())); //删除所有消息，返回"删除的消息数量"
    }

    @Override
    public Observable<List<HxMessageEntity>> getAllMessage() {
        return Observable.just(DataSupport
                .where("clientUuid = ?", mUserRepository.getUserUuid())
                .order("pushTime desc") //根据"推送时间"降序排列
                .find(HxMessageEntity.class)); //查找所有消息
    }

    @Override
    public Observable<List<HxMessageEntity>> getAllMessageByPage(int pageNum, int pageSize) {
        return Observable.just(DataSupport
                .where("clientUuid = ?", mUserRepository.getUserUuid())
                .order("pushTime desc") //根据"推送时间"降序排列
                .limit(pageSize)
                .offset(pageSize * pageNum)
                .find(HxMessageEntity.class)); //查找所有消息
    }

}
