package com.sound.birdstone


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sound.birdstone.adapter.ViewPagerAdapter
import com.sound.birdstone.ads.interstitial.InterstitialControllerListener
import com.sound.birdstone.ads.nativeAds.BaseNativeActivity
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.appdata.BirdCategory
import com.sound.birdstone.database.BirdEntity
import com.sound.birdstone.databinding.ActivityMainBinding
import com.sound.birdstone.fragments.CategoryFragment
import com.sound.birdstone.fragments.HomeFragment
import com.sound.birdstone.fragments.SettingFragment
import com.sound.birdstone.helper.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseNativeActivity() {

    val permission =
        arrayOf("android.permission.RECORD_AUDIO", "android.permission.MODIFY_AUDIO_SETTINGS")

    @Inject
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.imageAnimation.imageAssetsFolder="/image"

        (AppClass.appContext as AppClass).initializeOpenAd()


        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(HomeFragment(), "")

        val fragment = CategoryFragment()
        val bundle = Bundle()
        bundle.putInt("categoryId", BirdCategory.FAV)
        fragment.arguments = bundle

        adapter.addFragment(fragment, "")
        adapter.addFragment(SettingFragment(), "")

        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false



        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {



                    interstitialController.showInterstitial(activity,
                        AdsConstants.FS_AT_BOTTOM_MENU_CLICK, object :
                            InterstitialControllerListener {
                            override fun onAdClosed() {
                                binding.viewPager.setCurrentItem(0, false)
                                binding.imageSearch.visibility = View.VISIBLE
                            }

                            override fun onAdShow() {
                            }

                        })


                }
                R.id.favorite -> {
                    interstitialController.showInterstitial(activity,
                        AdsConstants.FS_AT_BOTTOM_MENU_CLICK, object :
                            InterstitialControllerListener {
                            override fun onAdClosed() {
                                binding.viewPager.setCurrentItem(1, false)
                                binding.imageSearch.visibility = View.VISIBLE
                            }

                            override fun onAdShow() {
                            }

                        })


                }
                R.id.setting -> {
                    interstitialController.showInterstitial(activity,
                        AdsConstants.FS_AT_BOTTOM_MENU_CLICK, object :
                            InterstitialControllerListener {
                            override fun onAdClosed() {
                                binding.viewPager.setCurrentItem(2, false)
                                binding.imageSearch.visibility = View.GONE
                            }

                            override fun onAdShow() {
                            }

                        })

                }
            }
            true
        }

        binding.imageSearch.setOnClickListener {
            Intent(activity, SearchActivity::class.java).putExtra(
                "isFav",
                binding.viewPager.currentItem == 1
            ).apply {
                startActivity(this)
            }
        }

        ensureBackgroundThread {
            viewModel.isEmpty {
                if (it) {


                    val list = ArrayList<BirdEntity>()

                    list.add(
                        BirdEntity(
                            "American Cliff Swallow",
                            "bird_american_cliff_swallow",
                            "birds_american_cliff_swallow",
                            BirdCategory.FARM_BIRDS
                        )
                    )
                    list.add(
                        BirdEntity(
                            "American Crow",
                            "bird_american_crow",
                            "birds_american_crow",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "American Woodcock",
                            "bird_american_woodcock",
                            "birds_american_woodcock",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "American Woodpecker",
                            "bird_american_woodpecker",
                            "birds_hair_woodpecker",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Barn Owl",
                            "bird_barn_owl",
                            "birds_barn_owl",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Barn Swallow",
                            "bird_barn_swallow",
                            "birds_barn_swallow",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Black Cpped Chickadee",
                            "bird_black_cpped_chickadee",
                            "birds_black_capped_chickadee",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Black Crowned Nignt Heron",
                            "bird_black_crowned_nignt_heron",
                            "birds_black_crowned_night_heron",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Black Headed Gull",
                            "bird_black_headed_gull",
                            "birds_black_headed_gull",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Bird Black Redstart",
                            "bird_black_redstart",
                            "birds_black_redstart",
                            BirdCategory.FARM_BIRDS,

                            )
                    )

                    list.add(

                        BirdEntity(
                            "Blad Eagle",
                            "bird_blad_eagle",
                            "birds_blad_eagle",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Blue Headed Vireo",
                            "bird_blue_headed_vireo",
                            "birds_blue_headed_vireo",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Broad Winged Hawk",
                            "bird_broad_winged_hawk",
                            "birds_broad_winged_hawk",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Brown Thrasher",
                            "bird_brown_thrasher",
                            "birds_brown_thrasher",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Campo Troupial",
                            "bird_campo_troupial",
                            "birds_compo_troupial",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Canada Goose",
                            "bird_canada_goose",
                            "birds_canada_goose",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Chestnut Wood Quail",
                            "bird_chestnut_wood_quail",
                            "birds_chestnut_wood_quail",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Cirl Bunting",
                            "bird_cirl_bunting",
                            "birds_cirl_bunting",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Common Chaffinch",
                            "bird_common_chaffinch_one",
                            "birds_common_chaffinch",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Common Chiffchaff",
                            "bird_common_chaffinch",
                            "birds_common_chiffchaff",
                            BirdCategory.FUNNY_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Crane",
                            "bird_common_crane",
                            "birds_crane",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Common Grackle",
                            "bird_common_grackle",
                            "birds_common_grackle",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Swallow",
                            "bird_barn_swallow",
                            "birds_barn_swallow",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Common Loon",
                            "bird_common_loon",
                            "birds_common_loon",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Common Moorhen",
                            "bird_common_moorhen",
                            "birds_common_moorhen",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Raven",
                            "bird_common_raven",
                            "birds_raven",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Dark Eyed Junco",
                            "bird_dark_eyed_junco",
                            "birds_dark_eyed_junco",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Dunlin",
                            "bird_dunlin",
                            "birds_dunlin",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Bullfinch",
                            "bird_earasian_bullfinch",
                            "birds_bullfinch",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )

                    list.add(
                        BirdEntity(
                            "Magpie",
                            "bird_erurasian_magpie",
                            "birds_magpie",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Hoopoe",
                            "bird_eurasian_hoopoe",
                            "birds_hoopoe",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Western Jackdaw",
                            "bird_eurasian_jackdaw",
                            "birds_western_jackdaw",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Jay",
                            "bird_eurasian_jay",
                            "birds_jay",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Eurasian Scops Owl",
                            "bird_eurasian_scops_owl",
                            "birds_eurasian_scops_owl",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Eurasian Wren",
                            "bird_eurasian_wren",
                            "birds_eurasian_wren",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Falcon",
                            "bird_falcon",
                            "birds_falcon",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Golden Crowned Kinglet",
                            "bird_golden_crowned_kinglet",
                            "birds_golden_crowned_kinglet",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Goose",
                            "bird_goose_101",
                            "birds_goose",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Gray Catbird",
                            "bird_gray_catbird",
                            "birds_gray_catbird",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Hair Woodpecker",
                            "bird_american_woodpecker",
                            "birds_hair_woodpecker",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
//            list.add(
//                BirdEntity(
//                    "Canada Goose",
//                    "bird_graylag_goose,
//                    "birds_canada_goose,
//                    BirdCategory.WATER_BIRDS,
//
//                )
//            )
                    list.add(
                        BirdEntity(
                            "Great Horned Owl",
                            "bird_great_horned_owl",
                            "birds_great_horned_owl",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Great Kiskadee",
                            "bird_great_kiskadee",
                            "birds_great_kiskadee",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Tit",
                            "bird_great_tit",
                            "birds_tit",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Greater Prairie Chicken",
                            "bird_greater_prairie_chicken",
                            "birds_greater_prairie_chicken",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Green Jay",
                            "bird_green_jay",
                            "birds_green_jay",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
//            list.add(
//                BirdEntity(
//                    "green parakeet",
//                    "bird_green_parakeet,
//                    ",
//                    BirdCategory.FARM_BIRDS,
//
//                )
//            )
                    list.add(
                        BirdEntity(
                            "Seagull",
                            "bird_herring_gull",
                            "birds_seagull",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Hooded Crow",
                            "bird_hooded_crow",
                            "birds_hooded_crow",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "House Bunting",
                            "bird_house_bunting",
                            "birds_house_bunting",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Japanese Robin",
                            "bird_japanese_robin",
                            "birds_japanese_robin",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Japanes Thrush",
                            "bird_japanese_thrush",
                            "birds_japanes_thrush",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Knobbed Hornbill",
                            "bird_knobbed_hornbill",
                            "birds_knobbed_hornbill",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Lazuli Bunting",
                            "bird_lazuli_bunting",
                            "birds_lazuli_bunting",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Mallard Duck",
                            "bird_mallard_duck",
                            "birds_duck",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Marsh Warbler",
                            "bird_marsh_warbler",
                            "birds_marsh_warbler",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Nightgale",
                            "bird_nightingale",
                            "birds_nightgale",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Northern Flicker",
                            "bird_northern_flicker",
                            "birds_northern_flicker",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Riorthern Goshawk",
                            "bird_northern_goshawk",
                            "birds_riorthern_goshawk",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Golden Oriole",
                            "bird_oriole",
                            "birds_golden_oriole",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Puffin",
                            "bird_puffin",
                            "birds_puffin",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Red Flanked Bluetail",
                            "bird_red_flanked_bluetail",
                            "birds_red_flanked_bluetail",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Chinese Bamboo Partridge",
                            "bird_red_legged_partridge",
                            "birds_chinese_bamboo_partridge",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Red Winged Blackbird",
                            "bird_red_winged_blackbird",
                            "birds_red_winged_blackbird",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Robin",
                            "bird_robin",
                            "birds_robin",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Rose Breasted Grosbeak",
                            "bird_rose_breasted_grosbeak",
                            "birds_rose_breasted_grosbeak",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Rufous Winged Sparrow",
                            "bird_rufous_winged_sparrow",
                            "birds_rufous_winged_sparrow",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Song Sparrow",
                            "bird_song_sparrow",
                            "birds_song_sparrow",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Sparrow",
                            "bird_sparrow",
                            "birds_sparrow",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Stellers Jay",
                            "bird_stellers_jay",
                            "birds_stellers_jay",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Swan",
                            "bird_swan",
                            "birds_swan",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Tawny Owl",
                            "bird_tawny_owl",
                            "birds_tawny_owl",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Trumpeter Swan",
                            "bird_trumpeter_swan",
                            "birds_trumpeter_swan",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Veery",
                            "bird_veery",
                            "birds_veery",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Warbling Vireo",
                            "bird_warbling_vireo",
                            "birds_warbling_vireo",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Western Meadowlark",
                            "bird_western_meadowlark",
                            "birds_western_meadowlark",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Tropical Screech Owl",
                            "bird_western_screech_owl",
                            "birds_tropical_screech_owl",
                            BirdCategory.FARM_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Stork",
                            "bird_white_stork",
                            "birds_stork",
                            BirdCategory.WILD_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "White Throated Sparrow",
                            "bird_white_throated_sparrow",
                            "birds_white_throated_sparrow",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Wild Duck",
                            "bird_wild_duck",
                            "birds_wild_duck",
                            BirdCategory.LOUND_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Wild Goose",
                            "bird_wild_goose",
                            "birds_wild_goose",
                            BirdCategory.SINGING_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Wild Turkey",
                            "bird_wild_turkey",
                            "birds_turkey",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Wood Thrush",
                            "bird_wood_thrush",
                            "birds_whites_thrush",
                            BirdCategory.WATER_BIRDS,

                            )
                    )
                    list.add(
                        BirdEntity(
                            "Woodpecker",
                            "bird_woodpecker",
                            "birds_woodpecker",
                            BirdCategory.WILD_BIRDS,

                            )
                    )


                    viewModel.addAll(list)
                }
            }

        }



        nativeSmallAdController.loadNativeAd(activity, AdsConstants.NATIVE_IN_SETTING_FRAGMENT)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 8) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "permission Allow", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "permission Deny", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permission, 80)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.imageAnimation.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.imageAnimation.pauseAnimation()
    }
}
