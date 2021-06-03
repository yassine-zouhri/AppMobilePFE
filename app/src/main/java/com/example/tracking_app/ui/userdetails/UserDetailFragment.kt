package com.example.tracking_app.ui.userdetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tracking_app.R
import com.example.tracking_app.servise.LocationService.ForegroundOnlyLocationService
import com.example.tracking_app.ui.login.LoginActivity
import com.example.tracking_app.utils.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_userdetail.*



class UserDetailFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var userDetailViewModel: UserDetailViewModel


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        userDetailViewModel = ViewModelProvider(this,
            UserDetailViewModelFactory(
                this.requireContext()
            )
        ).get(UserDetailViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_userdetail, container, false)

        userDetailViewModel.logoutState.observe(viewLifecycleOwner, Observer{
            val myService = Intent(activity, ForegroundOnlyLocationService::class.java)
            activity?.stopService(myService)
            val navIntent = Intent(context, LoginActivity::class.java)
            navIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            userDetailViewModel.navigationEventDone()
            FirebaseMessaging.getInstance().deleteToken()
            startActivity(navIntent)
        })

        userDetailViewModel.navigateEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                userDetailViewModel.navigationEventDone()
            }
        })


        userDetailViewModel.CurrentUser.observe(viewLifecycleOwner, Observer {

            if(it != null){
                loadImage(UserDetailImage,it.avatar)
                UserDetailFullName.text = it.lastName+" "+it.firstName
                UserDetailAddress.text = it.city + ", " +it.country
                UserDetailEmail.text = it.email
                UserDetailMobile.text = it.mobile
                UserDetailCompany.text = it.company
                UserDetailJobPosition.text = it.jobPosition
                UserDetailbirthDate.text = it.birthDate
                UserDetailUserName.text = it.username
            }
        })

        root.findViewById<FloatingActionButton>(R.id.settingsUser).setOnClickListener {
            showPopup(it)
        }

        return root
    }



    fun showPopup(v: View?) {

        val popup = PopupMenu(context, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.main, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                userDetailViewModel.logout()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


}