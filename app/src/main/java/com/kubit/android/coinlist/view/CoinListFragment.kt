package com.kubit.android.coinlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.adapter.CoinListAdapter
import com.kubit.android.common.deco.BorderItemDecoration
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentCoinListBinding
import com.kubit.android.main.viewmodel.MainViewModel

class CoinListFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    private var coinListAdapter: CoinListAdapter? = null

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
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        DLog.d(TAG, "onResume() is called!")
        super.onResume()
        model.requestTickerData()
    }

    override fun onStop() {
        super.onStop()
        model.stopTickerData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.coinSnapshotDataList.observe(viewLifecycleOwner, Observer { coinSnapshotDataList ->
            coinListAdapter?.update(coinSnapshotDataList)
        })
    }

    private fun init() {
        val mAdapter = CoinListAdapter(arrayListOf()) { coinSnapshotData ->

        }
        coinListAdapter = mAdapter

        binding.apply {
            rvCoinList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
                itemAnimator = null
                addItemDecoration(
                    BorderItemDecoration(
                        borderPos = listOf(BorderItemDecoration.BorderPos.BOTTOM),
                        borderWidth = ConvertUtil.dp2px(requireContext(), 1),
                        borderColor = ContextCompat.getColor(requireContext(), R.color.border)
                    )
                )
            }
        }
    }

    companion object {
        const val TAG: String = "CoinListFragment"

        private var instance: CoinListFragment? = null

        @JvmStatic
        fun getInstance(): CoinListFragment {
            if (instance == null) {
                instance = CoinListFragment()
            }

            return instance!!
        }
    }
}