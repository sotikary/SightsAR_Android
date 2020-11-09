/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.example.sotiria.sightsar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.lang.ref.WeakReference;

public final class LoadingDialogHandler extends Handler
{
    private final WeakReference<CameraActivity> mActivity;
    // Constants for Hiding/Showing Loading dialog
public static int HIDE_LOADING_DIALOG = 100;
 public static   int SHOW_LOADING_DIALOG = 101;
    public static final int HIDE_INFOMENU = 200;
    public static final int SHOW_INFOMENU = 201;

    public static final int HIDE_RECTANGLE = 300;
    public static final int SHOW_RECTANGLE = 301;

    public LoadingDialogHandler(CameraActivity activity)
    {
        mActivity = new WeakReference<CameraActivity>(activity);
    }

    public void handleMessage(Message msg)
    {
        CameraActivity imageTargets = mActivity.get();
        if (imageTargets == null) return;

          if (msg.what == SHOW_LOADING_DIALOG)
        {
            mActivity.get().showHideLoading(true);

        } else if (msg.what == HIDE_LOADING_DIALOG)
        {
            mActivity.get().showHideLoading(false);
        }

        if (msg.what == SHOW_INFOMENU)
        {
            mActivity.get().showHideInfoBox(true);


        } else if (msg.what == HIDE_INFOMENU)
        {
            mActivity.get().showHideInfoBox(false);
        }

    }


    
}
