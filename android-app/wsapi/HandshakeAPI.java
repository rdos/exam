package dubsapp.wsapi;

import android.net.Uri;

import dubsapp.core.abs.A;
import dubsapp.core.tool.rest.AJson;
import dubsapp.core.tool.rest.AbstractRequestPOST;
import dubsapp.core.tool.rest.IRestParam;

public class HandshakeAPI extends AbstractRequestPOST {

    @Override
    protected void onSetUrlPath(Uri.Builder url) {
        url.appendPath("handshake");
    }

    @Override
    protected void onSetBodyParam(AJson json) {
        json.put("clientModule", "ANDROID_APP");
        AJson jsonVersion = new AJson();
        String[] versionStructure = A.getVersionName().split("\\.");
        jsonVersion.put("major", Integer.parseInt(versionStructure[0]));
        jsonVersion.put("minor", Integer.parseInt(versionStructure[1]));
        jsonVersion.put("patch", Integer.parseInt(versionStructure[2]));

        json.put("dubsClientVersion", jsonVersion);
        json.put("osVersion", A.getOsVersion());
        json.put("deviceId", A.Device().getDeviceId());
    }

    @Override
    public boolean isRequiredHandshake() {
        return false;
    }

    @Override
    protected void onSetUrlParam(IRestParam param) {
        param.addParam("latitude", A.Device().getLatitude());
        param.addParam("longitude", A.Device().getLongitude());
    }

    @Override
    protected void onResponse(AJson json) {
        log().warn("onResponse.json={}", json.getJson());
        A.db().updateTableData(json);
    }


//    @Override
//    protected void onResponseError(int code, String text) {
//
//    }
}
