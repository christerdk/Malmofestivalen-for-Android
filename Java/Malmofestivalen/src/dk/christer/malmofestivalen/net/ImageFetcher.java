package dk.christer.malmofestivalen.net;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.ImageView;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.data.images.ImageCache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple image fetcher with an ImageCache for caching.
 *
 * Large parts are basic stuff copied from
 * http://developer.android.com/training/displaying-bitmaps/process-bitmap.html and
 * http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 *
 * The below copyright info should be added to some "about" dialog
 */
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ImageFetcher {
    private static final boolean LOG = true;

    private ImageCache mImageCache;
    private Bitmap mPlaceHolder;
    private static ImageFetcher sInstance;
    public static final int IMAGE_LOADED = 0;

    public static synchronized ImageFetcher getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new ImageFetcher(context);
        }

        return sInstance;
    }

    /**
     * Hidden singleton constructor
     * @param context The context
     */
    private ImageFetcher(final Context context) {
        mImageCache = ImageCache.newInstance(context);

        // Could be done in another thread, but a bit overkill in this case
        mPlaceHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.question);
    }

    /**
     *
     * @param imageView The image view to set the image to. No strong references are kept.
     * @param url The url of the image to load. Will be set to a placeholder if the url is null.
     * @param resultReceiver A result receiver to signal IMAGE_LOADED to when done. Can be null. No strong references are kept.
     */
    public void download(final ImageView imageView, final String url, ResultReceiver resultReceiver) {
        if (imageView == null) {
            return;
        }

        if (url == null) {
            imageView.setImageBitmap(mPlaceHolder);
            return;
        }

        Bitmap bitmap = mImageCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            if (resultReceiver != null) {
                resultReceiver.send(IMAGE_LOADED, null);
            }
        } else if (cancelPotentialWork(url, imageView)) {
            final DownloadBitmapTask task = new DownloadBitmapTask(imageView, resultReceiver);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(imageView.getResources(), mPlaceHolder, task);
            imageView.setImageDrawable(asyncDrawable);

            task.execute(url);
        }
    }

    public static boolean cancelPotentialWork(final String uri, final ImageView imageView) {
        final DownloadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String associatedUri = bitmapWorkerTask.mUri;
            if (!uri.equals(associatedUri)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static DownloadBitmapTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> mViewReference;
        private String mUri;
        private final WeakReference<ResultReceiver> mResultReceiverReference;


        public DownloadBitmapTask(final ImageView imageView, ResultReceiver resultReceiver) {
            mViewReference = new WeakReference<ImageView>(imageView);
            mResultReceiverReference = new WeakReference<ResultReceiver>(resultReceiver);
        }

        @Override
        protected Bitmap doInBackground(String... uris) {
            if (isCancelled()) {
                return null;
            }

            try {
                mUri = uris[0];
                Bitmap bitmap = downloadImage(mUri);

                if (bitmap != null) {
                    mImageCache.put(mUri, bitmap);
                }

                return bitmap;
            } catch (Exception e) {
                if (LOG) {
                    Log.e("Malmöfestivalen", "Failed to download an image");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                return;
            }

            if (mViewReference != null && bitmap != null) {
                final ImageView imageView = mViewReference.get();
                final DownloadBitmapTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    ResultReceiver resultReceiver = mResultReceiverReference.get();
                    if (resultReceiver != null) {
                        resultReceiver.send(IMAGE_LOADED, null);
                    }
                }
            }
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<DownloadBitmapTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap placeholder,
                             DownloadBitmapTask bitmapWorkerTask) {
            super(res, placeholder);
            bitmapWorkerTaskReference = new WeakReference<DownloadBitmapTask>(bitmapWorkerTask);
        }

        public DownloadBitmapTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static Bitmap downloadImage(final String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        InputStream is = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is));
        is.close();

        return bitmap;
    }
}
