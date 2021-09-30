package ir.taxi1880.operatormanagement.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.taxi1880.operatormanagement.databinding.FragmentOperatorMistakeListBinding

class OperatorMistakesFragment : Fragment() {

    private lateinit var binding: FragmentOperatorMistakeListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }
}