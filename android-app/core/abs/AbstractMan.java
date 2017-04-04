package dubsapp.core.abs;

import dubsapp.AEvent;

public abstract class AbstractMan extends AbstractObject {
    private Event mEvent;

    public AbstractMan() {
        super(LOG_LEVEL.TRACE);
    }

    protected AbstractMan(LOG_LEVEL logLevelType) {
        super(logLevelType);
    }

//    private int getExtraId() {
//        return A.AEvent.INT_IS_NULL;
//    }


    public void newEvent(AEvent type) {
        log().debug("newEvent.type={}", type);
        if (mEvent == null) {
            mEvent = new Event(type);
        }
    }

    public void addEventParam(String paramName, long paramValue) {
        log().debug("addEventParam.paramName={},paramValue={}", paramName, paramValue);
        mEvent.addParam(paramName, paramValue);
    }

    public void addEventParam(String paramName, String paramValue) {
        log().debug("addEventParam.paramName={},paramValue={}", paramName, paramValue);
        mEvent.addParam(paramName, paramValue);
    }

    private WaiteAnd mAppEventWaiter = new WaiteAnd(
            new Runnable() {

                @Override
                public void run() {
                    mEvent.send();
                    mEvent = null;
                }
            });

    public void sendEvent(long delayMS) {
        log().debug("sendEvent.delayMS={}", delayMS);
        if (delayMS < 0) {
            log().warn("sendEvent.delayMS={}", delayMS);
        }
        mAppEventWaiter.start(delayMS);
    }

    public void sendEvent(AEvent aTypeEvent) {
        log().debug("sendEvent.aTypeEvent={}", aTypeEvent);
        this.newEvent(aTypeEvent);
        mAppEventWaiter.run();
    }

//    /**
//     * This async task queries S3 for all files in the given bucket so that they
//     * can be displayed on the screen
//     */
//    private class GetFileListTask extends AsyncTask<Void, Void, Void> {
//        // The list of objects we find in the S3 bucket
//        private List<S3ObjectSummary> s3ObjList;
//        // A dialog to let the user know we are retrieving the files
//        private ProgressDialog dialog;
//
//        @Override
//        protected void onPreExecute() {
//            dialog = ProgressDialog.showAct(DownloadSelectionActivity.this,
//                    getString(R.string.refreshing),
//                    getString(R.string.please_wait));
//        }
//
//        @Override
//        protected Void doInBackground(Void... inputs) {
//            // Queries files in the bucket from S3.
//            s3ObjList = s3.listObjects(Constants.BUCKET_NAME).getObjectSummaries();
//            transferRecordMaps.clear();
//            for (S3ObjectSummary summary : s3ObjList) {
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("key", summary.getKey());
//                transferRecordMaps.add(map);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            dialog.dismiss();
//            simpleAdapter.notifyDataSetChanged();
//        }
//    }
//





}
