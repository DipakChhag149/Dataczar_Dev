/*
 * Copyright (c)  to Samrt Sense . Ai on 2021.
 */

package com.dataczar.main.activity;

public class WSMethods
{

    public static String WSURL                  = "https://connect.dataczar.com";

    public static String URL_REGISTER           = WSURL + "/register";
    public static String URL_FORGOTPASS         = WSURL + "/password/reset";

    public static String GETLOGINSTATUS         = WSURL + "/api/login_status";
    public static String CHECKLOGINAUTH         = WSURL + "/api/login";
    public static String GETUSERPROFILE         = WSURL + "/api/home_data";

    public static String CHECKLOGINAUTH_GOOGLE  = WSURL + "/api/login/google/callback";

    public static String  DashboardPageUrl      = WSURL + "/ar?r=mobileapp";
    public static String  AddPostPageUrl        = WSURL + "/ar?r=mobileapp/posts";

    public static String  UserProfile           = WSURL + "/profile/user";
    public static String  NotificationSettings  = WSURL + "/profile/notifications";
    public static String  ChangePassword        = WSURL + "/profile/password";

    public static String  UserSettings          = WSURL + "/ar?r=profile";
    public static String  ManageAccount         = WSURL + "/ar?r=accounts";
    public static String  Legal                 = WSURL + "/ar?r=legal";
    public static String  Help                  = WSURL + "/ar?r=education";
    public static String  Billings              = WSURL + "/ar?r=billing";

    public static String  Website               = WSURL + "/ar?r=websites";
    public static String  Email                 = WSURL + "/ar?r=emails";
    public static String  List                  = WSURL + "/ar?r=lists";
    public static String  Domain                = WSURL + "/ar?r=domains";
    public static String  Content               = WSURL + "/ar?r=content";

    public static String SWITCHACCOUNT          = WSURL + "/accounts/api/switch/";

    public static String NOTIFICATION_ALLLIST   = WSURL + "/api/notifications/all";
    public static String NOTIFICATION_COUNT     = WSURL + "/notifications/data";
    public static String NOTIFICATION_READ      = WSURL + "/notification/";
    public static String NOTIFICATION_ARCHIVED  = WSURL + "/notification/archive/";

    public static String GET_EBOOKS  = WSURL + "/api/education/ebooks";

    public static String LOGOUT  = WSURL + "/api/logout";
    public static String ADD_NOTIFICATION  = WSURL + "/api/notifications/token";
    public static String DELETE_NOTIFICATION  = WSURL + "/api/notifications/token";


    public static String GET_MY_IMAGE_LIST  = WSURL + "/content/list";


}
