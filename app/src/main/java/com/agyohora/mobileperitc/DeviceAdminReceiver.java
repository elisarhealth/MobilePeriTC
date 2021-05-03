package com.agyohora.mobileperitc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Invent on 8-1-18.
 */

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    /**
     * @param context The context of the application.
     * @return The component name of this component in the given context.
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return super.onDisableRequested(context, intent);
    }
}