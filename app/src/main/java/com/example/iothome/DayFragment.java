package com.example.iothome;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DayFragment extends Fragment {

    ListView listView;
    TodolistAdapter adapter;

    TextView textView;

    Button button_add;

    private boolean editing = false;
    // 추가 시 입력받을 변수들
    int t_Hour;
    int t_Minute;
    String t_Text = "";

    DBHelper db;

    private int targetDayId;
    private String targetTodolist;
    private int targetTodolistId;
    private boolean adding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // DB
        db = DBHelper.getInstance(getActivity().getApplicationContext());

        // 현재 날짜의 요일을 1 ~ 7의 숫자로 표현하기
        LocalDate current = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            current = LocalDate.now();
        }
        DayOfWeek dayOfWeek = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayOfWeek = current.getDayOfWeek();
        }
        int currentWeekNumber = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentWeekNumber = dayOfWeek.getValue();
        }

        // 클릭한 요일 숫자 가져오기
        Bundle args = getArguments();
        int targetWeekNumber = args.getInt("position");

        // 조회하려는 날짜 계산 후 id 조회
        String targetDay=args.getString("MonthDay");
        if(targetDay==null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                targetDay = current.plusDays(targetWeekNumber - currentWeekNumber).toString();
            }

        targetDayId = db.select_date_with_month_day(targetDay);
        // 해당 날짜의 record가 없다면 삽입
        if (targetDayId == 0){
            db.insert_date(targetDay);
            targetDayId = db.select_date_with_month_day(targetDay);
        }

        // 왼쪽 위 TextView의 내용을 조회하는 날짜로 변경
        textView = (TextView) view.findViewById(R.id.day_textView);
        textView.setText(targetDay);

        // listView
        listView = (ListView) view.findViewById(R.id.day_listView);
//        final ArrayList<String> items = new ArrayList<String>();
        final ArrayList<String> items = db.select_todolist_with_date_id(targetDayId);

        adapter = new TodolistAdapter(getContext(), R.layout.todolist_item, items);
        // listView에 adapter 연결
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 클릭한 아이템 수정
                targetTodolist = adapter.getItem(position);

                //클릭한 아이템의 타이틀 저장하기
                String hour = targetTodolist.substring(0, 2);
                String minute = targetTodolist.substring(3, 5);
                String task = targetTodolist.substring(6);

                targetTodolistId = db.select_id_from_todolist(targetDayId, Integer.parseInt(hour), Integer.parseInt(minute), task);
                adding = false;
                alertTodolistDialog();
            }
        });

        listView.setCacheColorHint(Color.parseColor("#00000000"));

        //다중 삭제를 위한 선택 모드
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.menu_contact, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.selectAll) {
                    final int checkedCount = items.size();

                    adapter.removeSelection();

                    for (int i = 0; i < checkedCount; i++) {
                        listView.setItemChecked(i, true);
                    }
                    actionMode.setTitle(checkedCount + " Selected");
                    return true;
                } else if (menuItem.getItemId() == R.id.delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("선택한 항목을 삭제하시겠습니까?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            SparseBooleanArray selected = adapter.getSelectedIds();

                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    String selecteditem = adapter.getItem(selected.keyAt(i));
                                    adapter.remove(selecteditem);
                                    // 시간, 분, task를 전달한다.
                                    db.delete_todolist(
                                            Integer.parseInt(selecteditem.substring(0, 2)),
                                            Integer.parseInt(selecteditem.substring(3, 5)),
                                            selecteditem.substring(6),
                                            targetDayId);
                                }
                            }
                            actionMode.finish();
                            selected.clear();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.setIcon(R.mipmap.ic_launcher_round);
                    alert.setTitle("Confirmation");
                    alert.show();
                    return true;
                }
                    else{
                        return false;
                    }
                }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        button_add = view.findViewById(R.id.day_add_button);
        button_add.setOnClickListener(this::onClick2);
    }

    // Add 버튼 클릭 시
    public void onClick2(View view){
        adding = true;
        alertTodolistDialog();
    }

    public void AlertTextInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What Task?");
        // 입력받을 EditText 객체
        final EditText input = new EditText(getActivity());
        // 입력받을 타입 설정. ex) input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // OK 버튼 설정
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                t_Text = input.getText().toString();
                String hour_String;
                String minute_String;

                // 시간이나 분이 10보다 작은 경우 수정
                if (t_Hour < 10){
                    hour_String = "0" + Integer.toString(t_Hour);
                }else{
                    hour_String = Integer.toString(t_Hour);
                }
                if (t_Minute < 10){
                    if (t_Minute == 0){
                        minute_String = "00";
                    }else{
                        minute_String = "0" + Integer.toString(t_Minute);
                    }
                }else{
                    minute_String = Integer.toString(t_Minute);
                }

                // 추가하는 경우
                if (adding){
                    adapter.add(hour_String + ":" + minute_String + " " + t_Text);
                    db.insert_todolist(t_Hour, t_Minute, t_Text, targetDayId);
                    Log.d("Test", "true");
                // 수정하는 경우
                }else{
                    adapter.update(targetTodolist, hour_String + ":" + minute_String + " " + t_Text);
                    db.update_todolist(targetTodolistId, t_Hour, t_Minute, t_Text);
                    Log.d("Test", "false");
                }

                // 자동 시간순 정렬을 위한 Comparator 객체
                Comparator<String> Asc = new Comparator<String>() {
                    @Override
                    public int compare(String item1, String item2) {

                        int ret ;

                        if (Integer.parseInt(item1.substring(0, 2)) < Integer.parseInt(item2.substring(0, 2))){
                            ret = -1 ;
                        } else if (Integer.parseInt(item1.substring(0, 2)) == Integer.parseInt(item2.substring(0, 2))){
//                            ret = 0 ;
//                            ret = item1.getMinute().compare(item2.getMinute());
                            // 시간이 같을 시 분으로 비교
                            // 아래에 쓰인 compare()는 현재 실행중인 메소드가 아닌 Integer의 메소드임
                            ret = Integer.compare(
                                    Integer.parseInt(item1.substring(3, 5)), Integer.parseInt(item2.substring(3, 5))
                            );
                        } else{
                            ret = 1 ;
                        }
                        return ret ;
                    }
                };

                // adapter에서 list를 얻어서 위에서 만든 Comparator객체로 오름차순 정렬
                Collections.sort(adapter.getMyList(), Asc);
                // 변경사항을 View에 반영
                adapter.notifyDataSetChanged();
            }
        });

        // Cancel 버튼 설정
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void alertTodolistDialog(){
        int cHour = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cHour = LocalTime.now().getHour();
        }
        int cMinute = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cMinute = LocalTime.now().getMinute();
        }
        // 시간 선택 dialog 띄우기
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), 2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                t_Hour = hour;
                t_Minute = minute;
                AlertTextInputDialog();     // 추가적으로 task가 무엇인지 입력하는 텍스트 dialog 띄우기
            }
        }, cHour, cMinute,false);
        timePickerDialog.show();
    }

}
