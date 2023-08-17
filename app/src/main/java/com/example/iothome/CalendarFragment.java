package com.example.iothome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CalendarFragment extends Fragment{
    CalendarView calendarView;
    String targetTask;
    EditText editText;
    Button button_search;
    DBHelper dbHelper;

    ListView resultList;
    TodolistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);
        dbHelper=DBHelper.getInstance(getActivity().getApplicationContext());

        calendarView=rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //선택된 날짜의 일정을 띄운다.
                Bundle bundle=new Bundle();
                bundle.putString("MonthDay",String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));

                Fragment dayFragment=new DayFragment();
                dayFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_calendar,dayFragment).addToBackStack(null).commit();
            }
        });

        resultList=rootView.findViewById(R.id.resultList);
        editText=rootView.findViewById(R.id.searchTask);
        button_search=rootView.findViewById(R.id.searchBtn);
        button_search.setOnClickListener(this::onClick);

        return rootView;
    }

    public void onClick(View v){
        targetTask = editText.getText().toString();
        ArrayList<String> search_result = dbHelper.select_todolist_with_task(targetTask);

        // 일치하는 task가 없는 경우
        if (search_result == null){
            Toast.makeText(getActivity(), "일치하는 일정이 없습니다.", Toast.LENGTH_SHORT).show();
            resultList.setVisibility(View.INVISIBLE);
            calendarView.setVisibility(View.VISIBLE);
            // 일치하는 task가 있는 경우
        }else{
            resultList.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.INVISIBLE);
            //빈문자열로 검색한 경우
            if (targetTask.length() == 0){
                Toast.makeText(getActivity(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                resultList.setAdapter(adapter);
            //빈 문자열이 아닌 경우
            } else{
                TodolistAdapter resultAdapter = new TodolistAdapter(getContext(), R.layout.todolist_item, search_result);
                resultList.setAdapter(resultAdapter);
            }
        }
    }

}