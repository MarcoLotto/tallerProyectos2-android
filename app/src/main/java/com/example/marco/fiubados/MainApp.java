package com.example.marco.fiubados;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

import android.app.Application;

@ReportsCrashes(
        formUri = "https://lgd.cloudant.com/acra-fiuba-campus-movil/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "GENERATED_USERNAME_WITH_WRITE_PERMISSIONS",
        formUriBasicAuthPassword = "GENERATED_PASSWORD",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST
)

/**
 * Created by ezequiel on 23/04/15.
 */
public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
