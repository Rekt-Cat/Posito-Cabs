package com.example.positocabs.Models;

import android.os.AsyncTask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;

public class NtpTimeFetcher {

    public interface NtpTimeListener {
        void onNtpTimeFetched(long ntpTime);
    }

    public static void fetchNtpTime(NtpTimeListener listener) {
        new NtpTimeFetchTask(listener).execute();
    }

    private static class NtpTimeFetchTask extends AsyncTask<Void, Void, Long> {

        private final NtpTimeListener listener;

        NtpTimeFetchTask(NtpTimeListener listener) {
            this.listener = listener;
        }

        @Override
        protected Long doInBackground(Void... voids){

            String ntpServer = "time.google.com";

            try {
                NTPUDPClient client = new NTPUDPClient();
                client.setDefaultTimeout(10000); // Set timeout in milliseconds (adjust as needed)
                client.open();

                InetAddress hostAddr = InetAddress.getByName(ntpServer);
                TimeInfo timeInfo = client.getTime(hostAddr);
                timeInfo.computeDetails();

                long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                client.close();
                return ntpTime;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0L;
        }

        @Override
        protected void onPostExecute(Long ntpTime) {
            listener.onNtpTimeFetched(ntpTime);
        }

    }



}
