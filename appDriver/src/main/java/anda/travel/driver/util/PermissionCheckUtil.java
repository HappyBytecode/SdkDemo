package anda.travel.driver.util;

import android.Manifest;
import android.content.Context;

import anda.travel.driver.widget.CommonAlertDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionCheckUtil {

    private final static int PERMISSION_REQUEST = 1000;

    public static final String[] LOCATION =
            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
            };

    public static boolean isLocationPermissionIgnored(Context context) {
        return !EasyPermissions.hasPermissions(context, LOCATION);
    }

    public static void showPermissionRequest(final Context context) {
        new CommonAlertDialog(context).builder()
                .setTitle("权限已被禁用，将导致您无法正常接单，请开启！")
                .setMsg("请前往设置界面授予权限\n<后台弹出界面>或<允许自动启动>\n")
                .setPositiveButton("立即开启", v -> {
                    JumpPermissionUtil.toSelfSetting(context);
                })
                .setNegativeButton("暂不开启", v -> {
                })
                .show();
    }

}
