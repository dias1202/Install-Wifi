package com.dias.installwifi.view.authentication.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dias.installwifi.R
import com.dias.installwifi.databinding.FragmentInputCodeBinding

class InputCodeFragment : Fragment() {

    private var _binding: FragmentInputCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputCodeBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}