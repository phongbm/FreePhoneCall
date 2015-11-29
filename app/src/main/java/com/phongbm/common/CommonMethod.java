package com.phongbm.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat.Builder;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommonMethod implements Cloneable, Serializable {

    private static class LazyInit {
        private static final CommonMethod INSTANCE = new CommonMethod();
    }

    public static CommonMethod getInstance() {
        return LazyInit.INSTANCE;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException();
    }

    protected Object readResolve() {
        return Profile.getInstance();
    }

    private CommonMethod() {
    }

    public void pushNotification(Activity srcActivity, Class destActivity, String content,
                                 int notificationId, int icon, boolean noClear) {
        if (noClear) {
            // outGoingCall, inComingCall
            Builder builder = new Builder(srcActivity).setSmallIcon(icon).setContentTitle("Free Phone Call")
                    .setContentText(content).setAutoCancel(false);
            Intent intent = new Intent(srcActivity, destActivity);
            PendingIntent pendingIntent = PendingIntent.getService(srcActivity, notificationId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager)
                    srcActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(notificationId, notification);
        } else {
            // missedCall
            Builder builder = new Builder(srcActivity).setSmallIcon(icon).setContentTitle("Free Phone Call")
                    .setContentText(content).setAutoCancel(true);
            Intent intent = new Intent(srcActivity, destActivity);
            PendingIntent pendingIntent = PendingIntent.getActivity(srcActivity, notificationId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager)
                    srcActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    public String convertTimeToString(int timeCall) {
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeCall);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeCall) - minutes * 60;
        return (minutes < 10 ? "0" + minutes : "" + minutes)
                + ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
    }

    public String getCurrentDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public static void uploadAvatar(ParseUser parseUser, Bitmap avatar) {
        if (parseUser == null) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (bytes != null) {
            ParseFile parseFile = new ParseFile(bytes);
            parseUser.put("avatar", parseFile);
            parseUser.saveInBackground();
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap decodeSampledBitmapFromResource(String uri, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize * 2;
    }

    public int getOrientation(String path) {
        int rotate = 0;
        try {
            File file = new File(path);
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public Bitmap getBitmap(int orientation, Bitmap bitmap) {
        if (orientation == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    public int convertSizeIcon(float density, int sizeDp) {
        return (int) (sizeDp * (density / 160));
    }

    public String getPathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

}