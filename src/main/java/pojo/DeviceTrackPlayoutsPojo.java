package pojo;
import java.util.List;

/**
 * @author Umesh.Shukla
 */
public class DeviceTrackPlayoutsPojo {

    List<DeviceTrackPlayout> deviceTrackPlayouts;

    public DeviceTrackPlayoutsPojo() {
    }

    public DeviceTrackPlayoutsPojo(List<DeviceTrackPlayout> deviceTrackPlayouts) {
        this.deviceTrackPlayouts = deviceTrackPlayouts;
    }

    public List<DeviceTrackPlayout> getDeviceTrackPlayouts() {
        return deviceTrackPlayouts;
    }

    public void setDeviceTrackPlayouts(List<DeviceTrackPlayout> deviceTrackPlayouts) {
        this.deviceTrackPlayouts = deviceTrackPlayouts;
    }
}