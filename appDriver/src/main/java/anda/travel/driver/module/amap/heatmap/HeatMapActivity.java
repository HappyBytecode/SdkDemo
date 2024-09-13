package anda.travel.driver.module.amap.heatmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.Gradient;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TileOverlayOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivityWithoutIconics;
import anda.travel.driver.data.entity.HeatMapEntity;
import anda.travel.driver.module.amap.assist.MapUtils;
import anda.travel.driver.module.amap.heatmap.dagger.DaggerHeatMapComponent;
import anda.travel.driver.module.amap.heatmap.dagger.HeatMapModule;
import anda.travel.driver.socket.utils.LocUtils;
import anda.travel.driver.widget.popwindow.EasyPopup;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * 热力图
 */
public class HeatMapActivity extends BaseActivityWithoutIconics implements HeatMapContract.View {

    @BindView(R2.id.heatmap)
    MapView mMapView;
    @BindView(R2.id.head_view)
    HeadView mHeadView;
    private TextView mYesterday, mThreeDay, mWeek;
    private EasyPopup mMorePop;

    private CustomMapStyleOptions mapStyleOptions;
    private AMap mAMap;

    @Inject
    HeatMapPresenter mPresenter;

    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),
            Color.argb(255, 0, 255, 0),
            Color.rgb(125, 191, 0),
            Color.rgb(185, 71, 0),
            Color.rgb(255, 0, 0)
    };

    private static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {0.0f, 0.1f,
            0.20f, 0.60f, 1.0f};

    private static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(
            ALT_HEATMAP_GRADIENT_COLORS, ALT_HEATMAP_GRADIENT_START_POINTS);

    public static void actionStart(Context context, String versionNo, String moduleUuid) {
        Intent intent = new Intent(context, HeatMapActivity.class);
        intent.putExtra("versionNo", versionNo);
        intent.putExtra("moduleUuid", moduleUuid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_heatmap);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        DaggerHeatMapComponent.builder()
                .appComponent(getAppComponent())
                .heatMapModule(new HeatMapModule(this))
                .build().inject(this);
        ButterKnife.bind(this);
        // 获取地图缩放类型
        String versionNo = getIntent().getStringExtra("versionNo");
        String moduleUuid = getIntent().getStringExtra("moduleUuid");
        mPresenter.getZoomType(versionNo, moduleUuid);

        mMapView.onCreate(savedInstanceState);
        initMap();
        setUpMapStyle();
        int DEFAULTTIMETYPE = 1;
        mPresenter.getPoints(DEFAULTTIMETYPE);
        mHeadView.getRightTextView().setOnClickListener(view -> {
            mMorePop.showAsDropDown(mHeadView.getRightView(), -30, 10);
        });
        mMorePop = EasyPopup.create()
                .setContentView(this, R.layout.hxyc_layout_popup_heatmap)
                .setAnimationStyle(R.style.CurtainsPopAnim)
                ///允许背景变暗
                .setBackgroundDimEnable(true)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(false)
                .apply();
        mYesterday = mMorePop.findViewById(R.id.yesterday);
        mYesterday.setSelected(true);
        mThreeDay = mMorePop.findViewById(R.id.threeday);
        mWeek = mMorePop.findViewById(R.id.week);
        mYesterday.setOnClickListener(view -> {
            resetPopState();
            mMorePop.dismiss();
            mHeadView.setRightTxt(R.string.yesterday);
            mYesterday.setSelected(true);
            mPresenter.getPoints(1);
        });
        mThreeDay.setOnClickListener(view -> {
            resetPopState();
            mMorePop.dismiss();
            mHeadView.setRightTxt(R.string.threeday);
            mThreeDay.setSelected(true);
            mPresenter.getPoints(2);
        });
        mWeek.setOnClickListener(view -> {
            resetPopState();
            mMorePop.dismiss();
            mHeadView.setRightTxt(R.string.week);
            mWeek.setSelected(true);
            mPresenter.getPoints(3);
        });
    }

    /**
     * 重置选择状态
     */
    private void resetPopState() {
        mYesterday.setSelected(false);
        mThreeDay.setSelected(false);
        mWeek.setSelected(false);
    }

    private void initMap() {
        mAMap = mMapView.getMap();
        mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
        if (LocUtils.get().getCurrentPoint() != null) {
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(LocUtils.get().getCurrentPoint()));
            MapUtils.addMarker(this, mAMap, R.drawable.map_loc, LocUtils.get().getCurrentPoint());
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mPresenter.subscribe();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mPresenter.unsubscribe();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mAMap = null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showHeatMap(List<HeatMapEntity> datas) {
        Observable.just(datas).map(heatMapEntities -> {
            List<LatLng> latLngs = new ArrayList<>();
            for (HeatMapEntity heatMapEntity : heatMapEntities) {
                if (heatMapEntity != null) {
                    latLngs.add(new LatLng(Double.parseDouble(heatMapEntity.getLatitude()), Double.parseDouble(heatMapEntity.getLongitude())));
                }
            }
            return latLngs;
        }).compose(RxUtil.applySchedulers()).subscribe(latLngs -> {
            if (latLngs != null) {
                if (mAMap != null) {
                    mAMap.clear();
                    if (LocUtils.get().getCurrentPoint() != null) {
                        MapUtils.addMarker(this, mAMap, R.drawable.map_loc, LocUtils.get().getCurrentPoint());
                    }
                    // 第二步： 构建热力图 TileProvider
                    HeatmapTileProvider.Builder builder = new HeatmapTileProvider.Builder();
                    builder.data(latLngs);///getData // 设置热力图绘制的数据
                    builder.gradient(ALT_HEATMAP_GRADIENT);
                    // Gradient 的设置可见参考手册
                    // 构造热力图对象
                    HeatmapTileProvider heatmapTileProvider = builder.build();
                    // 第三步： 构建热力图参数对象
                    TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
                    tileOverlayOptions.tileProvider(heatmapTileProvider); // 设置瓦片图层的提供者
                    // 第四步： 添加热力图
                    mAMap.addTileOverlay(tileOverlayOptions);
                    if (LocUtils.get().getCurrentPoint() != null) {
                        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(LocUtils.get().getCurrentPoint()));
                    }
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
                }
            } else {
                toastFail();
            }
        }, ex -> {
        });
    }

    @Override
    public void toastFail() {
        ToastUtil.getInstance().toast("未获取到数据");
        if (mAMap != null) {
            mAMap.clear();
        }
        if (LocUtils.get().getCurrentPoint() != null && mAMap != null) {
            MapUtils.addMarker(this, mAMap, R.drawable.map_loc, LocUtils.get().getCurrentPoint());
        }
    }

    private void setUpMapStyle() {
        mapStyleOptions = new CustomMapStyleOptions();
        setMapCustomStyleFile(this);
        mapStyleOptions.setEnable(true);
        mAMap.setCustomMapStyle(mapStyleOptions);
    }

    /**
     * 设置是否可缩放移动
     */
    @Override
    public void setCanZoom(boolean isZoom) {
        UiSettings uiSettings = mAMap.getUiSettings();
        if (null != uiSettings) {
            uiSettings.setAllGesturesEnabled(isZoom);
            uiSettings.setZoomControlsEnabled(isZoom);
        }
    }

    private void setMapCustomStyleFile(Context context) {
        String styleName = "amap/" + "style.data";
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            if (mapStyleOptions != null) {
                // 设置自定义样式
                mapStyleOptions.setStyleData(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
