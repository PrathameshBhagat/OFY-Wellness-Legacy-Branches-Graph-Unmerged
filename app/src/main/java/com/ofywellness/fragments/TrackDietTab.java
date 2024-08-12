package com.ofywellness.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.util.ArrayList;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel;
    private ProgressBar energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        // Assign the text views so that tracking data can be set
        energyValueLabel = view.findViewById(R.id.track_energy_display_label);
        proteinsValueLabel = view.findViewById(R.id.track_protein_display_label);
        fatsValueLabel = view.findViewById(R.id.track_fats_display_label);
        carbohydratesValueLabel = view.findViewById(R.id.track_carbohydrates_display_label);

        // Assign the progress bars so that the progress can be shown
        energyProgressBar = view.findViewById(R.id.track_energy_progress_bar);
        proteinsProgressBar = view.findViewById(R.id.track_protein_progress_bar);
        fatsProgressBar = view.findViewById(R.id.track_fats_progress_bar);
        carbohydratesProgressBar = view.findViewById(R.id.track_carbohydrates_progress_bar);

        // Assign the Line Graph
        LineChart lineChart = view.findViewById(R.id.track_line_chart);

        // Assign the Bar Chart
        BarChart barChart = view.findViewById(R.id.track_bar_chart);

        // Now assign Bar Chart entries and fill values
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 1));
        entries.add(new BarEntry(2, 2));
        entries.add(new BarEntry(3, 3));
        entries.add(new BarEntry(4, 4));
        entries.add(new BarEntry(5, 5));
        entries.add(new BarEntry(6, 6));
        entries.add(new BarEntry(7, 7));

        // Create a DataSet for our Bar chart and set its colors
        BarDataSet barDataset = new BarDataSet(entries, "Nutrient Graph");
        barDataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a data for our bar chart
        BarData barData = new BarData(barDataset);

        // Array of Days in a week for bar chart labels
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};

        barChart.getAxisRight().setEnabled(false);

        // Now we get our bar chart's X-axis and set it to show relevant labels
        XAxis barXAxis = barChart.getXAxis();
        barXAxis.setGranularity(1f);
        barXAxis.setValueFormatter((value, axis) -> days[(int) value - 1]);

        // Now Set the data for the bar chart and set its animation
        barChart.setData(barData);
        barChart.animateY(6000);

        // Show/Refresh the bar chart
        barChart.invalidate();

        setLineChart(view);

        // Update tracking tracking data as soon as this tab loads
        updateDietTrackingData();
        // Return view to onCreateView method and the method
        return view;
    }

    void setLineChart(View view){

        // Assign the Line Graph
        LineChart lineChart = view.findViewById(R.id.track_line_chart);
        lineChart.getDescription().setEnabled(false);
        lineChart.setOutlineAmbientShadowColor(Color.WHITE);


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1,10));
        entries.add(new Entry(2,20));
        entries.add(new Entry(3,30));
        entries.add(new Entry(4,40));
        entries.add(new Entry(5,50));
        entries.add(new Entry(6,60));
        entries.add(new Entry(7,70));

        LineDataSet lineDataset = new LineDataSet(entries, "");
        lineDataset.setColor(Color.BLACK);
        lineDataset.setCircleColor(Color.BLUE);


        // Create a data for our bar chart
        LineData lineData = new LineData(lineDataset);


        String[] days = {"","Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};

        lineChart.getAxisRight().setEnabled(false);

        YAxis lineYAxis = lineChart.getAxisLeft();
        lineYAxis.setAxisMinimum(0f);


        XAxis lineXAxis = lineChart.getXAxis();
        lineXAxis.setValueFormatter((value, axis) -> days[((int) value)]);
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setAxisMinimum(0.5f);

        lineChart.setData(lineData);
        lineChart.animateXY(6000,6000);

    }
    // Update tracking data each time user clicks update button
    void updateDietTrackingData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getTrackDietDataAndSetData(this, energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void updateProgress() {

        // Simple try catch block to catch any errors and exceptions
        try {
            // Variables required to get current total diet intake
            int currentEnergy, currentProteins , currentFats, currentCarbohydrates;

            // Get the current progress
            currentEnergy = Integer.parseInt(energyValueLabel.getText().toString().replace("Cal", ""));
            currentProteins = Integer.parseInt(proteinsValueLabel.getText().toString().replace("g", ""));
            currentFats = Integer.parseInt(fatsValueLabel.getText().toString().replace("g", ""));
            currentCarbohydrates = Integer.parseInt(carbohydratesValueLabel.getText().toString().replace("g", ""));

            // Store in a meal object
            Meal currentProgress = new Meal(null,null,
                    currentEnergy ,
                    currentProteins ,
                    currentFats ,
                    currentCarbohydrates );

            // Call the method to update the progress
            ofyDatabase.updateDietProgress( requireActivity(), currentProgress,energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar) ;

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}