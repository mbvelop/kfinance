package com.mbvelop.kfinance.enums

/**
 * When payments are due.
 */
public enum class PaymentSchedule(public val numericValue: Int) {
    /**
     * Payments are due at the beginning of the period.
     */
    BEGIN(1),

    /**
     * Payments are due at the end of the period.
     */
    END(0)
}
