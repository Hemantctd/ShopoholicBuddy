package com.shopoholicbuddy.coachmark;

import android.app.Activity;
import android.view.View;

import com.shopoholicbuddy.R;
import com.shopoholicbuddy.utils.AppSharedPreference;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class CoachMarksImpl {
    private static CoachMarksImpl coachMarks = null;

    /**
     * This method is used to provide the instance of Network Connection Class
     * @return instance of Network Connection Class
     */
    public static CoachMarksImpl getInstance() {
        if (coachMarks == null) {
            return new CoachMarksImpl();
        } else {
            return coachMarks;
        }
    }

    /**
     * function to set home coach marks
     * @param filter
     * @param map
     * @param search
     */
    public void setHomeScreenCoachMarks(Activity mActivity, View menu, View search, View map, View filter, View categoryDeal, View percentageDeal, View location, View category, View fabMenu) {
        GuideView.Builder addDealBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.add_deal_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(fabMenu).setGuideListener(view -> {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_HOME_SEEN, true);
                });

        GuideView.Builder categoryBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.category_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(category).setGuideListener(view -> addDealBuilder.build().show());

        GuideView.Builder locationBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.location_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(location).setGuideListener(view -> categoryBuilder.build().show());

        GuideView.Builder percentageDealBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.percentage_deal_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(percentageDeal).setGuideListener(view -> locationBuilder.build().show());

         GuideView.Builder categoryDealBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.category_deal_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(categoryDeal).setGuideListener(view -> percentageDealBuilder.build().show());

        GuideView.Builder filterBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.filter_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(filter).setGuideListener(view -> categoryDealBuilder.build().show());

        GuideView.Builder mapBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.map_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(map).setGuideListener(view -> filterBuilder.build().show());

        GuideView.Builder searchBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.search_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(search).setGuideListener(view -> mapBuilder.build().show());

        GuideView.Builder menuBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.menu_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(menu).setGuideListener(view -> searchBuilder.build().show());

        menuBuilder.build().show();
    }

    /**
     * function to set add product coach marks
     * @param mActivity
     * @param product
     * @param service
     */
    public void setAddProductOptionCoachMarks(Activity mActivity, View product, View service) {
        GuideView.Builder serviceBuilder = new GuideView.Builder(mActivity).setContentText("Easy to click here and post the Service offers you are expert at.").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(service).setGuideListener(view -> {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_POPUP_SEEN, true);
                });

        GuideView.Builder productBuilder = new GuideView.Builder(mActivity).setContentText("Post the products which you want to sell or promote").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(product).setGuideListener(view -> serviceBuilder.build().show());

        productBuilder.build().show();

    }

    /**
     * function to set side menu coach marks
     * @param order
     * @param deals
     * @param dedicatedRequest
     * @param offlineDeals
     * @param mActivity
     * @param profile
     * @param request
     * @param setting
     */
    public void sideMenuCoachMarks(Activity mActivity, View setting, View profile, View order, View deals, View offlineDeals, View request, View dedicatedRequest) {
        GuideView.Builder dedicatedRequestBuilder = new GuideView.Builder(mActivity).setContentText("List of requests which are just for you").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(dedicatedRequest).setGuideListener(view -> {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_SIDE_MENU_SEEN, true);
                });

        GuideView.Builder requestBuilder = new GuideView.Builder(mActivity).setContentText("Product or service hunt requests waiting for you to respond").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(request).setGuideListener(view -> dedicatedRequestBuilder.build().show());

        GuideView.Builder offlineDealsBuilder = new GuideView.Builder(mActivity).setContentText("Deals which you still need to post").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(offlineDeals).setGuideListener(view -> requestBuilder.build().show());

        GuideView.Builder dealsBuilder = new GuideView.Builder(mActivity).setContentText("List of your posted deals").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(deals).setGuideListener(view -> offlineDealsBuilder.build().show());

        GuideView.Builder orderBuilder = new GuideView.Builder(mActivity).setContentText("List of you’re your orders so far").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(order).setGuideListener(view -> dealsBuilder.build().show());

        GuideView.Builder profileBuilder = new GuideView.Builder(mActivity).setContentText("Check the details of your profile").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(profile).setGuideListener(view -> orderBuilder.build().show());

        GuideView.Builder settingBuilder = new GuideView.Builder(mActivity).setContentText("Check all the settings and Policies").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(setting).setGuideListener(view -> profileBuilder.build().show());

        settingBuilder.build().show();
    }
}
