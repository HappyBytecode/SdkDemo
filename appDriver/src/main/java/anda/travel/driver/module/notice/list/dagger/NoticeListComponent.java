package anda.travel.driver.module.notice.list.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.notice.list.NoticeListActivity;
import dagger.Component;

/**
 * 功能描述：
 */
@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = NoticeListModule.class)
public interface NoticeListComponent {

    void inject(NoticeListActivity activity);

}
