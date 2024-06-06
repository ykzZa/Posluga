package dev.ykzza.posluga.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.ykzza.posluga.databinding.FragmentHomeBinding
import dev.ykzza.posluga.ui.home.projects.BrowseProjectsFragment
import dev.ykzza.posluga.ui.home.services.BrowseServicesFragment
import java.lang.IllegalStateException

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw RuntimeException("FragmentHomeBinding")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTabs()
    }

    private fun setupTabs() {
        binding.apply {
            viewPager.adapter = FragmentAdapter(this@HomeFragment)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when(position) {
                    0 -> "Services"
                    1 -> "Projects"
                    else -> null
                }
            }.attach()
        }
    }

    class FragmentAdapter(fragment: HomeFragment): FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> BrowseServicesFragment.newInstance()
                1 -> BrowseProjectsFragment.newInstance()
                else -> throw IllegalStateException("Wrong $position")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}