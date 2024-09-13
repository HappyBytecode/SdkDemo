package anda.travel.driver.module.main.mine.help.feedback.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.help.feedback.FeedbackActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = FeedbackModule.class)
public interface FeedbackComponent {
    void inject(FeedbackActivity activity);
}
