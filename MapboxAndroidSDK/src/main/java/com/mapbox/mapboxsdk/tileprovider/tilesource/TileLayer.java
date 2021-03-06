package com.mapbox.mapboxsdk.tileprovider.tilesource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.tileprovider.BitmapPool;
import com.mapbox.mapboxsdk.tileprovider.MapTile;
import com.mapbox.mapboxsdk.tileprovider.ReusableBitmapDrawable;
import com.mapbox.mapboxsdk.tileprovider.constants.TileLayerConstants;
import com.mapbox.mapboxsdk.tileprovider.util.LowMemoryException;
import com.mapbox.mapboxsdk.views.util.constants.MapViewConstants;

import java.io.InputStream;

public class TileLayer implements ITileLayer, TileLayerConstants, MapViewConstants {

    protected String mUrl;
    protected String mName;
    protected String mDescription;
    protected String mShortAttribution;
    protected String mLegend;

    protected float mMinimumZoomLevel = 1;
    protected float mMaximumZoomLevel = 16;
    protected BoundingBox mBoundingBox = null;
    protected LatLng mCenter = new LatLng(0,0);
    private final int mTileSizePixels = DEFAULT_TILE_SIZE;

    public TileLayer(final String aUrl) {
        mUrl = aUrl;
    }

    public TileLayer setURL(final String aUrl) {
        mUrl = aUrl;
        return this;
    }

    public String getTileURL(final MapTile aTile, boolean hdpi) {
        return mUrl
                .replace("{z}", String.valueOf(aTile.getZ()))
                .replace("{x}", String.valueOf(aTile.getX()))
                .replace("{y}", String.valueOf(aTile.getY()))
                .replace("{2x}", hdpi ? "@2x" : "");
    }

    @Override
    public float getMinimumZoomLevel() {
        return mMinimumZoomLevel;
    }

    @Override
    public float getMaximumZoomLevel() {
        return mMaximumZoomLevel;
    }

    @Override
    public int getTileSizePixels() {
        return mTileSizePixels;
    }

    @Override
    public Drawable getDrawable(final InputStream aFileInputStream) throws LowMemoryException {
        try {
            // default implementation will load the file as a bitmap and create
            // a BitmapDrawable from it
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            BitmapPool.getInstance().applyReusableOptions(bitmapOptions);
            final Bitmap bitmap = BitmapFactory.decodeStream(aFileInputStream, null, bitmapOptions);
            if (bitmap != null) {
                return new ReusableBitmapDrawable(bitmap);
            }
        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError loading bitmap");
            System.gc();
            throw new LowMemoryException(e);
        }
        return null;
    }

    final private String TAG = "OnlineTileSource";

    @Override
    public BoundingBox getBoundingBox() { return mBoundingBox; }

    @Override
    public LatLng getCenterCoordinate() { return mCenter; }


    @Override
    public float getCenterZoom() {
        if(mCenter != null) {
            return (float)mCenter.getAltitude();
        }
        return Math.round(mMaximumZoomLevel + mMinimumZoomLevel)/2;
    }

    @Override
    public String getShortName() { return mName; }

    @Override
    public String getLongDescription() { return mDescription; }

    @Override
    public String getShortAttribution() { return mShortAttribution; }

    @Override
    public String getLongAttribution() { return mShortAttribution; }

    @Override
    public String getLegend() { return mLegend; }
}
