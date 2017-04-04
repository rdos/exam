package dubsapp;

import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractMan;
import dubsapp.core.tool.device.PlayerTool;
import dubsapp.core.tool.download.DownloadMan;
import dubsapp.core.tool.purchase.PurchaseMan;
import dubsapp.todo.DubsExpireService;
import dubsapp.todo.LocationMan;
import dubsapp.todo.MatcherDubs;
import dubsapp.wsapi.APIDubs;

// TODO: work with: all Tool
// TODO: extend service. usage
public final class AppManDubs extends AbstractMan {

    private boolean mIsAppInitialized = false;

    public void onAppInitialize() {
//        log().info("onAppInitialize");
//        //TODO: DeployGate???
//        if (DeployGate.isDeployGateAvaliable()) {
//            log().todo("DeployGate???");
//            DeployGate.install(this);
//            if (DeployGate.isAuthorized()) {
//                log().todo("DeployGate.AuthorUsernam={}", DeployGate.getAuthorUsername());
//                log().todo("DeployGate.LoginUsername={}", DeployGate.getLoginUsername());
//                log().todo("DeployGate.isManaged()={}", DeployGate.isManaged());
//            }
//        }
        if (mIsAppInitialized) {
            log().warnReturn("onAppInitialize.isAppInitialized={}", mIsAppInitialized);
            return;
        }
        mIsAppInitialized = true;
        log().todo("before A.Man.Purchase();");
        A.Man.Purchase();
        log().todo("after A.Man.Purchase();");
        APIDubs.TryDubs.send();
        log().todo("before A.Man.Download();");
        A.Man.Download().init();
        log().todo("after A.Man.Download();");
        APIDubs.Handshake.send();
        APIDubs.Events.send();
        APIDubs.MoviesList.send();
        APIDubs.DubWm.send();

        log().info("App is Initialized");
    }

    private LocationMan mLocationMan;
    public LocationMan Location() {
        if (mLocationMan == null) {
            mLocationMan = new LocationMan();
        }
        return mLocationMan;
    }

    private PurchaseMan mPurchaseMan;
    public PurchaseMan Purchase() {
        if (mPurchaseMan == null) {
            mPurchaseMan = new PurchaseMan(A.Context(), A.getAppLicenseKey());
        }
        return mPurchaseMan;
    }

    //TODO: DownloadMan
    private DownloadMan mDownloadMan;
    public DownloadMan Download() {
        if (mDownloadMan == null) {
            mDownloadMan = new DownloadMan(A.R.getString(R.string.aws_access_key),
                    A.R.getString(R.string.aws_secret_key));
        }
        return mDownloadMan;
    }

    private MatcherDubs sMatcherDubs;
    public MatcherDubs DubsMatcher() {
        if (sMatcherDubs == null) {
            sMatcherDubs = new MatcherDubs();
        }
        return sMatcherDubs;
    }

    private PlayerTool mDubsPlayer;
    public PlayerTool DubsPlayer() {
        if (mDubsPlayer == null) {
            mDubsPlayer = new PlayerTool();
        }
        return mDubsPlayer;
    }

    private DubsExpireService mDubsExpireService;
    public DubsExpireService Expire() {
        if (mDubsExpireService == null) {
            mDubsExpireService = new DubsExpireService();
        }
        return mDubsExpireService;
    }

    public void onAppException(Throwable throwable) {
        log().todo("onAppException");
        APIDubs.ErrorLog.send(this.getRootCause(throwable).getClass().getSimpleName().substring(0, 10));
    }

    //TODO: move to AppException!!!
    private Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable;
        }
        return this.getRootCause(throwable.getCause());
    }

}
