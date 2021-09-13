package pojo;

/**
 * @author Umesh.Shukla
 */
public class DeviceTrackPlayout{
    
    private int trackId;
    private int playout;
    private double deviceDuration;
    private double songDuration;

    public DeviceTrackPlayout() {
    }

    public DeviceTrackPlayout(int trackId, int playout, double deviceDuration, double songDuration) {
        this.trackId = trackId;
        this.playout = playout;
        this.deviceDuration = deviceDuration;
        this.songDuration = songDuration;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getPlayout() {
        return playout;
    }

    public void setPlayout(int playout) {
        this.playout = playout;
    }

    public double getDeviceDuration() {
        return deviceDuration;
    }

    public void setDeviceDuration(double deviceDuration) {
        this.deviceDuration = deviceDuration;
    }

    public double getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(double songDuration) {
        this.songDuration = songDuration;
    }
}

