package com.engine.photoalbum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.preference.PreferenceManager;
import android.util.Log;

public class EngineRenderer implements Renderer {
    private final String TAG = "xxx";
    private final int MAX_PICTURES = 10;

    private static final int[] mDrawables = {
        R.drawable.pic0,
        R.drawable.pic1,
        R.drawable.pic2,
        R.drawable.pic3,
        R.drawable.pic4,
        R.drawable.pic5,
        R.drawable.pic6,
        R.drawable.pic7,
        R.drawable.pic8,
        R.drawable.pic9};

    private final int STATE_DEFAULT = 0;
    private final int STATE_BOTTOM = 1;
    private final int STATE_UP = 2;

    private Picture[] mPicture = new Picture[MAX_PICTURES];
    private float mLastTouchX, mLastTouchY;
    private float mHeight;
    private int mPicID, mState;
    private int[] mList = new int[MAX_PICTURES];
    private float[] mDepth = new float[MAX_PICTURES];
    private Context mContext;
    private boolean mShouldDetectedPicture;
    boolean mIsLandscape;

	public EngineRenderer(Context context) {
	    mState = STATE_DEFAULT;
        mContext = context;
	}

    public void onActionDown(float x, float y) {
        //Log.i(TAG, "EngineRenderer.onActionDown " + x + "," + y);
        mLastTouchX = x;
        mLastTouchY = y;
        mShouldDetectedPicture = true;
    }
        
    public void onActionUp(float x, float y) {
        //Log.i(TAG, "EngineRenderer.onActionUp " + x + "," + y);
    }

    public void onActionMove(float x, float y) {
        //Log.i(TAG, "EngineRenderer.onActionMove " + x + "," + y);
/*
        if(x > mLastTouchX + 10) {
            //Log.i(TAG, "EngineRenderer.onActionMove RIGHT");
            mPicture[0].mPositionX += 0.01f;
        } else if(x < mLastTouchX -10) {
            //Log.i(TAG, "EngineRenderer.onActionMove LEFT");
            mPicture[0].mPositionX -= 0.01f;
        } else if(y > mLastTouchY + 10) {
            //Log.i(TAG, "EngineRenderer.onActionMove UP");
            mPicture[0].mPositionY -= 0.01f;
        } else if(y < mLastTouchY - 10) {
            //Log.i(TAG, "EngineRenderer.onActionMove DOWN");
            mPicture[0].mPositionY += 0.01f;
        }
        mLastTouchX = x;
        mLastTouchY = y;
*/        
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        SetReferenceColor(mShouldDetectedPicture);
        
        //Log.i("xxx","onDrawFrame " + mState);
        switch(mState) {
        case STATE_DEFAULT:
            for(int i = 0; i < MAX_PICTURES; i++) {
                gl.glLoadIdentity(); 
                mPicture[i].updatePosition(gl, mDepth[i]);
                mPicture[i].draw(gl, mShouldDetectedPicture);
            }
            break;
        case STATE_BOTTOM:
            // Apply transformations
            if(mDepth[mList[0]] < 0) {
                // Move towards screen
                mDepth[mList[0]] += Picture.VERTICAL_STEP;
            } else {
                mState = STATE_DEFAULT;

                // Update sorting
                int aux = mList[0];
                for(int i = 0; i<MAX_PICTURES-1; i++) {
                    mList[i] = mList[i+1]; 
                }
                mList[MAX_PICTURES-1] = aux;

                // Update list
                for(int i = 0; i<MAX_PICTURES; i++) {
                    if(mIsLandscape) {
                        mDepth[mList[i]] = Picture.DEPTH_LAND + Picture.BACK_RECTANGLE_DEPTH*2*i;
                    } else {
                        mDepth[mList[i]] = Picture.DEPTH_PORT + Picture.BACK_RECTANGLE_DEPTH*2*i;
                    }
                }
            }

            for(int i = 0; i < MAX_PICTURES; i++) {
                gl.glLoadIdentity(); 
                mPicture[i].updatePosition(gl, mDepth[i]);
                mPicture[i].draw(gl, mShouldDetectedPicture);
            }
            break;
        case STATE_UP:
            // Apply transformations
            mState = STATE_DEFAULT;

            // Update sorting
            int index = -1;
            for(int i = 0; i < MAX_PICTURES; i++) {
                if(mList[i] == mPicID) {
                    index = i;
                    break;
                }
            }
            for(int i = 0; i < index; i++) {
                mList[index-i] = mList[index-i-1]; 
            }
            mList[0] = mPicID;

            // Update list
            for(int i = 0; i<MAX_PICTURES; i++) {
                if(mIsLandscape) {
                    mDepth[mList[i]] = Picture.DEPTH_LAND + Picture.BACK_RECTANGLE_DEPTH*2*i;
                } else {
                    mDepth[mList[i]] = Picture.DEPTH_PORT + Picture.BACK_RECTANGLE_DEPTH*2*i;
                }
            }

            for(int i = 0; i < MAX_PICTURES; i++) {
                gl.glLoadIdentity(); 
                mPicture[i].updatePosition(gl, mDepth[i]);
                mPicture[i].draw(gl, mShouldDetectedPicture);
            }
           break;
        }

        if(mShouldDetectedPicture) {
            mPicID = getFacet(gl);
            if(mPicID >= 0 && mPicID < MAX_PICTURES) {
                mShouldDetectedPicture = false;

                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

                SetReferenceColor(mShouldDetectedPicture);

                for(int i = 0; i < MAX_PICTURES; i++) {
                    gl.glLoadIdentity(); 
                    mPicture[i].updatePosition(gl, mDepth[i]);
                    mPicture[i].draw(gl, mShouldDetectedPicture);
                }

                if(mPicID == mList[0]) {
                    mState = STATE_BOTTOM;
                } else {
                    mState = STATE_UP;
                }
                Log.i("xxx","mPicID " + mPicID + " " + mState);
            }
        }
    }

    private int getFacet(GL10 gl) {
        ByteBuffer PixelBuffer = ByteBuffer.allocateDirect(4);
        PixelBuffer.order(ByteOrder.nativeOrder());
        gl.glReadPixels((int)mLastTouchX, (int)(mHeight-mLastTouchY), 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, PixelBuffer);
        byte b[] = new byte[4];
        PixelBuffer.get(b);
        return b[0];
    }
    
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mHeight = height;

        mIsLandscape = (width > height);

        for(int i = 0; i < MAX_PICTURES; i++) {
            mPicture[i] = new Picture(mContext, false, width, height); // false on param 2 => Pic is portrait.
            mList[i] = i;
            if(mIsLandscape) {
                mDepth[mList[i]] = Picture.DEPTH_LAND + Picture.BACK_RECTANGLE_DEPTH*2*i;
            } else {
                mDepth[mList[i]] = Picture.DEPTH_PORT + Picture.BACK_RECTANGLE_DEPTH*2*i;
            }
            mPicture[i].initPosition(i);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> imageFiles = prefs.getStringSet(AndroidCustomGalleryActivity.SELECTED_IMAGE_SET, null); 

        // Load the image as texture only if displayImage flag is true
        if (imageFiles != null) {
            int i = 0;
            for (String imageFile : imageFiles) {
                Log.w(TAG, "EngineRenderer file=" + imageFile);
                mPicture[i++].loadGLTexture(gl, imageFile);
                if (i == MAX_PICTURES) {
                    break;
                }
            }
            for (; i < MAX_PICTURES; i++) {
                mPicture[i].loadGLTexture(gl, mDrawables[i]);
            }
        } else {
            for (int i = 0; i < mPicture.length ; i++) {
                mPicture[i].loadGLTexture(gl, mDrawables[i]);
            }
        }
        
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);
        
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        
        // Reset the projection matrix
        gl.glLoadIdentity();
        
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background color (rgba).
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //Enable Texture Mapping
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        //Enable Smooth Shading
        gl.glShadeModel(GL10.GL_SMOOTH);
        
        //Depth Buffer Setup
        gl.glClearDepthf(1.0f);
        
        //Enables Depth Testing
        gl.glEnable(GL10.GL_DEPTH_TEST);
        
        //The Type Of Depth Testing To Do
        gl.glDepthFunc(GL10.GL_LEQUAL);
    }
        
    private void SetReferenceColor(boolean shouldDetectedPicture) {
        for(int i = 0; i<MAX_PICTURES; i++) {
            if(shouldDetectedPicture) {
                mPicture[i].setColor((float)(i));
            } else {
                mPicture[i].resetColor();
            }
        }
    }
}
