/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vrviu.demo.player;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @Title: TouchTracker
 * @Description: git@github.com:raasun/cardboard.git
 * Added by kevin.zha@vrviu.com
 * @date 2017/10/13 10:12
 * @version V1.0
 */

public class TouchTracker implements View.OnTouchListener {
    private final TouchEnabledVrView target;
    private PointF lastTouchPointPx = new PointF();
    private PointF startTouchPointPx = new PointF();
    private boolean isYawing;
    private final float scrollSlopPx;
    private boolean touchTrackingEnabled = true;
    private boolean verticalTrackingEnabled = false;

    public TouchTracker(Context context, TouchEnabledVrView target) {
        this.target = target;
        this.scrollSlopPx = (float) ViewConfiguration.get(context).getScaledTouchSlop();
    }

    TouchTracker(TouchEnabledVrView target, float scrollSlopPx) {
        this.target = target;
        this.scrollSlopPx = scrollSlopPx;
    }

    void setVerticalTrackingEnabled(boolean enabled) {
        this.verticalTrackingEnabled = enabled;
    }

    void setTouchTrackingEnabled(boolean enabled) {
        this.touchTrackingEnabled = enabled;
    }

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.startTouchPointPx.set(event.getX(), event.getY());
                this.lastTouchPointPx.set(event.getX(), event.getY());
                view.getParent().requestDisallowInterceptTouchEvent(true);
                this.isYawing = false;
                return false;
            case MotionEvent.ACTION_UP:
                if (!this.touchTrackingEnabled || Math.abs(event.getX() - this.startTouchPointPx.x)
                        < this.scrollSlopPx && Math.abs(event.getY() - this.startTouchPointPx.y) < this.scrollSlopPx) {
//                    this.target.getEventListener().onClick();
                }

                view.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!this.touchTrackingEnabled) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                } else {
                    if (!this.isYawing) {
                        if (!this.verticalTrackingEnabled && Math.abs(event.getY() - this.startTouchPointPx.y) > this.scrollSlopPx) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            return false;
                        }

                        if (Math.abs(event.getX() - this.startTouchPointPx.x) > this.scrollSlopPx) {
                            this.isYawing = true;
                        }
                    }

                    this.target.onPanningEvent(event.getX() - this.lastTouchPointPx.x,
                            this.verticalTrackingEnabled ? event.getY() - this.lastTouchPointPx.y : 0.0F);
                    this.lastTouchPointPx.set(event.getX(), event.getY());
                    return true;
                }
            default:
                return false;
        }
    }

    interface TouchEnabledVrView {
        void onPanningEvent(float var1, float var2);
    }
}