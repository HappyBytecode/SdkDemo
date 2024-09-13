package anda.travel.driver.api;

import java.util.List;

import anda.travel.driver.data.entity.WithdrawRuleEntity;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @Author moyuwan
 * @Date 18/3/19
 */
public interface RootApi {

    @POST("api/driver/token/driverAccount/wallet/rules")
    Observable<List<WithdrawRuleEntity>> withdrawRule();

    @POST("api/driver/token/driverAccount/bill/rules")
    Observable<List<WithdrawRuleEntity>> billRule();

}
