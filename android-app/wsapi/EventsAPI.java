package dubsapp.wsapi;

import android.net.Uri;

import dubsapp.core.abs.A;
import dubsapp.core.tool.rest.AJson;
import dubsapp.core.tool.rest.AbstractRequestPUT;
import dubsapp.core.tool.rest.IRestParam;

public class EventsAPI extends AbstractRequestPUT {

    @Override
    protected void onSetUrlPath(Uri.Builder url) {
        url.appendPath("events");
    }

    @Override
    protected void onSetUrlParam(IRestParam param) {
        param.addParam("session", A.db().getVarS("SESSION_CODE"));
    }

    @Override
    protected void onSetBodyParam(AJson json) {
        json.putArray(A.db().getDbCursor("event_mobile_v"));
//        log().todo("onSetBodyParam.TODO: remove this method!!!!");
    }


    @Override
    protected void onResponse(AJson json) {
        A.db().deleteEvents();
    }


}
