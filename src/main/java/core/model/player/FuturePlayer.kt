package core.model.player

class FuturePlayer {
    @JvmField
	var passing = 0.0
    @JvmField
	var playmaking = 0.0
    @JvmField
	var stamina = 0.0
    @JvmField
	var form = 0.0
    @JvmField
	var setpieces = 0.0
    @JvmField
	var age = 0
    @JvmField
	var defense = 0.0
    @JvmField
	var attack = 0.0
    @JvmField
	var cross = 0.0
    @JvmField
	var goalkeeping = 0.0
    @JvmField
	var playerId = 0

    /**
     * toString methode: creates a String representation of the object
     * @return the String representation
     * @author info.vancauwenberge.tostring plugin
     */
    override fun toString(): String {
        val buffer = StringBuffer()
        buffer.append("FuturePlayer[")
        buffer.append("passing = $passing")
        buffer.append(", playmaking = $playmaking")
        buffer.append(", stamina = $stamina")
        buffer.append(", form = $form")
        buffer.append(", setpieces = $setpieces")
        buffer.append(", age = $age")
        buffer.append(", defense = $defense")
        buffer.append(", attack = $attack")
        buffer.append(", cross = $cross")
        buffer.append(", goalkeeping = $goalkeeping")
        buffer.append("]")
        return buffer.toString()
    }
}
