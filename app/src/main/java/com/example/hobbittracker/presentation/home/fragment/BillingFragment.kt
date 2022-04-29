package com.example.hobbittracker.presentation.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.example.hobbittracker.R
import com.example.hobbittracker.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_billing.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BillingFragment : Fragment(), BillingProcessor.IBillingHandler {

    private lateinit var bp: BillingProcessor

    private val vm: HomeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideNavigation()

        btn_cancel.setOnClickListener {
            onEventFinish()
        }

        billingInit()

        btn_subscribe_now.setOnClickListener {
            bp.subscribe(
                requireActivity(),
                "android.test.purchased"
            )
        }
    }

    override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    private fun onEventFinish() {
        vm.replaceFragment(
            requireActivity().supportFragmentManager,
            DashboardFragment()
        )
    }

    private fun billingInit() {
        bp = BillingProcessor.newBillingProcessor(
            requireActivity(),
            "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE",
            this
        )
        bp.initialize()
    }

    private fun hideNavigation() {
        requireActivity().buttomNavigation.visibility = View.GONE
        requireActivity().btn_add.visibility = View.GONE
    }


    // IBillingHandler implementation

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        /*
        * Called when requested PRODUCT ID was successfully purchased
        */
    }

    override fun onPurchaseHistoryRestored() {
        /*
        * Called when purchase history was restored and the list of all owned PRODUCT ID's
        * was loaded from Google Play
        */
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Toast.makeText(
            requireActivity().applicationContext,
            error?.localizedMessage,
            Toast.LENGTH_SHORT
        ).show()
        Log.e(TAG, error?.localizedMessage ?: "onBillingError - Error code: $errorCode")
    }

    override fun onBillingInitialized() {
        /*
        * Called when BillingProcessor was initialized and it's ready to purchase
        */
    }

    companion object {
        private val TAG = "BillingFragment"
    }
}