package anda.travel.driver.baselibrary.adapter;

public abstract class SimpleMulItemViewType<DATA> implements IMulItemViewType<DATA> {

    @Override
    public int getItemViewType(int position, DATA data) {
        return 0;
    }

}
