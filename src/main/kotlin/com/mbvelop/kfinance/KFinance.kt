package com.mbvelop.kfinance

import com.mbvelop.kfinance.enums.PaymentSchedule
import com.mbvelop.kfinance.enums.PaymentSchedule.END
import kotlin.math.pow

/**
 * Compute the payment against loan principal plus interest.
 *
 * Note that computing a monthly mortgage payment is only
 * one use for this function.  For example, pmt returns the
 * periodic deposit one must make to achieve a specified
 * future balance given an initial deposit, a fixed,
 * periodically compounded interest rate, and the total
 * number of periods.
 *
 * @param interestRate Rate of interest (per period)
 * @param numberOfPeriods Number of compounding periods
 * @param presentValue Present value
 * @param futureValue Future value
 * @param paymentSchedule When payments are due
 *
 * @return Payment against loan plus interest (the (fixed) periodic payment).
 */
public fun pmt(interestRate: Double,
               numberOfPeriods: Double,
               presentValue: Double,
               futureValue: Double = 0.0,
               paymentSchedule: PaymentSchedule = END): Double {

    val temp = (1 + interestRate).pow(numberOfPeriods)
    val mask = (interestRate == 0.0)
    val maskedRate = if (mask) 1.0 else interestRate

    val fact = if (mask) {
        numberOfPeriods
    } else {
        (1 + maskedRate * paymentSchedule.numericValue) * (temp - 1) / maskedRate
    }

    return -(futureValue + presentValue * temp) / fact
}
