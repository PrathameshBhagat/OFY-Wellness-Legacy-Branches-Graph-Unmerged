package com.ofywellness.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel;
    private ProgressBar energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar;
    private BarChart barChart;
    private LineChart lineChart, curvedLineChart;
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
        lineChart = view.findViewById(R.id.track_line_chart);

        // Assign the curved line chart
        curvedLineChart = view.findViewById(R.id.track_weight_curved_line_chart);

        // Assign the Bar Chart
        barChart = view.findViewById(R.id.track_water_intake_bar_chart);

        updateChart();
        // Call the method to set the line graph
        setLineChart();

        // Update tracking tracking data as soon as this tab loads
        updateDietTrackingData();
        // Return view to onCreateView method and the method
        return view;
    }

    private void updateChart() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getOtherDataAndSetCharts(this);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void setBarChartWithWaterIntakeData(HashMap<String, Integer> waterIntakeDataMap) {

        // Hide the description of the bar chart, we don't need it
        barChart.getDescription().setEnabled(false);

        // List for storing days for bar chart labels
        LinkedList<String> days = new LinkedList<>();

        // Entries for Bar Chart
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        // Integer for X values of the chart
        int i = 1;

        // Iterate over and convert intake data to bar chart entries
        for (Map.Entry<String, Integer> waterIntakeEntries : waterIntakeDataMap.entrySet()) {

            // Fill bar chart entries { X : [ 1,2,3,...], Y : [10,1,5,8,7,..] }
            barEntries.add(new BarEntry(i++, waterIntakeEntries.getValue()));

            // Add date labels
            days.add(waterIntakeEntries.getKey());
        }

        // Create a DataSet for bar chart
        BarDataSet barDataset = new BarDataSet(barEntries,"");
        // Set colors for the chart
        barDataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a data object for our bar chart
        BarData barData = new BarData(barDataset);

        // Get the right axis and disable it as it's not needed
        barChart.getAxisRight().setEnabled(false);

        // Now we get our bar chart's X-axis
        XAxis barXAxis = barChart.getXAxis();
        // Set granularity to 1 so that the X values don't repeat themselves on zoom
        barXAxis.setGranularity(1f);
        // Set x-axis position to bottom (default - top)
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Hide the grid lines as they are not required
        barXAxis.setDrawGridLines(false);

        // Set the X-axis to show relevant labels
        barXAxis.setValueFormatter((value, axis) ->
        {
            // Simple try-catch block
            try {

                // Get the day from list of days to be mapped
                String date = days.get((int) value - 1);

                // Now parse the date string to a date object
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                // Return the date in "Jan 13" like format
                return new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(dateObj);

            } catch (Exception e) {
                // If exception occurs, show a toast
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // And return a string with error text for each entry
                return "Error";
            }
        });

        // Now set the data and animation for the bar chart
        barChart.setData(barData);
        barChart.animateY(5000);

        // Show/Refresh the bar chart
        barChart.invalidate();

    }

    // Method to set the weight tracker line chart (called by ofyDatabase method)
    public void setCurvedLineChartWithDailyWeightData(HashMap<String, Integer> weightDataMap) {

        // Hide the description of the chart, we don't need it
        curvedLineChart.getDescription().setEnabled(false);

        // List for storing days for chart labels
        LinkedList<String> days = new LinkedList<>();

        // Entries for the chart
        ArrayList<Entry> curvedLineEntries = new ArrayList<>();

        // Integer for X-axis values of the chart
        int i = 1;

        // Iterate over and convert daily weight data to chart entries
        for (Map.Entry<String, Integer> dailyWeightEntries : weightDataMap.entrySet()) {

            // Fill chart entries { X : [ 1,2,3,...], Y : [10,1,5,8,7,...] }
            curvedLineEntries.add(new Entry(i++, dailyWeightEntries.getValue()));

            // Add date/day labels
            days.add(dailyWeightEntries.getKey());
        }


        // Create the dataset for the line chart
        LineDataSet curvedLineDataSet = new LineDataSet(curvedLineEntries, "");

        // Set the line color to black and circle (point) color to blue
        curvedLineDataSet.setColor(Color.BLACK);
        curvedLineDataSet.setCircleColor(Color.BLUE);

        // Make the line chart curved/smooth
        curvedLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Make the chart fill the area below the line/curve and set the drawable to fill it with
        curvedLineDataSet.setDrawFilled(true);
        curvedLineDataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.home_background));


        // Create a data object for our curved line chart from the dataset
        LineData lineData = new LineData(curvedLineDataSet);

        // Hide the right axis as we do not need a right axis for this chart
        curvedLineChart.getAxisRight().setEnabled(false);

        // Get the left axis and make it start with 0th point
        curvedLineChart.getAxisLeft().setAxisMinimum(0f);

        // Get the X-axis of the curved line chart
        XAxis lineXAxis = curvedLineChart.getXAxis();

        // Remove grid lines and set axis minimum to 0.5
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setAxisMinimum(0.5f);

        // Set X-axis position to bottom (default - top)
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set granularity to 1 so that on zoom the X-axis values do not get repeated
        lineXAxis.setGranularity(1f);

        // Now set its labels via value formatter
        lineXAxis.setValueFormatter((value, axis) ->
        {
            // Simple try-catch block
            try {

                // Get the exact day from list of days to be mapped
                String date = days.get((int) value - 1);

                // Now parse the date string to a date object
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                // Return the date in "Jan 13" like format
                return new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(dateObj);

            } catch (Exception e) {
                // If exception occurs, show a toast
                Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                // And return a string with error text for each entry
                return "Error";
            }
        });

        // Now set the line chart's animation
        curvedLineChart.animateY(5000);

        // Finally set the data
        curvedLineChart.setData(lineData);

    }

    // Method to set the line chart
    void setLineChart() {

        // Hide the description of the line chart, we don't need it
        lineChart.getDescription().setEnabled(false);

        // Get dummy entries
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1,10));
        entries.add(new Entry(2,20));
        entries.add(new Entry(3,30));
        entries.add(new Entry(4,40));
        entries.add(new Entry(5,50));
        entries.add(new Entry(6,60));
        entries.add(new Entry(7,70));

        // Create the dataset for the line chart
        LineDataSet lineDataset = new LineDataSet(entries, "");

        // Set the line color to black and circle (point) color to blue
        lineDataset.setColor(Color.BLACK);
        lineDataset.setCircleColor(Color.BLUE);

        // Create a data for our line chart from the dataset
        LineData lineData = new LineData(lineDataset);

        // String of days (first date needs to be empty as the X-axis's minimum is 0.5)
        String[] days = {"","Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};

        // Hide the right axis as we do not need a right axis for this chart
        lineChart.getAxisRight().setEnabled(false);

        // Get the left axis and make it start with 0th point
        lineChart.getAxisLeft().setAxisMinimum(0f);

        // Get the x axis of line chart
        XAxis lineXAxis = lineChart.getXAxis();

        // Remove grid lines and set axis minimum to 0.5
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setAxisMinimum(0.5f);

        // Set granularity to 1 so that on zoom the X values do not get repeated
        lineXAxis.setGranularity(1f);

        // Now set its labels via value formatter, note here's no "-1", as minimum is 0.5 and not 0
        lineXAxis.setValueFormatter((value, axis) -> days[((int) value)]);

        // Now set the line chart's animation
        lineChart.animateY(6000);

        // Finally set the data
        lineChart.setData(lineData);

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