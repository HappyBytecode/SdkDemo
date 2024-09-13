package anda.travel.driver.module.notice.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.notice.NoticeFragment;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = NoticeModule.class)
public interface NoticeComponent {

    void inject(NoticeFragment fragment);

}
