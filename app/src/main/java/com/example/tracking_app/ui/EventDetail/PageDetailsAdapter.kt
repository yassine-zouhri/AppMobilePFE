package com.example.tracking_app.ui.EventDetail

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.tracking_app.ui.EventDetailImage.EventDetailImageFragment
import com.example.tracking_app.ui.EventDetailsMap.EventDetailMapFragment

class PageDetailsAdapter(fm : FragmentManager , MyPositionID : String) : FragmentStatePagerAdapter(fm) {

    val Myposition : String = MyPositionID

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {return EventDetailMapFragment(Myposition)}
            1 -> {return EventDetailImageFragment(Myposition)}
            else ->{return EventDetailMapFragment(Myposition)}
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> {return "Map"}
            1 -> {return "Image"}
            else -> {return "Map"}
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE;
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
    }

}