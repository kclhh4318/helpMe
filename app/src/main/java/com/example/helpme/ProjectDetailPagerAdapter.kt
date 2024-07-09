package com.example.helpme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.helpme.model.ProjectDetail

class ProjectDetailPagerAdapter(fm: FragmentManager, private val project: ProjectDetail) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ContentsFragment.newInstance(project)
            1 -> ReferenceFragment.newInstance(project)
            2 -> RememberFragment.newInstance(project)
            else -> ContentsFragment.newInstance(project)
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Contents"
            1 -> "Reference"
            2 -> "Remember"
            else -> null
        }
    }
}
