package com.example.senannonces;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class SenAnnoncesApp extends Application {

    private static final String TAG = "SenAnnonces";

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "CRASH!", throwable);

            StringBuilder sb = new StringBuilder();
            sb.append("Erreur: ").append(throwable.getClass().getSimpleName()).append("\n");
            sb.append("Message: ").append(throwable.getMessage()).append("\n\n");
            for (StackTraceElement el : throwable.getStackTrace()) {
                if (el.getClassName().contains("senannonces")) {
                    sb.append("at ").append(el.getClassName()).append(".").append(el.getMethodName())
                      .append("(").append(el.getFileName()).append(":").append(el.getLineNumber()).append(")\n");
                }
            }

            Intent intent = new Intent(getApplicationContext(), CrashActivity.class);
            intent.putExtra("error", sb.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            System.exit(2);
        });
    }
}
