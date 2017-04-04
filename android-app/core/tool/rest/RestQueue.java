package dubsapp.core.tool.rest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.LinkedBlockingDeque;

import dubsapp.AEvent;
import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractMan;
import dubsapp.wsapi.HandshakeAPI;

//TODO: mInWork - ЗЛО!!! MINIrefactor!!!
final class RestQueue extends AbstractMan implements RequestQueue.RequestFinishedListener<AJson> {

    private final LinkedBlockingDeque<AbsRequest> mRequestQueue;
    private final RequestQueue mVolleyRequestQueue;
    private final ImageLoader mImageLoader;
    private boolean mInWork = false;

    public RestQueue(int capacity) {
        super();
        mRequestQueue = new LinkedBlockingDeque<>(capacity);
        mVolleyRequestQueue = Volley.newRequestQueue(A.Context());
        mVolleyRequestQueue.addRequestFinishedListener(this);
        mImageLoader = new ImageLoader(mVolleyRequestQueue, new ImageCache());
        log().info("created");
    }

    public void add(AbsRequest request) {
        add(request, false);
    }

    private void add(AbsRequest newRequest, boolean isFirst) {
        log().debug("add.request={}", newRequest.getName());
        log().debug("add.isFirst={}", isFirst);
        if (!newRequest.isNeeded()) {
            if (!mInWork) {
                sendEvent(AEvent.API_QUEUE_END);
            }
            return;
        }

        if (isFirst) {
            mRequestQueue.addFirst(newRequest);
        } else {
            mRequestQueue.addLast(newRequest);
        }
        if (mInWork) {
            return;
        }
        this.send();
    }


    public ImageLoader getImageLoader() {
        log().trace("getImageLoader");
        return mImageLoader;
    }

    private void send() {
        log().debug("send");
        mInWork = true;
        mVolleyRequestQueue.add(mRequestQueue.peekFirst().toVolleyRequest());
    }

    private void sendNext() {
        log().info("sendNext");
        if (mRequestQueue.getFirst().isRequiredHandshake()) {
            log().info("sendNext.isRequiredHandshake={}", true);
            if (!HandshakeAPI.class.isInstance(mRequestQueue.getFirst())) {
                mRequestQueue.getFirst().setIsRequiredHandshake(false);
                add(new HandshakeAPI(), true);
            }
        } else {
            mRequestQueue.removeFirst();
        }
        if (mRequestQueue.isEmpty()) {
            mInWork = false;
            log().info("isEmpty", mInWork);
            sendEvent(AEvent.API_QUEUE_END);
            return;
        }
        this.send();
    }

    //    public void addFirst(AbstractRequestGET request) {
//        log().debug("addFirst.request={}", request.getClass().getSimpleName());
//        mRequestQueue.addFirst(request);
//        this.sendNext();
//    }

    //    public void clear() {
//       log().debug("clear");
//        this.mRequestQueue.clear();
//        this.requestQueueInProgress = false;
//    }

    @Override
    public void onRequestFinished(Request<AJson> request) {
        log().trace("onRequestFinished");
        try {
            VolleyRequest volleyRequest = (VolleyRequest) request;
            log().debug("onRequestFinished.request={}", volleyRequest.getRequestName());

        } catch (ClassCastException ex) {
            log().refactor("ClassCastException");
            return;
        }
        this.sendNext();
        if (!mInWork) {
            sendEvent(AEvent.API_QUEUE_END);
        }
    }
}
