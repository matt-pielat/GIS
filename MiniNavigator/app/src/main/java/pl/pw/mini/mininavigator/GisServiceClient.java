package pl.pw.mini.mininavigator;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import pl.pw.mini.mininavigator.data.Location;
import pl.pw.mini.mininavigator.data.LocationDate;
import pl.pw.mini.mininavigator.data.WirelessSignal;
import pl.pw.mini.mininavigator.utilities.Callback;

public class GisServiceClient {

    //region Singleton

    private static GisServiceClient sInstance;

    public static GisServiceClient get() {
        if (sInstance == null) sInstance = getSync();
        return sInstance;
    }

    private static synchronized GisServiceClient getSync() {
        if (sInstance == null) sInstance = new GisServiceClient();
        return sInstance;
    }

    //endregion

    public static final int READ_TIMEOUT = 60000;
    public static final int CONNECT_TIMEOUT = 10000;

    private HttpServiceClient mHttpClient;
    private Gson mGson;

    private GisServiceClient() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.get());

        String address = prefs.getString("pref_gisServiceAddress", "");
        if (address.charAt(address.length() - 1) != '/') {
            address += "/";
        }

        mHttpClient = new HttpServiceClient(address, CONNECT_TIMEOUT, READ_TIMEOUT);
        mGson = new Gson();
    }

    public void getMyLocation(String id, List<WirelessSignal> signals, final Callback<LocationDate> callback) {

        Type listType = new TypeToken<List<WirelessSignal>>() {
        }.getType();

        JSONObject postArg = new JSONObject();
        try {
            postArg.put("id", id);
            postArg.put("signals", new JSONArray(mGson.toJson(signals, listType)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mHttpClient.sendPostRequest("GetLocation", postArg, new Callback<JSONObject>() {
                @Override
                public void callback(JSONObject param) {
                    if (param == null) return;

                    String response = null;
                    try {
                        response = param.getString("d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    LocationDate locationDate = mGson.fromJson(response, LocationDate.class);
                    callback.callback(locationDate);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getLocation(String id, final Callback<LocationDate> callback) {

        JSONObject postArg = new JSONObject();
        try {
            postArg.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("apka", "json to send: " + postArg);

        try {
            mHttpClient.sendPostRequest("GetHistory", postArg, new Callback<JSONObject>() {
                @Override
                public void callback(JSONObject param) {
                    if (param == null) return;

                    String response = null;
                    try {
                        response = param.getString("d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Type listType = new TypeToken<List<LocationDate>>() {
                    }.getType();

                    List<LocationDate> locationDate = mGson.fromJson(response, listType);
                    callback.callback(locationDate.get(0));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getLocationMap(Location location, final Callback<Bitmap> callback) {

        JSONObject postArg = new JSONObject();
        try {
            Log.d("apka", mGson.toJson(location));

            postArg.put("location", new JSONObject(mGson.toJson(location)));
        } catch (JSONException e) {
            Log.e("apka", e.getStackTrace()[0].toString());
           // e.printStackTrace();
            return;
        }

        try {
            mHttpClient.sendPostRequest("GetLocationMap", postArg, new Callback<JSONObject>() {
                @Override
                public void callback(JSONObject param) {
                    if (param == null) return;

                    String response = null;
                    try {
                        response = param.getString("d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    byte[] byteData = Base64.decode(response, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
                    callback.callback(image);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void getHistory(String id, final Callback<List<LocationDate>> callback) {

        JSONObject postArg = new JSONObject();
        try {
            postArg.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        try {
            mHttpClient.sendPostRequest("GetHistory", postArg, new Callback<JSONObject>() {
                @Override
                public void callback(JSONObject param) {
                    if (param == null) return;

                    String response = null;
                    try {
                        response = param.getString("d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Type listType = new TypeToken<List<LocationDate>>() {
                    }.getType();

                    List<LocationDate> locationDate = mGson.fromJson(response, listType);
                    callback.callback(locationDate);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void getHistoryMap(String id, final Callback<Bitmap> callback) {

        JSONObject postArg = new JSONObject();
        try {
            postArg.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            mHttpClient.sendPostRequest("GetHistoryMap", postArg, new Callback<JSONObject>() {
                @Override
                public void callback(JSONObject param) {
                    if (param == null) return;

                    String response = null;
                    try {
                        response = param.getString("d");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    byte[] byteData = Base64.decode(response, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
                    callback.callback(image);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
