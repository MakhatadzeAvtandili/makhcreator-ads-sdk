package com.makhcreator.ads.sdk.format;

import static com.makhcreator.ads.sdk.util.Constant.ADMOB;
import static com.makhcreator.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.makhcreator.ads.sdk.util.Constant.APPLOVIN;
import static com.makhcreator.ads.sdk.util.Constant.APPLOVIN_DISCOVERY;
import static com.makhcreator.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.makhcreator.ads.sdk.util.Constant.MOPUB;
import static com.makhcreator.ads.sdk.util.Constant.NONE;
import static com.makhcreator.ads.sdk.util.Constant.STARTAPP;
import static com.makhcreator.ads.sdk.util.Constant.UNITY;
import static com.makhcreator.ads.sdk.util.Constant.UNITY_ADS_BANNER_HEIGHT_MEDIUM;
import static com.makhcreator.ads.sdk.util.Constant.UNITY_ADS_BANNER_WIDTH_MEDIUM;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.applovin.adview.AppLovinAdView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.makhcreator.ads.sdk.R;
import com.makhcreator.ads.sdk.helper.AppLovinCustomEventBanner;
import com.makhcreator.ads.sdk.util.Tools;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class BannerAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private AdView adView;
        private MoPubView moPubView;
        private AppLovinAdView appLovinAdView;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String appLovinBannerZoneId = "";
        private String mopubBannerId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadBannerAd();
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Builder setAdMobBannerId(String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        public Builder setUnityBannerId(String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        public Builder setAppLovinBannerId(String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        public Builder setAppLovinBannerZoneId(String appLovinBannerZoneId) {
            this.appLovinBannerZoneId = appLovinBannerZoneId;
            return this;
        }

        public Builder setMopubBannerId(String mopubBannerId) {
            this.mopubBannerId = mopubBannerId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdView = activity.findViewById(R.id.startapp_banner_view_container);
                        Banner banner = new Banner(activity, new BannerListener() {
                            @Override
                            public void onReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                                Log.d(TAG, adNetwork + " failed load startapp banner ad : ");
                            }

                            @Override
                            public void onImpression(View view) {

                            }

                            @Override
                            public void onClick(View banner) {
                            }
                        });
                        startAppAdView.addView(banner);
                        break;

                    case UNITY:
                        RelativeLayout unityAdView = activity.findViewById(R.id.unity_banner_view_container);
                        BannerView bottomBanner = new BannerView(activity, unityBannerId, new UnityBannerSize(UNITY_ADS_BANNER_WIDTH_MEDIUM, UNITY_ADS_BANNER_HEIGHT_MEDIUM));
                        bottomBanner.setListener(new BannerView.IListener() {
                            @Override
                            public void onBannerLoaded(BannerView bannerView) {
                                unityAdView.setVisibility(View.VISIBLE);
                                Log.d("Unity_banner", "ready");
                            }

                            @Override
                            public void onBannerClick(BannerView bannerView) {

                            }

                            @Override
                            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                                Log.d("SupportTest", "Banner Error" + bannerErrorInfo);
                                unityAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onBannerLeftApplication(BannerView bannerView) {

                            }
                        });
                        unityAdView.addView(bottomBanner);
                        bottomBanner.load();
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + unityBannerId);
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        RelativeLayout appLovinAdView = activity.findViewById(R.id.applovin_banner_view_container);
                        MaxAdView maxAdView = new MaxAdView(appLovinBannerId, activity);
                        maxAdView.setListener(new MaxAdViewAdListener() {
                            @Override
                            public void onAdExpanded(MaxAd ad) {

                            }

                            @Override
                            public void onAdCollapsed(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                appLovinAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {

                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {

                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                appLovinAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                            }
                        });

                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                        int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                        if (darkTheme) {
                            maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundDark));
                        } else {
                            maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundLight));
                        }
                        appLovinAdView.addView(maxAdView);
                        maxAdView.loadAd();
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + appLovinBannerId);
                        break;

                    case APPLOVIN_DISCOVERY:
                        RelativeLayout appLovinDiscoveryAdView = activity.findViewById(R.id.applovin_discovery_banner_view_container);
                        AdRequest.Builder builder = new AdRequest.Builder();
                        Bundle bannerExtras = new Bundle();
                        bannerExtras.putString("zone_id", appLovinBannerZoneId);
                        builder.addCustomEventExtrasBundle(AppLovinCustomEventBanner.class, bannerExtras);

                        boolean isTablet2 = AppLovinSdkUtils.isTablet(activity);
                        AppLovinAdSize adSize = isTablet2 ? AppLovinAdSize.LEADER : AppLovinAdSize.BANNER;
                        this.appLovinAdView = new AppLovinAdView(adSize, activity);
                        this.appLovinAdView.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                appLovinDiscoveryAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                appLovinDiscoveryAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }
                        });
                        appLovinDiscoveryAdView.addView(this.appLovinAdView);
                        this.appLovinAdView.loadNextAd();
                        break;

                    case MOPUB:
                        RelativeLayout mopubAdView = activity.findViewById(R.id.mopub_banner_view_container);
                        moPubView = new MoPubView(activity);
                        moPubView.setAdUnitId(mopubBannerId);
                        mopubAdView.addView(moPubView);
                        moPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
                        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
                            @Override
                            public void onBannerLoaded(@NonNull MoPubView moPubView) {
                                mopubAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
                                mopubAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                            }

                            @Override
                            public void onBannerClicked(MoPubView moPubView) {

                            }

                            @Override
                            public void onBannerExpanded(MoPubView moPubView) {

                            }

                            @Override
                            public void onBannerCollapsed(MoPubView moPubView) {

                            }
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + mopubBannerId);
                        break;

                    case NONE:
                        //do nothing
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void loadBackupBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdView = activity.findViewById(R.id.startapp_banner_view_container);
                        Banner banner = new Banner(activity, new BannerListener() {
                            @Override
                            public void onReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.GONE);
                                Log.d(TAG, adNetwork + " failed load startapp banner ad : ");
                            }

                            @Override
                            public void onImpression(View view) {

                            }

                            @Override
                            public void onClick(View banner) {
                            }
                        });
                        startAppAdView.addView(banner);
                        break;

                    case UNITY:
                        RelativeLayout unityAdView = activity.findViewById(R.id.unity_banner_view_container);
                        BannerView bottomBanner = new BannerView(activity, unityBannerId, new UnityBannerSize(UNITY_ADS_BANNER_WIDTH_MEDIUM, UNITY_ADS_BANNER_HEIGHT_MEDIUM));
                        bottomBanner.setListener(new BannerView.IListener() {
                            @Override
                            public void onBannerLoaded(BannerView bannerView) {
                                unityAdView.setVisibility(View.VISIBLE);
                                Log.d("Unity_banner", "ready");
                            }

                            @Override
                            public void onBannerClick(BannerView bannerView) {

                            }

                            @Override
                            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                                Log.d("SupportTest", "Banner Error" + bannerErrorInfo);
                                unityAdView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onBannerLeftApplication(BannerView bannerView) {

                            }
                        });
                        unityAdView.addView(bottomBanner);
                        bottomBanner.load();
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + unityBannerId);
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        RelativeLayout appLovinAdView = activity.findViewById(R.id.applovin_banner_view_container);
                        MaxAdView maxAdView = new MaxAdView(appLovinBannerId, activity);
                        maxAdView.setListener(new MaxAdViewAdListener() {
                            @Override
                            public void onAdExpanded(MaxAd ad) {

                            }

                            @Override
                            public void onAdCollapsed(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                appLovinAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {

                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {

                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                appLovinAdView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                            }
                        });

                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                        int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height);
                        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                        if (darkTheme) {
                            maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundDark));
                        } else {
                            maxAdView.setBackgroundColor(activity.getResources().getColor(R.color.colorBackgroundLight));
                        }
                        appLovinAdView.addView(maxAdView);
                        maxAdView.loadAd();
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + appLovinBannerId);
                        break;

                    case APPLOVIN_DISCOVERY:
                        RelativeLayout appLovinDiscoveryAdView = activity.findViewById(R.id.applovin_discovery_banner_view_container);
                        AdRequest.Builder builder = new AdRequest.Builder();
                        Bundle bannerExtras = new Bundle();
                        bannerExtras.putString("zone_id", appLovinBannerZoneId);
                        builder.addCustomEventExtrasBundle(AppLovinCustomEventBanner.class, bannerExtras);

                        boolean isTablet2 = AppLovinSdkUtils.isTablet(activity);
                        AppLovinAdSize adSize = isTablet2 ? AppLovinAdSize.LEADER : AppLovinAdSize.BANNER;
                        this.appLovinAdView = new AppLovinAdView(adSize, activity);
                        this.appLovinAdView.setAdLoadListener(new AppLovinAdLoadListener() {
                            @Override
                            public void adReceived(AppLovinAd ad) {
                                appLovinDiscoveryAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void failedToReceiveAd(int errorCode) {
                                appLovinDiscoveryAdView.setVisibility(View.GONE);
                            }
                        });
                        appLovinDiscoveryAdView.addView(this.appLovinAdView);
                        this.appLovinAdView.loadNextAd();
                        break;

                    case MOPUB:
                        RelativeLayout mopubAdView = activity.findViewById(R.id.mopub_banner_view_container);
                        moPubView = new MoPubView(activity);
                        moPubView.setAdUnitId(mopubBannerId);
                        mopubAdView.addView(moPubView);
                        moPubView.loadAd(MoPubView.MoPubAdSize.HEIGHT_50);
                        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
                            @Override
                            public void onBannerLoaded(@NonNull MoPubView moPubView) {
                                mopubAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
                                mopubAdView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onBannerClicked(MoPubView moPubView) {

                            }

                            @Override
                            public void onBannerExpanded(MoPubView moPubView) {

                            }

                            @Override
                            public void onBannerCollapsed(MoPubView moPubView) {

                            }
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + mopubBannerId);
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

    }

}
