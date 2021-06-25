package com.example.mediaplayer.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mediaplayer.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadingPermissionGranted = false
    private var isWritingPermissionGranted = false
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Request permission
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isReadingPermissionGranted = it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            isWritingPermissionGranted = it[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
        }
        requestPermission()

        navController=findNavController(R.id.navHostFragment)
        bottomNavigation=bottomNav

        val appConfig= AppBarConfiguration(setOf(
            R.id.audioList,
            R.id.videoList
        ))
        setSupportActionBar(customToolBar)
        bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appConfig)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    private fun requestPermission() {
        val writePermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWritingPermissionGranted=writePermission
        Log.e("TAG", "$writePermission write")
        if(!writePermission){
            permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }
}