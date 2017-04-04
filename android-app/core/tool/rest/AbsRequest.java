package dubsapp.core.tool.rest;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractMan;
import dubsapp.core.abs.AbstractObject;

public abstract class AbsRequest extends AbstractMan implements  Request.Method, VolleyRequest.CallBack {
    static String ENCODING_GZIP = "gzip";
    public static String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static String SERVER_VERSION = "v4";
    private int mMethodType;
    private String mMethodName;
    private static AbsRequest sInstance;
    private boolean mIsRequiredHandshake = false;

    public AbsRequest() {
        super();
    }

    public AbsRequest(int methodType) {
        super();
        mMethodName = this.getClass().getSimpleName();
        mMethodType = methodType;
        log().info("MethodName={}", mMethodName);
        log().todo("MethodType={}", mMethodType);
    }


    protected abstract void onResponse(AJson json);
    protected abstract void onSetUrlPath(Uri.Builder url);
    protected abstract void onSetUrlParam(IRestParam param);
    protected abstract void onSetBodyParam(AJson json);

//    public void send() {
//        //A.REST().mRestQueue.add(this);
//        new VolleyRequest(this);
//    }

    public RestParam getHeader() {
        log().trace("getHeader");
        RestParam param = new RestParam();
//        param.put(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        log().todo("getHeader.enable ENCODING_GZIP");
        log().info("Header(param)={}", param);
        return param;
    }

    public byte[] getBody() {
        log().trace("getBody");
        AJson jsonBody = getJsonInput();
        if (A.isNull(jsonBody)) {
            jsonBody = new AJson();
            log().debug("onSetBodyParam.before");
            this.onSetBodyParam(jsonBody);
            log().debug("onSetBodyParam.after");

        }
        log().info("Body={}", jsonBody.getMagicInfo());
        return jsonBody.toBytes();
    }

    public int getMethodType() {
        return mMethodType;
    }

    private RestParam onSetUrlParam() {
        log().trace("onSetUrlParam");
        RestParam urlParams = new RestParam();
        this.onSetUrlParam(urlParams);
        log().debug("onSetUrlParam.urlParams={}", urlParams);
        return urlParams;
    }

    public String getUrl() {
        log().trace("getUrl");
        String result;
        String SERVER_SCHEME = "http";
        Uri.Builder url = new Uri.Builder().scheme(SERVER_SCHEME)
                .authority(A.getServerName())
                .appendPath(SERVER_VERSION);
        this.onSetUrlPath(url);

        for (Map.Entry<String, String> urlParam : this.onSetUrlParam().entrySet()) {
            if (urlParam.getValue() == null) {
                log().warn("getUrl.urlParam.getWidgetValue() is null. continue!!!");
                continue;
            }
            url.appendQueryParameter(urlParam.getKey(), urlParam.getValue());
        }
        result = url.build().toString();
        log().info("Url={}", result);
        return result;
    }

    public void onVolleyResponse(AJson json) {
        log().trace("onVolleyResponse.JSON={}", json.getMagicInfo());
        log().info("onResponse.before");
        log().info("onResponse.length={}", json.getJson().length());
        this.onResponse(json);
        log().info("onResponse.after");
    }


    public int getTimeoutMS() {
        //int DEFAULT_TIMEOUT_MS = 3000;
        int result = 15000;
        log().debug("getTimeoutMS.result={}", result);
        return result;
    }

    private String getServerResponse(NetworkResponse networkResponse) {
        if (A.isNull(networkResponse)) {
            return "No Server Response";
        }
        return String.format("%s%s)", networkResponse.statusCode, getVolleyResponse(networkResponse));
    }

    private String getVolleyResponse(NetworkResponse networkResponse) {
        log().error("getVolleyResponse.headers={}", networkResponse.headers);
        String charset = HttpHeaderParser.parseCharset(networkResponse.headers, A.CHARSET);
        try {
            return new String(networkResponse.data, charset);
        } catch (UnsupportedEncodingException uee) {
            log().error(uee);
            return String.format("Unsupported encoding while trying to get the string using %s", A.CHARSET);
        }
    }

    public boolean isRequiredHandshake() {
        return mIsRequiredHandshake;
    }

    public void setIsRequiredHandshake(boolean mIsRequiredHandshake) {
        this.mIsRequiredHandshake = mIsRequiredHandshake;
    }

    //TODO: new HandshakeAPI!!
    //TODO: Unauthorized (401)--  Internal server error occured or Dubs Server is not properly configured
    private final int SERVER_ERROR__UNAUTHORIZED = 401;

    //TODO: !!! OK (200) ???
    //TODO: Bad AbsRequest (400)
    //TODO  Specified app module not registered or mobile app version not exists
    private final int SERVER_ERROR__BAD_REQUEST = 400;


    //TODO Not Found (404)	Dub with specified ID not found
    private final int SERVER_ERROR__NOT_FOUND = 404;
    //TODO: Service Unavailable (503) -- Session with provided code not exists or is expired
    private final int SERVER_ERROR__SERVICE_UNAVAILABLE = 503;

    /**
     * Client Error(400)	The publicKey specified isn't correct. Other user's errors.
     Unauthorized (401)	Session with provided code not exists or is expired
     Not Found (404)	Dub with specified ID not found

     UnprocessableEntity(422)
     Receipt validation exception
     Service Unavailable (503)	Internal server error occured
     */

    @Override
    public void onVolleyError(VolleyError volleyError) {
        String errorType = volleyError.getClass().getSimpleName();
        log().info("onVolleyError.errorType={}", errorType);
        log().warn("onVolleyError.NetworkTimeMs={}", volleyError.getNetworkTimeMs());

        if (NoConnectionError.class.isInstance(volleyError)) {
            log().warnReturn("onVolleyError.NoConnectionError");
            return;
        }

        if (volleyError.networkResponse.statusCode == SERVER_ERROR__UNAUTHORIZED) {
            mIsRequiredHandshake = true;
            log().warnReturn("onVolleyError.mIsRequiredHandshake={}", mIsRequiredHandshake);
            return;
        }

        String serverResponse = this.getServerResponse(volleyError.networkResponse);
        log().error("onVolleyError.serverResponse={}", serverResponse);
        String errorText = String.format("%s: %s\n", getName(), serverResponse);
        if (A.isDEVELmode()) {
            A.Dialog().show(errorText);
            A.Dialog().show("%s", errorType);
        } else {
            log().error("onVolleyError:errorText={}", errorText);
            log().error("onVolleyError:errorType={}", errorType);
        }
    }















    //==============================================================================================
    //TODO==========================================================================================
    //==============================================================================================
    private ArrayList<String> mExtraParamList;
    private AbstractObject mExtraObject;

    protected String IDs(){
//        if (A.isNull(mExtraParam)) {
//        }
        log().trace("IDs={}", mExtraParamList);
        return TextUtils.join(",", mExtraParamList);
    }

    protected ArrayList<String> IDlist() {
        log().trace("IDlist", mExtraParamList);
        return mExtraParamList;
    }

    public void setExtraParam(ArrayList<String> paramList) {
        log().trace("setExtraObject.paramList={}", paramList);
        mExtraParamList = paramList;
    }

    protected AbstractObject getExtraObject() {
        log().trace("getExtraObject", mExtraObject);
        return mExtraObject;
    }

    protected AJson getJsonInput() {
        log().trace("getExtraObject");
        return null;
    }

    public void setExtraObject(AbstractObject extraObject) {
        log().trace("setExtraObject", extraObject);
        mExtraObject = extraObject;
    }


    public VolleyRequest toVolleyRequest() {
        return new VolleyRequest(this);
    }

    public boolean isNeeded() {
        return true;
    }

    }

/*
public void onErrorResponse(VolleyError error) {
            hideProgressDialog();
            if(error instanceof AuthFailureError) {
                showAlertError(ResourcesUtil.getString(R.string.login_error_invalid_credentials));
            }else if(error instanceof ClientError) {
                // Convert byte to String.
                String str = null;
                try {
                    str = new String(error.networkResponse.data, "UTF8");
                    JSONObject errorJson = new JSONObject(str);
                    if( errorJson.has(Constants.ERROR)) {
                        JSONObject err = errorJson.getJSONObject(Constants.ERROR);
                        if(err.has(Constants.ERROR_MESSAGE)) {
                            String errorMsg = errorJson.has(Constants.ERROR_MESSAGE);
                            showAlertError(errorMsg);
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                MessageUtil.showMessage(ResourcesUtil.getString(R.string.error_service), true);
            }
        }
 */