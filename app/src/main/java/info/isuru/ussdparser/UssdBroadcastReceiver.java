package info.isuru.ussdparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class UssdBroadcastReceiver extends BroadcastReceiver {
    Listener listener;
    public UssdBroadcastReceiver(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase("info.isuru.ussdparser.action.REFRESH")){
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("message");
            Log.d("USSD_LOG", message);
            this.listener.updateResult(message);
        }
    }
}
