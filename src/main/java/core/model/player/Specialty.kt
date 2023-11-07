package core.model.player

enum class Specialty(val value: Int) {
    /*
    SpecialtyID
    Value	Description
0	No specialty
1	Technical
2	Quick
3	Powerful
4	Unpredictable
5	Head specialist
6	resilient
8	support
*/
    NoSpecialty(0),
    Technical(1),
    Quick(2),
    Powerful(3),
    Unpredictable(4),
    Head(5),

    // renamed to fit to ls.player.speciality.head string
    Regainer(6),

    // renamed to fit to ls.player.speciality.regainer string
    Not_used(7),
    Support(8);

    companion object {
        private val map = HashMap<Int, Specialty>()

        // Init mapping
        init {
            for (s in entries) {
                map[s.value] = s
            }
        }

        @JvmStatic
        fun getSpecialty(s: Int?): Specialty? {
            return if (s != null) {
                map[s]
            } else null
        }

        fun getValue(specialty: Specialty?): Int? {
            return specialty?.value
        }
    }
}
