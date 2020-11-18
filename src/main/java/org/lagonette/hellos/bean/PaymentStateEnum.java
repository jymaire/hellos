package org.lagonette.hellos.bean;

/**
 * Enum defined by Hello Asso API
 *
 * @see Model section at https://api.helloasso.com/v5/swagger/ui/index
 */
public enum PaymentStateEnum {
    Pending,
    Authorized,
    Refused,
    Unknown,
    Registered,
    Error,
    Refunded,
    Refunding,
    Waiting,
    Canceled,
    Contested,
    WaitingBankValidation,
    WaitingBankWithdraw;
}
