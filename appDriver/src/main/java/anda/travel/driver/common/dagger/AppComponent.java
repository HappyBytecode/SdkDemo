package anda.travel.driver.common.dagger;

import android.app.Application;

import javax.inject.Singleton;

import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.client.NettyClient;
import anda.travel.driver.configurl.ParseUtils;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.message.MessageRepository;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.uploadpoint.UploadPointRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.amap.AMapFragment;
import anda.travel.driver.module.amap.navi.SingleNavigateActivity;
import anda.travel.driver.module.amap.navi.SingleRouteCalculateActivity;
import anda.travel.driver.module.launch.LaunchActivity;
import anda.travel.driver.module.login.LoginActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.main.mine.message.MessageActivity;
import anda.travel.driver.module.main.mine.setting.about.AboutActivity;
import anda.travel.driver.module.report.ReportActivity;
import anda.travel.driver.service.RecordingService;
import anda.travel.driver.socket.SocketService;
import dagger.Component;

/**
 * 功能描述：
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    UserRepository userRepository();

    DutyRepository dutyRepository();

    OrderRepository orderRepository();

    MessageRepository messageRepository();

    DispatchRepository dispatchRepository();

    UploadPointRepository uploadPointRepository();

    void inject(Application application);

    void inject(HxClientManager hxClientManager);

    void inject(LaunchActivity launchActivity);

    void inject(LoginActivity loginActivity);

    void inject(MainActivity mainActivity);

    void inject(SocketService socketService);

    void inject(ParseUtils parseUtils);

    void inject(AboutActivity activity);

    void inject(AMapFragment fragment);

    void inject(SingleRouteCalculateActivity activity);

    void inject(SingleNavigateActivity activity);

    void inject(ReportActivity activity);

    void inject(NettyClient client);

    void inject(MessageActivity messageActivity);

    void inject(RecordingService recordingService);

}

