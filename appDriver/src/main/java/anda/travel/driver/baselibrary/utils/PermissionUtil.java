package anda.travel.driver.baselibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.baselibrary.view.dialog.PermissionSetTipDialog;
import pub.devrel.easypermissions.AppSettingsDialog;

public class PermissionUtil {
    ////低于10的权限数组
    private static final String[] PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE};

    private static final String[] PERMISSIONS_FOR_Q =
            {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String[] PERMISSIONS_FOR_FROM_LOGIN = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[] PERMISSIONS_FOR_FROM_LOGIN_Q = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] PERMISSIONS_FOR_RECORD =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO};

    private static final String[] PERMISSIONS_FOR_HEAT_MAP_Q =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };

    private static final String[] PERMISSIONS_FOR_HEAT_MAP =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    public static final String[] PERMISSION_FOR_CAMERA = {
            Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_FOR_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[] PERMISSIONS_FOR_DUTY_Q =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            };

    private static final String[] PERMISSIONS_FOR_DUTY =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            };

    private static final String[] PERMISSIONS_FOR_DUTY_FIRST =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
            };

    public static PermissionSetTipDialog getPermissionSetTipDialog(
            @NonNull List<String> perms,
            Context context) {
        List<String> titleList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        for (String deniedPermission : perms) {
            switch (deniedPermission) {
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                    if (!titleList.contains("定位")) {
                        titleList.add("定位");
                        contentList.add("· 您需要同意始终允许开启定位权限，以便您能在后台继续使用和行约车司机端");
                    }
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    if (!titleList.contains("存储")) {
                        titleList.add("存储");
                        contentList.add("· 您需要同意开启存储权限，以便服务数据能顺利保存至您的手机");
                    }
                    break;
                case Manifest.permission.CAMERA:
                    titleList.add("拍照");
                    contentList.add("· 您需要同意开启拍照权限，以便您能顺利扫脸出车和上传资料");
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    titleList.add("麦克风");
                    contentList.add("· 您需要同意开启麦克风权限，以便您能联系乘客");
                    break;
                default:
                    break;
            }
        }
        PermissionSetTipDialog permissionSetTipDialog = new PermissionSetTipDialog(context);
        switch (titleList.size()) {
            case 1:
                permissionSetTipDialog.setTipsSmallTitleAndContent1
                        (titleList.get(0), contentList.get(0));
                break;
            case 2:
                permissionSetTipDialog.setTipsSmallTitleAndContent1
                        (titleList.get(0), contentList.get(0));
                permissionSetTipDialog.setTipsSmallTitleAndContent2
                        (titleList.get(1), contentList.get(1));
                break;
            case 3:
                permissionSetTipDialog.setTipsSmallTitleAndContent1
                        (titleList.get(0), contentList.get(0));
                permissionSetTipDialog.setTipsSmallTitleAndContent2
                        (titleList.get(1), contentList.get(1));
                permissionSetTipDialog.setTipsSmallTitleAndContent3
                        (titleList.get(2), contentList.get(2));
                break;
            case 4:
                permissionSetTipDialog.setTipsSmallTitleAndContent1
                        (titleList.get(0), contentList.get(0));
                permissionSetTipDialog.setTipsSmallTitleAndContent2
                        (titleList.get(1), contentList.get(1));
                permissionSetTipDialog.setTipsSmallTitleAndContent3
                        (titleList.get(2), contentList.get(2));
                permissionSetTipDialog.setTipsSmallTitleAndContent4
                        (titleList.get(3), contentList.get(3));
                break;
            default:
                break;
        }
        permissionSetTipDialog.setConfirmText("设置");
        permissionSetTipDialog.setCancelable(true);
        permissionSetTipDialog.setConfirmListener(() -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", context.getPackageName(), null));
            ((Activity) context).startActivityForResult(intent, AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE);
        });
        return permissionSetTipDialog;
    }

    public static String[] getNeededPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return PERMISSIONS_FOR_Q;
        } else {
            return PERMISSIONS;
        }
    }

    public static String[] getNeededFormLoginPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return PERMISSIONS_FOR_FROM_LOGIN_Q;
        } else {
            return PERMISSIONS_FOR_FROM_LOGIN;
        }
    }

    public static String[] getNeededAudioPermission() {
        return PERMISSIONS_FOR_RECORD;
    }

    public static String[] getHeatMapNeededPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return PERMISSIONS_FOR_HEAT_MAP_Q;
        } else {
            return PERMISSIONS_FOR_HEAT_MAP;
        }
    }

    public static String[] getDutyNeededPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return PERMISSIONS_FOR_DUTY_Q;
        } else {
            if (SP.getInstance(context).getBoolean("FIRST_PHONE_STATE", true)) {
                //第一次申请增加READ_PHONE_STATE权限
                return PERMISSIONS_FOR_DUTY_FIRST;
            } else {
                return PERMISSIONS_FOR_DUTY;
            }
        }
    }
}
