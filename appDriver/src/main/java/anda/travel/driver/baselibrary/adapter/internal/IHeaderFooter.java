package anda.travel.driver.baselibrary.adapter.internal;

import android.view.View;

interface IHeaderFooter {

    void addHeaderView(View header);

    void addFooterView(View footer);

    boolean removeHeaderView(View view);

    boolean removeFooterView(View view);

    int headerCount();

    int footerCount();

    boolean isHeaderView(int position);

    boolean isFooterView(int position);
}
