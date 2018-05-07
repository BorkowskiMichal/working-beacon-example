package kunstmaan.be.anothertest;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

public class BeaconApp extends Application implements BootstrapNotifier,RangeNotifier {
    private static final String TAG = "smallDox";
    private RegionBootstrap regionBootstrap;
    private Region allbeaconsregions;
    private BackgroundPowerSaver bgSaver;
    BeaconManager beaconManager;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        // To detect proprietary beacons, you must add a line likebelowcorresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        beaconManager = BeaconManager.getInstanceForApplication(this);
        Beacon.setHardwareEqualityEnforced(true);
        bgSaver = new BackgroundPowerSaver(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));


        allbeaconsregions = new Region("test.my.region", null, null, null);
        regionBootstrap = new RegionBootstrap(this, allbeaconsregions);
        //beaconManager.bind(this);
    }

    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("test.my.region", null, null, null));
            beaconManager.addRangeNotifier(this);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        Log.d(TAG, "Enter in  didDetermineStateForRegion call");
        onBeaconServiceConnect();
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        /*
        regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent);
        */


    }

    @Override
    public void didExitRegion(Region arg0) {
        Log.d(TAG, "Enter in  didExitRegion call");
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        //insert upload to server here
        if(beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                if (beacon.getDistance() < 1.0) {
                    Log.d(TAG, "I see a beacon transmitting a : " +
                            " approximately " + beacon.getDistance() + " meters away.");
                    Log.d(TAG, "BEACON DATA : " + beacon.getBluetoothAddress() + ":" + beacon.getBluetoothName() + ":" + beacon.getId1());
                }
            }
        }
    }



}

