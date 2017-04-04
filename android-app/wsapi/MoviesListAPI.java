package dubsapp.wsapi;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import dubsapp.core.abs.A;
import dubsapp.core.tool.database.DbCursor;
import dubsapp.core.tool.rest.AJson;
import dubsapp.core.tool.rest.AbstractRequestGET;
import dubsapp.core.tool.rest.IRestParam;

public class MoviesListAPI extends AbstractRequestGET {

    @Override
    protected void onSetUrlPath(Uri.Builder url) {
        url.appendPath("movies_listing");
    }

    @Override
    protected void onSetUrlParam(IRestParam param) {
        param.addParam("session", A.db().getVarS("SESSION_CODE"));
        param.addParam("applang", A.db().getVarS("APP_LANGUAGE_CODE"));
        param.addParam("radius", A.db().getVarS("SEARCH_RADIUS_IN_METERS"));
        param.addParam("lat", A.Device().getLatitude());
        param.addParam("lng", A.Device().getLongitude());
    }

    @Override
    public boolean isNeeded() {
        boolean result = true;
//        if (!result) {
//            log().warn("isNeeded.result={}", result);
//            return true;
//        }
        return result;
    }

    @Override
    protected void onSetBodyParam(AJson json) {

    }

    @Override
    protected void onResponse(AJson json) {
        setChangedRecordIds(json.getJson());
        //TODO: move (!mMovieIdList.isEmpty()) in send!!
        if (!mMovieIdList.isEmpty()) {
            APIDubs.Movies.send(mMovieIdList);
        }
        if (!mTheaterIdList.isEmpty()) {
            APIDubs.Theaters.send(mTheaterIdList);
        }
        if (!mMovieTheaterIdList.isEmpty()) {
            APIDubs.Showtimes.send(mMovieTheaterIdList);
        }
    }

    //TODO: refactor!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //TODO: ну... max просто тупое и без удобностей в sqlite...
    private ArrayList<String> mMovieIdList;
    private ArrayList<String> mMovieTheaterIdList;
    private ArrayList<String> mTheaterIdList;

    //TODO: insert into table: create tmp table??
    //!!!!!!!!!!!!!
    protected void setChangedRecordIds(JSONObject json) {
        log().trace("setChangedRecordIds");
        A.db().execSQL("delete from movie_list_mobile_t;");
        try {
            Iterator<String> tableNameList = json.keys();
            while (tableNameList.hasNext()) {
                String tableName = tableNameList.next();
                if (tableName.equals("by_IP_f")) {
                    log().warn("setChangedRecordIds.tableName={}: continue...", tableName);
                    continue;
                }
                if (tableName.equals("country_ids")) {
                    log().warn("setChangedRecordIds.tableName={}: continue...", tableName);
                    //TODO: :( !!!! this.setCountryIds(json.get(tableName))
                    continue;
                }

                //TODO: refactor na... :(
                JSONArray recordList = json.getJSONArray(tableName);
                A.db().startTransactoion();
                try {
                    for (int recordIndex = 0; recordIndex < recordList.length(); recordIndex++) {
                        JSONObject recordJSON = (JSONObject) recordList.get(recordIndex);
                        A.db().newContent("movie_list_mobile_t");
                        A.db().addContent().put("table_name", tableName);
                        A.db().addContent().put("id", recordJSON.getInt("id"));
                        A.db().addContent().put("hashcode", recordJSON.getString("hashcode"));
                        A.db().insertContent();
                    }
                    A.db().commitTransaction();
                } catch (Exception ex) {
                    A.db().rollbackTransaction(ex);
                }

            }
            mMovieIdList = processDateMovieList("movie_t");
            mTheaterIdList = processDateMovieList("theater_t");
            mMovieTheaterIdList = processDateMovieList("movie__theater_t");
            log().trace("setChangedRecordIds.after");
        } catch (JSONException ex) {
            raiseException(ex);
        }
    }

    protected ArrayList<String> processDateMovieList(String tableName) {
        ArrayList<String> result = getIdList(tableName);
        deleteNotFoundId(tableName);
        log().debug("processDateMovieList.result.size()={}", result.size());
        return result;
    }

    private void deleteNotFoundId(String tableName) {

        String sqlText = String.format("select count(1) row_cnt from %s where id not in " +
                " (select t.id from movie_list_mobile_t t where t.table_name = '%s')", tableName, tableName);
        DbCursor cursor = A.db().openSQL(sqlText);
        try {
            log().info("deleteNotFoundId.count={}", cursor.FBNI("row_cnt"));
        } finally {
            cursor.close();
        }
        sqlText = String.format("delete from %s where id not in " +
                " (select t.id from movie_list_mobile_t t where t.table_name = '%s')", tableName, tableName);
        A.db().execSQL(sqlText);
    }


    protected ArrayList<String> getIdList(String tableName) {
        ArrayList<String> result = new ArrayList<>();
        String sqlTextBeforeFormat = "select ml_m.id \n" +
                    "  from movie_list_mobile_t ml_m\n" +
                "  left join %s t \n" +
                "    on t.id = ml_m.id \n" +
                "   and t.hashcode = ml_m.hashcode\n" +
                " where ml_m.table_name ='%s'\n" +
                "   and t.id is null";
        String sqlText = String.format(sqlTextBeforeFormat, tableName, tableName);

        DbCursor cursor = A.db().openSQL__TMP(sqlText);
        try {
            while (cursor.scan()) {
                result.add(cursor.FBNS("id"));
            }
        } finally {
            cursor.close();
        }
        log().info("getIdList.tableName={}.size={}", tableName, result.size());
        return result;
    }

    //TODO: КонеЦ :)
}
