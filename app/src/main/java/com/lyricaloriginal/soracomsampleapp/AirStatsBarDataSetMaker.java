package com.lyricaloriginal.soracomsampleapp;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lyricaloriginal.soracomapiandroid.AirStats;
import com.lyricaloriginal.soracomapiandroid.TrafficStats;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LyricalMaestro on 2016/01/06.
 */
final class AirStatsBarDataSetMaker {

    static BarData make(List<AirStats> airStatsList, long fromDate, long toDate) {
        int from = toYearMonthInt(new Date(fromDate));
        int to = toYearMonthInt(new Date(toDate));

        ArrayList<String> months = makeMonthLabel(from, to);
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        for (int index = 0; index < months.size(); index++) {
            String month = months.get(index);
            AirStats airStats = findAirStats(airStatsList, month);
            if (airStats == null) {
                yVals.add(new BarEntry(new float[]{0, 0}, index));
                continue;
            }

            float upload = 0;
            float download = 0;
            for (TrafficStats trafficStats : airStats.dataTrafficStatsMap.values()) {
                upload += trafficStats.uploadByteSizeTotal;
                download += trafficStats.downloadByteSizeTotal;
            }
            yVals.add(new BarEntry(new float[]{upload, download}, index));
        }

        BarDataSet set1 = new BarDataSet(yVals, "");
        set1.setBarSpacePercent(35f);
        set1.setColors(new int[]{
                Color.BLUE, Color.YELLOW
        });
        set1.setStackLabels(new String[]{"Upload", "Download"});
        set1.setValueFormatter(new MyValueFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(months, dataSets);
        data.setValueTextSize(10f);
        return data;
    }

    private static AirStats findAirStats(List<AirStats> airStatsList, String month) {
        for (AirStats airStats : airStatsList) {
            if (airStats.date.equals(month)) {
                return airStats;
            }
        }
        return null;
    }

    private static int toYearMonthInt(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        return Integer.valueOf(format.format(date));
    }

    private static ArrayList<String> makeMonthLabel(int from, int to) {
        ArrayList<String> months = new ArrayList<>();
        int current = from;
        while (current <= to) {
            months.add(String.valueOf(current));
            current++;
            if (current % 100 > 12) {
                current += 88;    //  翌年の１月に移行する。ex)201213 -> 201301の差を計算すると88.
            }
        }
        return months;
    }

    private static class MyValueFormatter implements ValueFormatter{

        private DecimalFormat mFormat;

        MyValueFormatter(){
            mFormat = new DecimalFormat("##,###,###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format((value / 1024));
        }
    }
}
