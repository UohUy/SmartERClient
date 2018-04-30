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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class LineChartFragment extends Fragment {
    View vLineChartReport;
    private LineChart mLineChart;
    private List<Entry> entries1;
    private List<Entry> entries2;
    private int resid;
    private Spinner mSpinner;
    private String timePeriod;
    private float[] data;
    private float[] temperature;

    public LineChartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vLineChartReport = inflater.inflate(R.layout.fragment_line_chart, container, false);
        mLineChart = (LineChart) vLineChartReport.findViewById(R.id.line_chart);
        mSpinner = (Spinner) vLineChartReport.findViewById(R.id.line_chart_spinner);

        Intent intent = getActivity().getIntent();
        resid = intent.getExtras().getInt("resid");

        Button button = (Button) vLineChartReport.findViewById(R.id.line_chart_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePeriod = mSpinner.getSelectedItem().toString();
                GetData getData = new GetData();
                getData.execute((Void) null);
            }
        });

        return vLineChartReport;
    }

    public class GetData extends AsyncTask<Void, Void, Boolean> {
        public GetData(){}

        @Override
        protected Boolean doInBackground(Void... params){
            switch (timePeriod) {
                case "hourly":
                    data = HTTPRequest.findHourlyUsageByResidAndDate(resid, "2018-03-17");
                    temperature = HTTPRequest.findHourlyTemperatureByResidAndDate(resid, "2018-03-17");
                    break;
                case "daily":
                    data = new float[31];
                    temperature = new float[31];
                    for (int i = 0; i < 31; i ++){
                        temperature[i] = HTTPRequest.findDailyTemperatureByResidAndDate(resid, "2018-03-", i);
                        data[i] = HTTPRequest.findDailyUsageByResidAndDate(resid, "2018-03-", i);
                    }
                    break;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success){
            if (success){
                entries1 = new ArrayList<>();
                entries2 = new ArrayList<>();
                for (int i = 0; i < data.length; i ++){
                    Entry c1 = new Entry(i, data[i]);
                    Entry c2 = new Entry(i, temperature[i]);
                    entries1.add(c1);
                    entries2.add(c2);
                }

                LineDataSet set1 = new LineDataSet(entries1, "Usage");
                LineDataSet set2 = new LineDataSet(entries2, "Temperature");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
                set2.setColors(ColorTemplate.VORDIPLOM_COLORS);
                List<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                dataSets.add(set2);

                LineData lineData = new LineData(dataSets);
                mLineChart.setData(lineData);
                mLineChart.invalidate();
            }
        }
    }
}
