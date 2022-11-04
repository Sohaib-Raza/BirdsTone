package com.sound.birdstone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sound.birdstone.R
import com.sound.birdstone.adapter.ViewPagerFragmentAdapter
import com.sound.birdstone.appdata.BirdCategory
import com.sound.birdstone.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sound.birdstone.adapter.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    @Inject
    lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        binding.tabLayout.setupWithViewPager(binding.viewPagerLayout)


        /*  binding.viewPagerLayout.addOnPageChangeListener(object : OnPageChangeListener {
              override fun onPageScrolled(
                  position: Int,
                  positionOffset: Float,
                  positionOffsetPixels: Int
              ) {
              }

              override fun onPageSelected(position: Int) {

                  EventBus.getDefault().post(StopAudioEvent(position))
                  EventBus.getDefault().post(CloseWikipedia())
                  EventBus.getDefault().post(CloseSearch())

              }

              override fun onPageScrollStateChanged(state: Int) {}
          })
  */
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerFragmentAdapter(parentFragmentManager , lifecycle)


        adapter.addFragment(addBundleToFragment(BirdCategory.ALL, CategoryFragment()), getString(R.string.all_birds))
        adapter.addFragment(addBundleToFragment(BirdCategory.FUNNY_BIRDS, CategoryFragment()), getString(R.string.funny))
        adapter.addFragment(addBundleToFragment(BirdCategory.LOUND_BIRDS, CategoryFragment()), getString(R.string.lound))
        adapter.addFragment(addBundleToFragment(BirdCategory.SINGING_BIRDS, CategoryFragment()), getString(R.string.singing))
        adapter.addFragment(addBundleToFragment(BirdCategory.WATER_BIRDS, CategoryFragment()), getString(R.string.water))
        adapter.addFragment(addBundleToFragment(BirdCategory.WILD_BIRDS, CategoryFragment()), getString(R.string.wild))
        adapter.addFragment(addBundleToFragment(BirdCategory.FARM_BIRDS, CategoryFragment()), getString(R.string.farms))

        binding.viewPagerLayout.adapter = adapter
        binding.viewPagerLayout.setPageTransformer(ZoomOutPageTransformer())

        TabLayoutMediator(binding.tabLayout, binding.viewPagerLayout) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

    }

    private fun addBundleToFragment(category: Int, fragment: Fragment): Fragment {
        val bundle = Bundle()
        bundle.putInt("categoryId", category)
        fragment.arguments = bundle
        return fragment

    }

}