package com.engine.photoalbum;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class EngineView extends GLSurfaceView {
    private EngineRenderer mRenderer;

    public EngineView(Context context) {
        super(context);
        mRenderer = new EngineRenderer(context);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int act = ev.getActionMasked();
        switch(act) {
        case MotionEvent.ACTION_DOWN:
            mRenderer.onActionDown(ev.getX(), ev.getY());
            break;
        case MotionEvent.ACTION_UP:
            mRenderer.onActionUp(ev.getX(), ev.getY());
            break;
        case MotionEvent.ACTION_MOVE:
            mRenderer.onActionMove(ev.getX(), ev.getY());
            break;
        }
        return true;
    }
}