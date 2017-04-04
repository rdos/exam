package dubsapp.core.tool.download;

/**
 * Listener interface for downloaded progress changes.
 */
public interface ProgressListener {

    /**
     * Called when more bytes are transferred.
     *
     * @param percents Number of percentage downloaded currently.
     * @param bytesCurrent Number of bytes into proper scale downloaded currently.
     * @param bytesTotal The total bytes into proper scale to be downloaded..
     */

    void onProgressChanged(int percents, String bytesCurrent, String bytesTotal);
}