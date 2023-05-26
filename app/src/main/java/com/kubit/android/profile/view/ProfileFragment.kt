package com.kubit.android.profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.databinding.FragmentProfileBinding
import com.kubit.android.main.viewmodel.MainViewModel

class ProfileFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun init() {

    }

    companion object {
        const val TAG: String = "ProfileFragment"

        private var instance: ProfileFragment? = null

        @JvmStatic
        fun getInstance(): ProfileFragment {
            if (instance == null) {
                instance = ProfileFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}