package com.sound.birdstone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val arrayList: ArrayList<Fragment> = ArrayList()
    private val nameList: ArrayList<String> = ArrayList()

    fun addFragment(fragment: Fragment, name: String) {
        arrayList.add(fragment)
        nameList.add(name)
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return arrayList[position]
    }

    fun getTitle(pos: Int): String {
        return nameList[pos]
    }
}