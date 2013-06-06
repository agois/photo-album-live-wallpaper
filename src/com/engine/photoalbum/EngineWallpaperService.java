package com.engine.photoalbum;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class EngineWallpaperService extends GLWallpaperService {

    public Engine onCreateEngine() {
        MyEngine engine = new MyEngine(this);
        return engine;
    }

    class MyEngine extends GLEngine {
        private EngineRenderer mRenderer;

        public MyEngine(Context context) {
            super();
            mRenderer = new EngineRenderer(context);

            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            
            setRenderer(mRenderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        public void onDestroy() {
            mRenderer = null;
            setTouchEventsEnabled(false);
            super.onDestroy();
        }

        @Override
        public void onTouchEvent(MotionEvent ev) {
            super.onTouchEvent(ev);
            int act = ev.getAction();
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
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }
    }
}