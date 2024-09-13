package anda.travel.driver.module.main.mine.message;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import anda.travel.driver.R;

class MessagePagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;
    private final boolean isShow;

    public MessagePagerAdapter(FragmentManager fm, Context context, Boolean isShowConversation) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.message_titles);
        isShow = isShowConversation;
    }

    @Override
    public Fragment getItem(int position) {
        if (isShow) {
            switch (position) {
                case 0:
                    return MessageFragment.newInstance();
                default:
                    new Error("错误");
                    break;
            }
            return null;
        } else {
            return MessageFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        if (isShow)
            return titles.length;
        else return 1;
    }
}
