package anda.travel.driver.util;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class AnimationUtil {

    public static void scaleanim(View view, boolean isReverse) {
        float[] enlarge = {0.0F, 1.0F};
        float[] narrow = {1.0F, 0.0F};
        ObjectAnimator scaleX_anim = ObjectAnimator
                .ofFloat(view, "scaleX", isReverse ? narrow : enlarge)
                .setDuration(600);//
        scaleX_anim.setInterpolator(new AccelerateInterpolator());
        scaleX_anim.addUpdateListener(animation -> {
            float cVal = (Float) animation.getAnimatedValue();
            view.setAlpha(cVal);
            view.setScaleX(cVal);
            view.setScaleY(cVal);
        });
        scaleX_anim.start();
    }

    public static ObjectAnimator rotationInfinite(View view) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 359f);//最好是0f到359f，0f和360f的位置是重复的
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(2000);
        rotation.start();
        return rotation;
    }

}
