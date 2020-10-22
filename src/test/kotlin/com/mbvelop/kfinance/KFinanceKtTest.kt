package com.mbvelop.kfinance

import com.mbvelop.kfinance.enums.PaymentSchedule.BEGIN
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.pow

class KFinanceKtTest {

    @Nested
    inner class TotalPayment {
        @Test
        fun totalPaymentBasic() {
            val result = totalPayment(0.08 / 12, 5 * 12.0, 15000.0)
            result shouldBe (-304.1459143262052370338701494 plusOrMinus 10.0.pow(-10))
        }

        @Test
        fun totalPaymentRate0() {
            val result = totalPayment(0.0, 5 * 12.0, 15000.0)
            result shouldBe (-250.0 plusOrMinus 10.0.pow(-10))
        }

        @Test
        fun totalPaymentPaymentScheduleBegin() {
            val result = totalPayment(
                0.08 / 12,
                5 * 12.0,
                15000.0,
                paymentSchedule = BEGIN
            )
            result shouldBe (-302.1317029 plusOrMinus 10.0.pow(-7))
        }

        @Test
        fun totalPaymentAdditionalInputValues() {
            var result = totalPayment(0.0, 12.0, 2000.0)
            result shouldBe (-166.6666666666666666666666667 plusOrMinus 10.0.pow(-10))

            result = totalPayment(0.3, 12.0, 2000.0)
            result shouldBe (-626.90814017007577484025866 plusOrMinus 10.0.pow(-10))

            result = totalPayment(0.8, 3.0, 20000.0)
            result shouldBe (-19311.25827814569536423841060 plusOrMinus 10.0.pow(-10))
        }
    }

    @Nested
    inner class FutureValue {
        @Test
        fun futureValueBasic() {
            val result = futureValue(0.075, 20.0, -2000.0)

            result shouldBe (86609.362673042924 plusOrMinus 10.0.pow(-10))
        }

        @Test
        fun futureValuePaymentScheduleBegin() {
            val result = futureValue(0.075, 20.0, -2000.0, paymentSchedule = BEGIN)

            result shouldBe (93105.064874 plusOrMinus 10.0.pow(-6))
        }

        @Test
        fun futureValueAdditionalInputValues() {
            var result = futureValue(0.1, 5.0, 100.0)
            result shouldBe (-610.510000 plusOrMinus 10.0.pow(-10))

            result = futureValue(0.2, 5.0, 100.0)
            result shouldBe (-744.160000 plusOrMinus 10.0.pow(-10))

            result = futureValue(0.1, 5.0, 100.0, paymentSchedule = BEGIN)
            result shouldBe (-671.561000 plusOrMinus 10.0.pow(-10))

            result = futureValue(0.2, 5.0, 100.0, paymentSchedule = BEGIN)
            result shouldBe (-892.992000 plusOrMinus 10.0.pow(-10))

            result = futureValue(0.0, 5.0, 100.0)
            result shouldBe (-500.0 plusOrMinus 10.0.pow(-10))
        }
    }

    @Nested
    inner class NumberOfPeriodicPayments {
        @Test
        fun numberOfPeriodicPaymentsBasic() {
            var result = numberOfPeriodicPayments(0.0, -2000.0, 0.0, 100000.0)
            result shouldBe (50.0 plusOrMinus 10.0.pow(-5))

            result = numberOfPeriodicPayments(0.075, -2000.0, 0.0, 100000.0)
            result shouldBe (21.544944 plusOrMinus 10.0.pow(-5))

            result = numberOfPeriodicPayments(0.1, 0.0, -500.0, 1500.0)
            result shouldBe (11.52670461 plusOrMinus 10.0.pow(-6))
        }

        @Test
        fun numberOfPeriodicPaymentsPaymentScheduleBegin() {
            val result = numberOfPeriodicPayments(0.075, -2000.0, 0.0, 100000.0, paymentSchedule = BEGIN)
            result shouldBe (20.76156441 plusOrMinus 10.0.pow(-8))
        }

        @Test
        fun numberOfPeriodicPaymentsInfinitePayments() {
            val result = numberOfPeriodicPayments(0.0, -0.0, 1000.0)
            result shouldBe POSITIVE_INFINITY
        }

        @Test
        fun numberOfPeriodicPaymentsNoInterest() {
            val result = numberOfPeriodicPayments(0.0, -100.0, 1000.0)
            result shouldBe (10.0 plusOrMinus 10.0.pow(-10))
        }
    }

    @Nested
    inner class PresentValue {
        @Test
        fun presentValueBasic() {
            var result = presentValue(0.07, 20.0, 12000.0)
            result shouldBe (-127128.1709461939327295222005 plusOrMinus 10.0.pow(-10))

            result = presentValue(0.075, 20.0, -2000.0, 86609.362673042924)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))
        }

        @Test
        fun presentValuePaymentScheduleBegin() {
            val result = presentValue(0.075, 20.0, -2000.0, 93105.064874, BEGIN)

            result shouldBe (0.0 plusOrMinus 10.0.pow(-6))
        }

        @Test
        fun presentValueAdditionalInputValues() {
            var result = presentValue(0.1, 5.0, 100.0, -610.510000)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))

            result = presentValue(0.2, 5.0, 100.0, -744.160000)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))

            result = presentValue(0.1, 5.0, 100.0, -671.561000, BEGIN)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))

            result = presentValue(0.2, 5.0, 100.0, -892.992000, BEGIN)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))

            result = presentValue(0.0, 5.0, 100.0, -500.0)
            result shouldBe (0.0 plusOrMinus 10.0.pow(-10))
        }
    }
}
