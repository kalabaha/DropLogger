package name.kalabaha.droplogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class DropLogger {

    final static private String APP_KEY = "myii2xbmnyj27o3";
    final static private String APP_SECRET = "qcds61m551uqzvy";

    final static private String DROP_LOGGER_PREFERENCES = "drop_logger_preferences";
    final static private String DROP_LOGGER_TOKEN = "drop_logger_token";

    private Context mContext;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    public DropLogger(Context context) {

        mContext = context;

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);

        SharedPreferences preferences = mContext.getSharedPreferences(DROP_LOGGER_PREFERENCES, Context.MODE_PRIVATE);
        String mSessionToken = preferences.getString(DROP_LOGGER_TOKEN, null);
        if (mSessionToken != null) {
            mDBApi.getSession().setOAuth2AccessToken(mSessionToken);
        } else {
            mDBApi.getSession().startOAuth2Authentication(mContext);
        }
    }

    public void onActivityResume() {
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                SharedPreferences preferences = mContext.getSharedPreferences(DROP_LOGGER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(DROP_LOGGER_TOKEN, accessToken);
                editor.apply();
                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        } else {
            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
        }
    }
}
