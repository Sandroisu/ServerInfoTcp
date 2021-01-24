package ru.slatinin.serverinfotcp.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ru.slatinin.serverinfotcp.R;
import ru.slatinin.serverinfotcp.server.servernetlog.ServerNetLog;
import ru.slatinin.serverinfotcp.server.serverdf.ServerDFObjectKeeper;
import ru.slatinin.serverinfotcp.server.servernet.ServerNet;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsql;
import ru.slatinin.serverinfotcp.server.serverdf.SingleServerDF;
import ru.slatinin.serverinfotcp.server.serveriotop.ServerIoTopObjectKeeper;
import ru.slatinin.serverinfotcp.server.serveriotop.SingleIoTop;
import ru.slatinin.serverinfotcp.server.serverpsql.ServerPsqlObjectListKeeper;
import ru.slatinin.serverinfotcp.server.servertop.BaseTopInfo;
import ru.slatinin.serverinfotcp.server.servertop.ServerCommon;
import ru.slatinin.serverinfotcp.server.servertop.ServerCpu;
import ru.slatinin.serverinfotcp.server.servertop.ServerProcesses;
import ru.slatinin.serverinfotcp.server.servertop.ServerTasks;

import static ru.slatinin.serverinfotcp.server.servernet.ServerNet.N_RECEIVED;
import static ru.slatinin.serverinfotcp.server.servernet.ServerNet.N_SENT;

public class ChartUtil {

    public static void initBarChart(BarChart barChart, boolean leftAxisEnable, boolean descriptionEnable, boolean xAxisEnabled,
                                    boolean wordWrapEnabled) {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(xAxisEnabled);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4, false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(0.1f);
        leftAxis.setEnabled(leftAxisEnable);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(wordWrapEnabled);
        legend.setFormSize(8f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
        barChart.getDescription().setEnabled(descriptionEnable);
        barChart.animateXY(500, 1500);
    }

    public static void initPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        pieChart.setExtraBottomOffset(5f);
        pieChart.setExtraLeftOffset(5f);
        pieChart.setExtraRightOffset(5f);
        pieChart.setExtraTopOffset(5f);
        pieChart.setRotationEnabled(false);
        pieChart.animateXY(1500, 1500);
    }

    public static void initLineChart(LineChart lineChart, boolean nullDescription) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMinimum(0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMinimum(0f);
        //leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setEnabled(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setEnabled(false);
        if (nullDescription) {
            lineChart.setDescription(null);
        }
        lineChart.setExtraOffsets(5f, 5f, 5f, 15f);
        lineChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
    }

    private static LineDataSet createLineChartSet(LineDataSet lineDataSet, int lineColor, boolean filled, float lineWidth, boolean dashed) {
        lineDataSet.setDrawIcons(false);
        if (dashed) {
            lineDataSet.enableDashedLine(10f, 5f, 0f);
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        }
        lineDataSet.setColor(lineColor);
        lineDataSet.setCircleColor(lineColor);
        lineDataSet.setLineWidth(lineWidth);
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(0f);
        lineDataSet.setDrawFilled(filled);
        lineDataSet.setFormLineWidth(2f);
        lineDataSet.setFormSize(8f);
        return lineDataSet;
    }

    public static void updateNetList(List<ServerNet> list, LineChart chart) {
        if (list == null || list.size() == 0) {
            return;
        }
        ArrayList<Entry> newSentValues = new ArrayList<>();
        ArrayList<Entry> newReceivedValues = new ArrayList<>();
        String[] labels = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newSentValues.add(new Entry(i, list.get(i).n_sent));
            newReceivedValues.add(new Entry(i, list.get(i).n_received));
            labels[i] = list.get(i).time;
        }
        LineDataSet sentSet;
        LineDataSet receivedSet;
        sentSet = new LineDataSet(newSentValues, N_SENT);
        receivedSet = new LineDataSet(newReceivedValues, N_RECEIVED);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createLineChartSet(sentSet, Color.parseColor("#00c853"), true, 1f, true));
        dataSets.add(createLineChartSet(receivedSet, Color.parseColor("#dd2c00"), true, 1f, true));
        LineData data = new LineData(dataSets);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setData(data);
        chart.invalidate();
    }

    public static void updateNetLogList(List<ServerNetLog> serverNetLogList, LineChart lineChart) {
        ArrayList<Entry> newSentValues = new ArrayList<>();
        ArrayList<Entry> newReceivedValues = new ArrayList<>();
        String[] labels = new String[serverNetLogList.size()];
        for (int i = 0; i < serverNetLogList.size(); i++) {
            newSentValues.add(new Entry(i, serverNetLogList.get(i).n_sent));
            newReceivedValues.add(new Entry(i, serverNetLogList.get(i).n_received));
            labels[i] = serverNetLogList.get(i).time;
        }
        LineDataSet sentSet;
        LineDataSet receivedSet;
        sentSet = new LineDataSet(newSentValues, N_SENT);
        receivedSet = new LineDataSet(newReceivedValues, N_RECEIVED);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createLineChartSet(sentSet, Color.parseColor("#18ffff"), false, 2f, false));
        dataSets.add(createLineChartSet(receivedSet, Color.parseColor("#33691e"), false, 2f, false));
        LineData data = new LineData(dataSets);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.setData(data);
        lineChart.invalidate();
    }

    public static void updateCpuList(List<ServerCommon> serverCommonList, LineChart lcNetInfo) {
        if (serverCommonList == null || serverCommonList.size() == 0) {
            return;
        }
        ArrayList<Entry> first = new ArrayList<>();
        ArrayList<Entry> second = new ArrayList<>();
        ArrayList<Entry> third = new ArrayList<>();
        String[] labels = new String[serverCommonList.size()];
        for (int i = 0; i < serverCommonList.size(); i++) {
            first.add(new Entry(i, serverCommonList.get(i).load_average[0]));
            second.add(new Entry(i, serverCommonList.get(i).load_average[1]));
            third.add(new Entry(i, serverCommonList.get(i).load_average[2]));
            labels[i] = serverCommonList.get(i).time;
        }
        LineDataSet firstSet;
        LineDataSet secondSet;
        LineDataSet thirdSet;
        if (serverCommonList.size() > 0) {
            float[] currentValue = serverCommonList.get(serverCommonList.size() - 1).load_average;
            firstSet = new LineDataSet(first, "la 1: " + currentValue[0]);
            secondSet = new LineDataSet(second, "la 2: " + currentValue[1]);
            thirdSet = new LineDataSet(third, "la 3: " + currentValue[2]);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(createLineChartSet(firstSet, Color.parseColor("#00c853"), false, 1f, true));
            dataSets.add(createLineChartSet(secondSet, Color.parseColor("#dd2c00"), false, 1f, true));
            dataSets.add(createLineChartSet(thirdSet, Color.parseColor("#0091ea"), false, 1f, true));
            LineData data = new LineData(dataSets);
            lcNetInfo.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            lcNetInfo.setData(data);
            lcNetInfo.invalidate();
        }
    }

    public static void updatePsqlList(ServerPsqlObjectListKeeper serverPsqlObjectListKeeper, LineChart chart, boolean xActCommits) {
        int linesCount = serverPsqlObjectListKeeper.getLinesCount();
        int labelCapacity = serverPsqlObjectListKeeper.serverPsqlObjectKeeperList.size();
        if (xActCommits) {
            labelCapacity -= 1;
        }
        String[] labels = new String[labelCapacity];
        int[] colors = getBarChartColors(linesCount);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < linesCount; i++) {
            ServerPsql [] serverPsqls = serverPsqlObjectListKeeper.getSinglePsqlLineData(i);
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0; j < serverPsqls.length; j++) {
                if (xActCommits) {
                    if (j > 0) {
                        labels[j-1] = serverPsqls[j].time;
                        entries.add(new Entry(j - 1, serverPsqls[j].n_xact_commit_calculated));
                    }
                } else {
                    labels[j] =  serverPsqls[j].time;
                    entries.add(new Entry(j, serverPsqls[j].n_numbackends));
                }
            }
            LineDataSet set = createLineChartSet(new LineDataSet(entries, serverPsqlObjectListKeeper.getLineName(i)), colors[i], false, 2f, false);
            dataSets.add(set);
        }
        LineData data = new LineData(dataSets);
        chart.getXAxis().setLabelCount(labels.length + 1, true);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    if (value < labels.length - 1) {
                        int index = Math.round(value);
                        return labels[index];
                    }
                    return "";
                }
                return "";
            }
        });
        chart.getXAxis().setCenterAxisLabels(true);
        chart.getLegend().setWordWrapEnabled(true);
        chart.setData(data);
        chart.invalidate();
    }

    public static void updateIoTopBars(List<SingleIoTop> serverIoTopList, BarChart barChart, boolean showSpeed) {
        List<IBarDataSet> barDataSets;
        ArrayList<BarEntry> wrtn = new ArrayList<>();
        ArrayList<BarEntry> read = new ArrayList<>();
        String c_write = showSpeed ? "write, Kb/s" : "write, GB";
        String c_read = showSpeed ? "read, Kb/s" : "read, GB";
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            barDataSets = barChart.getData().getDataSets();
            barDataSets.clear();
        } else {
            barDataSets = new ArrayList<>();
        }
        String[] labels;
        labels = new String[serverIoTopList.size()];
        for (int i = 0; i < serverIoTopList.size(); i++) {
            labels[i] = serverIoTopList.get(i).c_device;
        }
        for (int i = 0; i < serverIoTopList.size(); i++) {
            SingleIoTop singleIoTop = serverIoTopList.get(i);
            if (showSpeed) {
                wrtn.add(new BarEntry((float) i, singleIoTop.n_kb_wrtn_s));
                read.add(new BarEntry((float) i, singleIoTop.n_kb_read_s));
            } else {
                wrtn.add(new BarEntry((float) i, singleIoTop.n_kb_wrtn));
                read.add(new BarEntry((float) i, singleIoTop.n_kb_read));
            }
        }
        BarDataSet set = new BarDataSet(wrtn, c_write);
        BarDataSet set1 = new BarDataSet(read, c_read);
        set.setColor(Color.parseColor("#00c853"));
        set1.setColor(Color.parseColor("#ffc400"));
        barDataSets.add(set1);
        barDataSets.add(set);
        if (showSpeed) {
            setMbMemoryAxisFormatter(barChart.getAxisLeft());
        } else {
            setGbMemoryAxisFormatter(barChart.getAxisLeft());
        }
        setMemoryBarFormatter(set1);
        setMemoryBarFormatter(set);
        BarData data = new BarData(barDataSets);
        data.setDrawValues(true);
        data.setValueTextSize(6f);
        data.setBarWidth(0.38f);
        barChart.setData(data);
        barChart.groupBars(0f, 0.09f, 0.01f);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setLabelCount(labels.length + 1, true);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    if (value < labels.length - 1) {
                        int index = Math.round(value);
                        return labels[index];
                    }
                    return "";
                }
                return "";
            }
        });
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getData().notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    public static void updateServerTasksBars(ServerTasks serverTasks, PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        float hundredPercents = 1f;
        float[] values = serverTasks.getFieldValues();
        String[] labels = serverTasks.getFieldAsLabels();
        for (float value : values) {
            hundredPercents += value;
        }
        for (int i = 0; i < values.length; i++) {
            float slice = (values[i] * 100f) / hundredPercents;
            entries.add(new PieEntry(slice, labels[i] + ":" + values[i]));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getBarChartColors(values.length));
        dataSet.setSliceSpace(1f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setValueLinePart1Length(.4f);
        dataSet.setValueLinePart2Length(.1f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(10f);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    public static void updateTopBars(BaseTopInfo info, BarChart barChart, boolean drawValues) {
        List<IBarDataSet> barDataSets;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            barDataSets = barChart.getData().getDataSets();
            barDataSets.clear();
        } else {
            barDataSets = new ArrayList<>();
        }
        String[] labels;
        float[] values;
        int[] colors;
        labels = info.getFieldAsLabels();
        values = info.getFieldValues();
        colors = getBarChartColors(labels.length);
        for (int i = 0; i < labels.length; i++) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(i, values[i]));
            String label = labels[i];
            if (!drawValues) {
                label += values[i];
            }
            BarDataSet set = new BarDataSet(entries, label);
            set.setColor(colors[i]);
            setMemoryBarFormatter(set);
            barDataSets.add(set);
        }
        BarData data = new BarData(barDataSets);
        data.setValueTextSize(8f);
        data.setDrawValues(drawValues);
        if (info instanceof ServerCpu) {

        } else {
            setGbMemoryAxisFormatter(barChart.getAxisLeft());
        }
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.getBarData().setBarWidth(0.9f);
        barChart.groupBars(-0.4f, 0.01f, 0.03f);
        barChart.getData().notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    public static void updateDiskInfoBars(ServerDFObjectKeeper serverDFObjectKeeper, BarChart barChart) {
        List<IBarDataSet> barDataSets;
        List<SingleServerDF> singleServerDFList = serverDFObjectKeeper.singleServerDFList;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            barDataSets = barChart.getData().getDataSets();
            barDataSets.clear();
        } else {
            barDataSets = new ArrayList<>();
        }
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < singleServerDFList.size(); i++) {
            if (singleServerDFList.get(i).n_used != 0) {
                SingleServerDF singleServerDF = singleServerDFList.get(i);

                entries.add(new BarEntry(position, singleServerDF.n_used));
                entries1.add(new BarEntry(position, singleServerDF.n_blocks));
                position++;
                labels.add(singleServerDF.c_name);
            }
        }
        BarDataSet set = new BarDataSet(entries, "n_used");

        BarDataSet set1 = new BarDataSet(entries1, "n_blocks");
        setMemoryBarFormatter(set);
        setMemoryBarFormatter(set1);
        set.setColor(Color.parseColor("#dd2c00"));
        set1.setColor(Color.parseColor("#0091ea"));
        barDataSets.add(set1);
        barDataSets.add(set);
        BarData data = new BarData(barDataSets);
        data.setDrawValues(true);
        data.setValueTextSize(6f);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        setGbMemoryAxisFormatter(barChart.getAxisRight());
        barChart.groupBars(-0.2f, 0.1f, 0.01f);
        barChart.invalidate();
    }

    public static void updateServerCommonText(ServerCommon serverCommon, TextView textView) {
        if (serverCommon == null){
            return;
        }
        StringBuilder info = new StringBuilder();
        Field[] commonFields = serverCommon.getClass().getFields();
        for (Field field : commonFields) {
            if (!"load_average".equals(field.getName()))
                info.append(field.getName()).append(": ").append(serverCommon.getValueByName(field.getName())).append(";\t\t");
        }
        if (textView.getVisibility() != View.VISIBLE && !info.toString().isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        }
        if (!info.toString().isEmpty()) {
            textView.setText(info.toString());
        }
    }

    private static void setMemoryBarFormatter(BarDataSet dataSet) {
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float x = value / 1000 / 1000;
                String mK = "Gb";
                if (x < 1) {
                    x = x * 1000;
                    mK = "Mb";
                }
                String y = String.valueOf(x);
                if (y.length() > 5) {
                    y = y.substring(0, 4);
                }
                return y + mK;
            }
        });
    }

    private static void setGbMemoryAxisFormatter(YAxis yAxis) {
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float x = value / 1000 / 1000;
                String y = String.valueOf(x);
                return y + "GB";
            }
        });
    }

    private static void setMbMemoryAxisFormatter(YAxis yAxis) {
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float x = value / 1000;
                String y = String.valueOf(x);
                return y + "MB";
            }
        });
    }

    private static void setMillionsAxisFormatter(YAxis yAxis) {
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float x = value / 1000000;
                int y = Math.round(x);
                String z = String.valueOf(y);
                return z + "M";
            }
        });
    }

    public static void buildTable(ServerIoTopObjectKeeper serverIoTopObjectKeeper, ConstraintLayout clIoTop, TableLayout tlIotop, Context mContext) {
        if (clIoTop != null && clIoTop.getVisibility() == View.GONE) {
            clIoTop.setVisibility(View.VISIBLE);
        }
        List<SingleIoTop> singleIoTopList = serverIoTopObjectKeeper.serverIoTopList;
        tlIotop.removeAllViews();
        TableRow namesRow = new TableRow(mContext);
        for (int i = 0; i < ServerIoTopObjectKeeper.columnNames.length; i++) {
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 10, 0);
            if (i > 0) {
                layoutParams.gravity = Gravity.END;
            }
            TextView name = new TextView(mContext);
            name.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
            String text = ServerIoTopObjectKeeper.columnNames[i] + " ";
            name.setText(text);
            name.setLayoutParams(layoutParams);
            name.setPadding(0, 0, 1, 0);
            name.setTextSize(12);
            namesRow.addView(name);
        }
        tlIotop.addView(namesRow);

        for (int i = 0; i < singleIoTopList.size(); i++) {
            if (singleIoTopList.get(i).isMoreThenOne) {
                List<String> values = singleIoTopList.get(i).serverIoTopStringValues;
                TableRow valueRow = new TableRow(mContext);
                for (int j = 0; j < values.size(); j++) {
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    params.setMargins(0, 1, 10, 0);
                    TextView value = new TextView(mContext);
                    if (j > 0) {
                        params.gravity = Gravity.END;
                    }
                    value.setLayoutParams(params);
                    value.setPadding(0, 0, 1, 0);
                    value.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
                    String text = values.get(j) + " ";
                    value.setText(text);
                    value.setTextSize(12);
                    valueRow.addView(value);
                }
                tlIotop.addView(valueRow);
            }
        }
    }

    public static void buildProcessesTable(List<ServerProcesses> serverProcessesList, TableLayout tlProcesses, Context mContext) {
        if (serverProcessesList == null || serverProcessesList.size() == 0) {
            return;
        }
        tlProcesses.removeAllViews();
        TableRow namesRow = new TableRow(mContext);
        for (int i = 0; i < ServerProcesses.getNames().length; i++) {
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 10, 0);
            if (i == 0 || i == 2 || i == 3) {
                layoutParams.gravity = Gravity.END;
            }
            TextView name = new TextView(mContext);
            name.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
            String text = ServerProcesses.getNames()[i] + " ";
            name.setText(text);
            name.setLayoutParams(layoutParams);
            name.setPadding(0, 0, 1, 0);
            name.setTextSize(12);
            namesRow.addView(name);
        }
        tlProcesses.addView(namesRow);

        for (int i = 0; i < serverProcessesList.size(); i++) {
            if (serverProcessesList.get(i).isNotZero) {
                List<String> values = serverProcessesList.get(i).serverProcessesStringValues;
                TableRow valueRow = new TableRow(mContext);
                for (int j = 0; j < values.size(); j++) {
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    params.setMargins(0, 1, 10, 0);
                    TextView value = new TextView(mContext);
                    if (j == 0 || j == 2 || j == 3) {
                        params.gravity = Gravity.END;
                    }
                    value.setLayoutParams(params);
                    value.setPadding(0, 0, 1, 0);
                    value.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
                    String text = values.get(j) + " ";
                    value.setText(text);
                    value.setTextSize(12);
                    valueRow.addView(value);
                }
                tlProcesses.addView(valueRow);
            }
        }
    }

    public static int[] getBarChartColors(int size) {
        int[] colors = new int[16];
        colors[0] = Color.parseColor("#0091ea");
        colors[1] = Color.parseColor("#00c853");
        colors[2] = Color.parseColor("#dd2c00");
        colors[3] = Color.parseColor("#ffc400");
        colors[4] = Color.parseColor("#e040fb");
        colors[5] = Color.parseColor("#18ffff");
        colors[6] = Color.parseColor("#33691e");
        colors[7] = Color.parseColor("#ff6f00");
        colors[8] = Color.parseColor("#3e2723");
        colors[9] = Color.parseColor("#4a148c");
        colors[10] = Color.parseColor("#00acc1");
        colors[11] = Color.parseColor("#2979ff");
        colors[12] = Color.parseColor("#dd2c00");
        colors[13] = Color.parseColor("#3d5afe");
        colors[14] = Color.parseColor("#00838f");
        colors[15] = Color.parseColor("#fdd835");
        if (size > 16) {
            int[] overColors = new int[size];
            for (int i = 0; i < 16; i++) {
                overColors[i] = colors[i];
            }
            for (int i = 16; i < size; i++) {
                overColors[i] = Color.parseColor("#ef9a9a");
            }
            return overColors;
        }
        return colors;
    }
}
