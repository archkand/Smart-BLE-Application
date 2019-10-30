package com.example.bleapplication;

import java.lang.String;

import androidx.core.app.ActivityCompat;
import permissions.dispatcher.PermissionUtils;

final class ScanActivityPermissionsDispatcher {
    private static final int REQUEST_STARTSCAN = 0;

    private static final String[] PERMISSION_STARTSCAN = new String[] {"android.permission.ACCESS_COARSE_LOCATION"};

    private ScanActivityPermissionsDispatcher() {
    }

    static void startScanWithPermissionCheck(MainActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_STARTSCAN)) {
            target.startScan();
        } else {
            ActivityCompat.requestPermissions(target, PERMISSION_STARTSCAN, REQUEST_STARTSCAN);
        }
    }

    static void onRequestPermissionsResult(MainActivity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STARTSCAN:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.startScan();
                }
                break;
            default:
                break;
        }
    }
}
