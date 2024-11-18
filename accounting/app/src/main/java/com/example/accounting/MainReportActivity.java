package com.example.accounting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainReportActivity extends AppCompatActivity {

    private ImageButton CashBookButton, SettingButton,CategoryButton, BudgetButton;
    private Button PDFbutton;
    private RecyclerView RecyclerView;
    private PieChart PieChart;

    private ArrayList<PieEntry> pieEntries = new ArrayList<>();
    private PieDataSet dataSet;
    private PieData data;

    private ViewModel ViewModel;
    private List<BookModel.Category> listOfCategory = new ArrayList<>();

    private ReportAdapter adapter;
    final static int REQUEST_CODE = 1232;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_view);

        PieChart = findViewById(R.id.pieChart);
        CashBookButton = findViewById(R.id.cashBookButton);
        SettingButton = findViewById(R.id.settingButton);
        CategoryButton = findViewById(R.id.CategoryButton);
        BudgetButton = findViewById(R.id.budgetButton);
        PDFbutton = findViewById(R.id.PDFButton);
        RecyclerView = findViewById(R.id.recyclerView);


        RecyclerView.setHasFixedSize(true);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter();
        RecyclerView.setAdapter(adapter);

        // Initialize ViewModel and load categories
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCategory().observe(this, category -> {
            listOfCategory = category;
            adapter.setCategory(category);

            // Initialize PieChart data
            setupPieChart();
            loadPieChartData(); // Populate the chart entries
        });


        CashBookButton.setOnClickListener((v -> navigateToMainCashBookView()));
        SettingButton.setOnClickListener((v ->navigateToMainSettingView()));
        CategoryButton.setOnClickListener((V ->navigateToMainCategoryView()));
        BudgetButton.setOnClickListener((V->navigateToMainBudgetView()));
        PDFbutton.setOnClickListener(v -> convertXmlToPdf(RecyclerView));
    }

    public void convertXmlToPdf(View view) {
        PdfDocument document = new PdfDocument();

        // Set up display metrics for page dimensions
        // Get display metrics for page dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getDisplay().getRealMetrics(displayMetrics);
        int pageWidth = displayMetrics.widthPixels;
        int pageHeight = displayMetrics.heightPixels;

        // Step 1: Draw the PieChart on the PDF
        // Capture the PieChart as a bitmap
        PieChart.invalidate();  // Ensure it's up-to-date
        Bitmap pieChartBitmap = Bitmap.createBitmap(PieChart.getWidth(), PieChart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas pieCanvas = new Canvas(pieChartBitmap);
        PieChart.draw(pieCanvas);

        // Add the PieChart to the PDF document
        PdfDocument.PageInfo piePageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page piePage = document.startPage(piePageInfo);
        Canvas pdfCanvas = piePage.getCanvas();

        // Center the PieChart on the page
        int pieChartX = (pageWidth - pieChartBitmap.getWidth()) / 2;
        int pieChartY = 100; // Some padding from the top
        pdfCanvas.drawBitmap(pieChartBitmap, pieChartX, pieChartY, null);
        document.finishPage(piePage);


// Initialize ViewModel and RecyclerView with adapter
        RecyclerView recyclerView = (androidx.recyclerview.widget.RecyclerView) view;

        // Observe the ViewModel data and update adapter
        ViewModel = new ViewModelProvider(this).get(ViewModel.class);
        ViewModel.getAllCategory().observe(this, category -> {
            adapter.setCategory(category);

            // Measure and layout RecyclerView with the loaded data
            recyclerView.measure(View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED));
            recyclerView.layout(0, 0, recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight());

            // Add RecyclerView content to a new page in the PDF
            PdfDocument.PageInfo recyclerViewPageInfo = new PdfDocument.PageInfo.Builder(recyclerView.getMeasuredWidth(),
                    recyclerView.getMeasuredHeight(), 2).create();
            PdfDocument.Page recyclerViewPage = document.startPage(recyclerViewPageInfo);
            recyclerView.draw(recyclerViewPage.getCanvas());  // Draw RecyclerView directly to canvas
            document.finishPage(recyclerViewPage);
            // Specify the path and filename of the output PDF file
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "Report.pdf";
            File filePath = new File(downloadsDir, fileName);
            try {
                // Save the document to a file
                FileOutputStream fos = new FileOutputStream(filePath);
                document.writeTo(fos);
                document.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    });
        Toast.makeText(this, "pdf report printed into download folder", Toast.LENGTH_LONG).show();

    }

    private void setupPieChart() {
        // Create initial dataset (empty)
        dataSet = new PieDataSet(pieEntries, "Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        data = new PieData(dataSet);

        PieChart.setData(data);
        PieChart.setUsePercentValues(true);
        PieChart.getDescription().setEnabled(false);
        PieChart.setCenterText("budget pie chart");
        PieChart.animateY(1000);
        PieChart.invalidate(); // Refresh the chart initially
        Log.d("cTag", "onCreate: pieChartSetup");
    }

    private void loadPieChartData() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (BookModel.Category category : listOfCategory) {
            float categoryValue = (float)(category.getCategoryBudget()+category.getCategoryTotal());
            if(categoryValue<0){
                categoryValue =0;
            }
            String categoryName = category.getCategory();
            pieEntries.add(new PieEntry(categoryValue, categoryName));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Category Spending");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set colors
        dataSet.setValueTextColor(Color.BLACK); // Set text color
        dataSet.setValueTextSize(12f); // Set text size

        PieData data = new PieData(dataSet);
        PieChart.setData(data);
        PieChart.invalidate(); // Refresh the chart
    }
    private void navigateToMainCashBookView() {
        Intent intent = new Intent(MainReportActivity.this, MainCashBookActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainSettingView() {
        Intent intent = new Intent(MainReportActivity.this, MainSettingActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainBudgetView() {
        Intent intent = new Intent(MainReportActivity.this, MainBudgetActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToMainCategoryView() {
        Intent intent = new Intent(MainReportActivity.this, MainCategoryActivity.class);
        startActivity(intent);
        finish();
    }
}
