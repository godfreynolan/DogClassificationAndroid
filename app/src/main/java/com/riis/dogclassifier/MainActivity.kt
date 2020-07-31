package com.riis.dogclassifier

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.riis.dogclassifier.fragments.FragmentAbout
import com.riis.dogclassifier.fragments.FragmentList
import com.riis.dogclassifier.fragments.FragmentSample
import com.riis.dogclassifier.fragments.FragmentUpload
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(getCurrentFragment() == null){
            loadFragment(FragmentTags.Sample)
        }

        loadBackStackChangedListener()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, topAppBar, R.string.open_nav_drawer, R.string.close_nav_drawer)
        //adds the toggle listener
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_sample -> {
                if(!nav_view.menu.getItem(0).isChecked) {
                    loadFragment(FragmentTags.Sample)
                }
            }
            R.id.nav_upload -> {
                if(!nav_view.menu.getItem(1).isChecked) {
                    loadFragment(FragmentTags.Upload)
                }
            }
            R.id.nav_list -> {
                if(!nav_view.menu.getItem(2).isChecked) {
                    loadFragment(FragmentTags.List)
                }
            }
            R.id.nav_about -> {
                if(!nav_view.menu.getItem(3).isChecked) {
                    loadFragment(FragmentTags.About)
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadBackStackChangedListener(){
        supportFragmentManager.addOnBackStackChangedListener {
            //gets current fragment loaded
            val currentFrag = getCurrentFragment()
            //checks if the fragment is null
            if (currentFrag != null) {
                //checks which fragment is loaded and highlights the menu item
                when(currentFrag.tag) {
                    FragmentTags.Sample.tag -> {
                        nav_view.menu.getItem(0).isChecked = true
                    }
                    FragmentTags.Upload.tag -> {
                        nav_view.menu.getItem(1).isChecked = true
                    }
                    FragmentTags.List.tag -> {
                        nav_view.menu.getItem(2).isChecked = true
                    }
                    FragmentTags.About.tag-> {
                        nav_view.menu.getItem(3).isChecked = true
                    }
                    else -> {
                        nav_view.menu.getItem(0).isChecked = true
                    }
                }
            }else{
                nav_view.menu.getItem(0).isChecked = true
            }
        }
    }

    private fun getCurrentFragment(): Fragment?{
        //checks if the backstack is empty
        return if(supportFragmentManager.backStackEntryCount == 0){
            null
        }else {
            //gets the current fragment by subtracting 1 from the back-stack entry count and getting its name
            val tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount -1).name
            supportFragmentManager.findFragmentByTag(tag)
        }
    }

    private fun loadFragment(fragment: FragmentTags){

        val transaction = supportFragmentManager.beginTransaction()
        val frag = when(fragment){
            FragmentTags.Sample -> {
                FragmentSample()
            }
            FragmentTags.Upload -> {
                FragmentUpload()
            }
            FragmentTags.List -> {
                FragmentList()
            }
            FragmentTags.About -> {
                FragmentAbout()
            }
        }

        transaction.replace(R.id.fragment_container, frag, fragment.tag)
        transaction.addToBackStack(fragment.tag)
        transaction.commit()
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    enum class FragmentTags(val tag: String) {
        Sample("sample_tag"),
        Upload("upload_tag"),
        List("list_tag"),
        About("about_tag")
    }
}