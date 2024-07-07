package com.example.helpme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProjectDetailPagerAdapter(activity: FragmentActivity, private val project: Project) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ContentsFragment.newInstance(project)
            1 -> ReferenceFragment.newInstance(project)
            2 -> RememberFragment.newInstance(project)
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
