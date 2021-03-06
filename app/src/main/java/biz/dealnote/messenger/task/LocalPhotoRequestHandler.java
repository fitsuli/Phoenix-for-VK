package biz.dealnote.messenger.task;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import biz.dealnote.messenger.db.Stores;

public class LocalPhotoRequestHandler extends RequestHandler {

    private final Context mContext;

    public LocalPhotoRequestHandler(Context context) {
        mContext = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return data.uri != null && data.uri.getScheme() != null && data.uri.getScheme().equals("content");
    }

    @Override
    public RequestHandler.Result load(Request data, int arg1) {
        long imageId = Long.parseLong(data.uri.getLastPathSegment());

        boolean isVideo = data.uri.getPath().contains("videos");

        Bitmap bm;
        if (!isVideo) {
            bm = Stores.getInstance()
                    .localPhotos()
                    .getImageThumbnail(imageId);
        } else {
            bm = Stores.getInstance()
                    .localPhotos()
                    .getVideoThumbnail(imageId);
        }

        return new RequestHandler.Result(bm, Picasso.LoadedFrom.DISK);
    }
}