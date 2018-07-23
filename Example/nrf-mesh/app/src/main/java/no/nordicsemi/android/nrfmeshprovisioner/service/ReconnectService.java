package no.nordicsemi.android.nrfmeshprovisioner.service;

import android.app.IntentService;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import no.nordicsemi.android.nrfmeshprovisioner.adapter.ExtendedBluetoothDevice;
import no.nordicsemi.android.nrfmeshprovisioner.livedata.ScannerLiveData;
import no.nordicsemi.android.nrfmeshprovisioner.repository.ProvisionedNodesScannerRepository;
import no.nordicsemi.android.nrfmeshprovisioner.repository.ReconnectRepository;

public class ReconnectService extends LifecycleService {
    private static final String TAG = ReconnectService.class.getSimpleName();

    private static final String ACTION_RECONNECT_START = "no.nordicsemi.android.nrfmeshprovisioner.service.action.RECONNECT_START";
    private static final String ACTION_RECONNECT_STOP = "no.nordicsemi.android.nrfmeshprovisioner.service.action.RECONNECT_STOP";

    private static final String EXTRA_PARAM_NETWORK_ID = "no.nordicsemi.android.nrfmeshprovisioner.service.extra.PARAM_NETWORK_ID";

    private ProvisionedNodesScannerRepository scannerRepository;
    private ReconnectRepository reconnectRepository;
    private boolean isRunning = false;


    public static void startReconnect(Context context, String networkID) {
        Intent intent = new Intent(context, ReconnectService.class);
        intent.setAction(ACTION_RECONNECT_START);
        intent.putExtra(EXTRA_PARAM_NETWORK_ID, networkID);
        context.startService(intent);
    }

    public static void stopReconnect(Context context) {
        Intent intent = new Intent(context, ReconnectService.class);
        intent.setAction(ACTION_RECONNECT_STOP);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        scannerRepository = new ProvisionedNodesScannerRepository(this);
        reconnectRepository = new ReconnectRepository(this);
        isRunning = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECONNECT_START.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_NETWORK_ID);
                if (!isRunning) {
                    isRunning = true;
                    handleStartReconnect(param1);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (isRunning) {
            isRunning = false;
            handleStopReconnect();
            scannerRepository.unbindService();
            reconnectRepository.unbindServiceConnection(this);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            super.onDestroy();
        }
    }

    private Observer<ScannerLiveData> scannerObserver = new Observer<ScannerLiveData>() {
        @Override
        public void onChanged(@Nullable ScannerLiveData scannerLiveData) {
            if (!scannerLiveData.getDevices().isEmpty()) {
                ExtendedBluetoothDevice device = scannerLiveData.getDevices().get(0);
                reconnectRepository.connect(device);
            }
        }
    };

    public void handleStartReconnect(String networkID) {
        Toast.makeText(this, "Reconnect start",
                Toast.LENGTH_LONG).show();
        Log.println(Log.DEBUG, TAG, String.format("Reconnect start"));
        scannerRepository.startScanning(networkID);
        scannerRepository.getScannerState().observeForever(scannerObserver);
    }

    public void handleStopReconnect() {
        Toast.makeText(this, "Reconnect stop", Toast.LENGTH_SHORT).show();
        Log.println(Log.DEBUG, TAG, String.format("Reconnect stop"));
        scannerRepository.stopScanning();
        scannerRepository.getScannerState().removeObserver(scannerObserver);
    }
}
