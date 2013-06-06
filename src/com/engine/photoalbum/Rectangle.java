package com.engine.photoalbum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.opengl.GLUtils;

public class Rectangle {
    private float vertices[] = {
            -1.5f, -1.0f, 0.0f,
             1.5f, -1.0f, 0.0f,
             1.5f,  1.0f, 0.0f,
            -1.5f,  1.0f, 0.0f };

    private float texture[] = {         
            0.0f, 1.0f, 
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f };

    private short indices[] = {0,1,2, 0,2,3};

    private final Context mContext;
	private int mTextureID;
    private float[] mRGBA = new float[]{1,1,1,1}; // Flat Color
    private boolean mHasColor = true;
    
	// Our vertex buffer.
	private FloatBuffer vertexBuffer;

	// Our index buffer.
	private ShortBuffer indexBuffer;

	// Our texture buffer.
	private FloatBuffer textureBuffer; // if displayImage

	public Rectangle(Context context, boolean picIsLandscape, int width, int height) {
		mContext = context;
        float aspect = width > height ? (float)width/(float)height : (float)height/(float)width; 

        if(picIsLandscape) {
		    vertices[0] = -aspect; vertices[1] = -1;
            vertices[3] =  aspect; vertices[4] = -1;
            vertices[6] =  aspect; vertices[7] =  1;
            vertices[9] = -aspect; vertices[10] =  1;
        } else {
            vertices[0] = -1; vertices[1] = -aspect;
            vertices[3] =  1; vertices[4] = -aspect;
            vertices[6] =  1; vertices[7] =  aspect;
            vertices[9] = -1; vertices[10] =  aspect;
        }
	    
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// short is 2 bytes, therefore we multiply the number if
		// indices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		// a float is 4 bytes, therefore we multiply the number if
		// texture with 4.
		ByteBuffer tbb = ByteBuffer.allocateDirect(texture.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}
	
    public void draw(GL10 gl) {
		//Bind our only previously generated texture in this case
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW); 
		
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE); 
		
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK); 

		// Enabled the vertices buffer for writing and to be used during rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// Specifies the location and data format of an array of vertex coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, 
                                 vertexBuffer);

        if(mHasColor) {
            //Set The Color
            gl. glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);  
        }

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
				  GL10.GL_UNSIGNED_SHORT, indexBuffer);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); 
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE); 

		// Disable the texture buffer.
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
    public void loadGLTexture(GL10 gl, int drawableID) {
        //Get the texture from the Android resource directory
        InputStream is = mContext.getResources().openRawResource(drawableID);
        loadGLTexture(gl, is, false);
    }

    public void loadGLTexture(GL10 gl, String filePath) {
        //Get the texture from the Android resource directory
        InputStream is;
        try {
            is = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        loadGLTexture(gl, is, true);
    }

    private void loadGLTexture(GL10 gl, InputStream is, boolean scale) {
        //Get the texture from the Android resource directory
        Bitmap bitmap = null;
        try {
            //BitmapFactory is an Android graphics utility for images
            BitmapFactory.Options opts =  new BitmapFactory.Options();
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inSampleSize = 2;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            if (scale && bitmap != null) {
                //int width = getNearestPowerOfTwoWithShifts(bitmap.getWidth());
                //int height = getNearestPowerOfTwoWithShifts(bitmap.getHeight());
                int width = 512;
                int height = 256;
                if (bitmap.getHeight() > bitmap.getWidth()) {
                    width = 256;
                    height = 512;
                }
                int padx = 0;//(int)(0.025 * width);
                int pady = 0;//(int)(0.025 * height);
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, width - 2 * padx, height - 2 * pady, true);

                bitmap.recycle();
//                bitmap = pad(bitmapScaled, padx, pady);
//                bitmapScaled.recycle();
                bitmap = bitmapScaled;
                
            }
        } finally {
            //Always clear and close
            try {
                is.close();
                is = null;
            } catch (IOException e) {
            }
        }

        //Generate one texture pointer...
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        //...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        
        //Create Nearest Filtered Texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        
        //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        
        //Clean up
        bitmap.recycle();
    }

    private static Bitmap pad(Bitmap src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(src.getWidth() + 2 * padding_x, src.getHeight() + 2 * padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF, 0xFF, 0xFF, 0xFF); //This represents White color
        can.drawBitmap(src, null, 
                new Rect(padding_x, padding_y, src.getWidth() + padding_x, src.getHeight() + padding_y),
                new Paint(Paint.ANTI_ALIAS_FLAG));
        return outputimage;
    }

    public static int getNearestPowerOfTwoWithShifts(int x) {
        // return X if it is a power of 2
        if ((x & (x-1)) == 0 ) return x;

        // integers in java are represented in 32 bits, so:
        for(int i=1; i<32; i = i*2){
          x |= ( x >>> i);
        }
        return x + 1; 
    }

    protected void setColor(float red, float green, float blue, float alpha) {
        mRGBA[0] = red;
        mRGBA[1] = green;
        mRGBA[2] = blue;
        mRGBA[3] = alpha;
        mHasColor = true;
    }

    protected void resetColor() {
        mHasColor = false;
    }
}
