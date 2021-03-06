package com.mapbox.mapboxsdk.tileprovider;

import com.mapbox.mapboxsdk.tileprovider.tilesource.ITileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.tileprovider.modules.*;
import com.mapbox.mapboxsdk.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;

public class MapTileLayerBasic extends MapTileLayerArray implements IMapTileProviderCallback {
    Context context;

    /**
     *
     * @param pContext
     * @param pTileSource
     * @param mapView
     */
    public MapTileLayerBasic(final Context pContext,
                             final ITileLayer pTileSource,
                             MapView mapView) {
        super(pTileSource, new SimpleRegisterReceiver(pContext));
        this.context = pContext;

        final MapTileDownloader downloaderProvider = new MapTileDownloader(
                pTileSource,
                new NetworkAvailabilityCheck(pContext),
                mapView);

        for (MapTileModuleLayerBase provider: mTileProviderList) {
            if (provider.getClass().isInstance(MapTileDownloader.class)) {
                mTileProviderList.remove(provider);
            }
        }

        mTileProviderList.add(downloaderProvider);
    }
    
    public void addTileSource(final Context pContext,
            final ITileLayer pTileSource,
            MapView mapView) {
    	final MapTileDownloader downloaderProvider = new MapTileDownloader(
                pTileSource,
                new NetworkAvailabilityCheck(pContext),
                mapView);
        mTileProviderList.add(downloaderProvider);
    }
    
    public void removeTileSource(final ITileLayer pTileSource) {
    	for (MapTileModuleLayerBase provider: mTileProviderList) {
            if (provider.getTileSource() == pTileSource) {
                mTileProviderList.remove(provider);
                return;
            }
        }
    }
}