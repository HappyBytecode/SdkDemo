package anda.travel.driver.widget.select;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.module.vo.TimeVO;
import anda.travel.driver.baselibrary.view.wheel.adapter.ArrayWheelAdapter;
import anda.travel.driver.baselibrary.view.wheel.hh.WheelView;

/**
 * 功能描述：选择"开始/结束时间"
 */
public class TimeDialog implements View.OnClickListener {

    /* ***** 说明：0-只显示今天、1-显示到明天、2-显示到后天、以此类推 ***** */
    private final static int dayAfterToday = 2;

    private Context mContext;
    private AlertDialog mDialog;
    private final SelectListener mListener;
    private final List<DayVO> dayList = new ArrayList<>();
    private TimeVO mSelectTime; //选中的时间
    private TimeVO mStartTime; //开始时间（如果为null，默认开始时间为当前时间）
    private int mDayIndex;
    private int mHourIndex;

    private WheelView wheel1;
    private WheelView wheel2;

    public TimeDialog(Context context, SelectListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    /**
     * 设置选中的时间
     *
     * @param selectTime
     * @return
     */
    public TimeDialog setSelectTime(TimeVO selectTime) {
        mSelectTime = selectTime;
        return this;
    }

    /**
     * 设置开始时间（mIsEnd = true是，才需要使用）
     *
     * @param startTime
     * @return
     */
    public TimeDialog setStartTime(TimeVO startTime) {
        mStartTime = startTime;
        return this;
    }

    public TimeDialog builder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.WheelDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.hxyc_layout_timedialog, null);
        initView(view); //初始化UI
        builder.setView(view); //设置View
        mDialog = builder.create();
        return this;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_sure).setOnClickListener(this);
        wheel1 = view.findViewById(R.id.wheel1);
        wheel2 = view.findViewById(R.id.wheel2);

        String[] dayArray = getDayArray();
        wheel1.setViewAdapter(new ArrayWheelAdapter<>(mContext, dayArray));
        mDayIndex = mSelectTime == null ? 0 : getSelectDayIndex(mSelectTime);
        wheel1.setCurrentItem(mDayIndex);

        String[] hourArray = mDayIndex < dayList.size() ? dayList.get(mDayIndex).getHourArray() : getDefaultDisplay();
        wheel2.setViewAdapter(new ArrayWheelAdapter<>(mContext, hourArray));
        mHourIndex = mSelectTime == null ? 0 : getSelectHourIndex(mSelectTime.hour);
        wheel2.setCurrentItem(mHourIndex);

        /* ***** 监听选择器的改变 ***** */
        wheel1.addChangingListener((wheel, oldValue, newValue) -> {
            mDayIndex = newValue;
            if ((oldValue == 0 && newValue > 0) || (oldValue > 0 && newValue == 0)) {
                mHourIndex = getNewIndex(oldValue, newValue); //获取新的下标
                wheel2.setViewAdapter(new ArrayWheelAdapter<>(mContext, dayList.get(mDayIndex).getHourArray()));
                wheel2.setCurrentItem(mHourIndex);
            }
        });
        wheel2.addChangingListener((wheel, oldValue, newValue) -> {
            mHourIndex = newValue;
        });
    }

    //initView中使用
    private int getSelectDayIndex(TimeVO selectTime) {
        if (dayList.isEmpty()) return 0;

        for (int i = 0; i < dayList.size(); i++) {
            DayVO vo = dayList.get(i);
            if ((selectTime.year == vo.year)
                    && (selectTime.month == vo.month)
                    && (selectTime.day == vo.day)) {
                return i;
            }
        }
        return 0;
    }

    //initView中使用
    private int getSelectHourIndex(int number) {
        DayVO vo = dayList.get(mDayIndex); // 获取选择的日期
        int index = number - vo.getStartHour(); // 获取number在数组中的index
        if (index < 0 || index >= vo.getHourArray().length) index = 0; // 异常情况，index都设置为0
        return index;
    }


    /**
     * 获取新的下标（wheel2使用）
     *
     * @param oldValue
     * @param newValue
     * @return
     */
    private int getNewIndex(int oldValue, int newValue) {
        try {
            String strNumber = dayList.get(oldValue).getHourArray()[mHourIndex];
            int number = getIntValue(strNumber); //选中的数值
            int index = number - dayList.get(newValue).getStartHour(); // 获取number在新数组中的index
            if (index < 0) index = 0;
            return index;
        } catch (Exception e) {
            return 0;
        }
    }

    private String[] getDayArray() {
        dayList.clear();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dayAfterToday); // 当前时间往后顺延2天
        DayVO maxDay = DayVO.createFrom(cal); // 显示的最大日期（重要，不能错）

        long currentTimeStamp = System.currentTimeMillis(); //当前时间
        long startTimeStamp = mStartTime != null ? mStartTime.timeStmap : currentTimeStamp; //开始时间
        cal.setTimeInMillis(startTimeStamp); // 从开始时间算起

        DayVO startDay = DayVO.createFrom(cal);
        if (startDay.isMatchCondition(maxDay)) {
            //第一项就不符合条件，则返回默认显示
            return getDefaultDisplay();
        }

        int startHour = cal.get(Calendar.HOUR_OF_DAY) + 1; //第一项对应的startHour
        if (startHour < 24) {
            startDay.setArray(startHour, getHourArray(startHour));
            dayList.add(startDay);
        }
        String[] fullHourArray = getFullHourArray();
        for (int i = 0; i < dayAfterToday; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1); //往后顺延1天
            DayVO vo = DayVO.createFrom(cal);
            if (vo.isMatchCondition(maxDay)) break; //不符合条件，跳出循环
            vo.setArray(0, fullHourArray); //重要！
            dayList.add(vo);
        }
        if (dayList.size() == 0) return getDefaultDisplay(); //没有符合条件的日期，返回默认显示

        String[] dayArray = new String[dayList.size()];
        for (int i = 0; i < dayList.size(); i++) {
            dayArray[i] = dayList.get(i).getStrDisplay();
        }
        return dayArray;
    }

    /**
     * 获取对应的"小时"列表（仅第一项DayVO需要使用）
     *
     * @param startHour
     * @return
     */
    private String[] getHourArray(int startHour) {
        if (startHour >= 24) return null;
        if (startHour < 0) startHour = 0;

        String[] array = new String[24 - startHour];
        for (int i = 0; i < array.length; i++) {
            int hour = startHour + i;
            array[i] = hour < 10 ? ("0" + hour) : String.valueOf(hour);
        }
        return array;
    }

    /**
     * 获取 0～23 的小时列表
     *
     * @return
     */
    private String[] getFullHourArray() {
        String[] array = new String[24];
        for (int i = 0; i < 24; i++) {
            String strHour = i < 10 ? ("0" + i) : String.valueOf(i);
            array[i] = strHour;
        }
        return array;
    }

    public void show() {
        if (mDialog != null) mDialog.show(); //显示dialog
    }

    public void dismiss() {
        if (mDialog != null) mDialog.dismiss();
        mDialog = null;
        mContext = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_sure) {
            if (mListener != null) {
                if (mDayIndex >= dayList.size()) return; //避免异常

                DayVO dayVO = dayList.get(mDayIndex); //获取选择的日期
                int hour = getIntValue(dayVO.getHourArray()[mHourIndex]); //获取选择的小时

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, dayVO.year);
                cal.set(Calendar.MONTH, dayVO.month - 1);
                cal.set(Calendar.DAY_OF_MONTH, dayVO.day);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                long timeStamp = cal.getTimeInMillis(); //说明：这个时间戳 不是最终返回的，需要通过TimeVO.createFrom处理
                mListener.onSelect(this, TimeVO.createFrom(timeStamp));
            }
        }
    }

    public interface SelectListener {

        void onSelect(TimeDialog dialog, TimeVO vo);
    }

    static class DayVO {

        static DayVO createFrom(Calendar cal) {
            DayVO vo = new DayVO();
            vo.year = cal.get(Calendar.YEAR);
            vo.month = cal.get(Calendar.MONTH) + 1;
            vo.day = cal.get(Calendar.DAY_OF_MONTH);
            return vo;
        }

        int year;
        int month;
        int day;

        private int startHour;
        private String[] hourArray; //对应的"小时"列表（需要主动设置）

        /**
         * 获取相应的显示
         *
         * @return
         */
        String getStrDisplay() {
            if (day < 10) return "0" + day;
            return String.valueOf(day);
        }

        /**
         * 是否符合条件：true - 比maxDay小、false - 比maxDay大
         * 小于maxDay才算符合条件！
         *
         * @param maxDay
         * @return
         */
        boolean isMatchCondition(DayVO maxDay) {
//            if (year <= maxDay.year
//                    && month <= maxDay.month
//                    && day <= maxDay.day)
//                return true;
//            return false;
            if (year > maxDay.year) return true;
            if (year < maxDay.year) return false;
            if (month > maxDay.month) return true;
            if (month < maxDay.month) return false;
            return day > maxDay.day;
        }

        void setArray(int hour, String[] array) {
            startHour = hour;
            hourArray = array;
        }

        String[] getHourArray() {
            return hourArray != null ? hourArray : getDefaultDisplay();
        }

        int getStartHour() {
            return startHour;
        }

    }

    private static String[] getDefaultDisplay() {
        return new String[]{""};
    }

    private static int getIntValue(String number) {
        if (TextUtils.isEmpty(number)) return 0;
        try {
            return Integer.valueOf(number);
        } catch (Exception e) {
            return 0;
        }
    }

}
