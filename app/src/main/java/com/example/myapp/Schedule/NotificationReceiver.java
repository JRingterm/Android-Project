package com.example.myapp.Schedule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapp.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long eventId = intent.getLongExtra("eventId", 0);
        String eventTitle = intent.getStringExtra("eventTitle");
        String Channel_id = intent.getStringExtra("Channel_ID");
        Log.v("확인용","onReceive 성공");

        // 알림 표시
        showNotification(context, eventId, eventTitle, Channel_id);
    }

    private void showNotification(Context context, long eventId, String eventTitle, String Channel_id) {
        Log.v("확인용","showNotification 성공");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Channel_id)//,chanel_id 뺐더니 되는거같기도..
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("일정 알림")
                .setContentText(eventTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        Log.v("확인용","빌더 성공");
        // Notification 표시
        if (notificationManager != null) {
            notificationManager.notify((int) eventId, builder.build());
            Log.v("확인용","알림 성공");
        }
    }
}
