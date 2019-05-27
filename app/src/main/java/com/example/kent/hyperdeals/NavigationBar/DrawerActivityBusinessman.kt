package com.example.kent.hyperdeals.NavigationBar

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.kent.hyperdeals.BusinessActivities.AddPromo
import com.example.kent.hyperdeals.BusinessActivities.Business_Stores
import com.example.kent.hyperdeals.FragmentActivities.*
import com.example.kent.hyperdeals.MainActivity
import com.example.kent.hyperdeals.NavigationOptionsActivity.Profile
import com.example.kent.hyperdeals.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.app_bar_drawer_activity_businessman.*
import kotlinx.android.synthetic.main.app_bar_main3.*
import kotlinx.android.synthetic.main.fragment_add_promo.*

class DrawerActivityBusinessman : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.businessman_activity_drawer)
        MainActivity.userLog = false
        setSupportActionBar(toolbar1)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
       containerbm.adapter = mSectionsPagerAdapter


        containerbm.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs1))
        tabs1.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(containerbm))




        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }



    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }


    class SectionsPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position){
                0 -> {
                    return FragmentAddPromo()
                }

                1 -> {
                    return FragmentReach()
                }

                else -> return null
            }

        }

        override fun getCount(): Int {
            return 2
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {

                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                return true

            }
            R.id.nav_gallery -> {

            }
            R.id.nav_stores -> {

                val intent = Intent (this, Business_Stores::class.java)
                startActivity(intent)
            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }

            R.id.nav_logout -> {

                val intent = Intent (this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun getUserStores(){

    }
}
