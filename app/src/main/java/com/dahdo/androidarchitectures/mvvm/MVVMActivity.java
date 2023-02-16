package com.dahdo.androidarchitectures.mvvm;

import androidx.appcompat.app.AppCompatActivity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dahdo.androidarchitectures.R;
import com.dahdo.androidarchitectures.mvc.MVCActivity;
import com.dahdo.androidarchitectures.mvp.CountriesPresenter;
import com.dahdo.androidarchitectures.mvp.MVPActivity;

import java.util.ArrayList;
import java.util.List;


public class MVVMActivity extends AppCompatActivity {
    private List<String> listValues = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView list;
    private CountriesViewModel viewModel;
    private Button retryButton;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvmactivity);
        setTitle("MVVM Activity");

        viewModel =  new ViewModelProvider.AndroidViewModelFactory(
                this.getApplication()).create(CountriesViewModel.class);

        retryButton = findViewById(R.id.retryButton);
        progress = findViewById(R.id.progress);
        list = findViewById(R.id.list);
        adapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.listText, listValues);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MVVMActivity.this, "You clicked " + listValues.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getCountries().observe(MVVMActivity.this, countries -> {
            if(countries != null) {
                listValues.clear();
                listValues.addAll(countries);
                list.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                list.setVisibility(View.GONE);
            }
        });

        viewModel.getCountryError().observe(this, error -> {
            progress.setVisibility(View.GONE);
            if(error) {
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT);
                retryButton.setVisibility(View.VISIBLE);
            }
            else {
                retryButton.setVisibility(View.GONE);
            }
        });
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, MVVMActivity.class);
    }

    public void onRetry(View view) {
        viewModel.onRefresh();
        list.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }
}