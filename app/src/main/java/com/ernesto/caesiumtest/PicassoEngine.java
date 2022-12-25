package com.ernesto.caesiumtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

public class PicassoEngine implements ImageEngine {

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        VideoRequestHandler videoRequestHandler = new VideoRequestHandler();
        if (PictureMimeType.isContent(url) || PictureMimeType.isHasHttp(url)) {
            Picasso.get().load(Uri.parse(url)).into(imageView);
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                Picasso picasso = new Picasso.Builder(context.getApplicationContext())
                        .addRequestHandler(videoRequestHandler)
                        .build();
                picasso.load(videoRequestHandler.SCHEME_VIDEO + ":" + url)
                        .into(imageView);
            } else {
                Picasso.get().load(new File(url)).into(imageView);
            }
        }
    }

    @Override
    public void loadImage(Context context, ImageView imageView, String url, int maxWidth, int maxHeight) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Picasso picasso = new Picasso.Builder(context)
                .build();
        RequestCreator request = picasso.load(PictureMimeType.isContent(url) ? Uri.parse(url) : Uri.fromFile(new File(url)));
        request.config(Bitmap.Config.RGB_565);
        if (maxWidth > 0 && maxHeight > 0) {
            request.resize(maxWidth, maxHeight);
        }
        request.into(imageView);
    }

    @Override
    public void loadAlbumCover(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        VideoRequestHandler videoRequestHandler = new VideoRequestHandler();
        if (PictureMimeType.isContent(url)) {
            Picasso.get()
                    .load(Uri.parse(url))
                    .resize(180, 180)
                    .centerCrop()
                    .noFade()
                    .transform(new RoundedCornersTransform(8))
                    .placeholder(R.drawable.ps_image_placeholder)
                    .into(imageView);
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                Picasso picasso = new Picasso.Builder(context.getApplicationContext())
                        .addRequestHandler(videoRequestHandler)
                        .build();
                picasso.load(videoRequestHandler.SCHEME_VIDEO + ":" + url)
                        .resize(180, 180)
                        .centerCrop()
                        .noFade()
                        .transform(new RoundedCornersTransform(8))
                        .placeholder(R.drawable.ps_image_placeholder)
                        .into(imageView);
            } else {
                Picasso.get()
                        .load(new File(url))
                        .resize(180, 180)
                        .centerCrop()
                        .noFade()
                        .transform(new RoundedCornersTransform(8))
                        .placeholder(R.drawable.ps_image_placeholder)
                        .into(imageView);
            }
        }
    }

    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        VideoRequestHandler videoRequestHandler = new VideoRequestHandler();
        if (PictureMimeType.isContent(url)) {
            Picasso.get()
                    .load(Uri.parse(url))
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .placeholder(R.drawable.ps_image_placeholder)
                    .into(imageView);
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                Picasso picasso = new Picasso.Builder(context.getApplicationContext())
                        .addRequestHandler(videoRequestHandler)
                        .build();
                picasso.load(videoRequestHandler.SCHEME_VIDEO + ":" + url)
                        .resize(200, 200)
                        .centerCrop()
                        .noFade()
                        .placeholder(R.drawable.ps_image_placeholder)
                        .into(imageView);
            } else {
                Picasso.get()
                        .load(new File(url))
                        .resize(200, 200)
                        .centerCrop()
                        .noFade()
                        .placeholder(R.drawable.ps_image_placeholder)
                        .into(imageView);
            }
        }
    }

    @Override
    public void pauseRequests(Context context) {
        Picasso.get().pauseTag(context);
    }

    @Override
    public void resumeRequests(Context context) {
        Picasso.get().resumeTag(context);
    }


    private PicassoEngine() {
    }

    private static final class InstanceHolder {
        static final PicassoEngine instance = new PicassoEngine();
    }

    public static PicassoEngine createPicassoEngine() {
        return InstanceHolder.instance;
    }
}