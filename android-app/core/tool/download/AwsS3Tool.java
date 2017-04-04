package dubsapp.core.tool.download;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractObject;


final class AwsS3Tool extends AbstractObject {

    private final AmazonS3Client mS3Client;
    /**
     * This is the main class for interacting with the Transfer Manager
     */
    public final TransferUtility mTransferUtility;
    /**
     * A List of all transfers
     */
    private final HashMap<Integer, TransferObserver> mObserversMap;
    private final TransferListener mTransferListener;
    //private final HashMap<Integer, Integer> mExtraIdMap;

    public AwsS3Tool(TransferListener stateListener, String awsAccessKey, String awsSecretKey) {
        super(LOG_LEVEL.DEVEL);
        mS3Client = new AmazonS3Client(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        mS3Client.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        mTransferUtility = new TransferUtility(mS3Client, A.Context().getApplicationContext());
        mObserversMap = new HashMap<>();
        mTransferListener = stateListener;
        //mExtraIdMap = new HashMap<>();
//        addToLogObserversInfo();
    }

    /**
     * Begins to download the file specified by the key in the bucket.
     *  @return Id of he TransferObserver used to track download progress and state
     */
    public int startDownload(boolean isSetListener, String outFilePath, String bucketName, String keyName) {
        File file = A.Device.file().newFile(outFilePath);
        TransferObserver observer = mTransferUtility.download(bucketName, keyName, file);
        addObserver(observer, isSetListener);
        return observer.getId();
    }

    public void pauseDownload(int observerId) {
        log().debug("pauseDownload.observerId={}", observerId);
        mTransferUtility.pause(observerId);
    }

    public void cancelDownload(int observerId) {
        log().debug("cancelDownload.observerId={}", observerId);
        mTransferUtility.cancel(observerId);
    }

    public void resumeDownload(int observerId) {
        //todo return
        log().debug("resumeDownload.observerId={}", observerId);
        mTransferUtility.resume(observerId);
    }

    /**
     * Gets all relevant transfers from the Transfer Service for populating the
     * UI
     */
    public void addToLogObserversInfo() {
        log().debug("addToLogObserversInfo");
        List<TransferObserver> observers = mTransferUtility.getTransfersWithType(TransferType.DOWNLOAD);
        for (TransferObserver observer : observers) {
            // Sets listeners to in progress transfers
            if (TransferState.WAITING.equals(observer.getState())
                    || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                    || TransferState.IN_PROGRESS.equals(observer.getState())) {
                log().debug("addToLogObserversInfo.id={},getAbsoluteFilePath={}", observer.getId(), observer.getAbsoluteFilePath());
                log().debug("addToLogObserversInfo.state={},getKey={}", observer.getState(), observer.getKey());
                log().debug("---");
            } else {
                log().debug("addToLogObserversInfo.id={},getAbsoluteFilePath={}", observer.getId(), observer.getAbsoluteFilePath());
                log().debug("addToLogObserversInfo.state={},getKey={}", observer.getState(), observer.getKey());
                log().debug("---");
            }
        }
        log().debug("==================");
        //simpleAdapter.notifyDataSetChanged();
    }

    private void addObserver(TransferObserver observer, boolean isSetListener) {
        log().debug("addObserver.id={},state={}", observer.getId(), observer.getState());
        log().debug("addObserver.key={},bucket={}", observer.getKey(), observer.getBucket());
        //private void addObserver(TransferObserver observer, int extraId, String extraCode, boolean isAddListener) {
        mObserversMap.put(observer.getId(), observer);
        //mExtraIdMap.put(observer.getId(), );
        //if (isAddListener) {
        if (isSetListener) {
            observer.setTransferListener(mTransferListener);
        }
        //}
    }

    @Override
    protected void finalize() throws Throwable {
        log().devel("finalize");
        mTransferUtility.pauseAllWithType(TransferType.DOWNLOAD);
        super.finalize();
    }

