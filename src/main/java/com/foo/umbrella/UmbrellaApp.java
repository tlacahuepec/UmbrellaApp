package com.foo.umbrella;

import android.app.Application;

import com.foo.umbrella.data.ApiServicesProvider;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class UmbrellaApp extends Application {

  private ApiServicesProvider apiServicesProvider;

  @Override public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
    apiServicesProvider = new ApiServicesProvider(this);
  }

  public ApiServicesProvider getApiServicesProvider() {
    return apiServicesProvider;
  }
}
