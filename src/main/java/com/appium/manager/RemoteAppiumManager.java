package com.appium.manager;

import com.appium.utils.Api;
import com.appium.utils.AvailablePorts;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class RemoteAppiumManager {
    static AppiumDriverLocalService appiumDriverLocalService;
    private static final Logger LOGGER = Logger.getLogger(Class.class.getSimpleName());

    public static AppiumDriverLocalService getAppiumDriverLocalService() {
        return appiumDriverLocalService;
    }

    public static void setAppiumDriverLocalService(
            AppiumDriverLocalService appiumDriverLocalService) {
        RemoteAppiumManager.appiumDriverLocalService = appiumDriverLocalService;
    }

    public URL getAppiumUrl() {
        return getAppiumDriverLocalService().getUrl();
    }

    public void destroyAppiumNode(String host) throws IOException {
        if (host.equals("127.0.0.1")) {
            getAppiumDriverLocalService().stop();
            if (getAppiumDriverLocalService().isRunning()) {
                LOGGER.info("AppiumServer didn't shut... Trying to quit again....");
                getAppiumDriverLocalService().stop();
            }
        } else {

        }

    }


    public String getRemoteWDHubIP(String host) throws IOException {
        if (host.equals("127.0.0.1")) {
            return getAppiumUrl().toString();
        } else {
            String hostIP = "http://" + host;
            String appiumRunningPort = new JSONObject(new Api().getResponse(hostIP + ":4567"
                    + "/appium/isRunning").body().string()).get("port").toString();
            return hostIP + ":" + appiumRunningPort + "/wd/hub";
        }

    }

    public void startAppiumServer(String host) throws Exception {
        if (host.equals("127.0.0.1")) {
            System.out.println(
                    "**************************************************************************\n");
            System.out.println("Starting Appium Server......");
            System.out.println(
                    "**************************************************************************\n");
            AppiumDriverLocalService appiumDriverLocalService;
            AppiumServiceBuilder builder =
                    new AppiumServiceBuilder().withAppiumJS(new File(ConfigFileManager
                            .configFileMap.get("APPIUM_JS_PATH")))
                            .withArgument(GeneralServerFlag.LOG_LEVEL, "info").withLogFile(new File(
                            System.getProperty("user.dir")
                                    + "/target/appiumlogs/appium_logs.txt"))
                            .withIPAddress(host)
                            .usingAnyFreePort();
        /* and so on */
            ;
            appiumDriverLocalService = builder.build();
            appiumDriverLocalService.start();
            System.out.println(
                    "**************************************************************************\n");
            System.out.println("Appium Server Started at......"
                    + appiumDriverLocalService.getUrl());
            System.out.println(
                    "**************************************************************************\n");
            setAppiumDriverLocalService(appiumDriverLocalService);
        } else {
              new Api().getResponse(host + ":4567"
                    + "/appium/start").body().string();
        }

    }

}