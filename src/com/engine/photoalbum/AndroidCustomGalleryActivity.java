package com.engine.photoalbum;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.engine.photoalbum.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ParserError")
public class AndroidCustomGalleryActivity extends Activity implements LoaderCallbacks<Cursor> {
    public static final String SELECTED_IMAGE_SET = "selected_image_set";
    private int mCount;
    private boolean[] mThumbnailsselection;
    private String[] mArrPath;
    private ImageAdapter mImageAdapter;
    private LruCache mMemoryCache;
    private Map<ViewHolder, Integer> mAsyncLoadMap = new HashMap<ViewHolder, Integer>();
    private AsyncTask mAsyncLoader;
    private Map<Integer, Integer> mIds;
    private int mNumItems;
    Button mSelectBtn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getLoaderManager().initLoader(0, null, this);
        mSelectBtn = (Button) findViewById(R.id.selectBtn);
        mSelectBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = mThumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                Set<String> values = new HashSet<String>();
                for (int i =0; i<len; i++)
                {
                    if (mThumbnailsselection[i]){
                        cnt++;
                        selectImages = selectImages + mArrPath[i] + "|";
                        values.add(mArrPath[i]);
                    }
                }
                if (cnt == 0){
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You've selected Total " + cnt + " image(s).",
                            Toast.LENGTH_LONG).show();
                    Log.d("SelectedImages", selectImages);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AndroidCustomGalleryActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putStringSet(SELECTED_IMAGE_SET, values);
                    editor.apply();
                    finish();
                }
            }
        });
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 10;

        mMemoryCache = new LruCache(cacheSize) {
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number of items.
                return bitmap.getByteCount();
            }
            
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null) {
                    oldValue.recycle();
                }
            }
            
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMemoryCache.evictAll();
        System.gc();
    }
    
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mCount;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (mThumbnailsselection[id]){
                        cb.setChecked(false);
                        mThumbnailsselection[id] = false;
                        mNumItems--;
                    } else {
                        cb.setChecked(true);
                        mThumbnailsselection[id] = true;
                        mNumItems++;
                    }
                    mSelectBtn.setText(getResources().getQuantityString(R.plurals.selectmore, mNumItems, mNumItems));
                }
            });
            holder.imageview.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + mArrPath[id]), "image/*");
                    startActivity(intent);
                }
            });
//            holder.imageview.setImageBitmap(mThumbnails[position]);
            holder.imageview.setImageBitmap(getBitmapFromMemCache(holder, mIds.get(position)));
            holder.checkbox.setChecked(mThumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }
    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }

    public void addBitmapToMemoryCache(ViewHolder holder, Integer id, Bitmap bitmap) {
        synchronized (mMemoryCache) {
            if (getBitmapFromMemCache(holder, id) == null) {
                mMemoryCache.put(id, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(ViewHolder holder, Integer id) {
        Bitmap b = null;
        synchronized (mMemoryCache) {
            b = (Bitmap) mMemoryCache.get(id);
        }
        if (b == null) {
            synchronized (mAsyncLoadMap) {
                mAsyncLoadMap.put(holder, id);
            }
            startAsyncLoader();
        }
        return b;
    }
    
    private void startAsyncLoader() {
        if (mAsyncLoader != null) {
            mAsyncLoader.cancel(true);
        }
        mAsyncLoader = new AsyncTask<Void, Void, Void> () {
            // Decode image in background.
            @Override
            protected Void doInBackground(Void... params) {
                Set<ViewHolder> holders = null;
                synchronized (mAsyncLoadMap){
                    holders = new HashSet<ViewHolder>(mAsyncLoadMap.keySet());
                }
                for (ViewHolder holder : holders) {
                    int id = mAsyncLoadMap.get(holder);
                    final Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                          getApplicationContext().getContentResolver(), id,
                          MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    addBitmapToMemoryCache(holder, id, bitmap);
                    final ImageView iv = holder.imageview;
                    iv.post(new Runnable() {
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });
                    synchronized (mAsyncLoadMap) {
                        mAsyncLoadMap.remove(holder);
                    }
                }
                return null;
            }
        }.execute();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        return new CursorLoader(this, 
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCount == data.getCount()) {
            return;
        }
        mCount = data.getCount();
        int image_column_index = data.getColumnIndex(MediaStore.Images.Media._ID);
        mArrPath = new String[mCount];
        mIds = new HashMap<Integer, Integer>(mCount);
        mThumbnailsselection = new boolean[mCount];
        for (int i = 0; i < mCount; i++) {
            data.moveToPosition(i);
            int id = data.getInt(image_column_index);
            int dataColumnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA);
            mIds.put(i, id);
            mArrPath[i]= data.getString(dataColumnIndex);
        }
        GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        mImageAdapter = new ImageAdapter();
        imagegrid.setAdapter(mImageAdapter);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
