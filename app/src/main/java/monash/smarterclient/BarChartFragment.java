package monash.smarterclient;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {
    View vBarChartReport;
    private BarChart mBarChart;
    private List<BarEntry> entries;
    private int resid;
    private Spinner mSpinner;
    private String timePeriod;
    private float[] data;

    public BarChartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vBarChartReport = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        mBarChart = (BarChart) vBarChartReport.findViewById(R.id.bar_chart);
        mSpinner = (Spinner) vBarChartReport.findViewById(R.id.bar_chart_spinner);

        Intent intent = getActivity().getIntent();
        resid = intent.getExtras().getInt("resid");

        Button button = (Button) vBarChartReport.findViewById(R.id.bar_chart_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePeriod = mSpinner.getSelectedItem().toString();
                GetData getData = new GetData();
                getData.execute((Void) null);
            }
        });

        return vBarChartReport;
    }

    public class GetData extends AsyncTask<Void, Void, Boolean>{
        public GetData(){}

        @Override
        protected Boolean doInBackground(Void... params){
            switch (timePeriod) {
                case "hourly":
                    data = HTTPRequest.findHourlyUsageByResidAndDate(resid, "2018-03-17");
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success){
            if (success){
                entries = new ArrayList<>();
                for (int i = 0; i < data.length; i ++){
                    if (!String.valueOf(data[i]).isEmpty()) {
                        entries.add(new BarEntry(i, data[i]));
                    } else {
                        entries.add(new BarEntry(i, 0));
                    }
                }

                BarDataSet set = new BarDataSet(entries, "Usage");
                BarData barData = new BarData(set);
                barData.setBarWidth(0.9f);
                mBarChart.setData(barData);
                mBarChart.setFitBars(true);
                mBarChart.invalidate();
            }
        }
    }

}
