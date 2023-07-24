@file:Suppress("DEPRECATION")

package com.example.firebasepractice.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.firebasepractice.fragments.CallsFragment
import com.example.firebasepractice.fragments.ChatFragment
import com.example.firebasepractice.fragments.StatusFragment

class pagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title:String? = null
        when(position) {
            0 -> title = "Chats"
            1 -> title = "Status"
            2 -> title = "Calls"
        }
        return title
    }
}