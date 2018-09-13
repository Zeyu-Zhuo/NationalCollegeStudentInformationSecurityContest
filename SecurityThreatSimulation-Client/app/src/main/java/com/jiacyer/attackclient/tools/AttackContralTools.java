package com.jiacyer.attackclient.tools;

import android.view.TextureView;

import com.jiacyer.attackclient.control.AttackActivity;

/**
 *  Created by Jiacy-PC on 2018/2/28.
 */

public class AttackContralTools {
    private static AttackActivity attackActivity = null;

    public static void setAttackActivity(AttackActivity activity) {
        attackActivity = activity;
    }

    public static void removeAttackActivity() {
        attackActivity = null;
    }

    public static TextureView getTextureView() {
        return attackActivity.getTextureView();
    }

}
