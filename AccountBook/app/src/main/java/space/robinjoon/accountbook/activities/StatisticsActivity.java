package space.robinjoon.accountbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import space.robinjoon.accountbook.R;
import space.robinjoon.accountbook.dto.*;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private AccountBook accountBook;
    private ObjectMapper mapper = new ObjectMapper();
    private static final int[] CHART_COLORS = {Color.rgb(244, 67, 54),
            Color.rgb(255,152,0), Color.rgb(255,193,7),
            Color.rgb(255,255,59),Color.rgb(205,220,57),
            Color.rgb(139,195,74),Color.rgb(76,175,80), Color.rgb(0,150,136),
            Color.rgb(0,188,212),Color.rgb(3,169,244),Color.rgb(33,150,243),
            Color.rgb(63,81,181),
            Color.rgb(103, 58, 183),Color.rgb(156, 39, 176),};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        try {
            accountBook = mapper.readValue(getIntent().getStringExtra("accountbook"),new TypeReference<AccountBook>(){});
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
        expenditureStatistics(findViewById(R.id.ExpenditureButton));
    }
    private void drawPieChart(long sum, List<CategoryWithValue> categoryWithValueList){
        ArrayList<PieEntry> yValues = new ArrayList<>();
        Map<PieEntry, CategoryWithValue> pieEntryCategoryWithValueMap = new HashMap<>();
        for(int i=0;i<categoryWithValueList.size();i++){
            PieEntry pieEntry = new PieEntry(categoryWithValueList.get(i).value,categoryWithValueList.get(i).name);
            //if(pieEntry.getValue()/sum*100<5)pieEntry.setLabel("");
            pieEntryCategoryWithValueMap.put(pieEntry,categoryWithValueList.get(i));
            yValues.add(pieEntry);
        }

        pieChart = findViewById(R.id.pieChart_view);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(getColor(R.color.primaryTextColor));
        pieChart.animateY(1000, Easing.EaseInOutCubic); //애니메이션
        pieChart.setUsePercentValues(true);
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(CHART_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.8f);
        dataSet.setValueLinePart2Length(0.6f);
        dataSet.setValueTextColors(Arrays.stream(CHART_COLORS).boxed().collect(Collectors.toList()));
        dataSet.setValueLineColor(getColor(R.color.primaryTextColor));
        dataSet.setValueFormatter(new PercentFormatter());
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);

        pieChart.setData(data);
    }
    private void drawChartDescription(long sum,List<CategoryWithValue> categoryWithValueList){
        LinearLayout entryLayout = findViewById(R.id.entryListView);
        entryLayout.removeAllViews();
        for(int i=categoryWithValueList.size()-1;i>=0;i--){
            CategoryWithValue category = categoryWithValueList.get(i);
            TextView textView = new TextView(getApplicationContext());
            textView.setText(category.getPercent(sum)+category.toString());
            textView.setTextColor(CHART_COLORS[i]);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            textView.setTextSize(size.y/90);
            //textView.setTextColor(getColor(R.color.primaryTextColor));
            entryLayout.addView(textView);
        }
    }
    private List<CategoryWithValue> makeCategoryWithValueList(long sum, List<Account> accountList, List<Category> categoryList){
        long[] values = new long[categoryList.size()];
        List<CategoryWithValue> categoryWithValueList = new ArrayList<>();
        for(int i=0;i<categoryList.size();i++){
            String categoryName = categoryList.get(i).getName();
            for(Account account : accountList) {
                if (categoryName.equals(account.getCategory())) {
                    values[i] += account.getValue();
                }
            }
            CategoryWithValue category = new CategoryWithValue(categoryName,values[i]);
            categoryWithValueList.add(category);
        }
        Collections.sort(categoryWithValueList);
        return categoryWithValueList;
    }
    public void incomeStatistics(View view){
        List<Category> incomeCategoryList = accountBook.getIncomeCategoryList();
        List<Account> incomeAccountList = accountBook.getIncomeAccountList();
        long totalIncome = accountBook.getTotalIncome();
        List<CategoryWithValue> categoryWithValueList = makeCategoryWithValueList(totalIncome,incomeAccountList,incomeCategoryList);
        drawPieChart(totalIncome,categoryWithValueList);
        drawChartDescription(totalIncome,categoryWithValueList);
    }
    public void expenditureStatistics(View view){
        List<Category> expenditureCategoryList = accountBook.getExpenditureCategoryList();
        List<Account> expenditureAccountList = accountBook.getExpenditureAccountList();
        long totalExpenditure = accountBook.getTotlaExpenditure();
        List<CategoryWithValue> categoryWithValueList = makeCategoryWithValueList(totalExpenditure,expenditureAccountList,expenditureCategoryList);
        drawPieChart(totalExpenditure,categoryWithValueList);
        drawChartDescription(totalExpenditure,categoryWithValueList);
    }
    private class CategoryWithValue implements Comparable<CategoryWithValue>{
        String name;
        long value;
        private CategoryWithValue(String name,long value){
            this.name = name;
            this.value = value;
        }
        private String getPercent(long sum){
            return String.format("%.2f%% ", (float)(value)/sum *100.0f);
        }
        @Override
        public int compareTo(CategoryWithValue categoryWithValue) {
            if(this.value<categoryWithValue.value)return -1;
            else if(this.value==categoryWithValue.value)return 0;
            else return 1;
        }

        @Override
        public String toString() {
            return name+" : "+new DecimalFormat("###,###").format(value) +"₩";
        }
    }
}