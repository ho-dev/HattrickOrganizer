/*
 * ArenaSizer.java
 *
 * Created on 21. März 2003, 08:19
 */
package tool.arenasizer

import core.model.UserParameter
import core.util.Helper
import java.math.BigDecimal
import java.math.RoundingMode

class ArenaSizer internal constructor() {
    var currencyFactor = UserParameter.instance().FXrate

    //~ Methods ------------------------------------------------------------------------------------
    fun calcMaxIncome(arena: Stadium): Int {
        var income = 0
        income = (income + arena.standing * ADMISSION_PRICE_TERRACES / currencyFactor).toInt()
        income = (income + arena.basicSeating * ADMISSION_PRICE_BASICS / currencyFactor).toInt()
        income = (income + arena.seatingUnderRoof * ADMISSION_PRICE_ROOF / currencyFactor).toInt()
        income = (income + arena.vip * ADMISSION_PRICE_VIP / currencyFactor).toInt()
        return income
    }

    fun calcConstructionArenas(currentArena: Stadium, supporter: Int): Array<Stadium> {
        val arenaMax = createArena(supporter * (SUPPORTER_NORMAL + 5), currentArena)
        val arenaNormal = createArena(supporter * SUPPORTER_NORMAL, currentArena)
        val arenaMin = createArena(supporter * (SUPPORTER_NORMAL - 5), currentArena)
        return arrayOf(arenaMax, arenaNormal, arenaMin)
    }

    fun calcConstructionArenas(
        currentArena: Stadium,
        maxSupporter: Int,
        normalSupporter: Int,
        minSupporter: Int
    ): Array<Stadium> {
        val arenaMax = createArena(maxSupporter, currentArena)
        val arenaNormal = createArena(normalSupporter, currentArena)
        val arenaMin = createArena(minSupporter, currentArena)
        return arrayOf(arenaMax, arenaNormal, arenaMin)
    }

    private fun createArena(size: Int, current: Stadium): Stadium {
        val tmp = Stadium()
        val sizeNumber = BigDecimal(size)
        tmp.standing = TERRACES_PERCENT.multiply(sizeNumber).toInt()
        tmp.expansionStanding = tmp.standing - current.standing
        tmp.basicSeating = BASICS_PERCENT.multiply(sizeNumber).toInt()
        tmp.expansionBasicSeating = tmp.basicSeating - current.basicSeating
        tmp.seatingUnderRoof = ROOF_PERCENT.multiply(sizeNumber).toInt()
        tmp.expansionSeatingUnderRoof = tmp.seatingUnderRoof - current.seatingUnderRoof
        tmp.vip = VIP_PERCENT.multiply(sizeNumber).toInt()
        tmp.expansionVip = tmp.vip - current.vip
        tmp.expansionCosts = calcConstructionCosts(
            tmp.expansionStanding.toFloat(),
            tmp.expansionBasicSeating.toFloat(),
            tmp.expansionSeatingUnderRoof.toFloat(),
            tmp.expansionVip.toFloat()
        )
        return tmp
    }

    fun calcConstructionCosts(steh: Float, sitz: Float, dach: Float, logen: Float): Int {
        var kosten = FIXKOSTEN / currencyFactor
        if (steh > 0) {
            kosten += steh * STEH_AUSBAU / currencyFactor
        } else {
            kosten -= steh * ABRISS / currencyFactor
        }
        if (sitz > 0) {
            kosten += sitz * SITZ_AUSBAU / currencyFactor
        } else {
            kosten -= sitz * ABRISS / currencyFactor
        }
        if (dach > 0) {
            kosten += dach * DACH_AUSBAU / currencyFactor
        } else {
            kosten -= dach * ABRISS / currencyFactor
        }
        if (logen > 0) {
            kosten += logen * LOGEN_AUSBAU / currencyFactor
        } else {
            kosten -= logen * ABRISS / currencyFactor
        }
        return kosten.toInt()
    }

    fun calcMaintenance(arena: Stadium): Float {
        var costs = 0.0f
        costs += arena.standing * MAINTENANCE_TERRACES / currencyFactor
        costs += arena.basicSeating * MAINTENANCE_BASICS / currencyFactor
        costs += arena.seatingUnderRoof * MAINTENANCE_ROOF / currencyFactor
        costs += arena.vip * MAINTENANCE_VIP / currencyFactor
        return Helper.round(costs, 1)
    }

    companion object {
        const val ADMISSION_PRICE_TERRACES = 70f
        const val ADMISSION_PRICE_BASICS = 100f
        const val ADMISSION_PRICE_ROOF = 190f
        const val ADMISSION_PRICE_VIP = 350f
        private const val MAINTENANCE_TERRACES = 5f
        private const val MAINTENANCE_BASICS = 7f
        private const val MAINTENANCE_ROOF = 10f
        private const val MAINTENANCE_VIP = 25f

        //CREATE
        private const val STEH_AUSBAU = 450f
        private const val SITZ_AUSBAU = 750f
        private const val DACH_AUSBAU = 900f
        private const val LOGEN_AUSBAU = 3000f
        private const val ABRISS = 60f
        private const val FIXKOSTEN = 100000f
        val TERRACES_PERCENT = BigDecimal("0.60").setScale(3, RoundingMode.HALF_DOWN)
        val BASICS_PERCENT = BigDecimal("0.235").setScale(3, RoundingMode.HALF_DOWN)
        val ROOF_PERCENT = BigDecimal("0.14").setScale(3, RoundingMode.HALF_DOWN)
        val VIP_PERCENT = BigDecimal("0.025").setScale(3, RoundingMode.HALF_DOWN)

        //SUPPORTER-DISTRIBUTION
        const val SUPPORTER_NORMAL = 20
    }
}
