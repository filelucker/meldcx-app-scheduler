package com.example.meldcxappscheduler

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meldcxappscheduler.receiver.AppLaunchReceiver
import com.example.meldcxappscheduler.adapter.ItemsAdapter
import com.example.meldcxappscheduler.data.AppInfo
import com.example.meldcxappscheduler.data.AppScheduleStore
import com.example.meldcxappscheduler.databinding.ActivityMainBinding
import com.example.meldcxappscheduler.scheduler.AppScheduler
import com.example.meldcxappscheduler.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), ItemsAdapter.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var installedApps = ArrayList<AppInfo>()
    private lateinit var mainViewModel: MainViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.v("App", "Build Version Greater than or equal to M: " + Build.VERSION_CODES.M);
            checkDrawOverlayPermission(this);
        } else {
            Log.v("App", "OS Version Less than M");
            //No need for Permission as less then M OS.
        }

        val intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")
        val bb = AppLaunchReceiver()
        registerReceiver(bb, intentFilter)

        val packageManager: PackageManager = packageManager

        // Create an intent to retrieve all launchable apps
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

//         // Get a list of ResolveInfo objects representing the buildIn installed apps
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            packageManager.queryIntentActivities(
//                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
//                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
//            )
//        } else {
//            packageManager.queryIntentActivities(
//                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
//                0
//            )
//        }

        // Get a list of ResolveInfo objects representing the installed apps
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        // Loop through the ResolveInfo objects and extract app information
        for (resolveInfo in resolveInfoList) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val icon = resolveInfo.loadIcon(packageManager)
            installedApps.add(AppInfo(0, appName, packageName, icon, ""))
        }


        //  this creates a vertical layout Manager
        binding.recyclerview1.layoutManager =
            LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)

        // Setting the Adapter with the recyclerview
        binding.recyclerview1.adapter = ItemsAdapter(mainViewModel, installedApps, this)


        loadScheduledData()


    }

    override fun onResume() {
        super.onResume()
        loadScheduledData()
    }

    private fun loadScheduledData() {

        mainViewModel.readAllData.observe(this, Observer { appSchadeleStoreList ->
            Log.v("App", "Package Name: " + appSchadeleStoreList)
            for (app in installedApps) {
                for (appSchaduled in appSchadeleStoreList) {
                    if (app.packageName.equals(appSchaduled.packageId)) {
                        var aa = installedApps.get(installedApps.indexOf(app))

                        var newAppInfo = AppInfo(
                            appSchaduled.id,
                            aa.appName,
                            aa.packageName,
                            aa.icon,
                            "Will Start at " + appSchaduled.timeLabel
                        )
                        installedApps.set(installedApps.indexOf(app), newAppInfo)
                    }else{

                    }
                }

            }
            (binding.recyclerview1.adapter as ItemsAdapter).resetView(installedApps)
        })
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkDrawOverlayPermission(context: Context) {
        Log.v("App", "Package Name: " + context.getPackageName())

        // Check if we already  have permission to draw over other apps
        if (!Settings.canDrawOverlays(context)) {
            Log.v("App", "Requesting Permission" + Settings.canDrawOverlays(context))
            // if not construct intent to request permission
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName())
            )
            // request permission via start activity for result
            resultLauncher.launch(intent) //It will call onActivityResult Function After you press Yes/No and go Back after giving permission

        } else {
            Log.v("App", "We already have permission for it.")
            // disablePullNotificationTouch();
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.v("App", "OnActivity Result.")
            //check if received result code
            //  is equal our requested code for draw permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // Permission Granted by Overlay
                    Log.v("App", "OnActivity Result. canDrawOverlays")
                }
            }
        }

    override fun onClick(position: Int) {
        showDateTimePicker(installedApps[position])
    }


    private fun showDateTimePicker(appInfo: AppInfo) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Set the selected date in the calendar
                calendar.set(year, monthOfYear, dayOfMonth)

                // Show the time picker after selecting the date
                showTimePicker(appInfo)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun showTimePicker(appInfo: AppInfo) {
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                // Set the selected time in the calendar
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Format the selected date and time
                val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy hh:mm a", Locale.getDefault())
                val formattedDateTime = dateFormat.format(calendar.time)
                // Log the selected date and time
                Log.d("MyApp", formattedDateTime)
                val currentTimeMillis = calendar.timeInMillis
                Log.d("MyApp", currentTimeMillis.toString())

                if (System.currentTimeMillis() <= currentTimeMillis) {
                    // Save to database
                    var appScheduleStore =
                        AppScheduleStore(
                            0,
                            appInfo.packageName,
                            currentTimeMillis.toString(),
                            formattedDateTime
                        )
                    mainViewModel.add(appScheduleStore)

                    // Set Schedule
                    val appScheduler = AppScheduler(this)
                    appScheduler.scheduleApp(appInfo.packageName, currentTimeMillis)
                } else {
                    Toast.makeText(
                        this,
                        "Schedule time is not greater than today",
                        Toast.LENGTH_LONG
                    ).show()
                }

            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )

        // Show the time picker dialog
        timePickerDialog.show()
    }
}


