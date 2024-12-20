package com.yulcomtechnologies.tresorms.events;

import com.yulcomtechnologies.sharedlibrary.events.Dispatchable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentSucceeded implements Dispatchable {
    private final String paymentId;
    private final Long documentRequestId;
}
