package anda.travel.driver.module.main.mine.help.problem.dagger;

import anda.travel.driver.baselibrary.annotation.FragmentScope;
import anda.travel.driver.common.dagger.AppComponent;
import anda.travel.driver.module.main.mine.help.problem.ProblemActivity;
import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class,
        modules = ProblemModule.class)
public interface ProblemComponent {
    void inject(ProblemActivity activity);
}
