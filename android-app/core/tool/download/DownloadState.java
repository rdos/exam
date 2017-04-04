package dubsapp.core.tool.download;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

/**
 * The current state of a download file.
 */
enum DownloadState {UNKNOWN, IN_PROGRESS, COMPLETED, PAUSED, FAILED;
    public static DownloadState valueOf(TransferState state) {
        try {
            return valueOf(state.name());
        }
        catch (Exception e) {
            return UNKNOWN;
        }
    }

}