package dubsapp.wsapi;


import java.util.ArrayList;

import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractObject;
import dubsapp.core.tool.rest.AbstractRequest;

public enum APIDubs {
    Handshake, Movies, MoviesList, Showtimes, Theaters,
    DubRequest, DubAcknowledge, DubPurchase, DubDrmKey, DubWm,
    Events, ErrorLog, UserinfoAdd, TryDubs;

    private Class< ? extends AbstractRequest> getRequestClazz () {
        switch (this) {
            case Handshake:
                return HandshakeAPI.class;
            case Movies:
                return MoviesAPI.class;
            case MoviesList:
                return MoviesListAPI.class;
            case Showtimes:
                return ShowtimesAPI.class;
            case Theaters:
                return TheatersAPI.class;
            case DubRequest:
                return DubRequestAPI.class;
            case DubAcknowledge:
                return DubAcknowledgeAPI.class;
            case DubPurchase:
                return DubPurchaseAPI.class;
            case DubDrmKey:
                return DubDrmKeyAPI.class;
            case DubWm:
                return DubWmAPI.class;
            case Events:
                return EventsAPI.class;
            case ErrorLog:
                return ErrorLogAPI.class;
            case UserinfoAdd:
                return UserinfoAddAPI.class;
            case TryDubs:
                return TryDubsAPI.class;
        }
        return null;
    }

    public void send() {
        send(null, null);
    }

    public void send(String... requestParams) {
        ArrayList<String> paramList = new ArrayList<>();
        for (String param: requestParams) {
            paramList.add(param);
        }
        send(paramList, null);
    }

    public void send(long... requestParams) {
        ArrayList<String> paramList = new ArrayList<>();
        for (Long param: requestParams) {
            paramList.add(String.valueOf(param));
        }
        send(paramList, null);
    }

    public void send(ArrayList<String> paramList) {
        send(paramList, null);
    }

    public void send(AbstractObject extraObject) {
        send(null, extraObject);
    }

    private void send(ArrayList<String> paramList, AbstractObject extraObject) {
        A.REST().addRequest(getRequestClazz(), paramList, extraObject);
    }
}
