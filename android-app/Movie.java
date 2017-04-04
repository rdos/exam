package dubsapp;

import android.support.annotation.IdRes;

import dubsapp.core.abs.A;
import dubsapp.core.tool.database.DbCursor;
import dubsapp.core.widget.abs.AbsFrameList;
import dubsapp.wsapi.APIDubs;

public class Movie extends AbsFrameList{

    @Override
    public int onSetListItemLayout() {
        return R.layout.movie;
    }

    @Override
    public int onSetListItemEmptyLayout() {
        return R.layout.movie__empty;
    }

    @Override
    public DbCursor onSetFrameDbCursor() {
        return A.db().getDbCursor("movie_mobile_v");
    }

    @Override
    protected void onOpen(DbCursor cursor) {
        super.onOpen(cursor);
        getAbsAct().enableBarComboBox();
        setTitle(R.string.movie__lb_title);
        A.Man.onAppInitialize();
    }

    @Override
    public void onClose() {
        getAbsAct().disableBarComboBox();
    }

    @Override
    public void onClick(long movieDescId) {
        showAct(MovieDtlA.class, movieDescId);
    }

    @Override
    protected void onPullToRefresh() {
        APIDubs.MoviesList.send();
    }

    @Override
    protected void onChangeComboboxWidget(@IdRes int widgetId, long id, String value) {
        A.db().setVar("APP_SHOWTIME_MOVIE_DT", value);
        A.db().setVar("APP_SHOWTIME_DT", value);
        refreshDbCursor();
    }

}
