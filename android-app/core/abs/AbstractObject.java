package dubsapp.core.abs;

import android.content.Context;

import dubsapp.core.tool.log.IAppLogger;

public abstract class AbstractObject {


    //TODO: LOG_LEVEL move to LogTool!
    public enum LOG_LEVEL {TODO, DEBUG, INFO, TRACE, REFACTOR, DEVEL, NO_INIT_INFO}

    public AbstractObject() {
        this(LOG_LEVEL.INFO);
    }

    //TODO: mv to LogTool
    protected AbstractObject(LOG_LEVEL logLevelType) {
        String INIT_LOG_TEXT = "before create";
        switch (logLevelType) {
            case TODO:
                log().todo(INIT_LOG_TEXT);
                break;
            case DEBUG:
                log().debug(INIT_LOG_TEXT);
                break;
            case INFO:
                log().info(INIT_LOG_TEXT);
                break;
            case TRACE:
                log().trace(INIT_LOG_TEXT);
                break;
            case REFACTOR:
                log().refactor(INIT_LOG_TEXT);
                break;
            case DEVEL:
                log().debug(INIT_LOG_TEXT);
                break;
            case NO_INIT_INFO:
                break;
        }
    }

    /**
     * Usability
     *  ...
     */
    protected Context AppContext() {
        return A.Context().getApplicationContext();
    }

    protected boolean isNull(Object object) {
        return A.isNull(object);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Log
     */
    private IAppLogger mAppLoger;
    protected IAppLogger log() {
        if(mAppLoger == null) {
            mAppLoger = A.Log().getLogger(this);
        }
        return mAppLoger;
    }

    protected void log(String msg) {
        this.log().debug(msg);
    }
    protected void log(String format, Object... args) {
        this.log().debug(format, args);
    }

    /**
     * Exception
     */
    protected void warnException(String msg, Exception ex) {
       log().warn("{}.Exception={}", msg, ex.getMessage());
    }

    protected void warnException(Exception ex) {
        log().warn("Exception={}", ex.getMessage());
    }

    protected void raiseException(String detailMessage) {
        log().error("raiseException.detailMessage={}", detailMessage);
        A.Exception.raise(detailMessage);
    }

    protected void raiseException(Exception ex) {
        log().error(ex);
        A.Exception.raise(ex);
    }

    protected void raiseException(String methodAndObjectName, Object object) {
        log().debug("raiseException.object={}", object);
        String detailMessage = String.format("%s is not found!!!", methodAndObjectName);
        this.raiseException(detailMessage);
    }

    protected void raiseExceptionForTest(Exception ex){
        //TODO: DubsMan.Dialog.!! and Toast
        A.Dialog().show(ex.toString());
        raiseException(ex);
//        Dialog.setTitle("Found exception :(");
//        Dialog.setMessage(this.toString());
//        Dialog.setNegativeButton("i sympathize :(", null);
//        Dialog.create().showAct();
    }

    /**
     * :)
     */

    @Override
    protected void finalize() throws Throwable {
        log().trace("finalize");
        super.finalize();
    }
}
