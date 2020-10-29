package com.mbvelop.kfinance

import com.mbvelop.kfinance.enums.PaymentSchedule
import com.mbvelop.kfinance.enums.PaymentSchedule.END
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

/**
 * Compute the payment against loan principal plus interest.
 *
 * Note that computing a monthly mortgage payment is only one use for this function. For example,
 * totalPayment returns the periodic deposit one must make to achieve a specified future balance
 * given an initial deposit, a fixed, periodically compounded interest rate, and the total number of
 * periods.
 *
 * @param interestRate Interest rate per period
 * @param numberOfPeriods Number of payment periods
 * @param presentValue Present value of the investment
 * @param futureValue Future value of the investment
 * @param paymentSchedule When payments are due
 *
 * @return Payment against loan (including interest) made in each (fixed) period
 */
public fun totalPayment(
    interestRate: Double,
    numberOfPeriods: Double,
    presentValue: Double,
    futureValue: Double = 0.0,
    paymentSchedule: PaymentSchedule = END
): Double {
    val totalInterestRate = (1 + interestRate).pow(numberOfPeriods)
    val mask = (interestRate == 0.0)
    val maskedRate = if (mask) 1.0 else interestRate

    val fact = if (mask) {
        numberOfPeriods
    } else {
        (1 + maskedRate * paymentSchedule.numericValue) * (totalInterestRate - 1) / maskedRate
    }

    return -(futureValue + presentValue * totalInterestRate) / fact
}

/**
 * Compute the future value of an investment at the end of the payment periods.
 *
 * @param interestRate Interest rate per period
 * @param numberOfPeriods Number of payment periods
 * @param payment Payment (including interest) made in each (fixed) period
 * @param presentValue Present value of the investment
 * @param paymentSchedule When payments are due
 *
 * @return Future value of the investment
 */
public fun futureValue(
    interestRate: Double,
    numberOfPeriods: Double,
    payment: Double,
    presentValue: Double = 0.0,
    paymentSchedule: PaymentSchedule = END
): Double {
    val totalInterestRate = if (interestRate != 0.0) {
        (1 + interestRate).pow(numberOfPeriods)
    } else {
        0.0
    }

    return if (interestRate != 0.0) {
        (
            -presentValue * totalInterestRate - payment * (
                1 + interestRate *
                    paymentSchedule.numericValue
                ) / interestRate * (totalInterestRate - 1.0)
            )
    } else {
        -(presentValue + payment * numberOfPeriods)
    }
}

/**
 * Compute the number of periodic payments needed to pay off loan.
 *
 * @param interestRate Interest rate per period
 * @param payment Payment (including interest) made in each (fixed) period
 * @param presentValue Present value of the investment
 * @param futureValue Future value of the investment
 * @param paymentSchedule When payments are due
 *
 * @return Future value of the investment
 */
public fun numberOfPeriodicPayments(
    interestRate: Double,
    payment: Double,
    presentValue: Double,
    futureValue: Double = 0.0,
    paymentSchedule: PaymentSchedule = END
): Double {
    return if (interestRate != 0.0) {
        val z = payment * (1 + interestRate * paymentSchedule.numericValue) / interestRate
        ln((-futureValue + z) / (presentValue + z)) / ln(1 + interestRate)
    } else {
        -(futureValue + presentValue) / payment
    }
}

/**
 * Compute the present value of an investment.
 *
 * @param interestRate Interest rate per period
 * @param numberOfPeriods Number of payment periods
 * @param payment Payment (including interest) made in each (fixed) period
 * @param futureValue Future value of the investment
 * @param paymentSchedule When payments are due
 *
 * @return Present value of the investment
 */
public fun presentValue(
    interestRate: Double,
    numberOfPeriods: Double,
    payment: Double,
    futureValue: Double = 0.0,
    paymentSchedule: PaymentSchedule = END
): Double {
    val totalInterestRate = (1 + interestRate).pow(numberOfPeriods)

    val fact = if (interestRate == 0.0) {
        numberOfPeriods
    } else {
        (1 + interestRate * paymentSchedule.numericValue) * (totalInterestRate - 1) / interestRate
    }

    return -(futureValue + payment * fact) / totalInterestRate
}

/**
 * Compute the net present value of a cash flow series with fixed periods.
 *
 * @param interestRate Interest rate per period.
 * @param cashFlow Projected incoming cash flows at the end of each per period.
 * @param initialInvestment Initial investment.
 *
 * @return Net present value of cash flow series.
 */
public fun netPresentValue(
    interestRate: Double,
    cashFlow: DoubleArray,
    initialInvestment: Double = 0.0
): Double {
    val interests = DoubleArray(cashFlow.size) { (1 + interestRate).pow(it) }
    return initialInvestment + (cashFlow / interests).sum()
}

/**
 * Modified internal rate of return.
 *
 * @param cashFlows Cash flows (must contain at least one positive and one negative value) or
 * <code>Double.NaN</code> is returned. The first value is considered a sunk cost at time zero
 * @param financeRate Interest rate paid on the cash flows
 * @param reinvestRate Interest rate received on the cash flows upon reinvestment
 *
 * @return Modified internal rate of return
 */
public fun modifiedInternalRateOfReturn(
    cashFlows: DoubleArray,
    financeRate: Double,
    reinvestRate: Double
): Double {
    val n = cashFlows.size
    val positiveCashFlow = DoubleArray(cashFlows.size) {
        if (cashFlows[it] < 0.0) {
            0.0
        } else {
            cashFlows[it]
        }
    }
    val numerator = netPresentValue(reinvestRate, positiveCashFlow)

    val negativeCashFlow = DoubleArray(cashFlows.size) {
        if (cashFlows[it] > 0.0) {
            0.0
        } else {
            cashFlows[it]
        }
    }
    val denominator = netPresentValue(financeRate, negativeCashFlow)

    return (numerator / denominator).pow(1 / (n - 1)) * (1 + reinvestRate) - 1
}

private const val MAX_ITERATION_COUNT = 20
private const val ABSOLUTE_ACCURACY = 1E-7

public fun internalRateOfReturn(cashFlows: DoubleArray, guess: Double): Double {
    require(cashFlows.size >= 2) { "cashFlow must have at least 2 values" }

    val initialInvestment = cashFlows[0]
    val payments = cashFlows.slice(1 until cashFlows.size)
    var x0 = guess

    (0 until MAX_ITERATION_COUNT).forEach { _ ->
        val factor = 1.0 + x0
        var denominator = factor
        if (denominator == 0.0) {
            return Double.NaN
        }
        var fValue = initialInvestment
        var fDerivative = 0.0

        payments.forEachIndexed { index, value ->
            fValue += value / denominator
            denominator *= factor
            fDerivative -= index * value / denominator
        }

        if (fDerivative == 0.0) {
            return Double.NaN
        }

        val x1 = x0 - fValue / fDerivative
        if (abs(x1 - x0) <= ABSOLUTE_ACCURACY) {
            return x1
        }
        x0 = x1
    }

    return Double.NaN
}
