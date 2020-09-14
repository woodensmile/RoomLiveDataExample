package com.doubles.roomlivedataexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.doubles.roomlivedataexample.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // DataBinding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.lifecycleOwner = this // liveData를 활용하기 위해서 해줘야한다 안해줄때는 liveData가 데이터 관찰이 되더라도 xml이 변경이 되지 않는다.

        // viewModel을 적용하면서 MainViewModel 클래스로 이동
        /*val db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "database-name"
        )// .allowMainThreadQueries()
            .build()*/

        // viewModel적용
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel

//        기존에 LiveData사용전 결과값을 반영시키는 코드 result_text.text = db.todoDao().getAll().toString()
        // LiveData로 TodoDao에서 getAll을 감쌓아서 관찰 대상을 정해놨음으로 getAll에 옵저버를 붙여서 관찰을 하다가 변화가 있으면 아래의 코드로 동작한다.
        viewModel.getAll().observe(this, Observer {
            // UI 갱신
            result_text.text = it.toString()
        })

        add_button.setOnClickListener {
            // 코루틴 lofecycleScope를 사용하여 비동기 처리하는 예제 launch에 Dispatchers.IO를 해줘야 백그라운드 스레드에서 동작한다.
            lifecycleScope.launch(Dispatchers.IO) {
                // 기본적으로 디비처리나 데이터 처리는 메인쓰레드에서 하면 안되며 UI요소만 하도록 되어있기때문에 기존에 db를 생성하면서 빌드하기전 .allowMainThreadQueries()를 넣어 메인스레드에서 처리했으나
                // 코루틴을 적용하여 비동기 처리를 해줌으로서 백그라운드 스레드에서 처리가되면서 .allowMainThreadQueries()코드는 빠져도 됨.
                viewModel.insert(Todo(todo_edit.text.toString()))
            }
//           기존에 LiveData사용전 결과값을 반영시키는 코드 result_text.text = db.todoDao().getAll().toString()
        }
    }
}