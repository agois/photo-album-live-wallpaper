package com.engine.photoalbum;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class Picture {
    private Rectangle mRectPicture;
    private Rectangle mRectBackground;

    public static final float DEPTH_PORT = -8.6f;
    public static final float DEPTH_LAND = -4.8f;
    public static final float BACK_RECTANGLE_DEPTH = -0.005f;
    public static final float VERTICAL_STEP = 0.5f;
    private static final float PIC_LAND_SCALE_FOR_BORDER_X = 1.05f;
    private static final float PIC_LAND_SCALE_FOR_BORDER_Y = 1.08f;
    private static final float PIC_PORT_SCALE_FOR_BORDER_X = 1.08f;
    private static final float PIC_PORT_SCALE_FOR_BORDER_Y = 1.05f;

    private float mAngle;
    private float mPositionX, mPositionY;
    boolean mIsLandscape;
    boolean mPicIsLandscape;
    
    public Picture(Context context, boolean picIsLandscape, int width, int height) {
        mRectPicture = new Rectangle(context, picIsLandscape, width, height);
        mRectBackground = new Rectangle(context, picIsLandscape, width, height);
        mIsLandscape = (width > height);
    }

    public void initPosition(int index) {
        if(mPicIsLandscape) {
            switch(index) {
            case 0:
                mAngle = 0f;
                if(mIsLandscape) {
                    mPositionX = 0f;
                    mPositionY = 0f;
                } else {
                    mPositionX = 0f;
                    mPositionY = 0f;
                }
                break;
            case 1:
                mAngle = -5f;
                if(mIsLandscape) {
                    mPositionX = -1.86f;
                    mPositionY = 0.97f;
                } else {
                    mPositionX = -0.50f;
                    mPositionY = 2.64f;
                }
                break;
            case 2:
                mAngle = 3f;
                if(mIsLandscape) {
                    mPositionX = 1.82f;
                    mPositionY = 1.17f;
                } else {
                    mPositionX = 0.41f;
                    mPositionY = 2.44f;
                }
                break;
            case 3:
                mAngle = 10f;
                if(mIsLandscape) {
                    mPositionX = -1.96f;
                    mPositionY = 0.06f;
                } else {
                    mPositionX = -0.23f;
                    mPositionY = 1.03f;
                }
                break;
            case 4:
                mAngle = 6f;
                if(mIsLandscape) {
                    mPositionX = 1.74f;
                    mPositionY = -0.51f;
                } else {
                    mPositionX = 0.37f;
                    mPositionY = 0.44f;
                }
                break;
            case 5:
                mAngle = 15f;
                if(mIsLandscape) {
                    mPositionX = 0.20f;
                    mPositionY = -0.65f;
                } else {
                    mPositionX = 0.5f;
                    mPositionY = -1.79f;
                }
                break;
            case 6:
                mAngle = -7f;
                if(mIsLandscape) {
                    mPositionX = 1.93f;
                    mPositionY = -0.72f;
                } else {
                    mPositionX = 0.13f;
                    mPositionY = -1.65f;
                }
                break;
            case 7:
                mAngle = -2f;
                if(mIsLandscape) {
                    mPositionX = 0.14f;
                    mPositionY = -1.01f;
                } else {
                    mPositionX = 0.31f;
                    mPositionY = -2.56f;
                }
                break;
            case 8:
                mAngle = 20f;
                if(mIsLandscape) {
                    mPositionX = -2.27f;
                    mPositionY = -0.43f;
                } else {
                    mPositionX = -1.28f;
                    mPositionY = -2.13f;
                }
                break;
            case 9:
                mAngle = 7f;
                if(mIsLandscape) {
                    mPositionX = 1.58f;
                    mPositionY = -1.46f;
                } else {
                    mPositionX = -0.50f;
                    mPositionY = -0.83f;
                }
                break;
            }
        } else {
            switch(index) {
            case 0:
                mAngle = 0f;
                if(mIsLandscape) {
                    mPositionX = 0f;
                    mPositionY = 0f;
                } else {
                    mPositionX = 0f;
                    mPositionY = 0f;
                }
                break;
            case 1:
                mAngle = -5f;
                if(mIsLandscape) {
                    mPositionX = -2.71f;
                    mPositionY = -0.08f;
                } else {
                    mPositionX = -1.31f;
                    mPositionY = 1.59f;
                }
                break;
            case 2:
                mAngle = 3f;
                if(mIsLandscape) {
                    mPositionX = -2.64f;
                    mPositionY = -0.10f;
                } else {
                    mPositionX = 1.21f;
                    mPositionY = 1.69f;
                }
                break;
            case 3:
                mAngle = 10f;
                if(mIsLandscape) {
                    mPositionX = -1.02f;
                    mPositionY = 0.58f;
                } else {
                    mPositionX = 0.28f;
                    mPositionY = 1.95f;
                }
                break;
            case 4:
                mAngle = 6f;
                if(mIsLandscape) {
                    mPositionX = 0.89f;
                    mPositionY = 0.23f;
                } else {
                    mPositionX = -1.36f;
                    mPositionY = -1.58f;
                }
                break;
            case 5:
                mAngle = 15f;
                if(mIsLandscape) {
                    mPositionX = -0.86f;
                    mPositionY = 0.21f;
                } else {
                    mPositionX = 0.16f;
                    mPositionY = -1.70f;
                }
                break;
            case 6:
                mAngle = -7f;
                if(mIsLandscape) {
                    mPositionX = -0.07f;
                    mPositionY = -0.34f;
                } else {
                    mPositionX = 0.10f;
                    mPositionY = -1.93f;
                }
                break;
            case 7:
                mAngle = -2f;
                if(mIsLandscape) {
                    mPositionX = 2.56f;
                    mPositionY = 0.33f;
                } else {
                    mPositionX = 1.12f;
                    mPositionY = -1.71f;
                }
                break;
            case 8:
                mAngle = 20f;
                if(mIsLandscape) {
                    mPositionX = 1.12f;
                    mPositionY = -1.71f;
                } else {
                    mPositionX = 0.40f;
                    mPositionY = 0.86f;
                }
                break;
            case 9:
                mAngle = 7f;
                if(mIsLandscape) {
                    mPositionX = 2.49f;
                    mPositionY = -0.62f;
                } else {
                    mPositionX = 0.05f;
                    mPositionY = -1.96f;
                }
                break;
            }
        }
    }
    
    public void updatePosition(GL10 gl, float depth) {
        gl.glRotatef(mAngle, 0, 0, 1);
        gl.glTranslatef(mPositionX, mPositionY, depth);
    }
    
    public void draw(GL10 gl, boolean detectPicture) {
        mRectPicture.draw(gl);
        if(detectPicture) {
            // Detect picture operation
            gl.glTranslatef(0, 0, -BACK_RECTANGLE_DEPTH); // position before the picture 
        } else {
            // Normal operation
            gl.glTranslatef(0, 0, BACK_RECTANGLE_DEPTH); // position behind the picture 
        }
        if(mPicIsLandscape) {
            gl.glScalef(PIC_LAND_SCALE_FOR_BORDER_X, PIC_LAND_SCALE_FOR_BORDER_Y, 0); 
        } else {
            gl.glScalef(PIC_PORT_SCALE_FOR_BORDER_X, PIC_PORT_SCALE_FOR_BORDER_Y, 0); 
        }
        mRectBackground.draw(gl);
    }
    
    public void loadGLTexture(GL10 gl, int drawableID) {
        mRectPicture.loadGLTexture(gl, drawableID);
    }

    public void loadGLTexture(GL10 gl, String file) {
        mRectPicture.loadGLTexture(gl, file);
    }

    public void setColor(float color) {
        mRectBackground.setColor(color/255f, 0, 0, 0);
    }

    public void resetColor() {
        mRectBackground.resetColor();
    }
}
