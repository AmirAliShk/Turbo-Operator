package ir.taxi1880.operatormanagement.model

import androidx.constraintlayout.solver.ArrayLinkedVariables

data class OperatorMistakeModel(
    var id: Int,
    var voipId: String,
    var serviceDate: String,
    var serviceTime:String,
    var description: String,
    var destinationAddress: String,
    var destinationStation: Int,
    var sourceAddress:String,
    var sourceStation:Int,
    var reason:String,
    var misStatus:Int
    )