package monash.smarterclient;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PieChartFragment extends Fragment {
    View vPieChartReport;
    private PieChart mPieChart;
    private List<PieEntry> entries;
    private int resid;
    private EditText mDateEditText;
    private String date;

    public PieChartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vPieChartReport = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        mPieChart = (PieChart) vPieChartReport.findViewById(R.id.pie_chart);

        mDateEditText = (EditText) vPieChartReport.findViewById(R.id.pie_chart_input_date);
        mDateEditText.setInputType(InputType.TYPE_NULL);
        mDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDatePickerDialog();
            }
        });
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button mButton = (Button) vPieChartReport.findViewById(R.id.pie_chart_bt);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = mDateEditText.getText().toString();
                GetDailyUsageData getDailyUsageData = new GetDailyUsageData();
                getDailyUsageData.execute((Void) null);
            }
        });

        Intent intent = getActivity().getIntent();
        resid = intent.getExtras().getInt("resid");

        entries = new ArrayList<>();

        return vPieChartReport;
    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                mDateEditText.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    public class GetDailyUsageData extends AsyncTask<Void, Void, Boolean>{
        float usage[];
        GetDailyUsageData(){}

        @Override
        protected Boolean doInBackground(Void... params){
            usage = HTTPRequest.findDailyUsageByResidAndDate(resid, date);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success){
            if (success){
                entries.add(new PieEntry(usage[0], "Fridge"));
                entries.add(new PieEntry(usage[1], "Air Condition"));
                entries.add(new PieEntry(usage[2], "Washing Machine"));

                PieDataSet set = new PieDataSet(entries, "Device");
                set.setColors(ColorTemplate.VORDIPLOM_COLORS);
                PieData pieData = new PieData(set);
                mPieChart.setData(pieData);
                mPieChart.invalidate();
            }
        }
    }
}
