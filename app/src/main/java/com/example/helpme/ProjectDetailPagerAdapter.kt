package com.example.helpme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ProjectDetailPagerAdapter(fm: FragmentManager, private val project: Project) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf("CONTENTS", "REFERENCE", "REMEMBER")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ContentsFragment.newInstance(project)
            1 -> ReferenceFragment.newInstance(project)
            2 -> RememberFragment.newInstance(project)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getCount(): Int = tabTitles.size

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}
